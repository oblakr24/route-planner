package com.rokoblak.routeplanner.repo

import app.cash.turbine.test
import com.rokoblak.routeplanner.data.datasource.RouteDetailsRemoteDataSource
import com.rokoblak.routeplanner.data.datasource.RouteRoutingDataSource
import com.rokoblak.routeplanner.data.repo.AppRouteDetailsRepo
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import com.rokoblak.routeplanner.domain.model.RouteDetails
import com.rokoblak.routeplanner.domain.model.RoutePoint
import com.rokoblak.routeplanner.domain.model.RouteRoutingDetails
import com.rokoblak.routeplanner.util.TestCoroutineRule
import com.rokoblak.routeplanner.util.awaitItem
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import java.time.Duration

class RouteDetailsRepoTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    private val routeId = "route_id-1"

    private val mockDetails = RouteDetails(
        id = routeId,
        name = "routeName1",
        stops = listOf(
            RouteDetails.Stop("stop1", RoutePoint(0.0, 0.0), emptyList()),
        ),
        firstStopCoord = RoutePoint(0.0, 0.0),
    )

    private val mockRoutingDetails = RouteRoutingDetails(
        points = listOf(listOf(RoutePoint(0.0, 0.0, name = "Pt1 name"))),
        time = Duration.ofSeconds(10),
        distanceInM = 123,
        legs = listOf(),
        startName = "Start pt name",
    )

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val detailsSource: RouteDetailsRemoteDataSource = mockk()
        val routingSource: RouteRoutingDataSource = mockk()
        coEvery { detailsSource.load(any()) } returns flowOf(LoadableResult.Loading)


        val repo = AppRouteDetailsRepo(
            source = detailsSource,
            routingSource = routingSource,
        )
        repo.loadResults(routeId).test {
            val firstResult = awaitItem()
            assertEquals(LoadableResult.Loading, firstResult)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testDetailsLoad() = coroutineTestRule.runTest {
        val detailsSource: RouteDetailsRemoteDataSource = mockk()
        val routingSource: RouteRoutingDataSource = mockk()

        val detailsFlow = MutableStateFlow<LoadableResult<RouteDetails>?>(null)
        coEvery { detailsSource.load(any()) } returns flow {
            emit(LoadableResult.Loading)
            delay(50)
            emitAll(detailsFlow.filterNotNull())
        }
        detailsFlow.tryEmit(LoadableResult.Success(mockDetails))

        val routingFlow = MutableStateFlow<LoadableResult<RouteRoutingDetails>?>(null)
        coEvery { routingSource.load(mockDetails) } returns flow {
            emit(LoadableResult.Loading)
            delay(50)
            emitAll(routingFlow.filterNotNull())
        }
        routingFlow.tryEmit(LoadableResult.Success(mockRoutingDetails))

        val repo = AppRouteDetailsRepo(
            source = detailsSource,
            routingSource = routingSource,
        )
        repo.loadResults(routeId).test {
            val firstResult = awaitItem()
            assertEquals(LoadableResult.Loading, firstResult)

            val expectedDetailsResult = LoadableResult.Success(
                ExpandedRouteDetails(
                    route = mockDetails,
                    firstPoint = RoutePoint(0.0, 0.0),
                    waypoints = listOf(RoutePoint(0.0, 0.0, "Start")),
                    pathPoints = listOf(listOf(RoutePoint(0.0, 0.0, "Start"))),
                    studentsToPickUpAtStart = emptyList(),
                    legs = emptyList(),
                    loadingRouting = true,
                    totalStudents = 0,
                    distanceInM = null,
                    totalTime = null,
                )
            )

            assertEquals(expectedDetailsResult, awaitItem { it !is LoadableResult.Loading })

            val expectedFullDetailsResult = LoadableResult.Success(
                ExpandedRouteDetails(
                    route = mockDetails,
                    firstPoint = RoutePoint(0.0, 0.0),
                    waypoints = listOf(RoutePoint(0.0, 0.0, name = "Start pt name")),
                    pathPoints = listOf(listOf(RoutePoint(0.0, 0.0, name = "Pt1 name"))),
                    studentsToPickUpAtStart = emptyList(),
                    legs = emptyList(),
                    loadingRouting = false,
                    totalStudents = 0,
                    distanceInM = 123,
                    totalTime = Duration.ofSeconds(10),
                )
            )

            assertEquals(expectedFullDetailsResult, awaitItem { it != expectedDetailsResult })

            cancelAndIgnoreRemainingEvents()
        }
    }
}
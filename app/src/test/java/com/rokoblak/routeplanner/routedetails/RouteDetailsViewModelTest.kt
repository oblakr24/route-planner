package com.rokoblak.routeplanner.routedetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import com.rokoblak.routeplanner.domain.model.RouteDetails
import com.rokoblak.routeplanner.domain.model.RoutePoint
import com.rokoblak.routeplanner.domain.usecases.RouteDetailsUseCase
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsAction
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsViewModel
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteMapsData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteScaffoldUIState
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteScaffoldUIState.MainContentState.Error.Type
import com.rokoblak.routeplanner.util.TestCoroutineRule
import com.rokoblak.routeplanner.util.TestUtils
import com.rokoblak.routeplanner.util.awaitItem
import io.mockk.coEvery
import io.mockk.every
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


@OptIn(ExperimentalCoroutinesApi::class)
class RouteDetailsViewModelTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    private val routeId = "route_id-1"
    private val routeName = "routeName1"

    private val stateHandle = SavedStateHandle().apply {
        set("key-id", routeId)
        set("key-name", routeName)
    }

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val listingUseCase: RouteDetailsUseCase = mockk()
        every { listingUseCase.loadResults(routeId) } returns flowOf(LoadableResult.Loading)

        val vm = RouteDetailsViewModel(
            savedStateHandle = stateHandle,
            routeNavigator = TestUtils.emptyNavigator,
            useCase = listingUseCase,
        )

        val expected = RouteScaffoldUIState(
            title = routeName,
            subtitle = TextRes.Res(R.string.details_loading_route),
            mainContent = RouteScaffoldUIState.MainContentState.Loading
        )
        vm.uiState.test {
            assertEquals(expected, this.awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testErrorIsRetriable() = coroutineTestRule.runTest {
        val listingUseCase: RouteDetailsUseCase = mockk()
        val detailsFlow = MutableStateFlow<LoadableResult<ExpandedRouteDetails>?>(null)
        every { listingUseCase.loadResults(routeId) } returns flow {
            emit(LoadableResult.Loading)
            delay(50)
            emit(LoadableResult.Error(LoadErrorType.NoNetwork))
            emitAll(detailsFlow.filterNotNull())
        }

        coEvery { listingUseCase.reload() } returns kotlin.run {
            detailsFlow.value = LoadableResult.Success(
                ExpandedRouteDetails(
                    route = RouteDetails(
                        id = routeId,
                        name = routeName,
                        stops = listOf(
                            RouteDetails.Stop("stop_1", RoutePoint(0.0, 0.0), emptyList())
                        ),
                        firstStopCoord = RoutePoint(0.0, 0.0)
                    ),
                    firstPoint = RoutePoint(0.0, 0.0),
                    waypoints = emptyList(),
                    pathPoints = emptyList(),
                    studentsToPickUpAtStart = emptyList(),
                    legs = emptyList(),
                    loadingRouting = true,
                    totalStudents = 0,
                    distanceInM = null,
                    totalTime = null,
                )
            )
        }

        val vm = RouteDetailsViewModel(
            savedStateHandle = stateHandle,
            routeNavigator = TestUtils.emptyNavigator,
            useCase = listingUseCase,
        )

        val expected = RouteScaffoldUIState(
            title = routeName,
            subtitle = TextRes.Res(R.string.details_loading_route),
            mainContent = RouteScaffoldUIState.MainContentState.Loading
        )
        vm.uiState.test {
            assertEquals(expected, this.awaitItem())

            val expectedError = RouteScaffoldUIState(
                title = routeName,
                subtitle = TextRes.Text(""),
                mainContent = RouteScaffoldUIState.MainContentState.Error(type = Type.NoConnection)
            )

            assertEquals(
                expectedError,
                awaitItem { it.mainContent != RouteScaffoldUIState.MainContentState.Loading })

            vm.onAction(RouteDetailsAction.RetryClicked)

            val expectedAfterRetry = RouteScaffoldUIState(
                title = routeName,
                subtitle = TextRes.Res.create(R.string.sub_details_stops_students, 1, 0),
                mainContent = RouteScaffoldUIState.MainContentState.Loaded(
                    data = RouteMapsData(
                        center = RouteMapsData.Point(0.0, 0.0),
                        markers = emptyList(),
                        polylines = emptyList(),
                    )
                )
            )

            assertEquals(
                expectedAfterRetry,
                awaitItem { it.mainContent is RouteScaffoldUIState.MainContentState.Loaded })
        }
    }

}
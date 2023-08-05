package com.rokoblak.routeplanner.repo

import app.cash.turbine.test
import com.rokoblak.routeplanner.data.datasource.RoutesRemoteDataSource
import com.rokoblak.routeplanner.data.repo.AppRoutesRepo
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.data.repo.model.RoutesPage
import com.rokoblak.routeplanner.domain.model.Route
import com.rokoblak.routeplanner.domain.model.RoutesListing
import com.rokoblak.routeplanner.util.TestCoroutineRule
import com.rokoblak.routeplanner.util.awaitItem
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoutesRepoTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    private val mockRoute = Route(
        routeId = "route_id-1",
        itemId = "route_id-1",
        name = "route1",
    )

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val remote: RoutesRemoteDataSource = mockk()
        coEvery { remote.load(any()) } returns CallResult.Success(
            RoutesPage(emptyList(), page = 0, end = true)
        )

        val repo = AppRoutesRepo(
            remote = remote,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )
        repo.flow.test {
            val firstResult = awaitItem()
            assertEquals(LoadableResult.Loading, firstResult)
        }
    }

    @Test
    fun testReload() = coroutineTestRule.runTest {
        val remote: RoutesRemoteDataSource = mockk()

        val repo = AppRoutesRepo(
            remote = remote,
            dispatcher = coroutineTestRule.testCoroutineDispatcher
        )

        // Given that we have a success response
        coEvery { remote.load(any()) } returns CallResult.Success(
            RoutesPage(listOf(mockRoute), page = 1, end = true)
        )

        repo.flow.test {
            val firstResult = awaitItem()

            // Then we expect to see it returned
            assertEquals(
                LoadableResult.Success(
                    RoutesListing(
                        listOf(
                            Route(
                                routeId = "route_id-1",
                                itemId = "route_id-1",
                                name = "route1",
                            )
                        ), loadingMore = false, page = 1, end = true
                    )
                ), firstResult
            )

            // Given that a new call would return a different response
            coEvery { remote.load(any()) } returns CallResult.Success(
                RoutesPage(listOf(mockRoute.copy(name = "route name updated")), page = 1, end = true)
            )

            repo.reload()

            val loadingRes = awaitItem { it != firstResult }

            assertEquals(LoadableResult.Loading, loadingRes)

            val nextResult = awaitItem { it != loadingRes }

            // Then we expect to get an updated result returned
            assertEquals(
                LoadableResult.Success(
                    RoutesListing(
                        listOf(
                            Route(
                                routeId = "route_id-1",
                                itemId = "route_id-1",
                                name = "route name updated",
                            )
                        ), loadingMore = false, page = 1, end = true
                    )
                ), nextResult
            )
        }
    }
}
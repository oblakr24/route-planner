package com.rokoblak.routeplanner.routeslisting

import app.cash.turbine.test
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.RoutesListing
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.routeplanner.domain.usecases.RouteListingUseCase
import com.rokoblak.routeplanner.ui.feature.routelisting.RouteListingAction
import com.rokoblak.routeplanner.ui.feature.routelisting.RouteListingViewModel
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteListingDrawerUIState
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteListingScaffoldUIState
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RoutesListingData
import com.rokoblak.routeplanner.util.TestCoroutineRule
import com.rokoblak.routeplanner.util.TestUtils
import com.rokoblak.routeplanner.util.awaitItem
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.collections.immutable.persistentListOf
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
class RoutesListingViewModelTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    @Test
    fun testInitialState() = coroutineTestRule.runTest {
        val listingUseCase: RouteListingUseCase = mockk()
        every { listingUseCase.flow } returns flowOf(LoadableResult.Loading)
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns flowOf(true)

        val vm = RouteListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase,
        )

        val expected = RouteListingScaffoldUIState(
            RouteListingDrawerUIState(darkMode = true),
            RoutesListingData.Initial
        )
        vm.uiState.test {
            assertEquals(expected, this.awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testErrorIsRetriable() = coroutineTestRule.runTest {
        val routesFlow = MutableStateFlow<LoadableResult<RoutesListing>?>(null)
        val listingUseCase: RouteListingUseCase = mockk()
        every { listingUseCase.flow } returns flow {
            emit(LoadableResult.Loading)
            delay(50)
            emit(LoadableResult.Error(LoadErrorType.NoNetwork))
            emitAll(routesFlow.filterNotNull())
        }

        coEvery { listingUseCase.reload() } returns kotlin.run {
            routesFlow.value = LoadableResult.Success(RoutesListing(emptyList(), false, 1, false))
        }
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns flowOf(true)

        val vm = RouteListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase,
        )

        vm.uiState.test {
            val expectedDrawerState = RouteListingDrawerUIState(darkMode = true)
            val expected =
                RouteListingScaffoldUIState(expectedDrawerState, RoutesListingData.Initial)
            assertEquals(expected, awaitItem())

            val expectedError = RouteListingScaffoldUIState(
                expectedDrawerState,
                RoutesListingData.Error(isNoConnection = true)
            )

            assertEquals(
                expectedError,
                awaitItem { it.innerContent != RoutesListingData.Initial })

            vm.onAction(RouteListingAction.RefreshTriggered)

            val expectedAfterRefresh = RouteListingScaffoldUIState(
                expectedDrawerState,
                RoutesListingData.Loaded(persistentListOf(), false)
            )
            assertEquals(
                expectedAfterRefresh,
                awaitItem { it.innerContent is RoutesListingData.Loaded })
        }
    }

    @Test
    fun testDarkModeSwitched() = coroutineTestRule.runTest {
        val listingUseCase: RouteListingUseCase = mockk()
        every { listingUseCase.flow } returns flowOf(LoadableResult.Loading)

        val darkModeFlow = MutableStateFlow<Boolean?>(null)
        val darkModeUseCase: DarkModeHandlingUseCase = mockk()
        every { darkModeUseCase.darkModeEnabled() } returns darkModeFlow

        val vm = RouteListingViewModel(
            routeNavigator = TestUtils.emptyNavigator,
            listingUseCase = listingUseCase,
            darkModeUseCase = darkModeUseCase
        )

        vm.uiState.test {
            val expected = RouteListingScaffoldUIState(
                RouteListingDrawerUIState(darkMode = null),
                RoutesListingData.Initial
            )
            val initialState = awaitItem()
            assertEquals(expected, initialState)

            coEvery { darkModeUseCase.updateDarkMode(true) } just runs
            darkModeFlow.value = true
            vm.onAction(RouteListingAction.SetDarkMode(true))

            val updatedState = awaitItem { it != initialState }

            val expectedUpdated = RouteListingScaffoldUIState(
                RouteListingDrawerUIState(darkMode = true),
                RoutesListingData.Initial
            )
            assertEquals(expectedUpdated, updatedState)
        }
    }
}

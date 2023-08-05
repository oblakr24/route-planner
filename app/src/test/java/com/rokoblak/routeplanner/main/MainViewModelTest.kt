package com.rokoblak.routeplanner.main

import app.cash.turbine.test
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.routeplanner.ui.feature.main.MainScreenUIState
import com.rokoblak.routeplanner.ui.feature.main.MainViewModel
import com.rokoblak.routeplanner.util.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    @Test
    fun testMappingWorksCorrectly() = coroutineTestRule.runTest {
        val storage: DarkModeHandlingUseCase = mockk()
        every { storage.darkModeEnabled() } returns flowOf(true)

        val vm = MainViewModel(storage)

        vm.uiState.test {
            assertEquals(MainScreenUIState(isDarkTheme = true), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

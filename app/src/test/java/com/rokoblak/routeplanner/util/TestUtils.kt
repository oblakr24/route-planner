package com.rokoblak.routeplanner.util

import app.cash.turbine.ReceiveTurbine
import com.rokoblak.routeplanner.ui.navigation.NavigationState
import com.rokoblak.routeplanner.ui.navigation.RouteNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeout

object TestUtils {

    val emptyNavigator = object : RouteNavigator {
        override fun onNavigated(state: NavigationState) = Unit

        override fun navigateUp() = Unit

        override fun popToRoute(route: String) = Unit

        override fun navigateToRoute(route: String) = Unit

        override val navigationState: StateFlow<NavigationState> =
            MutableStateFlow(NavigationState.Idle)
    }
}

/**
 * A helper to await a flow emission matching a condition. This is when we're not that interested in the exact sequence of emissions, we just want to await the right one.
 * The timeout works on a test scheduler so it passes near-instantly.
 */
suspend fun <T> ReceiveTurbine<T>.awaitItem(condition: (T) -> Boolean): T {
    return withTimeout(100) { // The flow should fill up near-instantly, this is just to fail the test in case there is an assertion failure
        var matching: T?
        do {
            matching = this@awaitItem.awaitItem().takeIf(condition)
        } while (matching == null)
        matching
    }
}

fun <T> Flow<T>.completeOnSignal(signal: Flow<Boolean>): Flow<T> {
    return combine(this@completeOnSignal, signal) { state, cancel ->
        state to cancel
    }.takeWhile { (_, cancel) ->
        !cancel
    }.map { (state, _) ->
        state
    }
}

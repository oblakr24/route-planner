package com.rokoblak.routeplanner.ui.feature.routedetails

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rokoblak.routeplanner.ui.navigation.NavRoute
import com.rokoblak.routeplanner.ui.navigation.getOrThrow


private const val KEY_ID = "key-id"
private const val KEY_NAME = "key-name"

object RouteDetailsRoute : NavRoute<RouteDetailsViewModel> {

    override val route =
        "repo/{$KEY_ID}?$KEY_NAME={$KEY_NAME}"

    fun get(input: Input): String = route
        .replace("{$KEY_ID}", input.routeId)
        .replace("{$KEY_NAME}", input.name)

    fun getIdFrom(savedStateHandle: SavedStateHandle): Input {
        val routeId = savedStateHandle.getOrThrow<String>(KEY_ID)
        val routeName = savedStateHandle.getOrThrow<String>(KEY_NAME)
        return Input(routeId = routeId, name = routeName)
    }

    override fun getArguments(): List<NamedNavArgument> = listOf(
        navArgument(KEY_ID) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType },
    )

    @Composable
    override fun viewModel(): RouteDetailsViewModel = hiltViewModel()

    @Composable
    override fun Content(viewModel: RouteDetailsViewModel) = RouteDetailsScreen(viewModel)

    data class Input(
        val routeId: String,
        val name: String,
    )
}

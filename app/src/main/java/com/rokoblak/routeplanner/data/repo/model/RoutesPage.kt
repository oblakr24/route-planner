package com.rokoblak.routeplanner.data.repo.model

import com.rokoblak.routeplanner.domain.model.Route


data class RoutesPage(
    val routes: List<Route>,
    val page: Int,
    val end: Boolean,
)

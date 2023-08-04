package com.rokoblak.routeplanner.domain.model


data class RoutesListing(
    val routes: List<Route>,
    val loadingMore: Boolean,
    val page: Int,
    val end: Boolean,
)

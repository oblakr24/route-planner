package com.rokoblak.routeplanner.data.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RoutesResponse(
    val data: List<Route>,
) {
    // This is just to use when mocking more data for a paginated response
    var continuePaginating: Boolean = false

    @Serializable
    data class Route(
        val id: String,
        val name: String,
    ) {
        var orgId: String = id
    }
}

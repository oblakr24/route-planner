package com.rokoblak.routeplanner.domain.model


data class RoutePoint(
    val lat: Double,
    val long: Double,
    val title: String? = null,
)

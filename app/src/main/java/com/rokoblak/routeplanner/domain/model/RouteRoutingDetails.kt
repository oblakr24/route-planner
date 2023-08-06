package com.rokoblak.routeplanner.domain.model

import java.time.Duration

data class RouteRoutingDetails(
    val points: List<List<RoutePoint>>,
    val time: Duration,
    val distanceInM: Int,
    val legs: List<Leg>,
    val startName: String,
)

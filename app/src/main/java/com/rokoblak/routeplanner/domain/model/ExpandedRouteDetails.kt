package com.rokoblak.routeplanner.domain.model

import java.time.Duration

data class ExpandedRouteDetails(
    val route: RouteDetails,
    val firstPoint: RoutePoint,
    val waypoints: List<RoutePoint>,
    val pathPoints: List<RoutePoint>,
    val studentsToPickUpAtStart: List<Student>,
    val legs: List<Leg>,
    val loadingRouting: Boolean,
    val totalStudents: Int,
    val distanceInM: Int?,
    val totalTime: Duration?,
)

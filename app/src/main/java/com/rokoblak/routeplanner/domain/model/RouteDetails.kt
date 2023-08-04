package com.rokoblak.routeplanner.domain.model

data class RouteDetails(
    val id: String,
    val name: String,
    val stops: List<Stop>,
    val firstStopCoord: RoutePoint,
) {
    data class Stop(
        val id: String,
        val coord: RoutePoint,
        val students: List<Student>,
    )
}

package com.rokoblak.routeplanner.data.service.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RouteDetailsResponse(
    val id: String,
    val name: String,
    val stops: List<Stop>,
) {
    @Serializable
    data class Stop(
        val id: String,
        val coord: Coord,
        val students: List<Student>,
    )

    @Serializable
    data class Coord(
        val lat: Double,
        val lng: Double,
    )

    @Serializable
    data class Student(
        val id: String,
        val name: String,
        val grade: String?,
    )
}

package com.rokoblak.routeplanner.data.service.api.model

import kotlinx.serialization.Serializable

@Serializable
class RoutingResponse(
    val results: List<Result>,
    val properties: Properties,
) {

    @Serializable
    data class Result(
        val mode: String,
        val waypoints: List<Waypoint>,
        val units: String,
        val distance: Int,
        val distance_units: String,
        val time: Double,
        val legs: List<Leg>,
        val geometry: List<List<Point>>,
    )

    @Serializable
    data class Leg(
        val distance: Int,
        val time: Double,
        val steps: List<LegStep>,
    )

    @Serializable
    data class LegStep(
        val from_index: Int,
        val to_index: Int,
        val distance: Int,
        val time: Double,
        val name: String?,
        val instruction: LegInstruction?,
    )

    @Serializable
    data class LegInstruction(
        val text: String,
        val type: String?,
        val transition_instruction: String?,
        val pre_transition_instruction: String?,
        val post_transition_instruction: String?,
    )

    @Serializable
    data class Waypoint(
        val location: List<Double>,
        val original_index: Int,
    )

    @Serializable
    data class Properties(
        val mode: String,
        val waypoints: List<Point>,
        val units: String,
    )

    @Serializable
    data class Point(
        val lat: Double,
        val lon: Double,
    )
}

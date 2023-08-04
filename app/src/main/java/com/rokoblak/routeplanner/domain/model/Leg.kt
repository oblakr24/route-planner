package com.rokoblak.routeplanner.domain.model

import java.time.Duration

data class Leg(
    val id: String,
    val time: Duration,
    val name: String,
    val distanceInM: Int,
    val marker: RoutePoint,
    val steps: List<LegStep>,
    val studentsToPickUpAtEnd: List<Student>,
)

data class LegStep(
    val id: String,
    val time: Duration,
    val name: String,
    val distanceInM: Int,
    val instruction: String,
)

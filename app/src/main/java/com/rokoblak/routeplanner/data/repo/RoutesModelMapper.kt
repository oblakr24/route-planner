package com.rokoblak.routeplanner.data.repo

import com.rokoblak.routeplanner.data.service.api.model.RouteDetailsResponse
import com.rokoblak.routeplanner.data.service.api.model.RoutesResponse
import com.rokoblak.routeplanner.data.service.api.model.RoutingResponse
import com.rokoblak.routeplanner.domain.model.Leg
import com.rokoblak.routeplanner.domain.model.LegStep
import com.rokoblak.routeplanner.domain.model.Route
import com.rokoblak.routeplanner.domain.model.RouteDetails
import com.rokoblak.routeplanner.domain.model.RoutePoint
import com.rokoblak.routeplanner.domain.model.RouteRoutingDetails
import com.rokoblak.routeplanner.domain.model.Student
import java.time.Duration

object RoutesModelMapper {

    fun mapRoutes(response: RoutesResponse): List<Route> = with(response) {
        return data.map { apiRoute ->
            Route(
                routeId = apiRoute.orgId,
                itemId = apiRoute.id,
                name = apiRoute.name,
            )
        }
    }

    fun mapRoutingDetails(response: RoutingResponse, studentsPerIdx: Map<Int, List<Student>>, pointsPerIdx: Map<Int, RoutePoint>): RouteRoutingDetails? = with(response) {
        val result = results.firstOrNull() ?: return null
        val points = result.geometry.map { linePoints ->
            linePoints.map { pt ->
                RoutePoint(lat = pt.lat, long = pt.lon)
            }
        }

        val nextLegNames = result.legs.mapIndexed { idx, leg ->
            idx-1 to leg.steps.firstOrNull()?.name
        }.toMap()

        val legs = result.legs.mapIndexed { idx, leg ->
            val nextLegName = nextLegNames[idx]
            val point = pointsPerIdx[idx] ?: pointsPerIdx.values.first()
            leg.toDomain(idx, students = studentsPerIdx[idx] ?: emptyList(), nextLegName = nextLegName, point = point)
        }

        return RouteRoutingDetails(
            points = points,
            legs = legs,
            time = mapTime(result.time),
            distanceInM = result.distance,
            startName = legs.firstOrNull()?.steps?.firstOrNull()?.name ?: "Start",
        )
    }

    fun mapDetails(response: RouteDetailsResponse): RouteDetails? = with(response) {
        val firstStop = stops.firstOrNull() ?: return null
        return RouteDetails(
            id = id,
            name = name,
            firstStopCoord = firstStop.toDomain().coord,
            stops = stops.map { it.toDomain() },
        )
    }

    private fun RoutingResponse.Leg.toDomain(legIdx: Int, students: List<Student>, nextLegName: String?, point: RoutePoint): Leg {
        val steps = steps.mapIndexed { index, legStep ->
            legStep.toDomain(index, legIdx)
        }

        val firstStepName = steps.firstOrNull()?.name ?: ""
        val name = if (firstStepName == nextLegName) {
            firstStepName
        } else if (firstStepName.isNotBlank() && nextLegName != null) {
            "$firstStepName - $nextLegName"
        } else {
            firstStepName.takeIf { it.isNotBlank() }
                ?: steps.reversed().firstNotNullOfOrNull { s -> s.name.takeIf { it.isNotBlank() } }
                ?: "Destination"
        }

        return Leg(
            id = legIdx.toString(),
            time = mapTime(time),
            name = name,
            distanceInM = distance,
            steps = steps,
            marker = point,
            studentsToPickUpAtEnd = students,
        )
    }

    private fun RoutingResponse.LegStep.toDomain(idx: Int, parentIdx: Int) = LegStep(
        id = "$idx|$parentIdx",
        time = mapTime(time),
        distanceInM = distance,
        name = name ?: "",
        instruction = instruction?.text ?: ""
    )

    private fun mapTime(timeInSec: Double) = Duration.ofMillis((timeInSec*1000).toLong())

    private fun RouteDetailsResponse.Stop.toDomain() = RouteDetails.Stop(
        id = id,
        coord = coord.toDomain(),
        students = students.map { it.toDomain() },
    )

    private fun RouteDetailsResponse.Coord.toDomain() = RoutePoint(
        lat = lat,
        long = lng,
    )

    private fun RouteDetailsResponse.Student.toDomain() = Student(
        id = id,
        name = name,
        grade = grade,
    )
}

package com.rokoblak.routeplanner.ui.feature.routedetails

import com.rokoblak.routeplanner.BuildConfig
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import com.rokoblak.routeplanner.domain.model.Leg
import com.rokoblak.routeplanner.domain.model.LegStep
import com.rokoblak.routeplanner.domain.model.Student
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.LegDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.LegSection
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteContentUIState
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteHeaderDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteLegsListingData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.StepDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.StudentDisplayData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.Duration

object RouteDetailsUIMapper {

    fun createUIState(
        details: ExpandedRouteDetails,
        expandedState: Map<String, Boolean>
    ): RouteContentUIState.Loaded = with(details) {
        val center = RouteContentUIState.Loaded.Point(lat = firstPoint.lat, long = firstPoint.long)

        val polyline = pathPoints.map {
            RouteContentUIState.Loaded.Point(it.lat, it.long)
        }

        val markers = waypoints.mapIndexed { idx, pt ->
            val studentsCount = if (idx == 0) {
                studentsToPickUpAtStart
            } else {
                legs.getOrNull(idx)?.studentsToPickUpAtEnd
            }?.size ?: 0
            val subtitle = if (studentsCount > 0) TextRes.Res.create(
                R.string.sub_students_count,
                studentsCount
            ) else null
            RouteContentUIState.Loaded.Point(
                pt.lat,
                pt.long,
                pt.name?.let { TextRes.Text(it) },
                subtitle = subtitle
            )
        }

        val listing = if (legs.isNotEmpty()) {
            val itemsPairs = legs.mapIndexed { idx, leg ->
                val steps = leg.steps.map { step ->
                    step.toUI()
                }
                val legData = leg.toUI(if (idx == 0) studentsToPickUpAtStart else null)
                val expanded = expandedState[leg.id] ?: false
                LegSection(
                    expanded = expanded, legData,
                    if (expanded) steps.toImmutableList() else persistentListOf()
                )
            }.toImmutableList()

            RouteLegsListingData(itemsPairs)
        } else {
            null
        }

        val subtitle =
            TextRes.Res.create(R.string.sub_details_stops_students, route.stops.size, totalStudents)
        val extraSubtitle = if (distanceInM != null && totalTime != null) {
            TextRes.Text(formatDistanceAndTime(distanceInM, totalTime))
        } else {
            if (details.loadingRouting) TextRes.Res(R.string.details_loading_routing) else TextRes.Text(
                ""
            )
        }

        val header = RouteHeaderDisplayData(
            showNoKeysWarning = BuildConfig.HAS_API_KEYS.not(),
            center = center,
            polyline = polyline,
            markers = markers,
            subtitle = subtitle,
            extraSubtitle = extraSubtitle,
        )

        return RouteContentUIState.Loaded(
            header = header,
            listingData = listing,
            loadingRouting = loadingRouting,
        )
    }

    private fun Leg.toUI(studentsAtStart: List<Student>?): LegDisplayData {
        val students = (studentsAtStart ?: studentsToPickUpAtEnd).map {
            it.toUI()
        }

        val timeDistDesc = formatDistanceAndTime(distanceInM, time)
        val timeStudentsDesc = if (students.isNotEmpty()) {
            if (students.size == 1) {
                TextRes.Res.create(R.string.time_students_desc_count_one, timeDistDesc)
            } else {
                TextRes.Res.create(
                    R.string.time_students_desc_count_multiple, timeDistDesc, students.size
                )
            }
        } else {
            TextRes.Text(timeDistDesc)
        }

        return LegDisplayData(
            id = id,
            title = name,
            subtitleCollapsed = timeStudentsDesc,
            subtitleExpanded = TextRes.Text(timeDistDesc),
            markerLat = marker.lat,
            markerLong = marker.long,
            students = students.toImmutableList()
        )
    }

    private fun Student.toUI() = StudentDisplayData(
        id = id,
        name = name,
        subtitle = grade?.let { TextRes.Res.create(R.string.student_desc_grade, it) }
    )

    private fun LegStep.toUI() = StepDisplayData(
        id = id,
        name = name,
        instruction = instruction.takeIf { it.isNotBlank() },
        subtitle = formatDistanceAndTime(distanceInM, time).let {
            if (it.isNotBlank()) TextRes.Text(it) else null
        },
        fallbackName = if (name.isBlank() && instruction.isBlank()) {
            TextRes.Res.create(R.string.step_fallback, metersToDisplay(distanceInM))
        } else {
            null
        }
    )

    private fun formatDistanceAndTime(distanceInM: Int, time: Duration): String {
        if (distanceInM == 0 || time < Duration.ofSeconds(1)) return ""
        return "${metersToDisplay(distanceInM)}, ${time.toDisplay()}"
    }

    private fun Duration.toDisplay(): String {
        if (isZero || isNegative) return "/"
        val mins = toMinutes()
        return if (mins > 0) {
            val secs = toSecondsPart()
            "${mins}m${secs}s"
        } else {
            val secs = toSeconds()
            "${secs}s"
        }
    }

    private fun metersToDisplay(meters: Int): String {
        if (meters > 1000) {
            return String.format("%.1fkm", meters / 1000F)
        }
        return "${meters}m"
    }
}
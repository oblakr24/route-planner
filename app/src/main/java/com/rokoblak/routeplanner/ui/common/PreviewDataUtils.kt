package com.rokoblak.routeplanner.ui.common

import com.rokoblak.routeplanner.ui.feature.routedetails.composables.LegDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.LegSection
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteContentUIState
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteHeaderDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteLegsListingData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.StepDisplayData
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.StudentDisplayData
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteDisplayData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList


object PreviewDataUtils {

    val routeData = RouteDisplayData(
        routeId =  "1",
        itemId = "1",
        name = "Route 1",
    )

    val routes by lazy {
        (0..10).map { idx ->
            routeData.copy(routeId = idx.toString(), itemId = idx.toString(), name = "Route $idx")
        }
    }

    val legsListing = RouteLegsListingData(
        items = (0..10).map { legIdx ->
            val leg = leg(legIdx)
            val steps = (0..10).map {
                step(it, legIdx)
            }.toImmutableList()
           LegSection(expanded = false, leg, steps)
        }.toImmutableList()
    )

    fun routeHeader() = RouteHeaderDisplayData(
        center = RouteContentUIState.Loaded.Point(-1.0, -1.0),
        polyline = emptyList(),
        markers = emptyList(),
        subtitle = TextRes.Text("Subtitle"),
        extraSubtitle = TextRes.Text("Extra subtitle"),
        showNoKeysWarning = false,
    )

    fun step(idx: Int = 1, parentIdx: Int = 1) = StepDisplayData(
        id = "$idx|$parentIdx",
        name = "$idx: Commonwealth Avenue",
        instruction = "Turn left at X",
        subtitle = TextRes.Text("800m (2:34s)"),
        fallbackName = TextRes.Text(""),
    )

    fun leg(idx: Int = 1) = LegDisplayData(
        id = idx.toString(),
        title = "Leg $idx",
        subtitleExpanded = TextRes.Text("Leg $idx subtitle expanded"),
        subtitleCollapsed = TextRes.Text("Leg $idx subtitle collapsed"),
        markerLat = 0.0,
        markerLong = 0.0,
        students = (0..10).map { student(it) }.toImmutableList(),
    )

    fun student(idx: Int = 1) = StudentDisplayData(
        id = idx.toString(),
        name = "Student $idx",
        subtitle = TextRes.Text("Grade 4"),
    )
}

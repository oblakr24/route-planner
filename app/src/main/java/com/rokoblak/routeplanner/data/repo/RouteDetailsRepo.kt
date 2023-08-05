package com.rokoblak.routeplanner.data.repo

import com.rokoblak.routeplanner.data.datasource.RouteDetailsRemoteDataSource
import com.rokoblak.routeplanner.data.datasource.RouteRoutingDataSource
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.data.repo.model.LoadableResult.Loading.concatOnSuccess
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import com.rokoblak.routeplanner.domain.model.RouteDetails
import com.rokoblak.routeplanner.domain.model.RoutePoint
import com.rokoblak.routeplanner.domain.model.RouteRoutingDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface RouteDetailsRepo {
    fun loadResults(routeId: String): Flow<LoadableResult<ExpandedRouteDetails>>
    suspend fun reload()
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppRouteDetailsRepo @Inject constructor(
    private val source: RouteDetailsRemoteDataSource,
    private val routingSource: RouteRoutingDataSource,
) : RouteDetailsRepo {

    private val inputs = MutableStateFlow<String?>(null)
    private var refreshing = MutableStateFlow(RefreshSignal())

    private val loadResults = combine(inputs.filterNotNull(), refreshing) { input, _ ->
        combinedFlow(input)
    }.flatMapLatest { loadFlow ->
        flow {
            emit(LoadableResult.Loading)
            emitAll(loadFlow)
        }
    }

    private fun combinedFlow(routeId: String) = source.load(routeId).concatOnSuccess(
        other = { routingSource.load(it) }, mapper = { details, routing, routingRes ->
            createExpandedDetails(details, routing, loadingRouting = routingRes is LoadableResult.Loading)
        })

    override fun loadResults(routeId: String): Flow<LoadableResult<ExpandedRouteDetails>> {
        return loadResults.also {
            inputs.value = routeId
        }
    }

    private fun createExpandedDetails(
        details: RouteDetails,
        routingDetails: RouteRoutingDetails?,
        loadingRouting: Boolean,
    ): ExpandedRouteDetails {
        val firstStop = details.firstStopCoord

        val waypoints = details.stops.mapIndexed { idx, stop ->
            val stopName = routingDetails?.legs?.getOrNull(idx-1)?.name ?: routingDetails?.startName ?: "Start"
            RoutePoint(lat = stop.coord.lat, long = stop.coord.long, name = stopName)
        }
        val pathPoints = routingDetails?.points ?: waypoints

        val studentsToPickUpAtStart = details.stops.first().students

        return ExpandedRouteDetails(
            route = details,
            firstPoint = RoutePoint(lat = firstStop.lat, long = firstStop.long),
            studentsToPickUpAtStart = studentsToPickUpAtStart,
            waypoints = waypoints,
            pathPoints = pathPoints,
            legs = routingDetails?.legs.orEmpty(),
            loadingRouting = loadingRouting,
            totalStudents = details.stops.sumOf { it.students.size },
            distanceInM = routingDetails?.distanceInM,
            totalTime = routingDetails?.time,
        )
    }

    override suspend fun reload() {
        refreshing.value = RefreshSignal()
    }

    class RefreshSignal
}

package com.rokoblak.routeplanner.data.datasource

import com.rokoblak.routeplanner.BuildConfig
import com.rokoblak.routeplanner.data.repo.RoutesModelMapper.toDomain
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.data.repo.model.toLoadable
import com.rokoblak.routeplanner.data.service.NetworkMonitor
import com.rokoblak.routeplanner.data.service.api.GeoApifyService
import com.rokoblak.routeplanner.domain.model.RouteDetails
import com.rokoblak.routeplanner.domain.model.RoutePoint
import com.rokoblak.routeplanner.domain.model.RouteRoutingDetails
import com.rokoblak.routeplanner.domain.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface RouteRoutingDataSource {
    fun load(details: RouteDetails): Flow<LoadableResult<RouteRoutingDetails>>
}

class AppRouteRoutingDataSource @Inject constructor(
    private val routingApi: GeoApifyService,
    private val networkMonitor: NetworkMonitor,
) : RouteRoutingDataSource {

    override fun load(details: RouteDetails): Flow<LoadableResult<RouteRoutingDetails>> =
        flow {
            emit(LoadableResult.Loading)
            if (!networkMonitor.connected.first()) {
                emit(LoadableResult.Error(LoadErrorType.NoNetwork))
                return@flow
            }

            val waypoints = details.stops.map { it.coord }
            val studentsPerIdx = details.stops.mapIndexed { index, stop ->
                // index+1 to associate it with the end of the leg coresponding to this stop
                index + 1 to stop.students
            }.toMap()
            val routingResult = loadRouting(waypoints, studentsPerIdx)
            emit(routingResult.toLoadable())
        }

    private suspend fun loadRouting(
        waypoints: List<RoutePoint>,
        studentsPerIdx: Map<Int, List<Student>>
    ) = CallResult.wrappedSafeCall {
        val joinedWaypoints = waypoints.joinToString(separator = "|") {
            "${it.lat},${it.long}"
        }
        routingApi.loadRouting(waypoints = joinedWaypoints, apiKey = BuildConfig.GEOAPIFY_API_KEY)
    }.flatMap {

        val idxToPoints = waypoints.withIndex().associate { (idx, pt) -> idx to pt }
        val result = it.toDomain(studentsPerIdx, idxToPoints)
            ?: return@flatMap CallResult.Error(LoadErrorType.ApiError("No results in response"))
        CallResult.Success(result)
    }
}

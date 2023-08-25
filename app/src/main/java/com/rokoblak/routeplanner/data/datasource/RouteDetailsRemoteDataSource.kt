package com.rokoblak.routeplanner.data.datasource

import com.rokoblak.routeplanner.data.repo.RoutesModelMapper
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.data.repo.model.toLoadable
import com.rokoblak.routeplanner.data.service.NetworkMonitor
import com.rokoblak.routeplanner.data.service.api.RoutesApiService
import com.rokoblak.routeplanner.domain.model.RouteDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface RouteDetailsRemoteDataSource {
    fun load(routeId: String): Flow<LoadableResult<RouteDetails>>
}

class AppRouteDetailsRemoteDataSource @Inject constructor(
    private val api: RoutesApiService,
    private val networkMonitor: NetworkMonitor,
) : RouteDetailsRemoteDataSource {

    override fun load(routeId: String): Flow<LoadableResult<RouteDetails>> =
        flow {
            emit(LoadableResult.Loading)
            if (!networkMonitor.connected.first()) {
                emit(LoadableResult.Error(LoadErrorType.NoNetwork))
                return@flow
            }
            val detailsRes = loadDetails(routeId = routeId)
            emit(detailsRes.toLoadable())
        }

    private suspend fun loadDetails(routeId: String) = CallResult.wrappedSafeCall {
        api.loadRouteDetails(routeId = routeId)
    }.flatMap {
        val details = RoutesModelMapper.mapDetails(it) ?: return@flatMap CallResult.Error(LoadErrorType.ApiError("No stops provided"))
        CallResult.Success(details)
    }
}

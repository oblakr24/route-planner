package com.rokoblak.routeplanner.domain.usecases

import com.rokoblak.routeplanner.data.repo.RouteDetailsRepo
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface RouteDetailsUseCase {
    fun loadResults(routeId: String): Flow<LoadableResult<ExpandedRouteDetails>>
    suspend fun reload()
}

class AppRouteDetailsUseCase @Inject constructor(
    private val repo: RouteDetailsRepo,
) : RouteDetailsUseCase {

    override fun loadResults(routeId: String): Flow<LoadableResult<ExpandedRouteDetails>> =
        repo.loadResults(routeId)

    override suspend fun reload() = repo.reload()
}

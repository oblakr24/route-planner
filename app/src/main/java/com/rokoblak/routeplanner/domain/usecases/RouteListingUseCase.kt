package com.rokoblak.routeplanner.domain.usecases

import com.rokoblak.routeplanner.data.repo.RoutesRepo
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.RoutesListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface RouteListingUseCase {

    val flow: Flow<LoadableResult<RoutesListing>>

    suspend fun loadNext()

    suspend fun reload()
}

class AppRouteListingUseCase @Inject constructor(
    private val repo: RoutesRepo,
) : RouteListingUseCase {

    override val flow: Flow<LoadableResult<RoutesListing>> = repo.flow

    override suspend fun loadNext() = repo.loadNext()

    override suspend fun reload() = repo.reload()
}

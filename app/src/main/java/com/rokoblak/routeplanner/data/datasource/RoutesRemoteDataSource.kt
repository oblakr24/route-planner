package com.rokoblak.routeplanner.data.datasource

import com.rokoblak.routeplanner.data.repo.RoutesModelMapper
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.RoutesPage
import com.rokoblak.routeplanner.data.service.NetworkMonitor
import com.rokoblak.routeplanner.data.service.api.RoutesApiService
import com.rokoblak.routeplanner.data.service.api.listRoutesWithExtraData
import kotlinx.coroutines.flow.first
import javax.inject.Inject


interface RoutesRemoteDataSource {
    suspend fun load(page: Int): CallResult<RoutesPage>

    companion object {
        const val PAGE_START = 1
    }
}

class AppRoutesRemoteDataSource @Inject constructor(
    private val api: RoutesApiService,
    private val networkMonitor: NetworkMonitor,
) : RoutesRemoteDataSource {

    override suspend fun load(page: Int): CallResult<RoutesPage> {
        if (!networkMonitor.connected.first()) {
            return CallResult.Error(LoadErrorType.NoNetwork)
        }

        // Real call here
//        return CallResult.wrappedSafeCall {
//            api.listRoutes()
//        }.map {
//            RoutesPage(
//                routes = RoutesModelMapper.mapRoutes(it),
//                page = page,
//                end = true,
//            )
//        }

        // Real call + extra data here
        return api.listRoutesWithExtraData(pageIdx = page).map {
            RoutesPage(
                routes = RoutesModelMapper.mapRoutes(it),
                page = page,
                end = if (it.continuePaginating) it.data.isEmpty() else true,
            )
        }
    }
}

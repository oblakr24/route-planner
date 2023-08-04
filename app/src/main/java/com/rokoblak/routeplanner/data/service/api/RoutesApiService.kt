package com.rokoblak.routeplanner.data.service.api

import com.rokoblak.routeplanner.data.datasource.RoutesRemoteDataSource
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.service.api.FakePagesStorage.extendWithFake
import com.rokoblak.routeplanner.data.service.api.model.RouteDetailsResponse
import com.rokoblak.routeplanner.data.service.api.model.RoutesResponse
import kotlinx.coroutines.delay
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface RoutesApiService {

    @GET("mobile/routes")
    suspend fun listRoutes(): Response<RoutesResponse>

    @GET("mobile/routes/{routeId}")
    suspend fun loadRouteDetails(
        @Path("routeId") routeId: String,
    ): Response<RouteDetailsResponse>
}

suspend fun RoutesApiService.listRoutesWithExtraData(pageIdx: Int): CallResult<RoutesResponse> {
    if (FakePagesStorage.USE_FAKE_DATA.not()) {
        return CallResult.wrappedSafeCall {
            listRoutes()
        }
    }

    return when (pageIdx) {
        RoutesRemoteDataSource.PAGE_START -> {
            CallResult.wrappedSafeCall {
                listRoutes()
            }.map { org ->
                extendWithFake(org, pageIdx)
            }
        }
        FakePagesStorage.PAGE_END -> {
            delay(1500)
            CallResult.Success(RoutesResponse(emptyList()))
        }
        else -> {
            delay(1500)
            val fakePage = FakePagesStorage.fakePage(pageIdx) ?: RoutesResponse(emptyList())
            CallResult.Success(fakePage)
        }
    }
}

/**
 * This is for duplication of data from the original API, such that we have:
 * 1) more data per page
 * 2) 5 fake pages' worth of data
 * The fake data is just duplicated real data with adjusted IDs and names.
 */
private object FakePagesStorage {

    const val USE_FAKE_DATA = true

    const val PAGE_END = 5

    private var orgResponse: RoutesResponse? = null

    fun fakePage(pageIdx: Int) = orgResponse?.let {
        RoutesResponse(fakeData(it.data, pageIdx)).apply {
            continuePaginating = true
        }
    }

    fun fakeData(org: List<RoutesResponse.Route>, pageIdx: Int): List<RoutesResponse.Route> = (0..10).flatMap { fakeIdx ->
        org.mapIndexed { idx, route ->
            route.copy(
                id = "Fake_${pageIdx}_${idx}_${route.id}_${fakeIdx}",
                name = route.name + " (fake copy $idx, page $pageIdx)"
            ).apply {
                orgId = route.id
            }
        }
    }

    fun extendWithFake(org: RoutesResponse, pageIdx: Int): RoutesResponse {
        this.orgResponse = org
        return RoutesResponse(
            data = org.data + fakeData(org.data, pageIdx)
        ).apply { continuePaginating = true }
    }

}
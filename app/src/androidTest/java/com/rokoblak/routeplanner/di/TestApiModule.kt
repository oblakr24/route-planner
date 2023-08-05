package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.service.api.GeoApifyService
import com.rokoblak.routeplanner.data.service.api.RoutesApiService
import com.rokoblak.routeplanner.data.service.api.model.RouteDetailsResponse
import com.rokoblak.routeplanner.data.service.api.model.RoutesResponse
import com.rokoblak.routeplanner.data.service.api.model.RoutingResponse
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
abstract class TestApiModule {

    @Singleton
    @Binds
    abstract fun bindRoutesApiService(impl: FakeRoutesApiService): RoutesApiService

    @Singleton
    @Binds
    abstract fun bindGeoApifyService(impl: FakeGeoApifyService): GeoApifyService
}

class FakeRoutesApiService @Inject constructor(
    private val mockJsonResponseHandler: MockJsonResponseHandler,
) : RoutesApiService {

    override suspend fun listRoutes(): Response<RoutesResponse> {
        return mockJsonResponseHandler.generateResponseFromJson(MockResponses.ROUTES)
    }

    override suspend fun loadRouteDetails(routeId: String): Response<RouteDetailsResponse> {
        return mockJsonResponseHandler.generateResponseFromJson(MockResponses.ROUTE_DETAILS)
    }
}

class FakeGeoApifyService @Inject constructor(
    private val mockJsonResponseHandler: MockJsonResponseHandler,
) : GeoApifyService {

    override suspend fun loadRouting(waypoints: String, apiKey: String): Response<RoutingResponse> {
        return mockJsonResponseHandler.generateResponseFromJson(MockResponses.ROUTING)
    }
}
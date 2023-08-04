package com.rokoblak.routeplanner.data.service.api

import com.rokoblak.routeplanner.data.service.api.model.RoutingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApifyService {

    @GET("routing?format=json&mode=bus&details=instruction_details,route_details,elevation")
    suspend fun loadRouting(
        @Query("waypoints") waypoints: String,
        @Query("apiKey") apiKey: String,
    ): Response<RoutingResponse>
}

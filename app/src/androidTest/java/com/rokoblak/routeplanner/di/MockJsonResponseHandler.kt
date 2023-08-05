package com.rokoblak.routeplanner.di

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject

class MockJsonResponseHandler @Inject constructor(
    val json: Json,
) {

    inline fun <reified T> generateResponseFromJson(filename: String): Response<T> {
        val content = readJsonFromAsset(filename)
        val data = json.decodeFromString<T>(content)
        return Response.success(data)
    }

    fun readJsonFromAsset(filename: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        val assetManager = context.assets
        val inputStream = assetManager.open(filename)
        return inputStream.bufferedReader().use { it.readText() }
    }
}

object MockResponses {

    const val ROUTES = "routes_response.json"
    const val ROUTE_DETAILS = "route_details_response.json"
    const val ROUTING = "routing_response.json"

}
package com.rokoblak.routeplanner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJsonParser(): Json {
        return Json {
            isLenient = true // In case the response doesn't conform to the strict RFC-4627 standard
            ignoreUnknownKeys = true // Allows to specify minimal models, without everything API returns
            encodeDefaults = true // Default parameters are still encoded
            explicitNulls = false // Nulls are not encoded. Decode absent values into nulls if no default set.
        }
    }
}

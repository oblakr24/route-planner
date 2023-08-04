package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.service.api.GeoApifyService
import com.rokoblak.routeplanner.data.service.api.RoutesApiService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Reusable
    fun provideRoutesApiService(@Named(DINames.RETROFIT_DEFAULT) retrofit: Retrofit): RoutesApiService {
        return retrofit.create(RoutesApiService::class.java)
    }

    @Provides
    @Reusable
    fun provideGeoApifyService(@Named(DINames.RETROFIT_GEO_APIFY) retrofit: Retrofit): GeoApifyService {
        return retrofit.create(GeoApifyService::class.java)
    }
}

package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.datasource.AppRouteDetailsRemoteDataSource
import com.rokoblak.routeplanner.data.datasource.AppRouteRoutingDataSource
import com.rokoblak.routeplanner.data.datasource.AppRoutesRemoteDataSource
import com.rokoblak.routeplanner.data.datasource.RouteDetailsRemoteDataSource
import com.rokoblak.routeplanner.data.datasource.RouteRoutingDataSource
import com.rokoblak.routeplanner.data.datasource.RoutesRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun provideRemoteRoutesDataSource(impl: AppRoutesRemoteDataSource): RoutesRemoteDataSource

    @Binds
    abstract fun provideRemoteRouteDetailsDataSource(impl: AppRouteDetailsRemoteDataSource): RouteDetailsRemoteDataSource

    @Binds
    abstract fun provideRoutingDetailsDataSource(impl: AppRouteRoutingDataSource): RouteRoutingDataSource
}

package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.repo.AppRouteDetailsRepo
import com.rokoblak.routeplanner.data.repo.AppRoutesRepo
import com.rokoblak.routeplanner.data.repo.RouteDetailsRepo
import com.rokoblak.routeplanner.data.repo.RoutesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {

    @Binds
    abstract fun bindRouteDetailsRepo(impl: AppRouteDetailsRepo): RouteDetailsRepo

    @Binds
    abstract fun bindRoutesRepo(impl: AppRoutesRepo): RoutesRepo
}

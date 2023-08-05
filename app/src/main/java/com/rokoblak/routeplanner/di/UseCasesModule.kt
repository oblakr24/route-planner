package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.domain.usecases.AppDarkModeHandlingUseCase
import com.rokoblak.routeplanner.domain.usecases.AppRouteDetailsUseCase
import com.rokoblak.routeplanner.domain.usecases.AppRouteListingUseCase
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.routeplanner.domain.usecases.RouteDetailsUseCase
import com.rokoblak.routeplanner.domain.usecases.RouteListingUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun provideDarkModeHandlingUseCase(impl: AppDarkModeHandlingUseCase): DarkModeHandlingUseCase

    @Binds
    abstract fun provideReposListingUseCase(impl: AppRouteListingUseCase): RouteListingUseCase

    @Binds
    abstract fun provideRepoDetailsUseCase(impl: AppRouteDetailsUseCase): RouteDetailsUseCase
}

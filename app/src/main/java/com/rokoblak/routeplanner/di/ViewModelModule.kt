package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.ui.navigation.AppRouteNavigator
import com.rokoblak.routeplanner.ui.navigation.RouteNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    @ViewModelScoped
    fun providesRouteNavigator(): RouteNavigator = AppRouteNavigator()
}

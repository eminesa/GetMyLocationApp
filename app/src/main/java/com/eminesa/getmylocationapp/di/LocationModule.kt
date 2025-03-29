package com.eminesa.getmylocationapp.di

import com.eminesa.getmylocationapp.service.LocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
/*
@Module
@InstallIn(SingletonComponent::class)
object LocationServiceModule {

    @Provides
    @Singleton
    fun provideLocationService(): LocationService {
        return LocationService()
    }
} */
package com.eminesa.getmylocationapp.di

import com.google.android.gms.maps.GoogleMap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarkerManagerModule {

    @Provides
    @Singleton
    fun provideMarkerManager(): MarkerManager {
        return MarkerManager()
    }
}

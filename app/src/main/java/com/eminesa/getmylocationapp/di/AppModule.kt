package com.eminesa.getmylocationapp.di

import android.content.Context
import androidx.room.Room
import com.eminesa.getmylocationapp.model.AddressDao
import com.eminesa.getmylocationapp.repository.AddressRepository
import com.eminesa.getmylocationapp.common.AddressDatabase
import com.eminesa.getmylocationapp.common.MarkerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AddressDatabase {
        return Room.databaseBuilder(context, AddressDatabase::class.java, "address_database").build()
    }

    @Provides
    fun provideAddressDao(database: AddressDatabase): AddressDao {
        return database.addressDao()
    }

    @Provides
    fun provideRepository(addressDao: AddressDao): AddressRepository {
        return AddressRepository(addressDao)
    }

    @Provides
    @Singleton
    fun provideMarkerManager(): MarkerManager {
        return MarkerManager()
    }
}

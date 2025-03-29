package com.eminesa.getmylocationapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Query("SELECT * FROM addresses")
    fun getAllAddresses(): Flow<List<AddressEntity>> // StateFlow için Flow kullanıyoruz

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("DELETE FROM addresses")
    suspend fun deleteAllAddresses()
}
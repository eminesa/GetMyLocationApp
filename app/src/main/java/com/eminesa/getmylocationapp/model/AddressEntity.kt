package com.eminesa.getmylocationapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val address: String?
)

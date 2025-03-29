package com.eminesa.getmylocationapp.common

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eminesa.getmylocationapp.model.AddressDao
import com.eminesa.getmylocationapp.model.AddressEntity

@Database(entities = [AddressEntity::class], version = 1, exportSchema = false)
abstract class AddressDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: AddressDatabase? = null

        fun getDatabase(context: Context): AddressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AddressDatabase::class.java,
                    "address_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

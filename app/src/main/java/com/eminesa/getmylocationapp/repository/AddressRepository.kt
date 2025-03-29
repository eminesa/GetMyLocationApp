package com.eminesa.getmylocationapp.repository

import com.eminesa.getmylocationapp.model.AddressDao
import com.eminesa.getmylocationapp.model.AddressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddressRepository @Inject constructor(private val addressDao: AddressDao) {

    suspend fun getAddresses(): Flow<List<AddressEntity>> {
        return addressDao.getAllAddresses()
    }

    suspend fun addAddress(address: AddressEntity) {
        addressDao.insertAddress(address)
    }

    suspend fun deleteAddresses() {
        addressDao.deleteAllAddresses()
    }
}

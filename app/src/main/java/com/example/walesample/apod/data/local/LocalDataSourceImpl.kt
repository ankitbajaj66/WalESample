package com.example.walesample.apod.data.local

import com.example.walesample.apod.domain.APODData

class LocalDataSourceImpl private constructor(private val apodDao: APODDao) : LocalDataSource {
    override suspend fun getData() = apodDao.retrieveData()

    override suspend fun saveData(apodData: APODData) {
        apodDao.insertData(apodData)
    }

    companion object {
        @Volatile
        private var instance: LocalDataSource? = null

        fun getInstance(apodDao: APODDao) = instance ?: synchronized(this) {
            instance ?: LocalDataSourceImpl(apodDao).also { instance = it }
        }
    }
}
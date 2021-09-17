package com.example.walesample.apod.data.local

import com.example.walesample.apod.domain.APODData

interface LocalDataSource {
    suspend fun getData(): APODData?
    suspend fun saveData(apodData: APODData)
}
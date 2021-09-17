package com.example.walesample.apod.data.remote

import com.example.walesample.apod.data.ResponseWrapper
import com.example.walesample.apod.domain.APODData

interface RemoteDataSource {
    suspend fun getAPODData(): ResponseWrapper<APODData>
}
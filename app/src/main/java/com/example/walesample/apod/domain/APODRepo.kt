package com.example.walesample.apod.domain

import com.example.walesample.apod.data.ResponseWrapper

interface APODRepo {

    suspend fun getAPODData(): ResponseWrapper<APODData>
}
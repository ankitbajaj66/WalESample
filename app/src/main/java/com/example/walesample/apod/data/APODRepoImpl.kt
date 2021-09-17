package com.example.walesample.apod.data

import com.example.walesample.apod.data.local.LocalDataSource
import com.example.walesample.apod.data.remote.RemoteDataSource
import com.example.walesample.apod.domain.APODData
import com.example.walesample.apod.domain.APODRepo
import java.lang.Exception
import java.util.*

class APODRepoImpl private constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : APODRepo {
    override suspend fun getAPODData(): ResponseWrapper<APODData> {
        var data: APODData? = localDataSource.getData()

        return if (data == null) {
            getNetworkResponse()
        } else {
            if (checkForDataChange(data.date)) {
                getNetworkResponse(true)
            } else {
                getDbResponse(data)
            }
        }
    }

    private suspend fun getNetworkResponse(isDateChanged: Boolean = false): ResponseWrapper<APODData> {
        var data: APODData?
        var remoteResponse: ResponseWrapper<APODData> = remoteDataSource.getAPODData()

        when (remoteResponse) {

            is ResponseWrapper.Success -> {
                data = remoteResponse.data

                // Save current date in object
                data.date = System.currentTimeMillis()
            }
            else -> {
                remoteResponse =
                    getDbResponse(localDataSource.getData()?.apply { oldDate = isDateChanged })
            }
        }
        return remoteResponse
    }

    private fun getDbResponse(data: APODData?): ResponseWrapper<APODData> {
        return if (data != null) ResponseWrapper.Success(data)
        else ResponseWrapper.Error(Exception())
    }

    private fun checkForDataChange(storedTme: Long): Boolean {
        val currentDateCalender = Calendar.getInstance()

        return Calendar.getInstance().run {
            timeInMillis = storedTme
            get(Calendar.YEAR) < currentDateCalender.get(Calendar.YEAR)
                    && get(Calendar.MONTH) < currentDateCalender.get(Calendar.MONTH)
                    && get(Calendar.DATE) < currentDateCalender.get(Calendar.DATE)
        }
    }

    companion object {
        @Volatile
        private var instance: APODRepo? = null

        fun getInstance(
            localDataSource: LocalDataSource,
            remoteDataSource: RemoteDataSource
        ) = instance ?: synchronized(this) {
            instance ?: APODRepoImpl(localDataSource, remoteDataSource).also { instance = it }
        }
    }
}
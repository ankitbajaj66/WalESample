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
        val data: APODData? = localDataSource.getData()

        return if (data == null) {
            // If data is not present in local DB then make service call
            getNetworkResponse()
        } else {
            // If data is present in local DB but date changed then make service call
            if (checkForDataChange(data.date)) {
                getNetworkResponse(true)
            } else {
                // If data is present in local DB and same date then use local DB
                getDbResponse(data)
            }
        }
    }

    /**
     * This Method is making network call to get the data and save in local DB
     * isDateChanged - to capture date changed
     */
    private suspend fun getNetworkResponse(isDateChanged: Boolean = false): ResponseWrapper<APODData> {
        val data: APODData?
        var remoteResponse: ResponseWrapper<APODData> = remoteDataSource.getAPODData()

        when (remoteResponse) {

            is ResponseWrapper.Success -> {
                data = remoteResponse.data

                // Save current date in object
                data.date = System.currentTimeMillis()

                localDataSource.saveData(data)
            }
            else -> {
                remoteResponse =
                    getDbResponse(localDataSource.getData()?.apply { oldDate = isDateChanged })
            }
        }
        return remoteResponse
    }

    /**
     * This Method is used to get the data from local DB
     */
    private fun getDbResponse(data: APODData?): ResponseWrapper<APODData> {
        return if (data != null) ResponseWrapper.Success(data)
        else ResponseWrapper.Error(Exception())
    }

    /**
     * This Method is used to check date changed
     */
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
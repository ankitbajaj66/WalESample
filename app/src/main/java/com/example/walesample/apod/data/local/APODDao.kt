package com.example.walesample.apod.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.walesample.apod.domain.APODData

@Dao
interface APODDao {

    @Query("select * from APODData")
    suspend fun retrieveData(): APODData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(apodData: APODData)

}
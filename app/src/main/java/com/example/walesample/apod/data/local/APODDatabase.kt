package com.example.walesample.apod.data.local

import android.app.Activity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.walesample.apod.domain.APODData
import java.lang.ref.WeakReference

private const val DB_NAME = "movies_db"

@Database(entities = [APODData::class], version = 1)
abstract class APODDatabase : RoomDatabase() {
    abstract fun apodDao(): APODDao

    companion object {
        @Volatile
        private var instance: RoomDatabase? = null

        fun getInstance(context: WeakReference<Activity>) =
            context.get()?.let {
                instance ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(it, APODDatabase::class.java, DB_NAME).build()
                        .also {
                            instance = it
                        }
                }
            }

    }
}
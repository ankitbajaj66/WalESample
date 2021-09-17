package com.example.walesample.apod.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
class APODData(
    var title: String,
    var description: String,
    var bitmapPath: String,
    var mediaType: String,
    var date: Long = -1,
    var oldDate: Boolean = false
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long = 1

    companion object {
        // Get the required field from Josn
        fun fromJson(jsonInput: String) = JSONObject(jsonInput).run {
            APODData(
                title = optString("title"),
                description = optString("explanation"),
                bitmapPath = optString("url"),
                mediaType = optString("media_type")
            )
        }
    }
}

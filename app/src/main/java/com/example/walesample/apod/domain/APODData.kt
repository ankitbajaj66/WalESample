package com.example.walesample.apod.domain

import org.json.JSONObject

class APODData(
    var title: String,
    var description: String,
    var bitmapPath: String,
    var date: Long = -1,
    var oldDate: Boolean = false
) {
    companion object {
        // Get the required field from Josn
        fun fromJson(jsonInput: String) = JSONObject(jsonInput).run {
            APODData(optString("title"), optString("explanation"), optString("url"))
        }
    }
}

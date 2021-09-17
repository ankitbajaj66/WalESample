package com.example.walesample.apod.data.remote

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.walesample.apod.data.ResponseWrapper
import com.example.walesample.apod.domain.APODData
import com.example.walesample.apod.util.AppConstants
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

private const val API_URL =
    "https://api.nasa.gov/planetary/apod?api_key=GEr6WC7IMh8oJlhfk7HuFqLxmFrjFMLgXz6DNglp"
private const val TYPE_IMAGE = "image"


class RemoteDataSourceImpl private constructor(private val context: WeakReference<Activity>) :
    RemoteDataSource {

    /**
     * This Method is used to get the apod data from server
     */
    override suspend fun getAPODData(): ResponseWrapper<APODData> {
        val url = URL(API_URL)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val bufferReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            var lines = bufferReader.readLine()
            val output = StringBuilder()

            while (lines != null) {
                output.append(lines)
                lines = bufferReader.readLine()
            }

            return ResponseWrapper.Success(APODData.fromJson(output.toString()).apply {
                if (TYPE_IMAGE == mediaType) {
                    bitmapPath = downloadImageAndReturnPath(bitmapPath)
                } else {
                    bitmapPath = ""

                }
            })
        } catch (e: Exception) {
            return ResponseWrapper.Error(e)
        }

    }

    /**
     * This Method is used to download image and return local path
     * urlPath - remote url of image
     */
    private fun downloadImageAndReturnPath(urlPath: String): String {
        return downloadImage(urlPath)?.let {
            saveToInternalStorage(it)
        } ?: ""
    }

    /**
     * This Method is used to download the bitmap from server and return it
     * urlPath - image server path
     */
    private fun downloadImage(urlPath: String): Bitmap? {
        val bitmap: Bitmap
        val url = URL(urlPath)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val bufferInputStream =
                BufferedInputStream(urlConnection.inputStream)
            bitmap = BitmapFactory.decodeStream(bufferInputStream)
            return bitmap
        } catch (e: Exception) {

        }

        return null
    }

    /**
     * This Method is used save the bitmap in internal storage and return local path
     * bitmap - downloaded bitmap
     */
    private fun saveToInternalStorage(bitmap: Bitmap): String? {
        val cw = context.get()

        val directory = cw?.getDir("imageDir", Context.MODE_PRIVATE)
        val myPath = File(directory, AppConstants.IMG_NAME)
        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(myPath)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            return null
        }
        return directory?.absolutePath
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(context: WeakReference<Activity>) = instance ?: synchronized(this) {
            instance ?: RemoteDataSourceImpl(context).also { instance = it }
        }
    }
}
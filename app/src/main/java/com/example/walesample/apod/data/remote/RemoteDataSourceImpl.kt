package com.example.walesample.apod.data.remote

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.walesample.apod.data.ResponseWrapper
import com.example.walesample.apod.domain.APODData
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

private const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=GEr6WC7IMh8oJlhfk7HuFqLxmFrjFMLgXz6DNglp"

class RemoteDataSourceImpl private constructor(private val context: WeakReference<Activity>) :
    RemoteDataSource {

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

            return ResponseWrapper.Success(APODData.fromJson(output.toString()).run {
                bitmapPath = downloadImageAndReturnPath(bitmapPath)
                this
            })
        } catch (e: Exception) {
            return ResponseWrapper.Error(e)
        } finally {
            urlConnection.disconnect()
        }

    }

    private fun downloadImageAndReturnPath(urlPath: String): String {
        return downloadImage(urlPath)?.let {
            saveToInternalStorage(it)
        } ?: ""
    }

    private fun downloadImage(urlPath: String): Bitmap? {
        var bitmap: Bitmap
        val url = URL(urlPath)
        var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val bufferInputStream =
                BufferedInputStream(urlConnection.inputStream)
            bitmap = BitmapFactory.decodeStream(bufferInputStream)
            return bitmap
        } catch (e: Exception) {

        } finally {
            urlConnection.disconnect()
        }

        return null
    }


    private fun saveToInternalStorage(bitmap: Bitmap): String? {
        val cw = context.get()

        val directory = cw?.getDir("imageDir", Context.MODE_PRIVATE)

        val myPath = File(directory, "apod_img.jpg")

        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(myPath)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
package com.example.walesample

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.walesample.apod.data.APODRepoImpl
import com.example.walesample.apod.data.local.APODDatabase
import com.example.walesample.apod.data.local.LocalDataSourceImpl
import com.example.walesample.apod.data.remote.RemoteDataSourceImpl
import com.example.walesample.apod.domain.APODData
import com.example.walesample.apod.presentation.APODViewModel
import com.example.walesample.apod.presentation.ErrorState
import com.example.walesample.apod.presentation.ProgressState
import com.example.walesample.apod.presentation.SuccessState
import kotlinx.android.synthetic.main.activity_apod.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.ref.WeakReference

class APODActivity : AppCompatActivity() {

    private val apodViewModel: APODViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return APODViewModel(
                    APODRepoImpl.getInstance(
                        LocalDataSourceImpl.getInstance((APODDatabase.getInstance(WeakReference(this@APODActivity)) as APODDatabase).apodDao()),
                        RemoteDataSourceImpl.getInstance(
                            WeakReference(this@APODActivity)
                        )
                    )
                ) as T
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apod)
        attachObserver()
    }

    private fun attachObserver() {
        apodViewModel.apod.observe(this, {
            when (it) {
                is ProgressState -> {
                    showProgressBar(true)
                }
                is SuccessState -> {
                    showProgressBar(false)
                    updateUI(it.data)
                }

                is ErrorState -> {
                    showProgressBar(false)
                }
            }
        })
    }

    private fun updateUI(apodData: APODData) {
        apodData.run {
            txt_title.text = title
            txt_desc.text = description
            loadImage(bitmapPath)
            if (oldDate) {
                notifyUserAboutOldData()
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show)
            prg_bar.visibility = View.VISIBLE
        else
            prg_bar.visibility = View.GONE
    }

    private fun loadImage(bitmapPath: String) {
        try {
            val file = File(bitmapPath, "apod_img.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(file))
            img_view.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun notifyUserAboutOldData() {
        Toast.makeText(this, "", Toast.LENGTH_LONG).show()
    }
}
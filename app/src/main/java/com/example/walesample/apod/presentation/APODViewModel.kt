package com.example.walesample.apod.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walesample.apod.data.ResponseWrapper
import com.example.walesample.apod.domain.APODRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class APODViewModel(private val apodRepo: APODRepo) : ViewModel() {
    private val _apod: MutableLiveData<UIViewState> = MutableLiveData()
    val apod: LiveData<UIViewState> = _apod

    init {
        Log.e("APODViewModel", "Ankit init called")
        getData()
    }

    private fun getData() {
        _apod.value = ProgressState

        viewModelScope.launch(Dispatchers.IO) {
            var result = apodRepo.getAPODData()

            _apod.postValue(
                when (result) {
                    is ResponseWrapper.Success -> SuccessState(result.data)
                    is ResponseWrapper.Error -> ErrorState(result.exception.toString())
                }
            )
        }
    }
}
package com.example.walesample.apod.presentation

import com.example.walesample.apod.domain.APODData

sealed class UIViewState

data class SuccessState(var data: APODData) : UIViewState()
data class ErrorState(var error: String) : UIViewState()
object ProgressState : UIViewState()
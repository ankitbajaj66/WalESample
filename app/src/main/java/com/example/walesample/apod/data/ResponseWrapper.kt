package com.example.walesample.apod.data

import java.lang.Exception

sealed class ResponseWrapper<out R> {
    data class Success<out T>(val data: T) : ResponseWrapper<T>()
    data class Error(val exception: Exception) : ResponseWrapper<Nothing>()
}
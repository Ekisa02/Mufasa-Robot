package com.joseph.mufasarobot.common


sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

fun <T> NetworkResult<T>.toResource(): Resource<T> = when (this) {
    is NetworkResult.Success -> Resource.Success(data)
    is NetworkResult.Error -> Resource.Error(message ?: exception.message ?: "Unknown error")
    is NetworkResult.Loading -> Resource.Loading
}
package com.example.acronymsmeaning.utils

sealed class RequestState {
    data class LOADING(val loading: Boolean = true) : RequestState()
    class SUCCESS<T>(val definitions: T, val isLoading: Boolean = false) : RequestState()
    class ERROR(val exception: Throwable, val isLoading: Boolean = false) : RequestState()
}

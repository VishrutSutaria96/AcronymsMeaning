package com.example.acronymsmeaning.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acronymsmeaning.data.AcronymRepository
import com.example.acronymsmeaning.data.AcronymRepositoryImpl
import com.example.acronymsmeaning.utils.RequestState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragmentViewModel(
    private val acronymRepository: AcronymRepository = AcronymRepositoryImpl(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _definition: MutableLiveData<RequestState> = MutableLiveData()
    val acronymDefinition: LiveData<RequestState> get() = _definition

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun handleSearch(acronym: String) {
        viewModelScope.launch(ioDispatcher) {
            acronymRepository.getAcronymDefinition(acronym).collect {
                when (it) {
                    is RequestState.LOADING -> {
                        _definition.postValue(it)
                        _isLoading.postValue(it.loading)
                    }
                    is RequestState.SUCCESS<*> -> {
                        _isLoading.postValue(it.isLoading)
                        _definition.postValue(it)
                    }
                    is RequestState.ERROR -> {
                        _definition.postValue(it)
                        _isLoading.postValue(it.isLoading)
                    }
                }
            }
        }
    }
}
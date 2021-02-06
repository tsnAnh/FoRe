package dev.tsnanh.fore.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel : ViewModel() {
    private val _onError = MutableStateFlow<Throwable?>(null)
    val onError: StateFlow<Throwable?> get() = _onError

    fun onError(t: Throwable) {
        _onError.value = t
    }
}
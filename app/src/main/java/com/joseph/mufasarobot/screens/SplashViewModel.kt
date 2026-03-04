package com.joseph.mufasarobot.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    data class SplashState(
        val isLoading: Boolean = true
    )

    sealed class SplashEvent {
        data object NavigateToLogin : SplashEvent()
        data object NavigateToDashboard : SplashEvent()

    }

    fun handleEvent(event: SplashEvent) {
        // Handle events if needed
    }

}
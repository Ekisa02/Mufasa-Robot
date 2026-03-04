package com.joseph.mufasarobot.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseph.mufasarobot.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    data class LoginState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null,
        val connection: MT5Connection? = null
    )

    sealed class LoginEvent {
        data class Connect(val login: String, val server: String, val password: String) : LoginEvent()
        data object NavigateToDashboard : LoginEvent()
        data object ResetMessages : LoginEvent()
    }

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Connect -> connect(event.login, event.server, event.password)
            is LoginEvent.NavigateToDashboard -> {
                // This will be handled by Navigation
            }
            is LoginEvent.ResetMessages -> {
                _state.value = _state.value.copy(errorMessage = null, successMessage = null)
            }
        }
    }

    private fun connect(login: String, server: String, password: String) {
        viewModelScope.launch {
            // Show loading
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            // Simulate network delay
            delay(2000)

            // Hardcoded credentials for demo
            val validLogin = "514787569"
            val validPassword = "@Ekisa02"

            if (login == validLogin && password == validPassword) {
                // Success
                val connection = MT5Connection(
                    isConnected = true,
                    serverName = server.ifEmpty { "FxPro-MTS" },
                    accountNumber = login,
                    connectionStatus = ConnectionStatus.CONNECTED
                )
                _state.value = _state.value.copy(
                    isLoading = false,
                    connection = connection,
                    successMessage = "Login successful! Redirecting..."
                )

                // Show success message briefly then navigate
                delay(1000)
                handleEvent(LoginEvent.NavigateToDashboard) // This triggers navigation

            } else {
                // Failure
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Invalid login credentials. Use 514787569 / @Ekisa02",
                    successMessage = null
                )
            }
        }
    }
}
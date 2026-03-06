package com.joseph.mufasarobot.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseph.mufasarobot.data.local.TokenManager
import com.joseph.mufasarobot.data.remote.ApiService
import com.joseph.mufasarobot.data.remote.ConnectRequest
import com.joseph.mufasarobot.data.remote.RetrofitClient
import com.joseph.mufasarobot.models.ConnectionStatus
import com.joseph.mufasarobot.models.MT5Connection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "MT5LoginViewModel"

class MT5LoginViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(MT5LoginState())
    val state: StateFlow<MT5LoginState> = _state.asStateFlow()

    private val apiService = RetrofitClient.apiService

    data class MT5LoginState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null,
        val isConnected: Boolean = false,
        val connection: MT5Connection? = null
    )

    sealed class MT5LoginEvent {
        data class Connect(val login: String, val server: String, val password: String) : MT5LoginEvent()
        data object NavigateToDashboard : MT5LoginEvent()
        data object ResetMessages : MT5LoginEvent()
    }

    fun handleEvent(event: MT5LoginEvent) {
        when (event) {
            is MT5LoginEvent.Connect -> connect(event.login, event.server, event.password)
            is MT5LoginEvent.NavigateToDashboard -> {
                _state.value = _state.value.copy(isConnected = true)
            }
            is MT5LoginEvent.ResetMessages -> {
                _state.value = _state.value.copy(errorMessage = null, successMessage = null)
            }
        }
    }

    private fun connect(login: String, server: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                // Get JWT token
                val token = tokenManager.getToken()
                if (token == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Not authenticated. Please login first."
                    )
                    return@launch
                }

                // Call backend to connect MT5
                val response = apiService.connectMT5(
                    token = "Bearer $token",
                    request = ConnectRequest(login, password, server)
                )

                if (response.success) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "MT5 Connected!",
                        connection = response.connection
                    )

                    // Navigate to dashboard
                    delay(1000)
                    handleEvent(MT5LoginEvent.NavigateToDashboard)

                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = response.message
                    )
                }

            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = when (e.code()) {
                        401 -> "Session expired. Please login again."
                        403 -> "Not authorized"
                        404 -> "MT5 connection failed"
                        else -> "Server error: ${e.code()}"
                    }
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Network error. Cannot reach server."
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }
}
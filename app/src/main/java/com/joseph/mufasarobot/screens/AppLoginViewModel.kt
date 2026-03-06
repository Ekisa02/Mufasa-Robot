package com.joseph.mufasarobot.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseph.mufasarobot.data.local.TokenManager
import com.joseph.mufasarobot.data.remote.ApiService
import com.joseph.mufasarobot.data.remote.LoginRequest
import com.joseph.mufasarobot.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "AppLoginViewModel"

class AppLoginViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(AppLoginState())
    val state: StateFlow<AppLoginState> = _state.asStateFlow()

    private val apiService = RetrofitClient.apiService

    data class AppLoginState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null,
        val isAuthenticated: Boolean = false,
        val token: String? = null
    )

    sealed class AppLoginEvent {
        data class SignIn(val email: String, val password: String) : AppLoginEvent()
        data object NavigateToMt5Connection : AppLoginEvent()
        data object ResetMessages : AppLoginEvent()
    }

    fun handleEvent(event: AppLoginEvent) {
        Log.d(TAG, "✅ Event received: $event")
        when (event) {
            is AppLoginEvent.SignIn -> {
                Log.d(TAG, "✅ SignIn with email: ${event.email}")
                signIn(event.email, event.password)
            }
            is AppLoginEvent.NavigateToMt5Connection -> {
                Log.d(TAG, "✅ NavigateToMt5Connection")
                _state.value = _state.value.copy(isAuthenticated = true)
            }
            is AppLoginEvent.ResetMessages -> {
                Log.d(TAG, "✅ ResetMessages")
                _state.value = _state.value.copy(errorMessage = null, successMessage = null)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "🔵 signIn() called with email: $email")

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            Log.d(TAG, "🔵 State: isLoading=true")

            try {
                Log.d(TAG, "🔵 Making API call to: ${RetrofitClient.BASE_URL}api/auth/login")
                Log.d(TAG, "🔵 Request body: {email: $email, password: [hidden]}")

                // Call backend API
                val response = apiService.login(LoginRequest(email, password))

                Log.d(TAG, "🔵 API Response received")
                Log.d(TAG, "🔵 Response success: ${response.success}")
                Log.d(TAG, "🔵 Response token: ${response.token?.substring(0, Math.min(20, response.token.length))}...")

                if (response.success) {
                    // Save JWT token
                    tokenManager.saveToken(response.token)
                    Log.d(TAG, "✅ Token saved successfully")

                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Login successful!",
                        token = response.token
                    )

                    // Navigate to MT5 connection after success
                    delay(1000)
                    handleEvent(AppLoginEvent.NavigateToMt5Connection)

                } else {
                    Log.e(TAG, "❌ Login failed - server returned success=false")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Login failed"
                    )
                }

            } catch (e: HttpException) {
                Log.e(TAG, "❌ HTTP Error: ${e.code()} - ${e.message()}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = when (e.code()) {
                        401 -> "Invalid email or password"
                        404 -> "User not found"
                        else -> "Server error: ${e.code()}"
                    }
                )
            } catch (e: IOException) {
                Log.e(TAG, "❌ Network Error: ${e.message}")
                Log.e(TAG, "❌ Make sure backend is running at 10.251.2.182:3000")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Network error. Cannot reach server at 10.251.2.182:3000"
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Unknown Error: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }
}
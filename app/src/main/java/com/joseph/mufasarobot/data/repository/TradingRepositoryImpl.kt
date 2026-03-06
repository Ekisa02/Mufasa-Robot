package com.joseph.mufasarobot.data.repository

import com.joseph.mufasarobot.common.Resource
import com.joseph.mufasarobot.data.local.TokenManager
import com.joseph.mufasarobot.data.remote.ApiService
import com.joseph.mufasarobot.data.remote.AutoTradingRequest
import com.joseph.mufasarobot.data.remote.ConnectRequest
import com.joseph.mufasarobot.data.remote.RetrofitClient
import com.joseph.mufasarobot.interfaces.TradingRepository
import com.joseph.mufasarobot.models.AutomationSettings
import com.joseph.mufasarobot.models.BotState
import com.joseph.mufasarobot.models.MT5Connection
import com.joseph.mufasarobot.models.TradeActivity
import com.joseph.mufasarobot.models.UserFeedback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

class TradingRepositoryImpl(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : TradingRepository {

    override suspend fun connectMT5(
        login: String,
        password: String,
        server: String
    ): Resource<MT5Connection> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.connectMT5(
                token = "Bearer $token",
                request = ConnectRequest(login, password, server)
            )

            if (response.success) {
                Resource.Success(response.connection)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun disconnectMT5(): Resource<Unit> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.disconnectMT5("Bearer $token")

            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun getConnectionStatus(): Flow<Resource<MT5Connection>> = flow {
        try {
            val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

            emit(Resource.Loading)

            val response = apiService.getConnectionStatus("Bearer $token")

            if (response.success) {
                emit(Resource.Success(response.connection))
            } else {
                emit(Resource.Error("Failed to get connection status"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Server error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: Cannot reach server"))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    override suspend fun getBotState(): Flow<Resource<BotState>> = flow {
        try {
            val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

            emit(Resource.Loading)

            val response = apiService.getBotStatus("Bearer $token")

            if (response.success) {
                emit(Resource.Success(response.bot))
            } else {
                emit(Resource.Error("Failed to get bot state"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Server error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: Cannot reach server"))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    override suspend fun startBot(): Resource<BotState> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.startBot("Bearer $token")

            if (response.success) {
                Resource.Success(response.bot)
            } else {
                Resource.Error("Failed to start bot")
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun stopBot(): Resource<BotState> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.stopBot("Bearer $token")

            if (response.success) {
                Resource.Success(response.bot)
            } else {
                Resource.Error("Failed to stop bot")
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun toggleAutoTrading(enabled: Boolean): Resource<BotState> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.toggleAutoTrading(
                token = "Bearer $token",
                request = AutoTradingRequest(enabled)
            )

            if (response.success) {
                Resource.Success(response.bot)
            } else {
                Resource.Error("Failed to toggle auto trading")
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun getTradeActivity(): Flow<Resource<TradeActivity>> = flow {
        try {
            val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

            emit(Resource.Loading)

            val response = apiService.getTradeActivity("Bearer $token")

            if (response.success) {
                val tradeActivity = TradeActivity(
                    autoOpenEnabled = response.summary.totalTrades > 0,
                    autoCloseEnabled = response.summary.totalTrades > 0,
                    lastTradeTimestamp = if (response.trades.isNotEmpty()) Date() else null,
                    profitLoss = response.summary.netProfit,
                    openTradesCount = response.trades.count { it.status == "OPEN" },
                    closedTradesCount = response.trades.count { it.status == "CLOSED" }
                )
                emit(Resource.Success(tradeActivity))
            } else {
                emit(Resource.Error("Failed to get trade activity"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Server error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: Cannot reach server"))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    override suspend fun setAutoOpenEnabled(enabled: Boolean): Resource<TradeActivity> {
        return Resource.Success(TradeActivity(autoOpenEnabled = enabled))
    }

    override suspend fun setAutoCloseEnabled(enabled: Boolean): Resource<TradeActivity> {
        return Resource.Success(TradeActivity(autoCloseEnabled = enabled))
    }

    override suspend fun getAutomationSettings(): Flow<Resource<AutomationSettings>> = flow {
        try {
            val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

            emit(Resource.Loading)

            val response = apiService.getAutomationSettings("Bearer $token")

            if (response.success) {
                emit(Resource.Success(response.settings))
            } else {
                emit(Resource.Error("Failed to get settings"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Server error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: Cannot reach server"))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    override suspend fun saveAutomationSettings(settings: AutomationSettings): Resource<AutomationSettings> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("Not authenticated")

            val response = apiService.saveAutomationSettings(
                token = "Bearer $token",
                settings = settings
            )

            if (response.success) {
                Resource.Success(response.settings)
            } else {
                Resource.Error("Failed to save settings")
            }
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: Cannot reach server")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun refreshAll(): Resource<Unit> {
        return try {
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Refresh failed: ${e.message}")
        }
    }

    override suspend fun connectWebSocket() {
        // WebSocket implementation would go here
    }

    override suspend fun disconnectWebSocket() {
        // WebSocket disconnection
    }

    override fun getWebSocketEvents(): Flow<Resource<UserFeedback>> = flow {
        // This would emit real-time events from WebSocket
    }
}
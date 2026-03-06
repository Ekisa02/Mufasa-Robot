package com.joseph.mufasarobot.data.remote

import com.joseph.mufasarobot.models.*
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): UserResponse

    @GET("api/connection/status")
    suspend fun getConnectionStatus(@Header("Authorization") token: String): ConnectionStatusResponse

    @POST("api/connection/connect")
    suspend fun connectMT5(
        @Header("Authorization") token: String,
        @Body request: ConnectRequest
    ): ConnectionResponse

    @POST("api/connection/disconnect")
    suspend fun disconnectMT5(@Header("Authorization") token: String): BaseResponse

    @GET("api/bot/status")
    suspend fun getBotStatus(@Header("Authorization") token: String): BotStatusResponse

    @POST("api/bot/start")
    suspend fun startBot(@Header("Authorization") token: String): BotStatusResponse

    @POST("api/bot/stop")
    suspend fun stopBot(@Header("Authorization") token: String): BotStatusResponse

    @POST("api/bot/auto-trading")
    suspend fun toggleAutoTrading(
        @Header("Authorization") token: String,
        @Body request: AutoTradingRequest
    ): BotStatusResponse

    @GET("api/trade/activity")
    suspend fun getTradeActivity(@Header("Authorization") token: String): TradeActivityResponse

    @GET("api/settings/automation")
    suspend fun getAutomationSettings(@Header("Authorization") token: String): SettingsResponse

    @POST("api/settings/automation")
    suspend fun saveAutomationSettings(
        @Header("Authorization") token: String,
        @Body settings: AutomationSettings
    ): SettingsResponse
}

// Request/Response Models
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val mt5Login: String,
    val mt5Password: String,
    val server: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val email: String,
    val mt5Login: String,
    val server: String,
    val isConnected: Boolean
)

data class UserResponse(val success: Boolean, val user: UserData)

data class ConnectionStatusResponse(
    val success: Boolean,
    val connection: MT5Connection
)

data class ConnectRequest(val login: String, val password: String, val server: String)

data class ConnectionResponse(
    val success: Boolean,
    val message: String,
    val connection: MT5Connection
)

data class BotStatusResponse(
    val success: Boolean,
    val bot: BotState
)

data class AutoTradingRequest(val enabled: Boolean)

data class TradeData(
    val tradeId: String,
    val symbol: String,
    val type: String,
    val volume: Double,
    val openPrice: Double,
    val closePrice: Double?,
    val profit: Double,
    val status: String,
    val openTime: java.util.Date?,
    val closeTime: java.util.Date?
)

data class TradeSummary(
    val totalTrades: Int,
    val totalProfit: Double,
    val totalLoss: Double,
    val winRate: Double,
    val netProfit: Double
)

data class TradeActivityResponse(
    val success: Boolean,
    val trades: List<TradeData>,
    val summary: TradeSummary
)

data class SettingsResponse(
    val success: Boolean,
    val settings: AutomationSettings
)

data class BaseResponse(
    val success: Boolean,
    val message: String
)



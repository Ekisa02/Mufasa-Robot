package com.joseph.mufasarobot.interfaces


import com.joseph.mufasarobot.common.Resource
import com.joseph.mufasarobot.models.AutomationSettings
import com.joseph.mufasarobot.models.BotState
import com.joseph.mufasarobot.models.MT5Connection
import com.joseph.mufasarobot.models.TradeActivity
import com.joseph.mufasarobot.models.UserFeedback
import kotlinx.coroutines.flow.Flow

interface TradingRepository {
    suspend fun connectMT5(login: String, password: String, server: String): Resource<MT5Connection>
    suspend fun disconnectMT5(): Resource<Unit>
    suspend fun getConnectionStatus(): Flow<Resource<MT5Connection>>

    suspend fun getBotState(): Flow<Resource<BotState>>
    suspend fun startBot(): Resource<BotState>
    suspend fun stopBot(): Resource<BotState>
    suspend fun toggleAutoTrading(enabled: Boolean): Resource<BotState>

    suspend fun getTradeActivity(): Flow<Resource<TradeActivity>>
    suspend fun setAutoOpenEnabled(enabled: Boolean): Resource<TradeActivity>
    suspend fun setAutoCloseEnabled(enabled: Boolean): Resource<TradeActivity>

    suspend fun getAutomationSettings(): Flow<Resource<AutomationSettings>>
    suspend fun saveAutomationSettings(settings: AutomationSettings): Resource<AutomationSettings>

    suspend fun refreshAll(): Resource<Unit>

    // WebSocket connection for real-time updates
    suspend fun connectWebSocket()
    suspend fun disconnectWebSocket()
    fun getWebSocketEvents(): Flow<Resource<UserFeedback>>
}
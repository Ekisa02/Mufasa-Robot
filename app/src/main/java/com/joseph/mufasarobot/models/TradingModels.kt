package com.joseph.mufasarobot.models

import java.util.Date

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED, CONNECTING, ERROR
}

enum class BotStatus {
    ACTIVE, INACTIVE, ERROR
}

enum class TradeStatus {
    OPEN, CLOSED, PENDING, ERROR
}

enum class RiskLevel {
    LOW, MEDIUM, HIGH
}

enum class FeedbackType {
    SUCCESS, ERROR, WARNING, INFO
}

data class MT5Connection(
    val isConnected: Boolean = false,
    val serverName: String? = null,
    val accountNumber: String? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val lastHeartbeat: Date? = null
)

data class BotState(
    val status: BotStatus = BotStatus.INACTIVE,
    val strategyName: String? = null,
    val isAutoTradingEnabled: Boolean = false,
    val isRunning: Boolean = false
)

data class TradeActivity(
    val autoOpenEnabled: Boolean = false,
    val autoCloseEnabled: Boolean = false,
    val lastTradeTimestamp: Date? = null,
    val profitLoss: Double? = null,
    val openTradesCount: Int = 0,
    val closedTradesCount: Int = 0
)

data class AutomationSettings(
    val enableAutoOpen: Boolean = false,
    val enableAutoClose: Boolean = false,
    val riskLevel: RiskLevel = RiskLevel.MEDIUM
)

data class UserFeedback(
    val message: String,
    val type: FeedbackType,
    val timestamp: Date = Date()
)
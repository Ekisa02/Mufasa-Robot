package com.joseph.mufasarobot.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.joseph.mufasarobot.models.AutomationSettings
import com.joseph.mufasarobot.models.BotState
import com.joseph.mufasarobot.models.BotStatus
import com.joseph.mufasarobot.models.ConnectionStatus
import com.joseph.mufasarobot.models.FeedbackType
import com.joseph.mufasarobot.models.MT5Connection
import com.joseph.mufasarobot.models.RiskLevel
import com.joseph.mufasarobot.models.TradeActivity
import com.joseph.mufasarobot.models.UserFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    data class DashboardState(
        val connection: MT5Connection = MT5Connection(
            isConnected = true,
            serverName = "ICMarkets-Demo",
            accountNumber = "12345678",
            connectionStatus = ConnectionStatus.CONNECTED
        ),
        val botState: BotState = BotState(
            status = BotStatus.ACTIVE,
            strategyName = "Scalper Pro",
            isAutoTradingEnabled = true,
            isRunning = true
        ),
        val tradeActivity: TradeActivity = TradeActivity(
            autoOpenEnabled = true,
            autoCloseEnabled = true,
            lastTradeTimestamp = Date(),
            profitLoss = 1250.50,
            openTradesCount = 3,
            closedTradesCount = 47
        ),
        val automationSettings: AutomationSettings = AutomationSettings(
            enableAutoOpen = true,
            enableAutoClose = true,
            riskLevel = RiskLevel.MEDIUM
        ),
        val feedbacks: List<UserFeedback> = listOf(
            UserFeedback("Bot started successfully", FeedbackType.SUCCESS, Date()),
            UserFeedback("Trade opened: EURUSD", FeedbackType.INFO, Date(Date().time - 300000)),
            UserFeedback("Connection stable", FeedbackType.SUCCESS, Date(Date().time - 600000))
        ),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val showRiskDialog: Boolean = false,
        val errorMessage: String? = null
    )

    sealed class DashboardEvent {
        data object Refresh : DashboardEvent()
        data object RefreshConnection : DashboardEvent()
        data object StartBot : DashboardEvent()
        data object StopBot : DashboardEvent()
        data class ToggleAutoTrading(val enabled: Boolean) : DashboardEvent()
        data class SetAutoOpenEnabled(val enabled: Boolean) : DashboardEvent()
        data class SetAutoCloseEnabled(val enabled: Boolean) : DashboardEvent()
        data object ShowRiskDialog : DashboardEvent()
        data object DismissRiskDialog : DashboardEvent()
        data class SaveAutomationSettings(val settings: AutomationSettings) : DashboardEvent()
        data class AddFeedback(val feedback: UserFeedback) : DashboardEvent()
    }

    fun handleEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> refresh()
            is DashboardEvent.RefreshConnection -> refreshConnection()
            is DashboardEvent.StartBot -> startBot()
            is DashboardEvent.StopBot -> stopBot()
            is DashboardEvent.ToggleAutoTrading -> toggleAutoTrading(event.enabled)
            is DashboardEvent.SetAutoOpenEnabled -> setAutoOpenEnabled(event.enabled)
            is DashboardEvent.SetAutoCloseEnabled -> setAutoCloseEnabled(event.enabled)
            is DashboardEvent.ShowRiskDialog -> showRiskDialog()
            is DashboardEvent.DismissRiskDialog -> dismissRiskDialog()
            is DashboardEvent.SaveAutomationSettings -> saveAutomationSettings(event.settings)
            is DashboardEvent.AddFeedback -> addFeedback(event.feedback)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshing = true)

            // Simulate API refresh - will be replaced with real repository
            delay(1500)

            // Update with new data (placeholder)
            _state.value = _state.value.copy(
                isRefreshing = false,
                feedbacks = listOf(
                    UserFeedback("Data refreshed", FeedbackType.SUCCESS, Date())
                ) + _state.value.feedbacks
            )
        }
    }

    private fun refreshConnection() {
        viewModelScope.launch {
            // Simulate connection check
            delay(500)

            _state.value = _state.value.copy(
                connection = _state.value.connection.copy(
                    connectionStatus = ConnectionStatus.CONNECTED
                )
            )
        }
    }

    private fun startBot() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // Simulate API call
            delay(1000)

            _state.value = _state.value.copy(
                botState = _state.value.botState.copy(
                    status = BotStatus.ACTIVE,
                    isRunning = true
                ),
                isLoading = false,
                feedbacks = listOf(
                    UserFeedback("Bot started successfully", FeedbackType.SUCCESS, Date())
                ) + _state.value.feedbacks
            )
        }
    }

    private fun stopBot() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // Simulate API call
            delay(1000)

            _state.value = _state.value.copy(
                botState = _state.value.botState.copy(
                    status = BotStatus.INACTIVE,
                    isRunning = false
                ),
                isLoading = false,
                feedbacks = listOf(
                    UserFeedback("Bot stopped", FeedbackType.WARNING, Date())
                ) + _state.value.feedbacks
            )
        }
    }

    private fun toggleAutoTrading(enabled: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                botState = _state.value.botState.copy(
                    isAutoTradingEnabled = enabled
                ),
                feedbacks = listOf(
                    UserFeedback(
                        if (enabled) "Auto trading enabled" else "Auto trading disabled",
                        FeedbackType.INFO,
                        Date()
                    )
                ) + _state.value.feedbacks
            )
        }
    }

    private fun setAutoOpenEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(
            tradeActivity = _state.value.tradeActivity.copy(
                autoOpenEnabled = enabled
            ),
            automationSettings = _state.value.automationSettings.copy(
                enableAutoOpen = enabled
            )
        )
    }

    private fun setAutoCloseEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(
            tradeActivity = _state.value.tradeActivity.copy(
                autoCloseEnabled = enabled
            ),
            automationSettings = _state.value.automationSettings.copy(
                enableAutoClose = enabled
            )
        )
    }

    private fun showRiskDialog() {
        _state.value = _state.value.copy(showRiskDialog = true)
    }

    private fun dismissRiskDialog() {
        _state.value = _state.value.copy(showRiskDialog = false)
    }

    private fun saveAutomationSettings(settings: AutomationSettings) {
        _state.value = _state.value.copy(
            automationSettings = settings,
            showRiskDialog = false,
            feedbacks = listOf(
                UserFeedback("Automation settings saved", FeedbackType.SUCCESS, Date())
            ) + _state.value.feedbacks
        )
    }

    private fun addFeedback(feedback: UserFeedback) {
        _state.value = _state.value.copy(
            feedbacks = listOf(feedback) + _state.value.feedbacks
        )
    }
}
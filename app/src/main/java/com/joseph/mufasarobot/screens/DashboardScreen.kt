package com.joseph.mufasarobot.screens


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.joseph.mufasarobot.models.BotStatus
import com.joseph.mufasarobot.models.ConnectionStatus
import com.joseph.mufasarobot.models.FeedbackType
import com.joseph.mufasarobot.models.RiskLevel
import com.joseph.mufasarobot.models.UserFeedback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardViewModel.DashboardState,
    onEvent: (DashboardViewModel.DashboardEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ConnectionStatusCard(state = state, onEvent = onEvent)
                }
            }

            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(delayMillis = 200)) + slideInVertically()
                ) {
                    BotStatusCard(state = state, onEvent = onEvent)
                }
            }

            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(delayMillis = 400)) + slideInVertically()
                ) {
                    TradeActivityCard(state = state)
                }
            }

            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(delayMillis = 600)) + slideInVertically()
                ) {
                    AutomationControlsCard(state = state, onEvent = onEvent)
                }
            }

            item {
                AnimatedVisibility(
                    visible = state.feedbacks.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(delayMillis = 800)) + slideInVertically()
                ) {
                    UserFeedbackSection(state = state)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Floating Action Button for Refresh
        FloatingActionButton(
            onClick = { onEvent(DashboardViewModel.DashboardEvent.Refresh) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            AnimatedContent(
                targetState = state.isRefreshing,
                label = "refresh animation"
            ) { refreshing ->
                if (refreshing) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    // Snackbar handling
    LaunchedEffect(state.feedbacks) {
        state.feedbacks.lastOrNull()?.let { feedback ->
            snackbarHostState.showSnackbar(
                message = feedback.message,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Risk Level Dialog
    if (state.showRiskDialog) {
        RiskLevelDialog(
            currentLevel = state.automationSettings.riskLevel,
            onDismiss = { onEvent(DashboardViewModel.DashboardEvent.DismissRiskDialog) },
            onConfirm = { level ->
                onEvent(DashboardViewModel.DashboardEvent.SaveAutomationSettings(
                    state.automationSettings.copy(riskLevel = level)
                ))
            }
        )
    }
}

@Composable
fun ConnectionStatusCard(
    state: DashboardViewModel.DashboardState,
    onEvent: (DashboardViewModel.DashboardEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔌 Connection Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { onEvent(DashboardViewModel.DashboardEvent.RefreshConnection) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Connection"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated status indicator
                val infiniteTransition = rememberInfiniteTransition()
                val pulse by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when (state.connection.connectionStatus) {
                                ConnectionStatus.CONNECTED -> Color.Green
                                ConnectionStatus.DISCONNECTED -> Color.Red
                                ConnectionStatus.CONNECTING -> Color.Yellow
                                ConnectionStatus.ERROR -> Color.Red
                            }.copy(alpha = pulse)
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = when (state.connection.connectionStatus) {
                        ConnectionStatus.CONNECTED -> "MT5 Connected"
                        ConnectionStatus.DISCONNECTED -> "MT5 Disconnected"
                        ConnectionStatus.CONNECTING -> "Connecting..."
                        ConnectionStatus.ERROR -> "Connection Error"
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            if (state.connection.isConnected) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Server: ${state.connection.serverName ?: "N/A"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "Account: ${state.connection.accountNumber ?: "N/A"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun BotStatusCard(
    state: DashboardViewModel.DashboardState,
    onEvent: (DashboardViewModel.DashboardEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🤖 Bot Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated status indicator
                    val infiniteTransition = rememberInfiniteTransition()
                    val pulse by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (state.botState.status) {
                                    BotStatus.ACTIVE -> Color.Green
                                    BotStatus.INACTIVE -> Color.Gray
                                    BotStatus.ERROR -> Color.Red
                                }.copy(alpha = pulse)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = when (state.botState.status) {
                            BotStatus.ACTIVE -> "Active"
                            BotStatus.INACTIVE -> "Inactive"
                            BotStatus.ERROR -> "Error"
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                Row {
                    Button(
                        onClick = {
                            if (state.botState.isRunning) {
                                onEvent(DashboardViewModel.DashboardEvent.StopBot)
                            } else {
                                onEvent(DashboardViewModel.DashboardEvent.StartBot)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.botState.isRunning)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (state.botState.isRunning) "STOP" else "START"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Strategy: ${state.botState.strategyName ?: "Default"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto Trading",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Switch(
                        checked = state.botState.isAutoTradingEnabled,
                        onCheckedChange = {
                            onEvent(DashboardViewModel.DashboardEvent.ToggleAutoTrading(it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TradeActivityCard(
    state: DashboardViewModel.DashboardState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Trade Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TradeStat(
                    icon = Icons.Default.TrendingUp,
                    label = "Auto Open",
                    value = if (state.tradeActivity.autoOpenEnabled) "ON" else "OFF",
                    color = if (state.tradeActivity.autoOpenEnabled)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                TradeStat(
                    icon = Icons.Default.TrendingDown,
                    label = "Auto Close",
                    value = if (state.tradeActivity.autoCloseEnabled) "ON" else "OFF",
                    color = if (state.tradeActivity.autoCloseEnabled)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Last Trade",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = state.tradeActivity.lastTradeTimestamp?.let {
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it)
                        } ?: "No trades",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "P/L",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = state.tradeActivity.profitLoss?.let {
                            if (it >= 0) "+$${"%.2f".format(it)}" else "-$${"%.2f".format(-it)}"
                        } ?: "$0.00",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = state.tradeActivity.profitLoss?.let {
                            if (it >= 0) Color.Green else Color.Red
                        } ?: MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun TradeStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun AutomationControlsCard(
    state: DashboardViewModel.DashboardState,
    onEvent: (DashboardViewModel.DashboardEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🔄 Automation Controls",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Auto Open",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = state.automationSettings.enableAutoOpen,
                    onCheckedChange = {
                        onEvent(DashboardViewModel.DashboardEvent.SetAutoOpenEnabled(it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Auto Close",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = state.automationSettings.enableAutoClose,
                    onCheckedChange = {
                        onEvent(DashboardViewModel.DashboardEvent.SetAutoCloseEnabled(it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedButton(
                    onClick = { onEvent(DashboardViewModel.DashboardEvent.ShowRiskDialog) },
                    shape = RoundedCornerShape(12.dp),
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = state.automationSettings.riskLevel.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onEvent(DashboardViewModel.DashboardEvent.SaveAutomationSettings(state.automationSettings)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = "SAVE SETTINGS",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UserFeedbackSection(
    state: DashboardViewModel.DashboardState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🧾 Notifications",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.feedbacks.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No notifications",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                state.feedbacks.takeLast(3).forEach { feedback ->
                    FeedbackItem(feedback = feedback)
                }
            }
        }
    }
}

@Composable
fun FeedbackItem(feedback: UserFeedback) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when (feedback.type) {
                        FeedbackType.SUCCESS -> Color.Green
                        FeedbackType.ERROR -> Color.Red
                        FeedbackType.WARNING -> Color.Yellow
                        FeedbackType.INFO -> Color.Blue
                    }
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feedback.message,
                fontSize = 14.sp,
                maxLines = 2
            )
            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(feedback.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun RiskLevelDialog(
    currentLevel: RiskLevel,
    onDismiss: () -> Unit,
    onConfirm: (RiskLevel) -> Unit
) {
    var selectedLevel by remember { mutableStateOf(currentLevel) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Risk Level",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                RiskLevel.entries.forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedLevel == level)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else
                                    Color.Transparent
                            )
                            .clickable { selectedLevel = level }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = level.name,
                            fontWeight = if (selectedLevel == level) FontWeight.Bold else FontWeight.Normal
                        )

                        if (selectedLevel == level) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(selectedLevel) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        state = DashboardViewModel.DashboardState(),
        onEvent = {}
    )
}
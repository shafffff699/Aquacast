package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidTabs
import com.example.components.LiquidToggleSwitch
import com.example.service.ChatMessage
import com.example.service.FloodAiChatService
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidBorderWhite
import com.example.ui.theme.LiquidBorderWhiteSubtle
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidGlassWhite
import com.example.ui.theme.LiquidGlassWhiteHover
import com.example.ui.theme.LiquidPrimaryPurpleStart
import com.example.ui.theme.LiquidSecondaryTealStart
import com.example.ui.theme.LiquidSolidSoftWhite
import com.example.ui.theme.LiquidSuccessGreen
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextLight
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.PrimaryPurpleGradient
import com.example.ui.theme.RainbowBorderGradient
import com.example.viewmodel.FloodUiState
import kotlinx.coroutines.launch

@Composable
fun FloodAiChatSheet(
    uiState: FloodUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showServerSettings by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello! I am **AquaAI**, your intelligent Chennai flood and urban drainage assistant. Ask me about street flood arrival times, water levels, ISRO elevations, tidal lock, traffic detours, or how to host this bot on your server!",
                isUser = false
            )
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Which street will flood first?",
        "Google Maps Data Integration",
        "Why does Velachery flood?",
        "How does Tidal Lock work?",
        "Is 25cm water safe for my car?",
        "How does traffic rerouting work?",
        "Server Hosting Setup"
    )

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isEmpty() || isLoading) return

        messages.add(ChatMessage(text = trimmed, isUser = true))
        inputText = ""
        isLoading = true

        coroutineScope.launch {
            val responseText = FloodAiChatService.sendChatMessage(
                userPrompt = trimmed,
                history = messages.toList(),
                radarStatus = uiState.radarStatus,
                predictions = uiState.predictions,
                selectedHour = uiState.selectedHour
            )
            messages.add(ChatMessage(text = responseText, isUser = false))
            isLoading = false
        }
    }

    // Main Liquid Glass Modal Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .shadow(24.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), ambientColor = Color(0x33000000))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color(0xF0FFFFFF))
            .border(1.5.dp, LiquidBorderWhite, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Avatar + Title + Server Settings button + Close
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Avatar with Rainbow Ring
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(6.dp, CircleShape, ambientColor = Color(0x226C5CE7))
                                .clip(CircleShape)
                                .background(PrimaryPurpleGradient)
                                .border(1.5.dp, RainbowBorderGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AquaAI Avatar",
                                tint = LiquidTextLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "AquaAI Assistant",
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LiquidTextDark
                                    )
                                )
                                // Status Pill
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (FloodAiChatService.isUsingCustomServer) LiquidSecondaryTealStart.copy(0.2f)
                                            else LiquidAccentPurple.copy(0.15f)
                                        )
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (FloodAiChatService.isUsingCustomServer) "Server API" else "Gemini 3.5 Flash",
                                        style = TextStyle(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (FloodAiChatService.isUsingCustomServer) LiquidSecondaryTealStart else LiquidAccentPurple
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Live Chennai Hydrology & App Guide",
                                style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                            )
                        }
                    }

                    // Action buttons: Server hosting icon + Close icon
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Server hosting settings button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LiquidSolidSoftWhite)
                                .border(1.dp, Color(0x99FFFFFF), CircleShape)
                                .clickable { showServerSettings = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = "Server Settings",
                                tint = LiquidAccentPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LiquidSolidSoftWhite)
                                .border(1.dp, Color(0x99FFFFFF), CircleShape)
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = LiquidTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Quick Prompt Chips Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickPrompts) { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LiquidGlassWhiteHover)
                                .border(1.dp, LiquidBorderWhiteSubtle, CircleShape)
                                .clickable {
                                    if (prompt == "Server Hosting Setup") {
                                        showServerSettings = true
                                    } else {
                                        sendMessage(prompt)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = prompt,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = LiquidTextDark
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    ChatBubbleItem(message = message)
                }

                if (isLoading) {
                    item {
                        ThinkingBubbleItem()
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Row: Glass pill input field + purple send button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Glass Pill Input Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(6.dp, CircleShape, ambientColor = Color(0x14000000))
                        .clip(CircleShape)
                        .background(LiquidSolidSoftWhite)
                        .border(1.dp, LiquidBorderWhite, CircleShape)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = LiquidTextDark,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(LiquidAccentPurple),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Ask about flood levels, streets, app doubts...",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color = LiquidTextMuted,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Send Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = LiquidPrimaryPurpleStart.copy(0.4f)
                        )
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isLoading) PrimaryPurpleGradient
                            else SolidColor(Color(0x338B6CF0))
                        )
                        .clickable(enabled = inputText.isNotBlank() && !isLoading) {
                            sendMessage(inputText)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = LiquidTextLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Server Hosting Settings Dialog
    if (showServerSettings) {
        ServerHostingSettingsModal(
            onDismiss = { showServerSettings = false }
        )
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            // User Message Bubble (Pill shape, purple gradient, white text)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = LiquidPrimaryPurpleStart.copy(0.4f))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp))
                    .background(PrimaryPurpleGradient)
                    .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = LiquidTextLight,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        } else {
            // Assistant Message Bubble (Frosted glass container, dark text, clean formatting)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x14000000))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 6.dp, bottomEnd = 20.dp))
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, LiquidBorderWhite, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 6.dp, bottomEnd = 20.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = LiquidAccentPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "AquaAI",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiquidAccentPurple
                            )
                        )
                    }

                    Text(
                        text = message.text,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = LiquidTextDark,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ThinkingBubbleItem() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LiquidSolidSoftWhite)
            .border(1.dp, LiquidBorderWhite, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LiquidAccentPurple.copy(alpha = alpha))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LiquidPrimaryPurpleStart.copy(alpha = alpha))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LiquidSecondaryTealStart.copy(alpha = alpha))
            )
            Text(
                text = "AquaAI is thinking...",
                style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted, fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun ServerHostingSettingsModal(
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedLangTab by remember { mutableStateOf(0) } // 0: Node.js, 1: Python
    var customUrlInput by remember { mutableStateOf(FloodAiChatService.customServerEndpoint) }
    var useCustomServer by remember { mutableStateOf(FloodAiChatService.isUsingCustomServer) }
    var copyStatus by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(LiquidSolidSoftWhite)
                .border(1.5.dp, LiquidBorderWhite, RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = LiquidAccentPurple,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Server Hosting & API",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x1F000000))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = LiquidTextDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "Deploy this AI flood chat bot to your own cloud server (Google Cloud Run, AWS, VPS) or connect to an existing municipal endpoint.",
                    style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted, lineHeight = 16.sp)
                )

                // Toggle Custom Server Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LiquidGlassWhite)
                        .border(1.dp, LiquidBorderWhite, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Use Custom Server Endpoint",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                        )
                        Text(
                            text = if (useCustomServer) "Directing queries to your server" else "Using Google Gemini directly",
                            style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                        )
                    }
                    LiquidToggleSwitch(
                        checked = useCustomServer,
                        onCheckedChange = {
                            useCustomServer = it
                            FloodAiChatService.isUsingCustomServer = it
                        }
                    )
                }

                // Server URL Input
                if (useCustomServer) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Server Endpoint URL:",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEDE9FE))
                                .border(1.dp, Color(0xFFC4B5FD), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = customUrlInput,
                                onValueChange = {
                                    customUrlInput = it
                                    FloodAiChatService.customServerEndpoint = it
                                },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = LiquidTextDark),
                                decorationBox = { inner ->
                                    if (customUrlInput.isEmpty()) {
                                        Text(
                                            text = "https://your-server.com/api/chat",
                                            style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                                        )
                                    }
                                    inner()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Server Code Template Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidTabs(
                        tabs = listOf("Node.js (Express)", "Python (FastAPI)"),
                        selectedIndex = selectedLangTab,
                        onTabSelected = { selectedLangTab = it }
                    )

                    // Copy Code Button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LiquidPrimaryPurpleStart)
                            .clickable {
                                val code = if (selectedLangTab == 0) FloodAiChatService.NODE_SERVER_CODE else FloodAiChatService.PYTHON_SERVER_CODE
                                clipboardManager.setText(AnnotatedString(code))
                                copyStatus = "Copied to clipboard!"
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = LiquidTextLight, modifier = Modifier.size(12.dp))
                            Text(text = "Copy Code", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidTextLight))
                        }
                    }
                }

                if (copyStatus != null) {
                    Text(
                        text = copyStatus!!,
                        style = TextStyle(fontSize = 11.sp, color = LiquidSuccessGreen, fontWeight = FontWeight.Bold)
                    )
                }

                // Code Display Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E1F29))
                        .padding(12.dp)
                ) {
                    val codeText = if (selectedLangTab == 0) FloodAiChatService.NODE_SERVER_CODE else FloodAiChatService.PYTHON_SERVER_CODE
                    LazyColumn {
                        item {
                            Text(
                                text = codeText.trim(),
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF8EE0D6),
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                LiquidPrimaryButton(
                    text = "Save & Close",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss
                )
            }
        }
    }
}

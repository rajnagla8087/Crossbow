package com.crossbow.translator.ui.screens.voice

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.domain.model.TranslationUiState
import com.crossbow.translator.ui.components.AudioWaveIndicator
import com.crossbow.translator.ui.components.LanguageSelectorModal
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandRose
import com.crossbow.translator.ui.theme.BrandViolet
import com.crossbow.translator.ui.theme.CrossbowPrimaryGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    viewModel: VoiceViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val spokenText by viewModel.spokenText.collectAsState()
    val translationState by viewModel.translationState.collectAsState()
    val isListening by viewModel.speechHelper.isListening.collectAsState()
    val soundLevel by viewModel.speechHelper.soundLevel.collectAsState()
    val isSpeaking by viewModel.ttsHelper.isSpeaking.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleListening()
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_anim"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Translation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Pair Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showSourcePicker = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(sourceLang.flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(sourceLang.name, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { viewModel.swapLanguages() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandIndigo.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = BrandIndigo)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showTargetPicker = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(targetLang.flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(targetLang.name, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Big Glowing Microphone Button with Audio Pulse
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    // Outer pulse waves
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale + (soundLevel * 0.3f))
                            .clip(CircleShape)
                            .background(BrandCyan.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(125.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(BrandIndigo.copy(alpha = 0.35f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) Brush.radialGradient(listOf(BrandCyan, BrandIndigo))
                            else CrossbowPrimaryGradient
                        )
                        .clickable {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = "Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isListening) "Listening to ${sourceLang.name}..." else "Tap microphone to speak",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (isListening) {
                Spacer(modifier = Modifier.height(8.dp))
                AudioWaveIndicator(isAnimating = true, barCount = 7, maxHeight = 28.dp)
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = BrandRose, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Original Recognized Text Card
            if (spokenText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "YOU SAID (${sourceLang.name})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = spokenText,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Translated Speech Output Card
            when (translationState) {
                is TranslationUiState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = BrandIndigo, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Translating speech...")
                    }
                }
                is TranslationUiState.Success -> {
                    val result = (translationState as TranslationUiState.Success).data
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(BrandIndigo.copy(alpha = 0.1f))
                            .border(1.dp, BrandIndigo.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TRANSLATION (${targetLang.name})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandCyan
                                )

                                Row {
                                    IconButton(
                                        onClick = { viewModel.replayAudio() },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        if (isSpeaking) {
                                            AudioWaveIndicator(isAnimating = true, maxHeight = 16.dp)
                                        } else {
                                            Icon(
                                                Icons.Default.Replay,
                                                contentDescription = "Replay Audio",
                                                tint = BrandCyan
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.clipboardHelper.copyToClipboard("Voice Translation", result.translatedText)
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }

                                    IconButton(
                                        onClick = {
                                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, result.translatedText)
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Share translation"))
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = result.translatedText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    lineHeight = 26.sp
                                )
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showSourcePicker) {
        LanguageSelectorModal(
            selectedLanguage = sourceLang,
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setSourceLanguage(it) },
            onDismissRequest = { showSourcePicker = false }
        )
    }

    if (showTargetPicker) {
        LanguageSelectorModal(
            selectedLanguage = targetLang,
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setTargetLanguage(it) },
            onDismissRequest = { showTargetPicker = false }
        )
    }
}

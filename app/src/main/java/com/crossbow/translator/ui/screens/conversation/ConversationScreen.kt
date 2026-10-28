package com.crossbow.translator.ui.screens.conversation

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.ui.components.AudioWaveIndicator
import com.crossbow.translator.ui.components.LanguageSelectorModal
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel
) {
    val context = LocalContext.current
    val speaker1Lang by viewModel.speaker1Lang.collectAsState()
    val speaker2Lang by viewModel.speaker2Lang.collectAsState()
    val activeSpeaker by viewModel.activeSpeaker.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val autoPlayAudio by viewModel.autoPlayAudio.collectAsState()
    val isListening by viewModel.speechHelper.isListening.collectAsState()
    val isSpeaking by viewModel.ttsHelper.isSpeaking.collectAsState()

    var showSpeaker1Picker by remember { mutableStateOf(false) }
    var showSpeaker2Picker by remember { mutableStateOf(false) }
    var pendingSpeakerPermission by remember { mutableIntStateOf(1) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startListeningForSpeaker(pendingSpeakerPermission)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Conversation Mode", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "${speaker1Lang.name} ⇋ ${speaker2Lang.name}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleAutoPlayAudio() }) {
                        Icon(
                            imageVector = if (autoPlayAudio) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Auto audio toggle",
                            tint = if (autoPlayAudio) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (messages.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.clipboardHelper.copyToClipboard("Conversation Transcript", viewModel.getTranscript())
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Transcript")
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.getTranscript())
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Transcript"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share Transcript")
                        }
                        IconButton(onClick = { viewModel.clearConversation() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Dual Microphone Action Dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speaker 1 Mic Button (Left)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSpeaker1Picker = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(speaker1Lang.flag, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(speaker1Lang.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (activeSpeaker == 1 && isListening) BrandIndigo
                                    else BrandIndigo.copy(alpha = 0.15f)
                                )
                                .clickable {
                                    pendingSpeakerPermission = 1
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speaker 1 Mic",
                                tint = if (activeSpeaker == 1 && isListening) Color.White else BrandIndigo,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )

                    // Speaker 2 Mic Button (Right)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSpeaker2Picker = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(speaker2Lang.flag, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(speaker2Lang.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (activeSpeaker == 2 && isListening) BrandViolet
                                    else BrandViolet.copy(alpha = 0.15f)
                                )
                                .clickable {
                                    pendingSpeakerPermission = 2
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speaker 2 Mic",
                                tint = if (activeSpeaker == 2 && isListening) Color.White else BrandViolet,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💬", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Face-to-Face Dual Conversation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Each person presses their microphone button below to speak. Messages are instantly recognized and translated with voice playback.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isSpeaker1 = msg.speakerId == 1
                    val align = if (isSpeaker1) Alignment.Start else Alignment.End
                    val bubbleColor = if (isSpeaker1) BrandIndigo.copy(alpha = 0.12f) else BrandViolet.copy(alpha = 0.12f)
                    val borderColor = if (isSpeaker1) BrandIndigo.copy(alpha = 0.3f) else BrandViolet.copy(alpha = 0.3f)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = align
                    ) {
                        Text(
                            text = if (isSpeaker1) "Speaker 1 (${msg.sourceLang.name})" else "Speaker 2 (${msg.sourceLang.name})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSpeaker1) BrandIndigo else BrandViolet,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = if (isSpeaker1) 4.dp else 18.dp,
                                        bottomEnd = if (isSpeaker1) 18.dp else 4.dp
                                    )
                                )
                                .background(bubbleColor)
                                .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = msg.originalText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg.translatedText,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { viewModel.playMessageAudio(msg) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.VolumeUp,
                                            contentDescription = "Speak",
                                            tint = if (isSpeaker1) BrandIndigo else BrandViolet,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSpeaker1Picker) {
        LanguageSelectorModal(
            selectedLanguage = speaker1Lang,
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setSpeaker1Language(it) },
            onDismissRequest = { showSpeaker1Picker = false }
        )
    }

    if (showSpeaker2Picker) {
        LanguageSelectorModal(
            selectedLanguage = speaker2Lang,
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setSpeaker2Language(it) },
            onDismissRequest = { showSpeaker2Picker = false }
        )
    }
}

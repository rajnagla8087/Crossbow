package com.crossbow.translator.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.ui.components.LanguageSelectorModal
import com.crossbow.translator.ui.theme.BrandAmber
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val defaultSourceCode by viewModel.defaultSourceLang.collectAsState()
    val defaultTargetCode by viewModel.defaultTargetLang.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val speechPitch by viewModel.speechPitch.collectAsState()
    val autoPlayVoice by viewModel.autoPlayVoice.collectAsState()
    val backendUrl by viewModel.backendUrl.collectAsState()

    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showBackendDialog by remember { mutableStateOf(false) }
    var backendInput by remember { mutableStateOf(backendUrl) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Theme Section
            SettingsSection(title = "Appearance & Theme", icon = Icons.Default.DarkMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("SYSTEM" to "System", "DARK" to "Dark", "LIGHT" to "Light").forEach { (mode, label) ->
                        val isSelected = themeMode.equals(mode, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BrandIndigo else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Language Defaults
            SettingsSection(title = "Language Preferences", icon = Icons.Default.Language) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showSourcePicker = true }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Default Source Language", fontWeight = FontWeight.Medium)
                    val lang = Language.findByCode(defaultSourceCode)
                    Text("${lang.flag} ${lang.name}", fontWeight = FontWeight.Bold, color = BrandIndigo)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showTargetPicker = true }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Default Target Language", fontWeight = FontWeight.Medium)
                    val lang = Language.findByCode(defaultTargetCode)
                    Text("${lang.flag} ${lang.name}", fontWeight = FontWeight.Bold, color = BrandCyan)
                }
            }

            // Voice & Speech Settings
            SettingsSection(title = "Voice & Speech (TTS)", icon = Icons.Default.Speed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-play Speech Output", fontWeight = FontWeight.Medium)
                    Switch(
                        checked = autoPlayVoice,
                        onCheckedChange = { viewModel.setAutoPlayVoice(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Speech Rate: ${String.format("%.1fx", speechRate)}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = speechRate,
                    onValueChange = { viewModel.setSpeechRate(it) },
                    valueRange = 0.5f..2.0f,
                    steps = 5
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Speech Pitch: ${String.format("%.1fx", speechPitch)}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(
                    value = speechPitch,
                    onValueChange = { viewModel.setSpeechPitch(it) },
                    valueRange = 0.5f..2.0f,
                    steps = 5
                )
            }

            // Data & Security
            SettingsSection(title = "Backend & Security", icon = Icons.Default.Security) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            backendInput = backendUrl
                            showBackendDialog = true
                        }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Secure API Backend", fontWeight = FontWeight.Medium)
                        Text(
                            text = backendUrl,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            maxLines = 1
                        )
                    }
                    Text("Configure", color = BrandIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.clearAllHistory() }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Clear Translation History", color = BrandRose, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Delete, contentDescription = null, tint = BrandRose, modifier = Modifier.size(20.dp))
                }
            }

            // Info & Legal
            SettingsSection(title = "About & Privacy", icon = Icons.Default.Info) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Privacy Policy & Security", fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showHelpDialog = true }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Help & Feedback", fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Help, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("App Version", fontWeight = FontWeight.Medium)
                    Text("1.0.0 (Precision Release)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Developer", fontWeight = FontWeight.Medium)
                    Text("Made by Raj Nagla", color = BrandIndigo, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Made by Raj Nagla",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSourcePicker) {
        LanguageSelectorModal(
            selectedLanguage = Language.findByCode(defaultSourceCode),
            allowAutoDetect = true,
            onLanguageSelected = { viewModel.setDefaultSourceLang(it.code) },
            onDismissRequest = { showSourcePicker = false }
        )
    }

    if (showTargetPicker) {
        LanguageSelectorModal(
            selectedLanguage = Language.findByCode(defaultTargetCode),
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setDefaultTargetLang(it.code) },
            onDismissRequest = { showTargetPicker = false }
        )
    }

    if (showBackendDialog) {
        AlertDialog(
            onDismissRequest = { showBackendDialog = false },
            title = { Text("Configure Secure Backend") },
            text = {
                Column {
                    Text(
                        "Set your private proxy backend server URL or use the built-in direct high-availability API.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = backendInput,
                        onValueChange = { backendInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setBackendUrl(backendInput.trim())
                    showBackendDialog = false
                }) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackendDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy & Security") },
            text = {
                Text(
                    "Crossbow Translator protects user confidentiality:\n\n" +
                            "• All network traffic is encrypted via HTTPS.\n" +
                            "• Translation history and saved favorites remain stored locally on your device in encrypted SQLite/Room tables.\n" +
                            "• Audio recordings are processed solely for speech recognition and are not permanently logged or sold.\n" +
                            "• You retain full control to clear history and delete stored content at any time.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Crossbow Translator Help") },
            text = {
                Text(
                    "Features Guide:\n\n" +
                            "1. Text Translation: Type or paste words, phrases, or full paragraphs with 100+ language support.\n" +
                            "2. Voice Translation: Tap the glowing mic to speak directly.\n" +
                            "3. Conversation: Two-speaker dual mic interface for live bilingual conversations.\n" +
                            "4. Scan Text: Point your camera at signs or menus for OCR recognition.\n" +
                            "5. Language Practice: Train with flashcards, quizzes, and streak tracking.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandIndigo,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

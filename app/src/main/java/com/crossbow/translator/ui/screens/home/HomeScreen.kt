package com.crossbow.translator.ui.screens.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.domain.model.TranslationUiState
import com.crossbow.translator.ui.components.CrossbowTopBar
import com.crossbow.translator.ui.components.GradientButton
import com.crossbow.translator.ui.components.LanguageSelectorModal
import com.crossbow.translator.ui.components.QuickFeatureCard
import com.crossbow.translator.ui.components.TranslationInputCard
import com.crossbow.translator.ui.components.TranslationOutputCard
import com.crossbow.translator.ui.theme.BrandAmber
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandEmerald
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandRose
import com.crossbow.translator.ui.theme.BrandViolet

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToVoice: () -> Unit,
    onNavigateToConversation: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val inputText by viewModel.inputText.collectAsState()
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val translationState by viewModel.translationState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isSpeaking by viewModel.textToSpeechHelper.isSpeaking.collectAsState()
    val isOnline by viewModel.networkMonitor.isOnline.collectAsState(initial = true)
    val recentHistory by viewModel.recentHistory.collectAsState()
    val dailyWords by viewModel.dailyWords.collectAsState()

    var showSourceLangPicker by remember { mutableStateOf(false) }
    var showTargetLangPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CrossbowTopBar(onSettingsClick = onNavigateToSettings)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Offline Warning Banner
            if (!isOnline) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BrandAmber.copy(alpha = 0.15f))
                            .border(1.dp, BrandAmber.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚠️ Offline mode active. Showing saved translations and downloaded vocabulary.",
                            style = MaterialTheme.typography.bodySmall.copy(color = BrandAmber, fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            // Quick Language Bar with Swap Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Source Lang Chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSourceLangPicker = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = sourceLang.flag, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = sourceLang.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Swap Button
                    IconButton(
                        onClick = { viewModel.swapLanguages() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandIndigo.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = BrandIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Target Lang Chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showTargetLangPicker = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = targetLang.flag, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = targetLang.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Input Text Card
            item {
                TranslationInputCard(
                    inputText = inputText,
                    onInputTextChange = { viewModel.onInputTextChanged(it) },
                    sourceLanguage = sourceLang,
                    onSelectSourceLanguage = { showSourceLangPicker = true },
                    onPasteClick = { viewModel.pasteFromClipboard() },
                    onClearClick = { viewModel.clearInput() },
                    onMicClick = onNavigateToVoice
                )
            }

            // Translate Action Button
            item {
                GradientButton(
                    text = if (translationState is TranslationUiState.Loading) "Translating..." else "Translate Now",
                    icon = Icons.Default.Translate,
                    isLoading = translationState is TranslationUiState.Loading,
                    onClick = { viewModel.translate() },
                    enabled = inputText.isNotBlank()
                )
            }

            // Translation Output Card
            item {
                AnimatedVisibility(
                    visible = translationState !is TranslationUiState.Idle || inputText.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TranslationOutputCard(
                        uiState = translationState,
                        targetLanguage = targetLang,
                        onSelectTargetLanguage = { showTargetLangPicker = true },
                        isSpeaking = isSpeaking,
                        onSpeakClick = { text -> viewModel.playAudio(text) },
                        onCopyClick = { text ->
                            viewModel.clipboardHelper.copyToClipboard("Translation", text)
                        },
                        onShareClick = { text ->
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share translation via"))
                        },
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onRetryClick = { viewModel.translate() }
                    )
                }
            }

            // Feature Cards Section
            item {
                Text(
                    text = "AI Translation Suite",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickFeatureCard(
                        title = "Voice Speech",
                        subtitle = "Speak & translate in real-time",
                        icon = Icons.Default.Mic,
                        accentColor = BrandIndigo,
                        onClick = onNavigateToVoice,
                        modifier = Modifier.weight(1f)
                    )
                    QuickFeatureCard(
                        title = "Conversation",
                        subtitle = "Bilingual two-way dialogue",
                        icon = Icons.Default.Chat,
                        accentColor = BrandViolet,
                        onClick = onNavigateToConversation,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickFeatureCard(
                        title = "Camera OCR",
                        subtitle = "Scan signs, menus & docs",
                        icon = Icons.Default.CameraAlt,
                        accentColor = BrandCyan,
                        onClick = onNavigateToCamera,
                        modifier = Modifier.weight(1f)
                    )
                    QuickFeatureCard(
                        title = "Dictionary",
                        subtitle = "Definitions, phonetics & audio",
                        icon = Icons.Outlined.Book,
                        accentColor = BrandAmber,
                        onClick = onNavigateToDictionary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Daily Vocabulary Card
            if (dailyWords.isNotEmpty()) {
                item {
                    val word = dailyWords.first()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(BrandEmerald.copy(alpha = 0.12f))
                            .border(1.dp, BrandEmerald.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                            .clickable { onNavigateToPractice() }
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = BrandEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "WORD OF THE DAY",
                                        color = BrandEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Practice →",
                                    color = BrandEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${word.word} (${word.translation})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (word.example.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${word.example}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            // Recent Translations Section
            if (recentHistory.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Translations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "See All",
                            color = BrandIndigo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigateToHistory() }
                        )
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(recentHistory, key = { it.id }) { item ->
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                    .clickable { viewModel.reuseHistory(item) }
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "${Language.findByCode(item.sourceLangCode).flag} → ${Language.findByCode(item.targetLangCode).flag}",
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.sourceText,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = item.translatedText,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Source Language Selector Bottom Sheet
    if (showSourceLangPicker) {
        LanguageSelectorModal(
            selectedLanguage = sourceLang,
            allowAutoDetect = true,
            onLanguageSelected = { viewModel.setSourceLanguage(it) },
            onDismissRequest = { showSourceLangPicker = false }
        )
    }

    // Target Language Selector Bottom Sheet
    if (showTargetLangPicker) {
        LanguageSelectorModal(
            selectedLanguage = targetLang,
            allowAutoDetect = false,
            onLanguageSelected = { viewModel.setTargetLanguage(it) },
            onDismissRequest = { showTargetLangPicker = false }
        )
    }
}

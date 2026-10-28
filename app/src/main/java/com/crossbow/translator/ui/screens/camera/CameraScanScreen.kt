package com.crossbow.translator.ui.screens.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.domain.model.TranslationUiState
import com.crossbow.translator.ui.components.GradientButton
import com.crossbow.translator.ui.components.LanguageSelectorModal
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandRose
import com.crossbow.translator.ui.theme.CrossbowAccentGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScanScreen(
    viewModel: CameraScanViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val targetLang by viewModel.targetLang.collectAsState()
    val extractedText by viewModel.extractedText.collectAsState()
    val isProcessing by viewModel.isProcessingImage.collectAsState()
    val translationState by viewModel.translationState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showTargetPicker by remember { mutableStateOf(false) }

    // Camera capture launcher
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            viewModel.processCapturedImage(cameraUri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = viewModel.prepareCameraCaptureUri()
            cameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.processCapturedImage(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Text (OCR)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (extractedText.isNotBlank()) {
                        IconButton(onClick = { viewModel.clear() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
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
            // Target Language Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                    .clickable { showTargetPicker = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Translate To:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = targetLang.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = targetLang.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandIndigo
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "▾", color = BrandIndigo)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Trigger Buttons (Camera & Gallery)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandIndigo.copy(alpha = 0.12f))
                        .border(1.dp, BrandIndigo.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = BrandIndigo,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Take Photo",
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo,
                            fontSize = 15.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandCyan.copy(alpha = 0.12f))
                        .border(1.dp, BrandCyan.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable {
                            galleryLauncher.launch("image/*")
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = BrandCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "From Gallery",
                            fontWeight = FontWeight.Bold,
                            color = BrandCyan,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading state while OCR scans
            if (isProcessing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = BrandCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Extracting text with ML Kit OCR...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = BrandCyan)
                    )
                }
            }

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandRose.copy(alpha = 0.12f))
                        .padding(12.dp)
                ) {
                    Text(text = errorMessage!!, color = BrandRose, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Extracted Text Editable Area
            if (extractedText.isNotBlank()) {
                Text(
                    text = "Extracted Text (Editable):",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = extractedText,
                    onValueChange = { viewModel.onExtractedTextChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(14.dp))

                GradientButton(
                    text = "Translate Extracted Text",
                    icon = Icons.Default.Translate,
                    onClick = { viewModel.translateExtractedText() },
                    gradient = CrossbowAccentGradient
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Translated Result Card
            when (translationState) {
                is TranslationUiState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = BrandIndigo, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Translating to ${targetLang.name}...")
                    }
                }
                is TranslationUiState.Success -> {
                    val res = (translationState as TranslationUiState.Success).data
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
                                    IconButton(onClick = { viewModel.playAudio(res.translatedText) }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen")
                                    }
                                    IconButton(onClick = {
                                        viewModel.clipboardHelper.copyToClipboard("Extracted Translation", res.translatedText)
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, res.translatedText)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share translation"))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = res.translatedText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 26.sp
                                )
                            )
                        }
                    }
                }
                is TranslationUiState.Error -> {
                    Text(
                        text = (translationState as TranslationUiState.Error).message,
                        color = BrandRose,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
            }
        }
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

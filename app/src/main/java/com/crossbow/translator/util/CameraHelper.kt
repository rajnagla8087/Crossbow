package com.crossbow.translator.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class CameraHelper(private val context: Context) {

    fun createTempImageUri(): Uri {
        val imageDir = File(context.cacheDir, "camera_images").apply {
            if (!exists()) mkdirs()
        }
        val tempFile = File.createTempFile("crossbow_scan_", ".jpg", imageDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    suspend fun recognizeTextFromUri(
        uri: Uri,
        scriptHint: String = "latin"
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = getRecognizerForScript(scriptHint)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text.trim()
                    if (text.isBlank()) {
                        continuation.resume(Result.failure(Exception("No clear text detected in image. Please ensure good lighting.")))
                    } else {
                        continuation.resume(Result.success(text))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun recognizeTextFromBitmap(
        bitmap: Bitmap,
        scriptHint: String = "latin"
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = getRecognizerForScript(scriptHint)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text.trim()
                    if (text.isBlank()) {
                        continuation.resume(Result.failure(Exception("No clear text found. Try capturing closer or with better lighting.")))
                    } else {
                        continuation.resume(Result.success(text))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    private fun getRecognizerForScript(scriptHint: String) = when (scriptHint.lowercase()) {
        "devanagari", "hi", "mr", "ne" ->
            TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
        "chinese", "zh" ->
            TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        "japanese", "ja" ->
            TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        "korean", "ko" ->
            TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        else ->
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
}

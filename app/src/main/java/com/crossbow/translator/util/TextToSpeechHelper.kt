package com.crossbow.translator.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechHelper(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
        } else {
            isInitialized = false
        }
    }

    fun speak(
        text: String,
        languageCode: String = "en",
        speechRate: Float = 1.0f,
        speechPitch: Float = 1.0f,
        onError: ((String) -> Unit)? = null
    ) {
        if (!isInitialized || textToSpeech == null) {
            onError?.invoke("Text to speech engine is initializing. Please try again.")
            return
        }

        if (text.isBlank()) return

        try {
            val locale = resolveLocale(languageCode)
            val result = textToSpeech?.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default language if specific locale isn't installed
                textToSpeech?.setLanguage(Locale.getDefault())
            }

            textToSpeech?.setSpeechRate(speechRate)
            textToSpeech?.setPitch(speechPitch)

            val utteranceId = "utterance_${System.currentTimeMillis()}"
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            _isSpeaking.value = false
            onError?.invoke(e.localizedMessage ?: "Failed to play speech")
        }
    }

    fun stop() {
        try {
            textToSpeech?.stop()
            _isSpeaking.value = false
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
    }

    private fun resolveLocale(languageCode: String): Locale {
        return when (languageCode.lowercase()) {
            "en" -> Locale.US
            "es" -> Locale("es", "ES")
            "hi" -> Locale("hi", "IN")
            "gu" -> Locale("gu", "IN")
            "mr" -> Locale("mr", "IN")
            "bn" -> Locale("bn", "IN")
            "ta" -> Locale("ta", "IN")
            "te" -> Locale("te", "IN")
            "pa" -> Locale("pa", "IN")
            "ur" -> Locale("ur", "PK")
            "fr" -> Locale.FRANCE
            "de" -> Locale.GERMANY
            "it" -> Locale.ITALY
            "pt" -> Locale("pt", "BR")
            "ru" -> Locale("ru", "RU")
            "zh", "zh-cn" -> Locale.SIMPLIFIED_CHINESE
            "zh-tw" -> Locale.TRADITIONAL_CHINESE
            "ja" -> Locale.JAPAN
            "ko" -> Locale.KOREA
            "ar" -> Locale("ar", "SA")
            else -> Locale(languageCode)
        }
    }
}

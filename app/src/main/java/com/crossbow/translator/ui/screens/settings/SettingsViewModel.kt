package com.crossbow.translator.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val appPreferences = app.appPreferences
    private val historyRepo = app.historyRepository
    val clipboardHelper = app.clipboardHelper

    val themeMode: StateFlow<String> = appPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val defaultSourceLang: StateFlow<String> = appPreferences.defaultSourceLang
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "auto")

    val defaultTargetLang: StateFlow<String> = appPreferences.defaultTargetLang
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "es")

    val speechRate: StateFlow<Float> = appPreferences.speechRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val speechPitch: StateFlow<Float> = appPreferences.speechPitch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val autoPlayVoice: StateFlow<Boolean> = appPreferences.autoPlayVoice
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val backendUrl: StateFlow<String> = appPreferences.backendUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "https://api.mymemory.translated.net/")

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            appPreferences.setThemeMode(mode)
        }
    }

    fun setDefaultSourceLang(code: String) {
        viewModelScope.launch {
            appPreferences.setDefaultSourceLang(code)
        }
    }

    fun setDefaultTargetLang(code: String) {
        viewModelScope.launch {
            appPreferences.setDefaultTargetLang(code)
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            appPreferences.setSpeechRate(rate)
        }
    }

    fun setSpeechPitch(pitch: Float) {
        viewModelScope.launch {
            appPreferences.setSpeechPitch(pitch)
        }
    }

    fun setAutoPlayVoice(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setAutoPlayVoice(enabled)
        }
    }

    fun setBackendUrl(url: String) {
        viewModelScope.launch {
            appPreferences.setBackendUrl(url)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepo.clearAllHistory()
            clipboardHelper.copyToClipboard("Settings", "Translation history cleared!", true)
        }
    }
}

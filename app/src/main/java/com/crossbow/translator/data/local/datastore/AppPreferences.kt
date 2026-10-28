package com.crossbow.translator.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "crossbow_settings")

class AppPreferences(private val context: Context) {

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode") // "SYSTEM", "LIGHT", "DARK"
        val DEFAULT_SOURCE_LANG = stringPreferencesKey("default_source_lang")
        val DEFAULT_TARGET_LANG = stringPreferencesKey("default_target_lang")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val SPEECH_PITCH = floatPreferencesKey("speech_pitch")
        val AUTO_PLAY_VOICE = booleanPreferencesKey("auto_play_voice")
        val BACKEND_URL = stringPreferencesKey("backend_url")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "SYSTEM"
    }

    val defaultSourceLang: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_SOURCE_LANG] ?: "auto"
    }

    val defaultTargetLang: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_TARGET_LANG] ?: "es"
    }

    val speechRate: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[SPEECH_RATE] ?: 1.0f
    }

    val speechPitch: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[SPEECH_PITCH] ?: 1.0f
    }

    val autoPlayVoice: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_PLAY_VOICE] ?: false
    }

    val backendUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[BACKEND_URL] ?: "https://api.mymemory.translated.net/"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[THEME_MODE] = mode }
    }

    suspend fun setDefaultSourceLang(langCode: String) {
        context.dataStore.edit { prefs -> prefs[DEFAULT_SOURCE_LANG] = langCode }
    }

    suspend fun setDefaultTargetLang(langCode: String) {
        context.dataStore.edit { prefs -> prefs[DEFAULT_TARGET_LANG] = langCode }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.dataStore.edit { prefs -> prefs[SPEECH_RATE] = rate }
    }

    suspend fun setSpeechPitch(pitch: Float) {
        context.dataStore.edit { prefs -> prefs[SPEECH_PITCH] = pitch }
    }

    suspend fun setAutoPlayVoice(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[AUTO_PLAY_VOICE] = enabled }
    }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { prefs -> prefs[BACKEND_URL] = url }
    }
}

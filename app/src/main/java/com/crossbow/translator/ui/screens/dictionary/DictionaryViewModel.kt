package com.crossbow.translator.ui.screens.dictionary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.domain.model.DictionaryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val dictionaryRepo = app.dictionaryRepository
    private val favoritesRepo = app.favoritesRepository
    val ttsHelper = app.textToSpeechHelper
    val clipboardHelper = app.clipboardHelper

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _dictionaryState = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Idle)
    val dictionaryState: StateFlow<DictionaryUiState> = _dictionaryState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _dictionaryState.value = DictionaryUiState.Idle
        }
    }

    fun lookupWord() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _dictionaryState.value = DictionaryUiState.Loading
            val result = dictionaryRepo.lookupWord(query)
            _dictionaryState.value = result
        }
    }

    fun playAudio(word: String) {
        ttsHelper.speak(word, "en")
    }

    fun saveToFavorites(word: String, definition: String) {
        viewModelScope.launch {
            favoritesRepo.addFavorite(
                sourceText = word,
                translatedText = definition,
                sourceLangCode = "en",
                targetLangCode = "en",
                category = "Dictionary",
                note = "Saved from Crossbow Dictionary"
            )
            clipboardHelper.copyToClipboard("Saved", "Saved \"$word\" to Favorites!")
        }
    }
}

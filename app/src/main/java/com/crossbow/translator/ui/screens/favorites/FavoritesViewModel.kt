package com.crossbow.translator.ui.screens.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val favoritesRepo = app.favoritesRepository
    val clipboardHelper = app.clipboardHelper
    val ttsHelper = app.textToSpeechHelper

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val categories = listOf("All", "General", "Travel", "Business", "Food", "Social", "Dictionary")

    val favorites: StateFlow<List<FavoriteEntity>> = combine(
        favoritesRepo.allFavorites,
        _selectedCategory,
        _searchQuery
    ) { list, category, query ->
        list.filter { item ->
            val matchesCategory = if (category == "All") true else item.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                item.sourceText.contains(query, ignoreCase = true) ||
                        item.translatedText.contains(query, ignoreCase = true) ||
                        item.note.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            favoritesRepo.removeFavorite(id)
        }
    }

    fun updateFavoriteNote(favorite: FavoriteEntity, note: String) {
        viewModelScope.launch {
            favoritesRepo.updateFavorite(favorite.copy(note = note))
        }
    }

    fun playAudio(text: String, langCode: String) {
        ttsHelper.speak(text, langCode)
    }
}

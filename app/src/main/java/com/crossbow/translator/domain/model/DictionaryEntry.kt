package com.crossbow.translator.domain.model

data class DictionaryEntry(
    val word: String,
    val phonetic: String? = null,
    val audioUrl: String? = null,
    val meanings: List<Meaning> = emptyList(),
    val sourceUrls: List<String> = emptyList()
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<DefinitionItem> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

data class DefinitionItem(
    val definition: String,
    val example: String? = null,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

sealed interface DictionaryUiState {
    data object Idle : DictionaryUiState
    data object Loading : DictionaryUiState
    data class Success(val entry: DictionaryEntry) : DictionaryUiState
    data class Error(val message: String) : DictionaryUiState
}

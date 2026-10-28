package com.crossbow.translator.data.repository

import com.crossbow.translator.data.api.RetrofitClient
import com.crossbow.translator.domain.model.DefinitionItem
import com.crossbow.translator.domain.model.DictionaryEntry
import com.crossbow.translator.domain.model.DictionaryUiState
import com.crossbow.translator.domain.model.Meaning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryRepository {

    suspend fun lookupWord(word: String, languageCode: String = "en"): DictionaryUiState =
        withContext(Dispatchers.IO) {
            val query = word.trim()
            if (query.isEmpty()) {
                return@withContext DictionaryUiState.Idle
            }

            try {
                val apiLang = if (languageCode == "auto") "en" else languageCode
                val response = RetrofitClient.dictionaryService.getWordDefinition(
                    language = apiLang,
                    word = query
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val rawWord = response.body()!!.first()
                    val audio = rawWord.phonetics?.firstOrNull { !it.audio.isNullOrBlank() }?.audio
                    val phonetic = rawWord.phonetic ?: rawWord.phonetics?.firstOrNull { !it.text.isNullOrBlank() }?.text

                    val meanings = rawWord.meanings?.map { m ->
                        Meaning(
                            partOfSpeech = m.partOfSpeech ?: "definition",
                            definitions = m.definitions?.map { d ->
                                DefinitionItem(
                                    definition = d.definition ?: "",
                                    example = d.example,
                                    synonyms = d.synonyms ?: emptyList(),
                                    antonyms = d.antonyms ?: emptyList()
                                )
                            } ?: emptyList(),
                            synonyms = m.synonyms ?: emptyList(),
                            antonyms = m.antonyms ?: emptyList()
                        )
                    } ?: emptyList()

                    DictionaryUiState.Success(
                        DictionaryEntry(
                            word = rawWord.word ?: query,
                            phonetic = phonetic,
                            audioUrl = audio,
                            meanings = meanings,
                            sourceUrls = rawWord.sourceUrls ?: emptyList()
                        )
                    )
                } else {
                    // Fallback to contextual explanation
                    DictionaryUiState.Error("No dictionary definition found for \"$query\". Check spelling or try a single root word.")
                }
            } catch (e: Exception) {
                DictionaryUiState.Error(
                    e.localizedMessage ?: "Unable to fetch dictionary definitions. Please check your internet connection."
                )
            }
        }
}

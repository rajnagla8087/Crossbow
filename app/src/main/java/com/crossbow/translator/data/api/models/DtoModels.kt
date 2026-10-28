package com.crossbow.translator.data.api.models

import com.google.gson.annotations.SerializedName

// MyMemory API Models
data class MyMemoryResponseDto(
    @SerializedName("responseData")
    val responseData: MyMemoryDataDto?,
    @SerializedName("responseStatus")
    val responseStatus: Int?,
    @SerializedName("responseDetails")
    val responseDetails: String?,
    @SerializedName("matches")
    val matches: List<MyMemoryMatchDto>?
)

data class MyMemoryDataDto(
    @SerializedName("translatedText")
    val translatedText: String?,
    @SerializedName("match")
    val match: Double?
)

data class MyMemoryMatchDto(
    @SerializedName("segment")
    val segment: String?,
    @SerializedName("translation")
    val translation: String?,
    @SerializedName("quality")
    val quality: Any?
)

// Free Dictionary API Models
data class DictionaryWordDto(
    @SerializedName("word")
    val word: String?,
    @SerializedName("phonetic")
    val phonetic: String?,
    @SerializedName("phonetics")
    val phonetics: List<DictionaryPhoneticDto>?,
    @SerializedName("meanings")
    val meanings: List<DictionaryMeaningDto>?,
    @SerializedName("sourceUrls")
    val sourceUrls: List<String>?
)

data class DictionaryPhoneticDto(
    @SerializedName("text")
    val text: String?,
    @SerializedName("audio")
    val audio: String?
)

data class DictionaryMeaningDto(
    @SerializedName("partOfSpeech")
    val partOfSpeech: String?,
    @SerializedName("definitions")
    val definitions: List<DictionaryDefinitionDto>?,
    @SerializedName("synonyms")
    val synonyms: List<String>?,
    @SerializedName("antonyms")
    val antonyms: List<String>?
)

data class DictionaryDefinitionDto(
    @SerializedName("definition")
    val definition: String?,
    @SerializedName("example")
    val example: String?,
    @SerializedName("synonyms")
    val synonyms: List<String>?,
    @SerializedName("antonyms")
    val antonyms: List<String>?
)

// Crossbow Secure Backend Proxy Models
data class BackendTranslateRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("target")
    val target: String
)

data class BackendTranslateResponse(
    @SerializedName("translatedText")
    val translatedText: String,
    @SerializedName("sourceLang")
    val sourceLang: String,
    @SerializedName("targetLang")
    val targetLang: String,
    @SerializedName("provider")
    val provider: String?
)

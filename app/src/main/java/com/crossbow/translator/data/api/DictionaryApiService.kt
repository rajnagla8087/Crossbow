package com.crossbow.translator.data.api

import com.crossbow.translator.data.api.models.DictionaryWordDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {

    @GET("entries/{language}/{word}")
    suspend fun getWordDefinition(
        @Path("language") language: String,
        @Path("word") word: String
    ): Response<List<DictionaryWordDto>>
}

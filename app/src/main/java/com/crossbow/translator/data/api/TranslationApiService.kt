package com.crossbow.translator.data.api

import com.crossbow.translator.data.api.models.BackendTranslateRequest
import com.crossbow.translator.data.api.models.BackendTranslateResponse
import com.crossbow.translator.data.api.models.MyMemoryResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslationApiService {

    // Direct MyMemory Endpoint (High Availability Free & Open Tier)
    @GET("get")
    suspend fun translateWithMyMemory(
        @Query("q") query: String,
        @Query("langpair") langPair: String, // e.g. "en|es" or "auto|fr"
        @Query("de") userEmail: String? = null // Optional registered user email for higher quota
    ): Response<MyMemoryResponseDto>

    // Secure Custom Backend Endpoint (for private key proxying)
    @POST("api/translate")
    suspend fun translateWithBackend(
        @Body request: BackendTranslateRequest
    ): Response<BackendTranslateResponse>
}

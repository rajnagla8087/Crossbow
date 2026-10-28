package com.crossbow.translator.data.repository

import com.crossbow.translator.data.local.dao.FavoriteDao
import com.crossbow.translator.data.local.entity.FavoriteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoritesRepository(private val favoriteDao: FavoriteDao) {

    val allFavorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    val categories: Flow<List<String>> = favoriteDao.getAllCategories()

    fun getFavoritesByCategory(category: String): Flow<List<FavoriteEntity>> {
        return if (category == "All") {
            favoriteDao.getAllFavorites()
        } else {
            favoriteDao.getFavoritesByCategory(category)
        }
    }

    fun searchFavorites(query: String): Flow<List<FavoriteEntity>> {
        return favoriteDao.searchFavorites(query)
    }

    fun isFavorite(sourceText: String, targetLangCode: String): Flow<Boolean> {
        return favoriteDao.isFavorite(sourceText, targetLangCode)
    }

    suspend fun addFavorite(
        sourceText: String,
        translatedText: String,
        sourceLangCode: String,
        targetLangCode: String,
        category: String = "General",
        note: String = ""
    ) = withContext(Dispatchers.IO) {
        favoriteDao.insertFavorite(
            FavoriteEntity(
                sourceText = sourceText,
                translatedText = translatedText,
                sourceLangCode = sourceLangCode,
                targetLangCode = targetLangCode,
                category = category,
                note = note
            )
        )
    }

    suspend fun removeFavorite(id: Long) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(id)
    }

    suspend fun updateFavorite(favorite: FavoriteEntity) = withContext(Dispatchers.IO) {
        favoriteDao.updateFavorite(favorite)
    }
}

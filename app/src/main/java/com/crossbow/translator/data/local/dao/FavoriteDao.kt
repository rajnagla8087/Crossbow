package com.crossbow.translator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.crossbow.translator.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE category = :category ORDER BY timestamp DESC")
    fun getFavoritesByCategory(category: String): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE sourceText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE sourceText = :sourceText AND targetLangCode = :targetLangCode LIMIT 1)")
    fun isFavorite(sourceText: String, targetLangCode: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Update
    suspend fun updateFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE sourceText = :sourceText AND targetLangCode = :targetLangCode")
    suspend fun deleteBySourceAndTarget(sourceText: String, targetLangCode: String)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT DISTINCT category FROM favorites")
    fun getAllCategories(): Flow<List<String>>
}

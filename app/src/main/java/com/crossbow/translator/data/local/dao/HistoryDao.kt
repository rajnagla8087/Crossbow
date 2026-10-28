package com.crossbow.translator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TranslationHistoryEntity>>

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 10): Flow<List<TranslationHistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE sourceText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<TranslationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: TranslationHistoryEntity): Long

    @Delete
    suspend fun deleteHistory(entry: TranslationHistoryEntity)

    @Query("DELETE FROM translation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM translation_history")
    suspend fun clearAll()
}

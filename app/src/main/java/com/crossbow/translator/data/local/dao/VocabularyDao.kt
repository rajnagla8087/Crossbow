package com.crossbow.translator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.crossbow.translator.data.local.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary WHERE level = :level ORDER BY id ASC")
    fun getVocabularyByLevel(level: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary ORDER BY RANDOM() LIMIT :limit")
    fun getRandomDailyWords(limit: Int = 5): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE isMastered = 1")
    fun getMasteredWords(): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<VocabularyEntity>)

    @Update
    suspend fun updateWord(word: VocabularyEntity)

    @Query("UPDATE vocabulary SET isMastered = :mastered WHERE id = :id")
    suspend fun setMastered(id: Long, mastered: Boolean)
}

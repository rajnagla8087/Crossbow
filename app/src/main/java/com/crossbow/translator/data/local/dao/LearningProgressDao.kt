package com.crossbow.translator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.crossbow.translator.data.local.entity.LearningProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {

    @Query("SELECT * FROM learning_progress WHERE id = 1 LIMIT 1")
    fun getProgress(): Flow<LearningProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: LearningProgressEntity)

    @Query("UPDATE learning_progress SET quizHighScore = :score WHERE id = 1 AND :score > quizHighScore")
    suspend fun updateHighScore(score: Int)

    @Query("UPDATE learning_progress SET dailyGoalCompleted = dailyGoalCompleted + 1 WHERE id = 1")
    suspend fun incrementDailyGoal()
}

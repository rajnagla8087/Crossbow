package com.crossbow.translator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translation: String,
    val languageCode: String,
    val definition: String = "",
    val phonetic: String = "",
    val example: String = "",
    val level: String = "BEGINNER", // BEGINNER, INTERMEDIATE, ADVANCED
    val isMastered: Boolean = false,
    val timesReviewed: Int = 0
)

@Entity(tableName = "learning_progress")
data class LearningProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val streakDays: Int = 1,
    val wordsMastered: Int = 0,
    val dailyGoalTotal: Int = 10,
    val dailyGoalCompleted: Int = 4,
    val quizHighScore: Int = 0,
    val totalQuizzesTaken: Int = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

package com.crossbow.translator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLangCode: String,
    val targetLangCode: String,
    val note: String = "",
    val category: String = "General", // e.g. General, Travel, Business, Food, Social
    val timestamp: Long = System.currentTimeMillis()
)

package com.crossbow.translator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crossbow.translator.data.local.dao.FavoriteDao
import com.crossbow.translator.data.local.dao.HistoryDao
import com.crossbow.translator.data.local.dao.LearningProgressDao
import com.crossbow.translator.data.local.dao.VocabularyDao
import com.crossbow.translator.data.local.entity.FavoriteEntity
import com.crossbow.translator.data.local.entity.LearningProgressEntity
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import com.crossbow.translator.data.local.entity.VocabularyEntity

@Database(
    entities = [
        TranslationHistoryEntity::class,
        FavoriteEntity::class,
        VocabularyEntity::class,
        LearningProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CrossbowDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun learningProgressDao(): LearningProgressDao

    companion object {
        @Volatile
        private var INSTANCE: CrossbowDatabase? = null

        fun getDatabase(context: Context): CrossbowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrossbowDatabase::class.java,
                    "crossbow_translator.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

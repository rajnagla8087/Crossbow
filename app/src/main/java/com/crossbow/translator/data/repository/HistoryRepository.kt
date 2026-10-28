package com.crossbow.translator.data.repository

import com.crossbow.translator.data.local.dao.HistoryDao
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: Flow<List<TranslationHistoryEntity>> = historyDao.getAllHistory()

    val recentHistory: Flow<List<TranslationHistoryEntity>> = historyDao.getRecentHistory(10)

    fun searchHistory(query: String): Flow<List<TranslationHistoryEntity>> {
        return historyDao.searchHistory(query)
    }

    suspend fun deleteHistory(id: Long) = withContext(Dispatchers.IO) {
        historyDao.deleteById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAll()
    }
}

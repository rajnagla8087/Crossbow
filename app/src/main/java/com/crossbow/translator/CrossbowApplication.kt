package com.crossbow.translator

import android.app.Application
import com.crossbow.translator.data.local.CrossbowDatabase
import com.crossbow.translator.data.local.datastore.AppPreferences
import com.crossbow.translator.data.repository.DictionaryRepository
import com.crossbow.translator.data.repository.FavoritesRepository
import com.crossbow.translator.data.repository.HistoryRepository
import com.crossbow.translator.data.repository.PracticeRepository
import com.crossbow.translator.data.repository.TranslationRepository
import com.crossbow.translator.util.CameraHelper
import com.crossbow.translator.util.ClipboardHelper
import com.crossbow.translator.util.NetworkMonitor
import com.crossbow.translator.util.SpeechRecognizerHelper
import com.crossbow.translator.util.TextToSpeechHelper

class CrossbowApplication : Application() {

    lateinit var database: CrossbowDatabase
        private set

    lateinit var appPreferences: AppPreferences
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    lateinit var clipboardHelper: ClipboardHelper
        private set

    lateinit var speechRecognizerHelper: SpeechRecognizerHelper
        private set

    lateinit var textToSpeechHelper: TextToSpeechHelper
        private set

    lateinit var cameraHelper: CameraHelper
        private set

    lateinit var translationRepository: TranslationRepository
        private set

    lateinit var dictionaryRepository: DictionaryRepository
        private set

    lateinit var practiceRepository: PracticeRepository
        private set

    lateinit var favoritesRepository: FavoritesRepository
        private set

    lateinit var historyRepository: HistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = CrossbowDatabase.getDatabase(this)
        appPreferences = AppPreferences(this)
        networkMonitor = NetworkMonitor(this)
        clipboardHelper = ClipboardHelper(this)
        speechRecognizerHelper = SpeechRecognizerHelper(this)
        textToSpeechHelper = TextToSpeechHelper(this)
        cameraHelper = CameraHelper(this)

        historyRepository = HistoryRepository(database.historyDao())
        favoritesRepository = FavoritesRepository(database.favoriteDao())
        practiceRepository = PracticeRepository(database.vocabularyDao(), database.learningProgressDao())
        dictionaryRepository = DictionaryRepository()

        translationRepository = TranslationRepository(
            historyDao = database.historyDao(),
            favoriteDao = database.favoriteDao(),
            appPreferences = appPreferences,
            networkMonitor = networkMonitor
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        textToSpeechHelper.shutdown()
        speechRecognizerHelper.stopListening()
    }
}

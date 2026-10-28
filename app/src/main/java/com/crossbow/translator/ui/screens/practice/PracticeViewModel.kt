package com.crossbow.translator.ui.screens.practice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.domain.model.LearningLevel
import com.crossbow.translator.domain.model.PracticeProgress
import com.crossbow.translator.domain.model.QuizQuestion
import com.crossbow.translator.domain.model.VocabularyCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val practiceRepo = app.practiceRepository
    val ttsHelper = app.textToSpeechHelper

    private val _selectedLevel = MutableStateFlow(LearningLevel.BEGINNER)
    val selectedLevel: StateFlow<LearningLevel> = _selectedLevel.asStateFlow()

    private val _vocabularyList = MutableStateFlow<List<VocabularyCard>>(emptyList())
    val vocabularyList: StateFlow<List<VocabularyCard>> = _vocabularyList.asStateFlow()

    val progress: StateFlow<PracticeProgress> = practiceRepo.getProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PracticeProgress())

    // Quiz Mode state
    private val _isQuizActive = MutableStateFlow(false)
    val isQuizActive: StateFlow<Boolean> = _isQuizActive.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    init {
        viewModelScope.launch {
            practiceRepo.initializeDefaultVocabularyIfNeeded()
            loadVocabularyForCurrentLevel()
        }
    }

    fun setLevel(level: LearningLevel) {
        _selectedLevel.value = level
        loadVocabularyForCurrentLevel()
    }

    private fun loadVocabularyForCurrentLevel() {
        viewModelScope.launch {
            practiceRepo.getVocabularyByLevel(_selectedLevel.value).collect { list ->
                _vocabularyList.value = list
            }
        }
    }

    fun startQuiz() {
        viewModelScope.launch {
            val questions = practiceRepo.generateQuiz(_selectedLevel.value)
            if (questions.isNotEmpty()) {
                _quizQuestions.value = questions
                _currentQuestionIndex.value = 0
                _currentScore.value = 0
                _selectedAnswer.value = null
                _isAnswerSubmitted.value = false
                _isQuizFinished.value = false
                _isQuizActive.value = true
            }
        }
    }

    fun selectAnswer(answer: String) {
        if (!_isAnswerSubmitted.value) {
            _selectedAnswer.value = answer
        }
    }

    fun submitAnswer() {
        if (_selectedAnswer.value == null || _isAnswerSubmitted.value) return
        _isAnswerSubmitted.value = true

        val currentQ = _quizQuestions.value.getOrNull(_currentQuestionIndex.value) ?: return
        if (_selectedAnswer.value.equals(currentQ.correctAnswer, ignoreCase = true)) {
            _currentScore.value += 20
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value + 1 < _quizQuestions.value.size) {
            _currentQuestionIndex.value += 1
            _selectedAnswer.value = null
            _isAnswerSubmitted.value = false
        } else {
            // Finish Quiz
            _isQuizFinished.value = true
            viewModelScope.launch {
                practiceRepo.saveQuizScore(_currentScore.value)
            }
        }
    }

    fun exitQuiz() {
        _isQuizActive.value = false
        _isQuizFinished.value = false
        _selectedAnswer.value = null
        _isAnswerSubmitted.value = false
    }

    fun toggleMastered(card: VocabularyCard) {
        viewModelScope.launch {
            practiceRepo.markWordMastered(card.id, !card.isMastered)
        }
    }

    fun pronounceWord(word: String, langCode: String) {
        ttsHelper.speak(word, langCode)
    }
}

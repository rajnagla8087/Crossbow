package com.crossbow.translator.domain.model

enum class LearningLevel(val title: String, val description: String) {
    BEGINNER("Beginner", "Essential everyday vocabulary & greetings"),
    INTERMEDIATE("Intermediate", "Conversational phrases, travel & work"),
    ADVANCED("Advanced", "Nuanced idioms, professional & cultural terms")
}

data class VocabularyCard(
    val id: Long = 0,
    val word: String,
    val translation: String,
    val languageCode: String,
    val definition: String = "",
    val phonetic: String = "",
    val example: String = "",
    val level: LearningLevel = LearningLevel.BEGINNER,
    val isMastered: Boolean = false
)

sealed class QuizQuestion(
    open val id: Int,
    open val questionText: String,
    open val correctAnswer: String,
    open val targetLanguage: String
) {
    data class MultipleChoice(
        override val id: Int,
        override val questionText: String,
        override val correctAnswer: String,
        override val targetLanguage: String,
        val options: List<String>
    ) : QuizQuestion(id, questionText, correctAnswer, targetLanguage)

    data class FillInBlank(
        override val id: Int,
        override val questionText: String,
        override val correctAnswer: String,
        override val targetLanguage: String,
        val hint: String
    ) : QuizQuestion(id, questionText, correctAnswer, targetLanguage)

    data class SentenceTranslation(
        override val id: Int,
        override val questionText: String,
        override val correctAnswer: String,
        override val targetLanguage: String
    ) : QuizQuestion(id, questionText, correctAnswer, targetLanguage)
}

data class PracticeProgress(
    val streakDays: Int = 1,
    val wordsMastered: Int = 0,
    val dailyGoalTotal: Int = 10,
    val dailyGoalCompleted: Int = 4,
    val quizHighScore: Int = 0,
    val totalQuizzesTaken: Int = 0
)

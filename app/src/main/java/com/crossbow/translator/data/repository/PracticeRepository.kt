package com.crossbow.translator.data.repository

import com.crossbow.translator.data.local.dao.LearningProgressDao
import com.crossbow.translator.data.local.dao.VocabularyDao
import com.crossbow.translator.data.local.entity.LearningProgressEntity
import com.crossbow.translator.data.local.entity.VocabularyEntity
import com.crossbow.translator.domain.model.LearningLevel
import com.crossbow.translator.domain.model.PracticeProgress
import com.crossbow.translator.domain.model.QuizQuestion
import com.crossbow.translator.domain.model.VocabularyCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PracticeRepository(
    private val vocabularyDao: VocabularyDao,
    private val progressDao: LearningProgressDao
) {

    suspend fun initializeDefaultVocabularyIfNeeded() = withContext(Dispatchers.IO) {
        val count = vocabularyDao.getCount()
        if (count == 0) {
            val starterWords = listOf(
                // Beginner Spanish
                VocabularyEntity(
                    word = "Hola",
                    translation = "Hello",
                    languageCode = "es",
                    definition = "A greeting used upon meeting someone",
                    phonetic = "/ˈo.la/",
                    example = "¡Hola! ¿Cómo estás?",
                    level = "BEGINNER"
                ),
                VocabularyEntity(
                    word = "Gracias",
                    translation = "Thank you",
                    languageCode = "es",
                    definition = "An expression of gratitude",
                    phonetic = "/ˈɡɾa.sjas/",
                    example = "Muchas gracias por tu ayuda.",
                    level = "BEGINNER"
                ),
                VocabularyEntity(
                    word = "Por favor",
                    translation = "Please",
                    languageCode = "es",
                    definition = "A polite request expression",
                    phonetic = "/poɾ faˈβoɾ/",
                    example = "¿Me pasas el agua, por favor?",
                    level = "BEGINNER"
                ),
                VocabularyEntity(
                    word = "Amigo",
                    translation = "Friend",
                    languageCode = "es",
                    definition = "A person with whom one has a bond of mutual affection",
                    phonetic = "/aˈmi.ɣo/",
                    example = "Él es mi mejor amigo.",
                    level = "BEGINNER"
                ),
                VocabularyEntity(
                    word = "Buenos días",
                    translation = "Good morning",
                    languageCode = "es",
                    definition = "Morning greeting",
                    phonetic = "/ˈbwe.noz ˈði.as/",
                    example = "¡Buenos días a todos!",
                    level = "BEGINNER"
                ),

                // Intermediate Spanish
                VocabularyEntity(
                    word = "Desafío",
                    translation = "Challenge",
                    languageCode = "es",
                    definition = "A demanding or stimulating situation",
                    phonetic = "/de.saˈfi.o/",
                    example = "Aprender un nuevo idioma es un gran desafío.",
                    level = "INTERMEDIATE"
                ),
                VocabularyEntity(
                    word = "Oportunidad",
                    translation = "Opportunity",
                    languageCode = "es",
                    definition = "A set of circumstances that makes it possible to do something",
                    phonetic = "/o.poɾ.tu.niˈðað/",
                    example = "Es una excelente oportunidad para viajar.",
                    level = "INTERMEDIATE"
                ),
                VocabularyEntity(
                    word = "Desarrollar",
                    translation = "To develop",
                    languageCode = "es",
                    definition = "Grow or cause to grow and become more mature or advanced",
                    phonetic = "/de.sa.roˈʎaɾ/",
                    example = "Queremos desarrollar nuevas habilidades.",
                    level = "INTERMEDIATE"
                ),

                // Advanced Spanish
                VocabularyEntity(
                    word = "Inconmensurable",
                    translation = "Immeasurable",
                    languageCode = "es",
                    definition = "Too large, extensive, or extreme to measure",
                    phonetic = "/iŋ.kom.men.suˈɾa.βle/",
                    example = "El impacto de la tecnología es inconmensurable.",
                    level = "ADVANCED"
                ),
                VocabularyEntity(
                    word = "Efímero",
                    translation = "Ephemeral / Fleeting",
                    languageCode = "es",
                    definition = "Lasting for a very short time",
                    phonetic = "/eˈfi.me.ɾo/",
                    example = "La belleza del atardecer fue efímera.",
                    level = "ADVANCED"
                ),
                VocabularyEntity(
                    word = "Resiliencia",
                    translation = "Resilience",
                    languageCode = "es",
                    definition = "Capacity to recover quickly from difficulties",
                    phonetic = "/re.siˈljen.sja/",
                    example = "Demostraron una notable resiliencia ante la adversidad.",
                    level = "ADVANCED"
                )
            )
            vocabularyDao.insertAll(starterWords)

            // Initial progress entity
            progressDao.insertOrUpdate(
                LearningProgressEntity(
                    id = 1,
                    streakDays = 3,
                    wordsMastered = 5,
                    dailyGoalTotal = 10,
                    dailyGoalCompleted = 6,
                    quizHighScore = 80,
                    totalQuizzesTaken = 4
                )
            )
        }
    }

    fun getVocabularyByLevel(level: LearningLevel): Flow<List<VocabularyCard>> {
        return vocabularyDao.getVocabularyByLevel(level.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getDailyWords(limit: Int = 5): Flow<List<VocabularyCard>> {
        return vocabularyDao.getRandomDailyWords(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getProgress(): Flow<PracticeProgress> {
        return progressDao.getProgress().map { entity ->
            if (entity != null) {
                PracticeProgress(
                    streakDays = entity.streakDays,
                    wordsMastered = entity.wordsMastered,
                    dailyGoalTotal = entity.dailyGoalTotal,
                    dailyGoalCompleted = entity.dailyGoalCompleted,
                    quizHighScore = entity.quizHighScore,
                    totalQuizzesTaken = entity.totalQuizzesTaken
                )
            } else {
                PracticeProgress()
            }
        }
    }

    suspend fun markWordMastered(id: Long, mastered: Boolean) = withContext(Dispatchers.IO) {
        vocabularyDao.setMastered(id, mastered)
        progressDao.incrementDailyGoal()
    }

    suspend fun saveQuizScore(score: Int) = withContext(Dispatchers.IO) {
        progressDao.updateHighScore(score)
    }

    suspend fun generateQuiz(level: LearningLevel): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val words = vocabularyDao.getVocabularyByLevel(level.name).first()
        if (words.size < 3) return@withContext emptyList()

        val questions = mutableListOf<QuizQuestion>()

        words.forEachIndexed { index, item ->
            // Distractors
            val wrongOptions = words.filter { it.id != item.id }.map { it.translation }.shuffled().take(3)
            val allOptions = (wrongOptions + item.translation).shuffled()

            questions.add(
                QuizQuestion.MultipleChoice(
                    id = index + 1,
                    questionText = "What is the translation of \"${item.word}\"?",
                    correctAnswer = item.translation,
                    targetLanguage = item.languageCode,
                    options = allOptions
                )
            )

            if (item.example.isNotBlank()) {
                questions.add(
                    QuizQuestion.FillInBlank(
                        id = index + 100,
                        questionText = item.example.replace(item.word, "_______", ignoreCase = true),
                        correctAnswer = item.word,
                        targetLanguage = item.languageCode,
                        hint = "Meaning: ${item.translation}"
                    )
                )
            }
        }

        questions.shuffled().take(6)
    }

    private fun VocabularyEntity.toDomain(): VocabularyCard {
        val parsedLevel = try {
            LearningLevel.valueOf(level)
        } catch (_: Exception) {
            LearningLevel.BEGINNER
        }
        return VocabularyCard(
            id = id,
            word = word,
            translation = translation,
            languageCode = languageCode,
            definition = definition,
            phonetic = phonetic,
            example = example,
            level = parsedLevel,
            isMastered = isMastered
        )
    }
}

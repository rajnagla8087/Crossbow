package com.crossbow.translator.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossbow.translator.domain.model.LearningLevel
import com.crossbow.translator.domain.model.QuizQuestion
import com.crossbow.translator.domain.model.VocabularyCard
import com.crossbow.translator.ui.components.GradientButton
import com.crossbow.translator.ui.theme.BrandAmber
import com.crossbow.translator.ui.theme.BrandCyan
import com.crossbow.translator.ui.theme.BrandEmerald
import com.crossbow.translator.ui.theme.BrandIndigo
import com.crossbow.translator.ui.theme.BrandRose
import com.crossbow.translator.ui.theme.CrossbowAccentGradient
import com.crossbow.translator.ui.theme.GoldAccentGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: PracticeViewModel
) {
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val vocabList by viewModel.vocabularyList.collectAsState()
    val progress by viewModel.progress.collectAsState()

    val isQuizActive by viewModel.isQuizActive.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val currentQuestionIdx by viewModel.currentQuestionIndex.collectAsState()
    val currentScore by viewModel.currentScore.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language Practice", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isQuizActive) {
            // Interactive Quiz Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
            ) {
                if (isQuizFinished) {
                    // Result Screen
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎉", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Quiz Completed!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your Final Score: $currentScore points",
                            style = MaterialTheme.typography.titleLarge.copy(color = BrandAmber, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        GradientButton(
                            text = "Finish & Return",
                            onClick = { viewModel.exitQuiz() }
                        )
                    }
                } else {
                    val q = quizQuestions.getOrNull(currentQuestionIdx)
                    if (q != null) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Quiz Progress & Score
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question ${currentQuestionIdx + 1}/${quizQuestions.size}",
                                    fontWeight = FontWeight.Bold,
                                    color = BrandIndigo
                                )
                                Text(
                                    text = "Score: $currentScore",
                                    fontWeight = FontWeight.Bold,
                                    color = BrandAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (currentQuestionIdx + 1).toFloat() / quizQuestions.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BrandIndigo
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = q.questionText,
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            when (q) {
                                is QuizQuestion.MultipleChoice -> {
                                    q.options.forEach { opt ->
                                        val isSelected = selectedAnswer == opt
                                        val isCorrect = opt.equals(q.correctAnswer, ignoreCase = true)
                                        val optionBg = when {
                                            isAnswerSubmitted && isCorrect -> BrandEmerald.copy(alpha = 0.2f)
                                            isAnswerSubmitted && isSelected && !isCorrect -> BrandRose.copy(alpha = 0.2f)
                                            isSelected -> BrandIndigo.copy(alpha = 0.15f)
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                                        val optionBorder = when {
                                            isAnswerSubmitted && isCorrect -> BrandEmerald
                                            isAnswerSubmitted && isSelected && !isCorrect -> BrandRose
                                            isSelected -> BrandIndigo
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(optionBg)
                                                .border(1.5.dp, optionBorder, RoundedCornerShape(16.dp))
                                                .clickable(enabled = !isAnswerSubmitted) {
                                                    viewModel.selectAnswer(opt)
                                                }
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = opt,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }
                                    }
                                }
                                is QuizQuestion.FillInBlank -> {
                                    Text(
                                        text = "Hint: ${q.hint}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = BrandCyan)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (selectedAnswer == q.correctAnswer) BrandIndigo.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surface
                                            )
                                            .border(1.5.dp, BrandIndigo, RoundedCornerShape(16.dp))
                                            .clickable(enabled = !isAnswerSubmitted) {
                                                viewModel.selectAnswer(q.correctAnswer)
                                            }
                                            .padding(16.dp)
                                    ) {
                                        Text(text = "Tap to choose: ${q.correctAnswer}", fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {}
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (!isAnswerSubmitted) {
                                GradientButton(
                                    text = "Submit Answer",
                                    onClick = { viewModel.submitAnswer() },
                                    enabled = selectedAnswer != null
                                )
                            } else {
                                GradientButton(
                                    text = if (currentQuestionIdx + 1 == quizQuestions.size) "Show Results" else "Next Question →",
                                    onClick = { viewModel.nextQuestion() },
                                    gradient = BrandEmerald.run { CrossbowAccentGradient }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Main Practice Dashboard & Flashcard Decks
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Streak & Progress Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GoldAccentGradient.copy(alpha = 0.12f))
                            .border(1.dp, BrandAmber.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = "Streak",
                                        tint = BrandAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${progress.streakDays} Days Streak",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = BrandAmber
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "High Score",
                                        tint = BrandAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Best: ${progress.quizHighScore} pts",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrandAmber
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Daily Goal: ${progress.dailyGoalCompleted}/${progress.dailyGoalTotal} Words Practiced",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (progress.dailyGoalCompleted.toFloat() / progress.dailyGoalTotal).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BrandAmber
                            )
                        }
                    }
                }

                // Level Filter Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LearningLevel.entries.forEach { level ->
                            val isSelected = level == selectedLevel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) BrandIndigo else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) BrandIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { viewModel.setLevel(level) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level.title,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Start Quiz Button
                item {
                    GradientButton(
                        text = "Start Practice Quiz (Level: ${selectedLevel.title})",
                        icon = Icons.Default.PlayArrow,
                        onClick = { viewModel.startQuiz() }
                    )
                }

                // Vocabulary Flashcards List Header
                item {
                    Text(
                        text = "Interactive Flashcards (Tap to flip)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(vocabList, key = { it.id }) { card ->
                    FlashcardItem(
                        card = card,
                        onPronounce = { viewModel.pronounceWord(card.word, card.languageCode) },
                        onToggleMastered = { viewModel.toggleMastered(card) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun FlashcardItem(
    card: VocabularyCard,
    onPronounce: () -> Unit,
    onToggleMastered: () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (card.isMastered) BrandEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(22.dp)
            )
            .clickable { isFlipped = !isFlipped }
            .padding(18.dp)
    ) {
        if (rotation <= 90f) {
            // Front Side
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandIndigo.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = card.languageCode.uppercase(), color = BrandIndigo, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Row {
                        IconButton(onClick = onPronounce, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = BrandCyan)
                        }
                        IconButton(onClick = onToggleMastered, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Mastered",
                                tint = if (card.isMastered) BrandEmerald else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = card.word,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )

                if (card.phonetic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.phonetic,
                        color = BrandIndigo,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap to reveal English translation ↻",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Back Side
            Column(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                Text(
                    text = "TRANSLATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandEmerald
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.translation,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BrandEmerald)
                )
                if (card.definition.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = card.definition,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (card.example.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Example: “${card.example}”",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

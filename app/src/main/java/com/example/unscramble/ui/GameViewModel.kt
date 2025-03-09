package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords

class GameViewModel : ViewModel() {
    var uiState: GameUiState by mutableStateOf(GameUiState())
        private set

    private lateinit var currentWord: String

    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        return when (usedWords.contains(currentWord)) {
            true -> pickRandomWordAndShuffle()
            false -> {
                usedWords.add(currentWord)
                shuffleCurrentWord(currentWord)
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun checkUserGuess() {
        when (userGuess.equals(currentWord, ignoreCase = true)) {
            true -> updateGameState(uiState.score.plus(SCORE_INCREASE))
            false -> uiState.copy(isGuessedWordWrong = true)
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        when (usedWords.size < MAX_NO_OF_WORDS) {
            true -> uiState.copy(
                isGuessedWordWrong = false,
                score = updatedScore,
                isGameOver = true
            )

            false -> uiState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = uiState.currentWordCount.inc()
            )
        }
    }

    fun skipWord() {
        updateGameState(uiState.score)
        updateUserGuess("")
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun resetGame() {
        usedWords.clear()
        uiState = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }
}
package com.example.lexiapp.ui.games.correctword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexiapp.domain.model.gameResult.CorrectWordGameResult
import com.example.lexiapp.domain.useCases.CorrectWordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CorrectWordViewModel @Inject constructor(
    private val useCases: CorrectWordUseCases
) : ViewModel() {

    init {
        generateWords()
    }

    var correctWord = ""

    private var _basicWords = MutableLiveData<List<String>>()
    var basicWords = _basicWords as LiveData<List<String>>

    fun generateWords() {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getWords()
                .collect {
                    withContext(Dispatchers.Main) {
                        _basicWords.value = it
                        correctWord = it[0]
                    }
                }
        }
    }

    fun validateAnswer(selecterWord: String): Boolean {
        val success = selecterWord == correctWord
        viewModelScope.launch {
            useCases.saveAnswer(
                CorrectWordGameResult(
                    email = "",
                    wordSelected = selecterWord,
                    correctWord = correctWord,
                    success = success,
                    date = null
                )
            )
        }
        return success
    }

    fun checkIfObjectivesHasBeenCompleted(game: String, typeGame: String, gameName: String) {
        viewModelScope.launch {
            useCases.generateNotificationForObjectives(game, typeGame, gameName)
        }
    }
}


package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {
    private lateinit var gameSettings: GameSettings

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _isEnoughCountRightAnswers = MutableLiveData<Boolean>()
    val isEnoughCountRightAnswers: LiveData<Boolean>
        get() = _isEnoughCountRightAnswers

    private val _isEnoughPercentRightAnswers = MutableLiveData<Boolean>()
    val isEnoughPercentRightAnswers: LiveData<Boolean>
        get() = _isEnoughPercentRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    init {
        startGame()
    }

    private fun startGame() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers

        startTimer()
        generateQuestion()
        updateProgressQuestion()
    }

    private fun updateProgressQuestion() {
        val percent = ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )

        _isEnoughCountRightAnswers.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _isEnoughPercentRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    fun chooseAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
        updateProgressQuestion()
        generateQuestion()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLISECONDS_IN_SECONDS,
            MILLISECONDS_IN_SECONDS
        ) {
            override fun onTick(ms: Long) {
                _formattedTime.value = convertMS(ms)
            }

            override fun onFinish() {
                finishGame()
            }

        }
        timer?.start()
    }

    private fun convertMS(ms: Long): String {
        val seconds = ms / MILLISECONDS_IN_SECONDS
        val minutes = seconds / SEC_IN_MIN
        val leftSeconds = seconds - (minutes * SEC_IN_MIN)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            winner = isEnoughCountRightAnswers.value == true && isEnoughPercentRightAnswers.value == true,
            countOfRightAnswers,
            countOfQuestions,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLISECONDS_IN_SECONDS = 1000L
        private const val SEC_IN_MIN = 60
    }
}
package com.example.composition.presentation

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult

interface OnOptionClickListener {
    fun onOptionClick(option: Int)
}

@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}

@BindingAdapter("scoreAnswers")
fun bindScoreAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        count
    )
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        count
    )
}

@BindingAdapter("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult)
    )
}

@BindingAdapter("resultImage")
fun bindResultImage(imageView: ImageView, isWinner: Boolean) {
    imageView.setImageResource(if (isWinner) {
        R.drawable.ic_smile
    } else R.drawable.ic_sad)
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}

//FRAGMENT_GAME.XML
@BindingAdapter("numberAsText")
fun bindSum(textView: TextView, number: Int) {
    textView.text = number.toString()
}

@BindingAdapter("enoughCount")
fun bindEnoughCount(textView: TextView, isEnough: Boolean) {
    val colorResourceId = if (isEnough) {
        android.R.color.holo_green_light
    } else android.R.color.holo_red_light
    val color = ContextCompat.getColor(textView.context, colorResourceId)

    textView.setTextColor(color)
}

@BindingAdapter("enoughPercent")
fun bindEnoughPercent(progressBar: ProgressBar, isEnough: Boolean) {
    val colorResourceId = if (isEnough) {
        android.R.color.holo_green_light
    } else android.R.color.holo_red_light
    val color = ContextCompat.getColor(progressBar.context, colorResourceId)

    progressBar.progressTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("onOptionClickListener")
fun bindOptionClickListener(textView: TextView, callback: OnOptionClickListener) {
    textView.setOnClickListener {
        callback.onOptionClick(textView.text.toString().toInt())
    }
}
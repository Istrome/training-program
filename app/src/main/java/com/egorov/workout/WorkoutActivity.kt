package com.egorov.workout

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.content.Intent

class WorkoutActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var exerciseTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var completeButton: Button
    private lateinit var imageView: ImageView

    private var isSingleExercise = false

    private var exerciseIndex = 0
    private var timer: CountDownTimer? = null

    private val exercises = listOf(
        Exercise(
            "Отжимания",
            "Поставьте руки на пол на ширине плеч. Опускайте туловище, пока грудь почти не коснется пола. Поднимайте туловище назад, пока руки не будут полностью вытянуты.",
            30,
            "https://i.pinimg.com/originals/9c/01/d2/9c01d2cd204f20d3e3ac937ff0340c37.gif"
        ),
        Exercise(
            "Приседания",
            "Встаньте, ноги на ширине плеч. Опустите туловище как можно ниже, отведя бедра назад и согнув колени. Вернитесь в исходноее положение.",
            45,
            "https://reminder.media/storage/images/posts/tOq/tOq2TugvE6m67b74217bf368.gif"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        titleTextView = findViewById(R.id.titleTextView)
        exerciseTextView = findViewById(R.id.exerciseTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        completeButton = findViewById(R.id.completeButton)
        imageView = findViewById(R.id.imageView)

        startButton.setOnClickListener { startWorkout() }
        completeButton.setOnClickListener { completeExercise() }

        val searchBtn = findViewById<Button>(R.id.searchButton)

        searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        val selectedExercise = intent.getStringExtra("exercise")

        if (selectedExercise != null) {
            val index = exercises.indexOfFirst { it.name == selectedExercise }

            if (index != -1) {
                exerciseIndex = index
                isSingleExercise = true
                val ex = exercises[exerciseIndex]
                titleTextView.text = "Выбрано упражнение"
                exerciseTextView.text = ex.name
                descriptionTextView.text = ex.description
                Glide.with(this)
                    .asGif()
                    .load(ex.gifImageUrl)
                    .into(imageView)
                timerTextView.text = formatTime(ex.durationInSeconds)
                startButton.isEnabled = true
                completeButton.isEnabled = false
            }
        }
    }
    private fun startWorkout() {
        titleTextView.text = "Тренировка началась"
        startButton.isEnabled = false
        if (!isSingleExercise) {
            exerciseIndex = 0
            startNextExercise()
        } else {
            startNextExercise()
        }
    }

    private fun completeExercise() {
        timer?.cancel()


        if (isSingleExercise) {
            titleTextView.text = "Тренировка завершена"
            exerciseTextView.text = ""
            descriptionTextView.text = ""
            timerTextView.text = "00:00"

            imageView.setImageDrawable(null)

            startButton.isEnabled = true
            completeButton.isEnabled = false

            return
        }

        startNextExercise()
    }

    private fun startNextExercise() {
        timer?.cancel()
        if (exerciseIndex < exercises.size) {
            val ex = exercises[exerciseIndex]
            exerciseTextView.text = ex.name
            descriptionTextView.text = ex.description

            Glide.with(this)
                .asGif()
                .load(ex.gifImageUrl)
                .into(imageView)

            timerTextView.text = formatTime(ex.durationInSeconds)

            timer = object : CountDownTimer(ex.durationInSeconds * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerTextView.text = formatTime((millisUntilFinished / 1000).toInt())
                }
                override fun onFinish() {
                    completeButton.isEnabled = true
                }
            }.start()
            if (!isSingleExercise){
                exerciseIndex++
            }
        } else {
            exerciseTextView.text = "Тренировка завершена"
            startButton.isEnabled = true
            completeButton.isEnabled = false
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", minutes, sec)
    }

}

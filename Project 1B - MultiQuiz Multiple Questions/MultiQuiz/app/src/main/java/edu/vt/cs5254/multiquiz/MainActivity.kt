package edu.vt.cs5254.multiquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.vt.cs5254.multiquiz.databinding.ActivityMainBinding
import android.widget.Button
import androidx.activity.viewModels

//private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    // Name: Layne Pu
    // PID: laynepu

    private lateinit var binding: ActivityMainBinding

    private lateinit var answerButtonList: List<Button>

    private val quizVm: QuizViewModel by viewModels()

    private lateinit var buttonAnswerZip: List<Pair<Button, Answer>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerButtonList = listOf(
            binding.answer0Button,
            binding.answer1Button,
            binding.answer2Button,
            binding.answer3Button
        )

        binding.hintButton.setOnClickListener {
            quizVm.currentQuestionAnswerList.filter { !it.isCorrect && it.isEnabled }
                .random()
                .let {
                    it.isSelected = false
                    it.isEnabled = false
            }
            updateViews()
        }

        binding.submitButton.setOnClickListener {
            quizVm.moveToNextQuestion()
            updateQuestion()
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        buttonAnswerZip = answerButtonList.zip(quizVm.currentQuestionAnswerList)

        buttonAnswerZip.forEach { (button, answer) ->
            button.setOnClickListener {
                answer.isSelected = !answer.isSelected
                quizVm.currentQuestionAnswerList.filterNot { it == answer }
                    .forEach { ansToBeDeselected ->
                        ansToBeDeselected.isSelected = false
                }
                updateViews()
            }
            button.setText(answer.textResId)
        }

        updateViews()

        binding.questionTextView.setText(quizVm.currentQuestionText)
    }

    private fun updateViews() {
        buttonAnswerZip.forEach { (button, answer) ->
            button.isSelected = answer.isSelected
            button.isEnabled = answer.isEnabled
            button.updateColor()
        }
        binding.submitButton.isEnabled = quizVm.currentQuestionAnswerList.any { it.isSelected }
        binding.hintButton.isEnabled = quizVm.currentQuestionAnswerList.filter { it.isEnabled }.any { !it.isCorrect }
    }
}
package edu.vt.cs5254.multiquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.vt.cs5254.multiquiz.databinding.ActivityMainBinding
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

//private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    // Name: Layne Pu
    // PID: laynepu

    private lateinit var binding: ActivityMainBinding

    private lateinit var answerButtonList: List<Button>

    private val quizVM: QuizViewModel by viewModels()

    private lateinit var buttonAnswerZip: List<Pair<Button, Answer>>

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if(result.data?.getBooleanExtra(EXTRA_RESET_ALL, false) == true) {
                quizVM.resetAllQuizzes()
                updateQuestion()
            }
        }
    }

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
            quizVM.currentQuestionAnswerList.filter { !it.isCorrect && it.isEnabled }
                .random()
                .let {
                    it.isSelected = false
                    it.isEnabled = false
            }
            quizVM.numOfHintUsed++
            updateViews()
        }

        binding.submitButton.setOnClickListener {
            if (quizVM.hasMoreQuestions()) {
                quizVM.moveToNextQuestion()
                updateQuestion()
            } else {
                val intent = ResultActivity.newIntent(this, quizVM.numOfCorrectAnswers, quizVM.numOfQuestions, quizVM.numOfHintUsed)
                resultLauncher.launch(intent)
            }
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        buttonAnswerZip = answerButtonList.zip(quizVM.currentQuestionAnswerList)

        buttonAnswerZip.forEach { (button, answer) ->
            button.setOnClickListener {
                answer.isSelected = !answer.isSelected
                quizVM.currentQuestionAnswerList.filterNot { it == answer }
                    .forEach { ansToBeDeselected ->
                        ansToBeDeselected.isSelected = false
                }
                updateViews()
            }
            button.setText(answer.textResId)
        }

        updateViews()

        binding.questionTextView.setText(quizVM.currentQuestionText)
    }

    private fun updateViews() {
        buttonAnswerZip.forEach { (button, answer) ->
            button.isSelected = answer.isSelected
            button.isEnabled = answer.isEnabled
            button.updateColor()
        }
        binding.submitButton.isEnabled = quizVM.currentQuestionAnswerList.any { it.isSelected }
        binding.hintButton.isEnabled = quizVM.currentQuestionAnswerList.any { !it.isCorrect && it.isEnabled }
    }
}
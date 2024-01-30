package edu.vt.cs5254.multiquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.view.View
import edu.vt.cs5254.multiquiz.databinding.ActivityMainBinding
import android.widget.Button
//import android.util.Logpro
//private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    // Name: Layne Pu
    // PID: laynepu

    private lateinit var binding: ActivityMainBinding

    private lateinit var answerButtonList: List<Button>

    private val answerBank = listOf(
        Answer(R.string.answer_brisbane, false),
        Answer(R.string.answer_canberra, true),
        Answer(R.string.answer_melbourne, false),
        Answer(R.string.answer_sydney, false))

    private lateinit var buttonAnswerZip: List<Pair<Button, Answer>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerButtonList = listOf(
            binding.answerBrisbane,
            binding.answerCanberra,
            binding.answerMelbourne,
            binding.answerSydney
        )

        buttonAnswerZip = answerButtonList.zip(answerBank)

        buttonAnswerZip.forEach { (button, answer) ->
            button.setOnClickListener {
                answerBank.forEach { currentAnswer ->
                    currentAnswer.isSelected = currentAnswer == answer
                }
                updateViews()
            }
        }

        binding.button5050.setOnClickListener {
            answerBank.filterNot { it.isCorrect }
                .take(2)
                .forEach {
                    it.isSelected = false
                    it.isEnabled = false
            }
            updateViews()
        }

        binding.buttonReset.setOnClickListener {
            answerBank.forEach {
                it.isSelected = false
                it.isEnabled = true
            }
            updateViews()
        }

        updateViews()
    }

    private fun updateViews() {
        buttonAnswerZip.forEach { (button, answer) ->
            button.isSelected = answer.isSelected
            button.isEnabled = answer.isEnabled
            button.updateColor()
        }

        binding.button5050.isEnabled = answerBank.all { it.isEnabled }
    }
}
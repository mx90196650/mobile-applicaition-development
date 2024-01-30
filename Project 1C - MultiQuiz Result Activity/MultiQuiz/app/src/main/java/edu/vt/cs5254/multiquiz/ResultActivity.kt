package edu.vt.cs5254.multiquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import edu.vt.cs5254.multiquiz.databinding.ActivityResultBinding

const val EXTRA_RESET_ALL = "edu.vt.cs5254.multiquiz.reset_all"
private const val EXTRA_CA = "edu.vt.cs5254.multiquiz.correct_answers"
private const val EXTRA_TQ = "edu.vt.cs5254.multiquiz.total_questions"
private const val EXTRA_HU = "edu.vt.cs5254.multiquiz.hints_used"

class ResultActivity : AppCompatActivity() {

    private val vm: ResultViewModel by viewModels()

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetAllButton.setOnClickListener {
            vm.isReset = true
            setResetAllResult()
            updateView()
        }
        if(vm.isReset) {
            setResetAllResult()
        }
        updateView()
    }

    private fun setResetAllResult() {
        val data = Intent().apply {
            putExtra(EXTRA_RESET_ALL, vm.isReset)
        }
        setResult(Activity.RESULT_OK, data)
    }

    private fun updateView() {
        binding.resetAllButton.isEnabled = !vm.isReset
        binding.correctAnswersValue.text = intent.getStringExtra(EXTRA_CA)
        binding.totalQuestionsValue.text = intent.getStringExtra(EXTRA_TQ)
        binding.hintsUsedValue.text = intent.getStringExtra(EXTRA_HU)
    }

    companion object {
        fun newIntent(context: Context, correctAnswers: Int, totalQuestions: Int, hintsUsed: Int): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(EXTRA_CA, correctAnswers.toString())
                putExtra(EXTRA_TQ, totalQuestions.toString())
                putExtra(EXTRA_HU, hintsUsed.toString())
            }
        }
    }
}
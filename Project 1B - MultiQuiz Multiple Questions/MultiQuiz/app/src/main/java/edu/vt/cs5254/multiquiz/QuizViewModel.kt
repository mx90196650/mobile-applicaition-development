package edu.vt.cs5254.multiquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    private val answerList0 = listOf(
        Answer(R.string.question_0_answer_0, true),
        Answer(R.string.question_0_answer_1, false),
        Answer(R.string.question_0_answer_2, false),
        Answer(R.string.question_0_answer_3, false)
    )
    private val answerList1 = listOf(
        Answer(R.string.question_1_answer_0, false),
        Answer(R.string.question_1_answer_1, true),
        Answer(R.string.question_1_answer_2, false),
        Answer(R.string.question_1_answer_3, false)
    )
    private val answerList2 = listOf(
        Answer(R.string.question_2_answer_0, false),
        Answer(R.string.question_2_answer_1, false),
        Answer(R.string.question_2_answer_2, true),
        Answer(R.string.question_2_answer_3, false)
    )
    private val answerList3 = listOf(
        Answer(R.string.question_3_answer_0, false),
        Answer(R.string.question_3_answer_1, false),
        Answer(R.string.question_3_answer_2, false),
        Answer(R.string.question_3_answer_3, true)
    )

    private val questionList = listOf(
        Question(R.string.question_0, answerList0),
        Question(R.string.question_1, answerList1),
        Question(R.string.question_2, answerList2),
        Question(R.string.question_3, answerList3)
    )

    private var currentIndex: Int = 0

    val currentQuestionAnswerList: List<Answer>
        get() = questionList[currentIndex].answerList

    val currentQuestionText: Int
        get() = questionList[currentIndex].questionResId

    fun moveToNextQuestion() {
        currentIndex = (currentIndex + 1) % questionList.size
    }
}

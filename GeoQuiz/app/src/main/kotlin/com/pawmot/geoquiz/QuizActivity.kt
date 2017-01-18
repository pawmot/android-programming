package com.pawmot.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.pawmot.geoquiz.model.Question
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {

    companion object QuizActivity {
        private val TAG = QuizActivity::class.simpleName
        private val KEY_INDEX = "index"
        private val REQUEST_CODE_CHEAT = 0
    }

    private val questions = arrayOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true)
    )

    private var currentIndex = 0

    private var isCheater = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle) called")
        setContentView(R.layout.activity_quiz)

        val idx = savedInstanceState?.getInt(KEY_INDEX, 0)
        currentIndex = idx ?: 0

        trueButton.setOnClickListener { checkAnswer(true) }
        falseButton.setOnClickListener { checkAnswer(false) }
        prevButton.setOnClickListener { selectPreviousQuestion() }
        nextButton.setOnClickListener { selectNextQuestion() }
        questionTextView.setOnClickListener { selectNextQuestion() }
        cheatButton.setOnClickListener { startCheatActivity() }

        showCurrentQuestionText()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return
            }

            isCheater = CheatingActivity.wasAnswerShown(data)
        }
    }

    private fun startCheatActivity() {
        val intent = CheatingActivity.newIntent(this, questions[currentIndex].answer)
        startActivityForResult(intent, REQUEST_CODE_CHEAT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, currentIndex)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_quiz, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectPreviousQuestion() {
        currentIndex = (if (currentIndex > 0) currentIndex else questions.size) - 1
        isCheater = false
        showCurrentQuestionText()
    }

    private fun selectNextQuestion() {
        currentIndex = (currentIndex + 1) % questions.size
        isCheater = false
        showCurrentQuestionText()
    }

    private fun showCurrentQuestionText() {
        questionTextView.setText(questions[currentIndex].textResId)
    }

    private fun checkAnswer(userPressedTrue: Boolean) {
        val answerIsTrue = questions[currentIndex].answer

        val messageResId =
                if (isCheater)
                    R.string.judgment_toast
                else if (userPressedTrue == answerIsTrue)
                    R.string.correct_toast
                else
                    R.string.incorrect_toast

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}

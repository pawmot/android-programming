package com.pawmot.geoquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import kotlinx.android.synthetic.main.activity_cheating.*

class CheatingActivity : AppCompatActivity() {

    companion object Static {
        private val EXTRA_ANSWER_IS_TRUE = "com.pawmot.geoquiz.answer_is_true"
        private val EXTRA_ANSWER_SHOWN = "com.pawmot.geoquiz.answer_shown"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            val result = Intent(packageContext, CheatingActivity::class.java)
            result.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            return result
        }

        fun wasAnswerShown(intent: Intent): Boolean {
            return intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
        }
    }

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheating)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        showAnswerButton.setOnClickListener {
            if (answerIsTrue) {
                answerTextView.setText(R.string.true_button)
            } else {
                answerTextView.setText(R.string.false_button)
            }
            setAnswerShownResult(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = showAnswerButton.width/2
                val cy = showAnswerButton.height/2
                val radius = showAnswerButton.width.toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(showAnswerButton, cx, cy, radius, 0f)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        answerTextView.visibility = VISIBLE
                        showAnswerButton.visibility = INVISIBLE
                    }
                })
                anim.start()
            } else {
                answerTextView.visibility = VISIBLE
                showAnswerButton.visibility = INVISIBLE
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(Activity.RESULT_OK, intent)
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
}

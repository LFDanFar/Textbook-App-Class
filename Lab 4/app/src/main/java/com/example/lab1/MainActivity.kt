package com.example.lab1

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

private const val KEY_INDEX = "index"
private const val KEY_CHEAT = "cheater"
private const val KEY_CHEAT_SEARCH = "cheatSearch"
private const val TAG = "MainActivity"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatWebSearchButton: Button


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putBoolean(KEY_CHEAT, quizViewModel.isCheater)
        savedInstanceState.putBoolean(KEY_CHEAT_SEARCH, quizViewModel.isCheaterBySearch)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")

        setContentView(R.layout.activity_main)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val cheat = savedInstanceState?.getBoolean(KEY_CHEAT, false) ?: false
        val cheatSearch = savedInstanceState?.getBoolean(KEY_CHEAT_SEARCH, false) ?: false
        quizViewModel.currentIndex = currentIndex
        quizViewModel.isCheater = cheat
        quizViewModel.isCheaterBySearch = cheatSearch

        //val provider: ViewModelProvider = ViewModelProviders.of(this)
        //val quizViewModel = provider.get(QuizViewModel::class.java)
        //Log.d(TAG, "Got a QuizViewModel: $quizViewModel")
        trueButton = findViewById(R.id.true_Button)
        falseButton = findViewById(R.id.false_Button)
        nextButton = findViewById(R.id.next_Button)
        cheatButton = findViewById(R.id.cheat_button)
        cheatWebSearchButton = findViewById(R.id.cheat_search_button)
        questionTextView = findViewById(R.id.question_textview)

        trueButton.setOnClickListener { view: View -> checkAnswer(true) }
        falseButton.setOnClickListener { view: View -> checkAnswer(false) }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener{
            //val intent = Intent(this, CheatActivity::class.java)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            //startActivity(intent)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
        cheatWebSearchButton.setOnClickListener{
            Intent(Intent.ACTION_WEB_SEARCH).apply{
                this.putExtra(SearchManager.QUERY, questionTextView.text)
            }.also {intent ->
                startActivity(intent)
                quizViewModel.isCheaterBySearch = true
            }
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        /*val messageResId = if(userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }*/
        val messageResId:Int = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            quizViewModel.isCheaterBySearch -> R.string.search_judgment_toast
            userAnswer == correctAnswer ->R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}

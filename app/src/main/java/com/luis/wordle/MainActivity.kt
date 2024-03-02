package com.luis.wordle

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import com.github.jinatonic.confetti.CommonConfetti
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiView
import com.luis.wordle.FourLetterWordList.getRandomFourLetterWord
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var title: TextView

    // 1st Guess
    private lateinit var guess1: TextView
    private lateinit var guessChecked1: TextView
    private lateinit var word1: TextView
    private lateinit var answer1: TextView

    // 2nd Guess
    private lateinit var guess2: TextView
    private lateinit var guessChecked2: TextView
    private lateinit var word2: TextView
    private lateinit var answer2: TextView

    // 3rd Guess
    private lateinit var guess3: TextView
    private lateinit var guessChecked3: TextView
    private lateinit var word3: TextView
    private lateinit var answer3: TextView

    //Bottom items
    private lateinit var WORD: TextView
    private lateinit var editText: EditText
    private lateinit var button: Button

    private var wasItGuessed: Boolean = false
    private var numberOfCorrectGuesses: Int = 0

    private var frameLayout: FrameLayout? = null
    private var viewGroup: ViewGroup? = null

    private lateinit var toggleButton: Button

    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frameLayout = findViewById(R.id.frameLayout)
        // Cast FrameLayout to ViewGroup
        viewGroup = frameLayout

        title = findViewById(R.id.purpleLabel)
        title.text = "WORDLE - Correct Guesses: $numberOfCorrectGuesses"

        toggleButton = findViewById(R.id.toggleButton)

        // 1st Guess
        guess1 = findViewById(R.id.guess1)
        guessChecked1 = findViewById(R.id.guessChecked1)
        word1 = findViewById(R.id.word1)
        answer1 = findViewById(R.id.answer1)

        // 2nd Guess
        guess2 = findViewById(R.id.guess2)
        guessChecked2 = findViewById(R.id.guessChecked2)
        word2 = findViewById(R.id.word2)
        answer2 = findViewById(R.id.answer2)

        // 3rd Guess
        guess3 = findViewById(R.id.guess3)
        guessChecked3 = findViewById(R.id.guessChecked3)
        word3 = findViewById(R.id.word3)
        answer3 = findViewById(R.id.answer3)

        // Initialize references to the EditText and Button
        WORD = findViewById(R.id.WORD)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)

        button.isEnabled = false

        // reset all TextViews and hide them again
        resetlabels()

        // this is used to make user not entered more than 4 characters for the word
        val maxLength = 4
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

        // Add a TextWatcher to the EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used in this example

            }

            override fun afterTextChanged(s: Editable?) {
                // Filter of editText was set to keep it under 5 characters
                //WORD.text = s.toString()
                if (s?.length ?: 0 < 4 && !wasItGuessed) {
                    button.isEnabled = false
                }
                else {
                    button.isEnabled = true
                }
            }
        })

        // Set a click listener for the button
        button.setOnClickListener {
            // Get the text from the EditText
            //val enteredText = editText.text.toString()

//            if (!answer1.isInvisible && !answer2.isInvisible && !answer3.isInvisible) {
//                resetlabels()
//            }

            if (wasItGuessed){
                resetlabels()
                WORD.isInvisible = true
                wasItGuessed = false
                button.text = "GUESS!"
                button.isEnabled = false
                editText.isEnabled = true
                editText.isInvisible = false
            }
            else if (!answer1.isInvisible && !answer2.isInvisible){

                // Word 3
                guess3.isInvisible = false
                guessChecked3.isInvisible = false
                word3.isInvisible = false
                word3.text = editText.text.toString().lowercase()
                answer3.isInvisible = false
                answer3.text = checkGuess(word3.text)

                if (answer3.text.toString().uppercase() == wordToGuess.uppercase()) {
                    Toast.makeText(this, "Bravo! You have guessed word correctly!", Toast.LENGTH_SHORT).show()
                    reset(true)
                } else {
                    Toast.makeText(this, "You have exceeded your number of guesses\n", Toast.LENGTH_SHORT).show()
                    reset(false)
                }

            } else if(!answer1.isInvisible){

                // Word 2
                guess2.isInvisible = false
                guessChecked2.isInvisible = false
                word2.isInvisible = false
                word2.text = editText.text.toString().lowercase()
                answer2.isInvisible = false
                answer2.text = checkGuess(word2.text)

                if (answer2.text.toString().uppercase() == wordToGuess.uppercase()) {
                    Toast.makeText(this, "Bravo! You have guessed word correctly!", Toast.LENGTH_SHORT).show()
                    reset(true)
                }
            } else {

                // Word 1
                guess1.isInvisible = false
                guessChecked1.isInvisible = false
                word1.isInvisible = false
                word1.text = editText.text.toString().lowercase()
                answer1.isInvisible = false
                answer1.text = checkGuess(word1.text)

                val y = answer1.text
                val x = wordToGuess.uppercase()

                // Print to console
//                println("Uppercased Spannable Result: $y")// Print to console
//                println("Uppercased Spannable Result: $x")

                if (answer1.text.toString().uppercase() == wordToGuess.uppercase()) {
                    Toast.makeText(this, "Bravo! You have guessed word correctly!", Toast.LENGTH_SHORT).show()
                    reset(true)
                }
            }

            // Hide the keyboard
            // Empty word shown
            // Empty editText to start again typing
            hideKeyboard()
            editText.setText("")

            // Do something with the entered text, e.g., display it in a Toast
            // For example, you can use the Toast.makeText() method to display the text
            // in a short-lived popup message.

            // Replace the above line with the desired action based on the entered text.
        }

        // Set a click listener for the toggle button
        toggleButton.setOnClickListener {
            // Toggle between word lists
            flag = !flag
            toggleButton.text = if (flag) "Fruits" else "Animals"
            wordToGuess = getRandomFourLetterWord(flag)
        }
    }

    private var wordToGuess = getRandomFourLetterWord(flag)

    /**
     * Parameters / Fields:
     *   wordToGuess : String - the target word the user is trying to guess
     *   guess : String - what the user entered as their guess
     *
     * Returns a String of 'O', '+', and 'X', where:
     *   'O' represents the right letter in the right place
     *   '+' represents the right letter in the wrong place
     *   'X' represents a letter not in the target word
     */
    private fun checkGuess(guess: CharSequence): SpannableString {
        val spannableResult = SpannableString(guess.toString().uppercase())

        for (i in guess.indices) {

            when (guess[i].uppercaseChar()) {
                wordToGuess[i].uppercaseChar() ->
                    spannableResult.setSpan(
                        ForegroundColorSpan(Color.GREEN),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                in wordToGuess.uppercase(Locale.ROOT) ->
                    spannableResult.setSpan(
                        ForegroundColorSpan(Color.argb(255, 255, 165, 0)),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                else -> spannableResult.setSpan(
                    ForegroundColorSpan(Color.RED),
                    i,
                    i + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannableResult
    }

    private fun reset(guessCorrect: Boolean){

        if (guessCorrect) {
            numberOfCorrectGuesses++
            // If equal, trigger confetti animation
            viewGroup?.let { triggerConfettiAnimation(it) }

        }
        WORD.text = wordToGuess
        WORD.isInvisible = false
        wasItGuessed = true
        editText.isEnabled = false
        editText.isInvisible = true
        button.text = "RESET!"
        //wordToGuess = getRandomFourLetterWord()
        title.text = "WORDLE - Correct Guesses: $numberOfCorrectGuesses"
    }

    private fun resetlabels() {

        //Make all Top TextView Invisible
        guess1.isInvisible = true
        guessChecked1.isInvisible = true
        word1.isInvisible = true
        answer1.isInvisible = true

        guess2.isInvisible = true
        guessChecked2.isInvisible = true
        word2.isInvisible = true
        answer2.isInvisible = true

        guess3.isInvisible = true
        guessChecked3.isInvisible = true
        word3.isInvisible = true
        answer3.isInvisible = true
    }

    private fun triggerConfettiAnimation(viewGroup: ViewGroup) {
        // Configure and trigger confetti animation
        CommonConfetti.rainingConfetti(viewGroup, intArrayOf(Color.GREEN, Color.YELLOW, Color.RED))
            .infinite()
            .setEmissionDuration(3000)
            .setEmissionRate(45f)
            .setVelocityX(100f, 50f)
            .setVelocityY(200f, 100f)
            .setRotationalVelocity(180f, 90f)
            .setTouchEnabled(true)
            .animate()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

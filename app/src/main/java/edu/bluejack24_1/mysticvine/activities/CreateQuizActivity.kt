package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCreateQuizBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.QuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel
import java.util.UUID

class CreateQuizPage : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        questionViewModel = QuestionViewModel()
        quizViewModel = QuizViewModel()
        binding.addQuestion.setOnClickListener {
            addNewQuestion()
        }
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            binding.createQuiz.setOnClickListener {
                if (binding.questionContainer.childCount < 1) {
                    Toast.makeText(this, "Please add at least one question.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var allQuestionsEligible = true
                    for (i in 0 until binding.questionContainer.childCount) {
                        val questionView = binding.questionContainer.getChildAt(i)

                        val selectedAnswer = questionView.tag as? String
                        val questions = questionView.findViewById<TextView>(R.id.question)

                        val optionA = questionView.findViewById<TextView>(R.id.option_a)
                        val optionB = questionView.findViewById<TextView>(R.id.option_b)
                        val optionC = questionView.findViewById<TextView>(R.id.option_c)
                        val optionD = questionView.findViewById<TextView>(R.id.option_d)
                        val optionE = questionView.findViewById<TextView>(R.id.option_e)

                        val optionAValue = optionA.text.toString()
                        val optionBValue = optionB.text.toString()
                        val optionCValue = optionC.text.toString()
                        val optionDValue = optionD.text.toString()
                        val optionEValue = optionE.text.toString()
                        Toast.makeText(this, questionView.tag.toString(), Toast.LENGTH_SHORT).show()

                        if (selectedAnswer.isNullOrEmpty() || !questionViewModel.isQuestionEligible(
                                questions.text.toString(),
                                selectedAnswer,
                                optionAValue,
                                optionBValue,
                                optionCValue,
                                optionDValue,
                                optionEValue
                            )
                        ) {
                            allQuestionsEligible = false
                            break
                        }
                    }
                    if (!(allQuestionsEligible && quizViewModel.isQuizEligible(binding.quizName.text.toString()))) {
                        Toast.makeText(
                            this,
                            "Please ensure all fields are filled out correctly.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val id: String = UUID.randomUUID().toString()
                        quizViewModel.createQuizzes(id, user.id, binding.quizName.text.toString())

                        for (i in 0 until binding.questionContainer.childCount) {
                            val questionView = binding.questionContainer.getChildAt(i)
                            val selectedAnswer = questionView.tag as String
                            val questions =
                                questionView.findViewById<TextView>(R.id.question).text.toString()
                            val optionA = questionView.findViewById<TextView>(R.id.option_a)
                            val optionB = questionView.findViewById<TextView>(R.id.option_b)
                            val optionC = questionView.findViewById<TextView>(R.id.option_c)
                            val optionD = questionView.findViewById<TextView>(R.id.option_d)
                            val optionE = questionView.findViewById<TextView>(R.id.option_e)
                            val optionAValue = optionA.text.toString()
                            val optionBValue = optionB.text.toString()
                            val optionCValue = optionC.text.toString()
                            val optionDValue = optionD.text.toString()
                            val optionEValue = optionE.text.toString()
                            questionViewModel.createQuestion(
                                id,
                                questions,
                                selectedAnswer,
                                optionAValue,
                                optionBValue,
                                optionCValue,
                                optionDValue,
                                optionEValue
                            )
                        }
                    }
                }
            }
            quizViewModel.quizResult.observe(this) { result ->
                if (result == "Quiz created") {
                    Utils.showSnackBar(binding.root, result, false)
                    val intent = Intent(this, ProfilePage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Utils.showSnackBar(binding.root, result, true)
                }
            }
        }
    }

    private fun addNewQuestion() {
        val inflater = LayoutInflater.from(this)
        val questionView =
            inflater.inflate(R.layout.create_question_card, binding.questionContainer, false)

        val closeQuestion = questionView.findViewById<ImageView>(R.id.close_question)

        val optionA = questionView.findViewById<TextView>(R.id.right_answer_A)
        val optionB = questionView.findViewById<TextView>(R.id.right_answer_B)
        val optionC = questionView.findViewById<TextView>(R.id.right_answer_C)
        val optionD = questionView.findViewById<TextView>(R.id.right_answer_D)
        val optionE = questionView.findViewById<TextView>(R.id.right_answer_E)

        closeQuestion.setOnClickListener {
            binding.questionContainer.removeView(questionView)
        }
        optionA.setOnClickListener { onOptionSelected(optionA, questionView) }
        optionB.setOnClickListener { onOptionSelected(optionB, questionView) }
        optionC.setOnClickListener { onOptionSelected(optionC, questionView) }
        optionD.setOnClickListener { onOptionSelected(optionD, questionView) }
        optionE.setOnClickListener { onOptionSelected(optionE, questionView) }
        binding.questionContainer.addView(questionView)
    }

    private fun onOptionSelected(selectedOption: TextView, questionView: View) {
        resetOptions(questionView)
        selectedOption.background = ContextCompat.getDrawable(this, R.drawable.dark_orange_circle)
        questionView.tag = selectedOption.text.toString()

//        Toast.makeText(this, questionView.tag.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun resetOptions(questionView: View) {
        val optionA = questionView.findViewById<TextView>(R.id.right_answer_A)
        val optionB = questionView.findViewById<TextView>(R.id.right_answer_B)
        val optionC = questionView.findViewById<TextView>(R.id.right_answer_C)
        val optionD = questionView.findViewById<TextView>(R.id.right_answer_D)
        val optionE = questionView.findViewById<TextView>(R.id.right_answer_E)

        val defaultBackground = ContextCompat.getDrawable(this, R.drawable.light_orange_circle)
        optionA.background = defaultBackground
        optionB.background = defaultBackground
        optionC.background = defaultBackground
        optionD.background = defaultBackground
        optionE.background = defaultBackground
    }
}

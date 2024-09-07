package edu.bluejack24_1.mysticvine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.RecommendedQuizzesCardBinding
import edu.bluejack24_1.mysticvine.model.Quizzes

class Random3QuizAdapter : RecyclerView.Adapter<Random3QuizAdapter.ViewHolder>() {

    private var quizzesList: List<Quizzes> = emptyList()

    fun updateList(update: List<Quizzes>) {
        this.quizzesList = update
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: RecommendedQuizzesCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: Quizzes) {
            binding.quiz = quiz
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzesList[position]
        holder.bind(quiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecommendedQuizzesCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizzesList.size
    }
}

package edu.bluejack24_1.mysticvine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.QuizPlayCardBinding
import edu.bluejack24_1.mysticvine.model.Quizzes

class QuizzesAdapter (private val onItemClicked: (quizId: String, creatorId : String) -> Unit) :
    RecyclerView.Adapter<QuizzesAdapter.ViewHolder>() {

    private var quizzesList: List<Quizzes> = emptyList()
    private lateinit var binding: QuizPlayCardBinding

    fun updateList(update: List<Quizzes>) {
        this.quizzesList = update
        this.notifyDataSetChanged()
    }
    class ViewHolder(private val binding: QuizPlayCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: Quizzes) {
            binding.quiz = quiz
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            QuizPlayCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizzesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzesList[position]
        holder.bind(quiz)
        holder.itemView.setOnClickListener {
            onItemClicked(quiz.id, quiz.userId)
        }
    }


}
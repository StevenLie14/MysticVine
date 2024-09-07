package edu.bluejack24_1.mysticvine.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.CustomQuizAnswerCardBinding
import edu.bluejack24_1.mysticvine.databinding.LandingLeaderboardCardBinding
import edu.bluejack24_1.mysticvine.databinding.ProfileFlashCardBinding
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class CustomQuizAnswerAdapter : RecyclerView.Adapter<CustomQuizAnswerAdapter.ViewHolder>() {

    private var customQuizAnswerList: List<Pair<CustomQuizAnswer,Users>> = emptyList()
    private lateinit var binding: CustomQuizAnswerCardBinding

    fun updateList(update : List<Pair<CustomQuizAnswer,Users>>){
        this.customQuizAnswerList = update
        this.notifyDataSetChanged()
    }

    class ViewHolder(private val binding: CustomQuizAnswerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Pair<CustomQuizAnswer,Users>) {
            Log.d("CustomQuizAnswerAdapter", "bind: $answer")
            binding.answer = answer.first
            binding.user = answer.second
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CustomQuizAnswerCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return customQuizAnswerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val answer = customQuizAnswerList[position]
        holder.bind(answer)

    }


}
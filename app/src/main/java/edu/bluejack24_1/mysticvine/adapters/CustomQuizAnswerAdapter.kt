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

class CustomQuizAnswerAdapter (userViewModel: UserViewModel) : RecyclerView.Adapter<CustomQuizAnswerAdapter.ViewHolder>() {

    private var customQuizAnswerList: List<CustomQuizAnswer> = emptyList()
    private lateinit var binding: CustomQuizAnswerCardBinding
    private val userViewModel = userViewModel

    fun updateList(update : List<CustomQuizAnswer>){
        this.customQuizAnswerList = update
        this.notifyDataSetChanged()
    }

    class ViewHolder(private val binding: CustomQuizAnswerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: CustomQuizAnswer,userViewModel: UserViewModel) {
            binding.answer = answer
            userViewModel.getUserById(answer.userId).observe(itemView.context as androidx.lifecycle.LifecycleOwner){
                binding.user = it
            }
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
        val user = customQuizAnswerList[position]
        holder.bind(user,userViewModel)

    }


}
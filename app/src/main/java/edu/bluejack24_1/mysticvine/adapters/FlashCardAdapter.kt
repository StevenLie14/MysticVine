package edu.bluejack24_1.mysticvine.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.LandingLeaderboardCardBinding
import edu.bluejack24_1.mysticvine.databinding.ProfileFlashCardBinding
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel

class FlashCardAdapter(flashCardViewModel: FlashCardViewModel) :
    RecyclerView.Adapter<FlashCardAdapter.ViewHolder>() {

    private var flashCardList: List<FlashCard> = emptyList()
    private lateinit var binding: ProfileFlashCardBinding
    private val fla = flashCardViewModel

    fun updateList(update: List<FlashCard>) {
        this.flashCardList = update
        this.notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ProfileFlashCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(flashCard: FlashCard, flashCardViewModel: FlashCardViewModel) {
            binding.ivDeleteFlashCard.setOnClickListener {
                flashCardViewModel.deleteFlashCard(flashCard)
            }
            binding.flashCard = flashCard
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ProfileFlashCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return flashCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = flashCardList[position]
        holder.bind(user, fla)

    }


}
package edu.bluejack24_1.mysticvine.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.LandingLeaderboardCardBinding
import edu.bluejack24_1.mysticvine.model.Users

class LandingLeaderBoardAdapter : RecyclerView.Adapter<LandingLeaderBoardAdapter.ViewHolder>() {

    private var userList: List<Users> = emptyList()
    private lateinit var binding: LandingLeaderboardCardBinding

    fun updateUserList(update: List<Users>) {
        this.userList = update
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: LandingLeaderboardCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LandingLeaderboardCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }


}
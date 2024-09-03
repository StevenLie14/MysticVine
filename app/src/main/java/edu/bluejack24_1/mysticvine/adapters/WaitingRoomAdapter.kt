package edu.bluejack24_1.mysticvine.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.mysticvine.databinding.LandingLeaderboardCardBinding
import edu.bluejack24_1.mysticvine.databinding.WaitingRoomUserProfileBinding
import edu.bluejack24_1.mysticvine.model.Users

class WaitingRoomAdapter : RecyclerView.Adapter<WaitingRoomAdapter.ViewHolder>() {

    private var userList: List<Users> = emptyList()
    private lateinit var binding: WaitingRoomUserProfileBinding

     fun updateUserList(update : List<Users>){
        this.userList = update
         notifyDataSetChanged()
    }
    class ViewHolder(private val binding: WaitingRoomUserProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = WaitingRoomUserProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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
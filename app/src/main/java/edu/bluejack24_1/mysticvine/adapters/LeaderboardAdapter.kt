import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.LeaderboardCardBinding
import edu.bluejack24_1.mysticvine.model.Users

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    private var userList: List<Users> = emptyList()
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, currentUserId)
    }

    fun updateUserList(update: List<Users>) {
        this.userList = update
        notifyDataSetChanged()
    }
    class ViewHolder(private val binding: LeaderboardCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users, currentUserId: String?) {
            binding.user = user

            binding.ranking = "${adapterPosition + 4}"
            val drawableResource = if (user.id == currentUserId) {
                binding.isCurrentUser = true
            } else {
                binding.isCurrentUser = false
            }

            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LeaderboardCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}

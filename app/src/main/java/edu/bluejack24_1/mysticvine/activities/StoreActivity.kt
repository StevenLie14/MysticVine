package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityStoreBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class StorePage : AppCompatActivity() {
    private lateinit var binding:ActivityStoreBinding
    private lateinit var userViewModel: UserViewModel
    private var currentCoin: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            binding.currentCoin.text = user.coin.toString()
            currentCoin =  user.coin
            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)
        }



        binding.buyExpBooster.setOnClickListener {
            userViewModel.addExpBooster()
        }


        binding.buyCoinBooster.setOnClickListener(){
            userViewModel.addCoinBooster()

        }

        binding.buyShieldBooster.setOnClickListener(){
            userViewModel.addShieldBooster()
        }

        userViewModel.boosterResult.observe(this) { result ->
            if (result.contains("obtain")) {
                Utils.showSnackBar(binding.root, result, false)
            } else {
                Utils.showSnackBar(binding.root, result, true)
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }

    }
}
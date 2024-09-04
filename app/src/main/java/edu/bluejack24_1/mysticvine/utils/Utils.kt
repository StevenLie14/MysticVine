package edu.bluejack24_1.mysticvine.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.activities.LandingPage
import edu.bluejack24_1.mysticvine.activities.LoginPage
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel
import java.time.LocalDate
import kotlin.math.pow

class Utils {

    companion object {
        fun showSnackBar(view: View, message: String, isError: Boolean = true) {
            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            val snackBarView = snackBar.view
            val params = snackBarView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackBarView.layoutParams = params
            val textView = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackBarView.setBackgroundColor(ContextCompat.getColor(view.context, if (isError) R.color.light_red else R.color.light_green))
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.peach))
            textView.maxLines = 5
            textView.ellipsize = null
            snackBar.show()
        }

        fun loggedInMiddleware(userViewModel: UserViewModel, context : Context) {
            userViewModel.currentUser.observe(context as LifecycleOwner) { user ->
                if (user != null) {
                    val intent = Intent(context, LandingPage::class.java)
                    context.startActivity(intent)
                    if (context is AppCompatActivity) {
                        context.finish()
                    }
                }
            }
        }

        fun guestMiddleware(userViewModel: UserViewModel, context : Context) {
            userViewModel.currentUser.observe(context as LifecycleOwner) { user ->
                if (user == null) {
                    val intent = Intent(context, LoginPage::class.java)
                    context.startActivity(intent)
                    if (context is AppCompatActivity) {
                        context.finish()
                    }
                }
            }
        }

        fun getExpForLevel(level: Int, baseExp: Int = 100, growthFactor: Double = 1.5): Int {
            return (baseExp * growthFactor.pow((level - 1).toDouble())).toInt()
        }


        fun backButton (activity: AppCompatActivity) {
            activity.onBackPressed()
        }

        fun getLocalDate(date : String): LocalDate {
            return LocalDate.parse(date)
        }

        fun generateCode(): String {
            val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..6)
                .map { chars.random() }
                .joinToString("")
        }

    }
}
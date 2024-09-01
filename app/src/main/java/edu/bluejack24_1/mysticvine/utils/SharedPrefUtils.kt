package edu.bluejack24_1.mysticvine.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SharedPrefUtils (context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "mystic_vine"
        const val CURRENT_USER = "current_user"

    }

    fun <T> saveData (key: String, data: T) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(data)
        editor.putString(key, json)
        editor.apply()
    }
    fun <T> loadData(key: String, clazz: Class<T>): T? {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            gson.fromJson(json, clazz)
        } else {
            null
        }
    }


    fun removeData(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }



}
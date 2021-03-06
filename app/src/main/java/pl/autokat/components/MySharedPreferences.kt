package pl.autokat.components

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences {
    companion object {
        //object of shared preferences
        private lateinit var preferences: SharedPreferences
        //init
        fun init(context: Context) {
            preferences = context.getSharedPreferences(
                MyConfiguration.MY_SHARED_PREFERENCES_NAME,
                MyConfiguration.MY_SHARED_PREFERENCES_MODE
            )
        }
        //get key
        fun getKeyFromFile(key: String) : String{
            return preferences.getString(key, "").toString()
        }
        //set key
        fun setKeyToFile(key : String, value: String){
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.commit() //editor.commit() -> calling synchronously, what I want
        }
    }
}
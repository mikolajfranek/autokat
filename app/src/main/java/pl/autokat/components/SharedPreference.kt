package pl.autokat.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPreference {
    companion object {
        private const val PREFERENCES_NAME: String = "MyKatSharedPreferences"
        private const val PREFERENCES_MODE = Context.MODE_PRIVATE
        private lateinit var preferences: SharedPreferences
        fun init(context: Context) {
            preferences = context.getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE)
        }

        fun getKey(key: String): String {
            return preferences.getString(key, "").toString()
        }

        @SuppressLint("ApplySharedPref")
        fun setKey(key: String, value: String) {
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.commit()
        }

        /* authentication */
        const val CURRENT_TIMESTAMP: String = "CurrentTimestamp"
        const val ACCESS_TOKEN: String = "AccessToken"
        const val ACCESS_TOKEN_TIMESTAMP: String = "AccessTokenTimestamp"

        /* user */
        const val LOGIN: String = "Login"
        const val LICENCE_DATE_OF_END: String = "LicenceDate"
        const val DISCOUNT: String = "Discount"
        const val VISIBILITY: String = "Visibility"
        const val UPDATE_COURSE_TIMESTAMP: String = "UpdateCourseTimestamp"
        const val MINUS_PLATINUM: String = "MinusPlatinum"
        const val MINUS_PALLADIUM: String = "MinusPalladium"
        const val MINUS_RHODIUM: String = "MinusRhodium"
        const val LAST_SEARCHED_TEXT: String = "LastSearchedText"

        /* courses elements */
        const val PLATINUM: String = "Platinum"
        const val PLATINUM_DATE: String = "PlatinumDate"
        const val PALLADIUM: String = "Palladium"
        const val PALLADIUM_DATE: String = "PalladiumDate"
        const val RHODIUM: String = "Rhodium"
        const val RHODIUM_DATE: String = "RhodiumDate"
        const val ACTUAL_COURSES_DATE: String = "ActualCoursesDate"
        const val ACTUAL_COURSES_CHOICE: String = "ActualCoursesChoice"

        /* courses exchanges */
        const val USD_PLN: String = "UsdPln"
        const val USD_PLN_DATE: String = "UsdPlnDate"
        const val EUR_PLN: String = "EurPln"
        const val EUR_PLN_DATE: String = "EurPlnDate"
    }
}
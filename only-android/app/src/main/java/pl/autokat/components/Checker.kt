package pl.autokat.components

import android.annotation.SuppressLint
import org.json.JSONObject
import pl.autokat.enums.ProgramMode
import pl.autokat.enums.TimeChecking
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Checker {
    companion object {
        private const val URL_TIMESTAMP: String =
            "https://worldtimeapi.org/api/timezone/Europe/Warsaw"

        fun checkTimeIsGreaterThanNow(dateInput: String): Boolean {
            val timestamp: Long = Date().time
            val timestampInput: Long =
                (SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.GERMANY
                ).parse(dateInput)!!.time) + Configuration.ONE_DAY_IN_MILLISECONDS
            return timestampInput > timestamp
        }

        @SuppressLint("SimpleDateFormat")
        fun checkTimeOnPhone(timeChecking: TimeChecking): Boolean {
            when (timeChecking) {
                TimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    val json = JSONObject(URL(URL_TIMESTAMP).readText())
                    val timestampWeb: Long =
                        (json.getLong("unixtime") * 1000L) - Configuration.ONE_HOUR_IN_MILLISECONDS
                    val timestampPhone: Long = Date().time
                    if (timestampPhone > timestampWeb) {
                        SharedPreference.setKey(
                            SharedPreference.CURRENT_TIMESTAMP,
                            timestampPhone.toString()
                        )
                        return true
                    }
                    return false
                }
                TimeChecking.CHECKING_LICENCE -> {
                    if (Configuration.PROGRAM_MODE == ProgramMode.CLIENT) return true
                    val dateSavedInSharedPreference =
                        SharedPreference.getKey(SharedPreference.LICENCE_DATE_OF_END)
                    if (dateSavedInSharedPreference.isEmpty()) return false
                    val timestampLicence: Long =
                        Parser.parseStringDateToDate(dateSavedInSharedPreference).time.plus(
                            Configuration.ONE_DAY_IN_MILLISECONDS
                        )
                    val timestamp: Long = Date().time
                    val timestampSavedInSharedPreference: Long =
                        SharedPreference.getKey(SharedPreference.CURRENT_TIMESTAMP).toLong()
                    if ((timestampLicence > timestamp) && (timestamp > timestampSavedInSharedPreference)) {
                        SharedPreference.setKey(
                            SharedPreference.CURRENT_TIMESTAMP,
                            timestamp.toString()
                        )
                        return true
                    }
                    return false
                }
            }
        }
    }
}
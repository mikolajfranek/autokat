package pl.autokat.components

import android.annotation.SuppressLint
import org.json.JSONObject
import pl.autokat.enums.TimeChecking
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Checker {
    companion object {
        private const val URL_TIMESTAMP: String =
            "https://worldtimeapi.org/api/timezone/Europe/Warsaw"

        @SuppressLint("SimpleDateFormat")
        fun checkTimeOnPhone(dateInput: String, timeChecking: TimeChecking): Boolean {
            when (timeChecking) {
                TimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    val json = JSONObject(URL(URL_TIMESTAMP).readText())
                    val timestampWeb: Long =
                        (json.getLong("unixtime") * 1000L) - MyConfiguration.ONE_HOUR_IN_MILLISECONDS
                    val timestampPhone: Long = Date().time
                    if (timestampPhone > timestampWeb) {
                        SharedPreference.setKeyToFile(
                            SharedPreference.CURRENT_TIMESTAMP,
                            timestampPhone.toString()
                        )
                        return true
                    }
                    return false
                }
                TimeChecking.PARAMETER_IS_GREATER_THAN_NOW -> {
                    val timestamp: Long = Date().time
                    val timestampInput: Long =
                        (SimpleDateFormat("yyyy-MM-dd").parse(dateInput)!!.time) + MyConfiguration.ONE_DAY_IN_MILLISECONDS
                    return timestampInput > timestamp
                }
                TimeChecking.CHECKING_LICENCE -> {
                    val timestamp: Long = Date().time
                    val timestampLicence: Long = (SimpleDateFormat("yyyy-MM-dd").parse(
                        SharedPreference.getKeyFromFile(
                            SharedPreference.LICENCE_DATE_OF_END
                        )
                    )!!.time) + MyConfiguration.ONE_DAY_IN_MILLISECONDS
                    val timestampFromConfiguration: Long = SharedPreference.getKeyFromFile(
                        SharedPreference.CURRENT_TIMESTAMP
                    ).toLong()
                    if ((timestamp > timestampFromConfiguration) && (timestampLicence > timestamp)) {
                        SharedPreference.setKeyToFile(
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
package pl.autokat.components

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class Formatter {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun formatStringDate(date: String): String {
            if (date.isEmpty()) return ""
            return SimpleDateFormat("dd.MM.yyyy").format((SimpleDateFormat("yyyy-MM-dd").parse(date)!!))
                .toString()
        }

        fun formatStringFloat(floatString: String, precision: Int): String {
            if (floatString.isEmpty()) return (String.format(
                "%." + precision + "f",
                (0.00).toFloat()
            )).replace(",", ".")
            return String.format("%." + precision + "f", floatString.toFloat()).replace(",", ".")
        }
    }
}
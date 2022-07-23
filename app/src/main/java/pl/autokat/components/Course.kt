package pl.autokat.components

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.kittinunf.fuel.Fuel
import com.github.ramonrabello.opencv.android.ktx.threshold
import com.googlecode.tesseract.android.TessBaseAPI
import com.kizitonwose.calendarview.utils.yearMonth
import org.json.JSONObject
import org.jsoup.Jsoup
import org.opencv.core.Mat
import pl.autokat.enums.Metal
import pl.autokat.exceptions.NoneCoursesException
import pl.autokat.models.ModelCourse
import java.net.URL
import java.net.UnknownHostException
import java.time.LocalDate
import java.util.*


class Course {
    companion object {
        private const val MY_CATALYST_VALUES_URL_USD_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/usd"
        private const val MY_CATALYST_VALUES_URL_EUR_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/eur"
        private const val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM =
            "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM =
            "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM =
            "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        private fun getUrlNBP(date: LocalDate): String {
            return "$MY_CATALYST_VALUES_URL_USD_PLN/$date?format=json"
        }

        private fun getURLKitcoLondonFix(date: LocalDate): String {
            val yearShortcut = date.year.toString().substring(2, 4)
            return "https://www.kitco.com/londonfix/gold.londonfix$yearShortcut.html"
        }

        private fun getURLKitcoRhodiumTable(): String {
            return "https://www.kitco.com/LFgif/rd00-09D.gif"
        }

        private fun getValueKitcoLondonFix(
            date: LocalDate,
            cssQuery1: String,
            cssQuery2: String
        ): String {
            val doc = Jsoup.connect(getURLKitcoLondonFix(date)).get();
            val element = doc.select("td.date:contains($date)")
            if (element.size != 1) throw NoneCoursesException()
            val result1 = element[0].parent()!!.select(cssQuery1).text()
            if (result1.equals("-") == false) return result1
            val result2 = element[0].parent()!!.select(cssQuery2).text()
            if (result2.equals("-")) throw NoneCoursesException()
            return result2
        }

        private fun getValueKitcoRhodium(date: LocalDate): String {
            val byteArray = URL(getURLKitcoRhodiumTable()).readBytes()
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val tess = TessBaseAPI()






            //TODO
            //for get rhodium avg of month

            tess.init("/data/user/0/pl.autokat/", "digits")



            val bitmap2 =
                Bitmap.createBitmap(bitmap, 40, 50, bitmap.width - 40, bitmap.height - 50 - 40)

            val mat = Mat()
            mat.threshold(bitmap2, 106.0) {
                tess.setImage(it)
            }

            val text = tess.utF8Text


            return text
        }

        private fun getValuesCourses(metal: Metal, date: LocalDate): Pair<String, String> {
            var left = ""
            var right = ""
            //TODO
            when (metal) {
                Metal.PLATINUM -> {
                    left = getValueKitcoLondonFix(date, "td.pt.pm", "td.pt.am")
                }
                Metal.PALLADIUM -> {
                    left = getValueKitcoLondonFix(date, "td.pl.pm", "td.pl.am")
                }
                Metal.RHODIUM -> {
                    left = getValueKitcoRhodium(date)
                }
            }
            return Pair("", "")
        }

        private fun getCourseUsdPln(
            savingToSharedPreferences: Boolean,
            date: LocalDate
        ): Pair<String, String> {
            val (_, response, result) = Fuel.get(getUrlNBP(date)).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                SharedPreference.setKey(SharedPreference.USD_PLN, value)
                SharedPreference.setKey(SharedPreference.USD_PLN_DATE, valueDate)
            }
            return Pair(value, valueDate)
        }

        private fun getCourseEurPln(
            savingToSharedPreferences: Boolean,
            date: LocalDate
        ): Pair<String, String> {
            val (_, response, result) = Fuel.get(getUrlNBP(date)).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                SharedPreference.setKey(SharedPreference.EUR_PLN, value)
                SharedPreference.setKey(SharedPreference.EUR_PLN_DATE, valueDate)
            }
            return Pair(value, valueDate)
        }

        private fun getCoursePlatinum(
            savingToSharedPreferences: Boolean,
            date: LocalDate
        ): Pair<String, String> {
            var value = ""
            var valueDate = ""
            if (date == LocalDate.now()) {
                val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PLATINUM)
                    .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
                if (response.statusCode != 200) throw UnknownHostException()
                val content: List<String> = result.get().split(',')
                value = content[4].replace(',', '.')
                valueDate = content[3].split(' ')[0]
            } else {
                val pair = getValuesCourses(Metal.PLATINUM, date)
                value = pair.first
                valueDate = pair.second
            }
            if (savingToSharedPreferences) {
                SharedPreference.setKey(SharedPreference.PLATINUM, value)
                SharedPreference.setKey(SharedPreference.PLATINUM_DATE, valueDate)
            }
            return Pair(value, valueDate)
        }

        private fun getCoursePalladium(
            savingToSharedPreferences: Boolean,
            date: LocalDate
        ): Pair<String, String> {
            var value = ""
            var valueDate = ""
            if (date == LocalDate.now()) {
                val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM)
                    .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
                if (response.statusCode != 200) throw UnknownHostException()
                val content: List<String> = result.get().split(',')
                value = content[4].replace(',', '.')
                valueDate = content[3].split(' ')[0]
            } else {
                val pair = getValuesCourses(Metal.PALLADIUM, date)
                value = pair.first
                valueDate = pair.second
            }
            if (savingToSharedPreferences) {
                SharedPreference.setKey(SharedPreference.PALLADIUM, value)
                SharedPreference.setKey(SharedPreference.PALLADIUM_DATE, valueDate)
            }
            return Pair(value, valueDate)
        }

        private fun getCourseRhodium(
            savingToSharedPreferences: Boolean,
            date: LocalDate
        ): Pair<String, String> {
            var value = ""
            var valueDate = ""
            if (date == LocalDate.now()) {
                val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_RHODIUM)
                    .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
                if (response.statusCode != 200) throw UnknownHostException()
                val content: List<String> = result.get().split(',')
                value = content[4].replace(',', '.')
                valueDate = content[3].split(' ')[0]
            } else {
                val pair = getValuesCourses(Metal.RHODIUM, date)
                value = pair.first
                valueDate = pair.second
            }
            if (savingToSharedPreferences) {
                SharedPreference.setKey(SharedPreference.RHODIUM, value)
                SharedPreference.setKey(SharedPreference.RHODIUM_DATE, valueDate)
            }
            return Pair(value, valueDate)
        }

        @Suppress("ReplaceCallWithBinaryOperator")
        fun getValues(database: Database, date: LocalDate = LocalDate.now()) {
            val savingToSharedPreferences: Boolean = isCoursesSelected() == false
            val (usdPln, usdDate) = getCourseUsdPln(savingToSharedPreferences, date)
            val (eurPln, eurDate) = getCourseEurPln(savingToSharedPreferences, date)
            val (platinum, platinumDate) = getCoursePlatinum(savingToSharedPreferences, date)
            val (palladium, palladiumDate) = getCoursePalladium(savingToSharedPreferences, date)
            val (rhodium, rhodiumDate) = getCourseRhodium(savingToSharedPreferences, date)
            if (usdDate.equals(eurDate) && eurDate.equals(platinumDate) && platinumDate.equals(
                    palladiumDate
                ) && palladiumDate.equals(rhodiumDate)
            ) {
                val commonDate = Formatter.formatStringDate(eurDate)
                val localDate = Parser.parseStringDateToLocalDate(commonDate)
                try {
                    database.insertCourses(
                        ModelCourse(
                            platinum,
                            palladium,
                            rhodium,
                            eurPln,
                            usdPln,
                            commonDate,
                            localDate.yearMonth.toString()
                        )
                    )
                } catch (_: Exception) {
                    //
                }
            }
            SharedPreference.setKey(
                SharedPreference.UPDATE_COURSE_TIMESTAMP,
                Date().time.toString()
            )
        }

        fun saveSelectedCourses(modelCourse: ModelCourse) {
            val date = Parser.parseStringDateToLocalDate(modelCourse.date)
            SharedPreference.setKey(SharedPreference.ACTUAL_COURSES_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.USD_PLN, modelCourse.usdPln)
            SharedPreference.setKey(SharedPreference.USD_PLN_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.EUR_PLN, modelCourse.eurPln)
            SharedPreference.setKey(SharedPreference.EUR_PLN_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.PLATINUM, modelCourse.platinum)
            SharedPreference.setKey(SharedPreference.PLATINUM_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.PALLADIUM, modelCourse.palladium)
            SharedPreference.setKey(SharedPreference.PALLADIUM_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.RHODIUM, modelCourse.rhodium)
            SharedPreference.setKey(SharedPreference.RHODIUM_DATE, date.toString())
        }

        fun isCoursesSelected(): Boolean {
            val actualCoursesChoice =
                SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_CHOICE)
            val actualCoursesDate = SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
            return actualCoursesChoice == "0" && actualCoursesDate.isNotEmpty()
        }

        fun calculateCoursesToPln(courseInDollar: String, dollarCourse: String): String {
            if (courseInDollar.isEmpty()) return (0.00).toString()
            return (courseInDollar.toFloat() * (if (dollarCourse.isEmpty()) (0.0F) else dollarCourse.toFloat())).toString()
        }
    }
}
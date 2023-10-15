package pl.autokat.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.kittinunf.fuel.Fuel
import com.github.ramonrabello.opencv.android.ktx.threshold
import com.googlecode.tesseract.android.TessBaseAPI
import com.kizitonwose.calendarview.utils.yearMonth
import org.json.JSONObject
import org.jsoup.Jsoup
import org.opencv.core.Mat
import pl.autokat.enums.Currency
import pl.autokat.enums.Metal
import pl.autokat.exceptions.NoneCoursesException
import pl.autokat.models.ModelCourse
import java.net.URL
import java.net.UnknownHostException
import java.time.LocalDate
import java.util.*
import kotlin.math.absoluteValue

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

        private fun getUrlNBP(currency: Currency, date: LocalDate?): String {
            val basicURL = when (currency) {
                Currency.USD -> {
                    MY_CATALYST_VALUES_URL_USD_PLN
                }
                Currency.EUR -> {
                    MY_CATALYST_VALUES_URL_EUR_PLN
                }
                else -> {
                    ""
                }
            }
            if (date == null) {
                return "$basicURL?format=json"
            }
            return "$basicURL/$date?format=json"
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

        private fun getValueKitcoRhodium(date: LocalDate, dataPathTessBaseAPI: String): String {
            var tessBaseAPI: TessBaseAPI? = null
            try {
                tessBaseAPI = TessBaseAPI()
                tessBaseAPI.init(dataPathTessBaseAPI, "digits")
                val byteArray = URL(getURLKitcoRhodiumTable()).readBytes()
                var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                val cropTop = 50
                val cropLeft = 40
                val cropBottom = 40
                bitmap = Bitmap.createBitmap(
                    bitmap,
                    cropLeft,
                    cropTop,
                    bitmap.width - cropLeft,
                    bitmap.height - cropTop - cropBottom
                )
                Mat().threshold(bitmap, 106.0) {
                    tessBaseAPI.setImage(it)
                }
                val text = tessBaseAPI.utF8Text
                val rowText = text.split("\n")[date.month.value - 1]
                val yearShortcut = date.year.absoluteValue % 2000
                return rowText.split(" ")[yearShortcut]
            } finally {
                tessBaseAPI?.clear()
            }
        }

        private fun getHistoricalCourses(
            metal: Metal,
            date: LocalDate,
            dataPathTessBaseAPI: String
        ): String {
            val courseString = when (metal) {
                Metal.PLATINUM -> {
                    getValueKitcoLondonFix(date, "td.pt.pm", "td.pt.am")
                }
                Metal.PALLADIUM -> {
                    getValueKitcoLondonFix(date, "td.pl.pm", "td.pl.am")
                }
                Metal.RHODIUM -> {
                    getValueKitcoRhodium(date, dataPathTessBaseAPI)
                }
            }
            return courseString.toDouble().div(Configuration.OZ_VALUE).toString()
        }

        private fun getCourse(
            currency: Currency,
            savingToSharedPreferences: Boolean,
            dateHistorical: LocalDate? = null,
            downloadedHistoricalCourses: Boolean = false
        ): Pair<String, String> {
            val (_, response, result) = Fuel.get(
                getUrlNBP(
                    currency,
                    if (downloadedHistoricalCourses) dateHistorical else null
                )
            ).responseString()
            if (downloadedHistoricalCourses == true && response.statusCode == 404) throw NoneCoursesException()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                when (currency) {
                    Currency.USD -> {
                        SharedPreference.setKey(SharedPreference.USD_PLN, value)
                        SharedPreference.setKey(SharedPreference.USD_PLN_DATE, valueDate)
                    }
                    Currency.EUR -> {
                        SharedPreference.setKey(SharedPreference.EUR_PLN, value)
                        SharedPreference.setKey(SharedPreference.EUR_PLN_DATE, valueDate)
                    }
                    else -> {
                        //
                    }
                }
            }
            return Pair(value, valueDate)
        }

        private fun getCourse(
            metal: Metal,
            savingToSharedPreferences: Boolean,
            dateHistorical: LocalDate = LocalDate.now(),
            dataPathTessBaseAPI: String = "",
            downloadedHistoricalCourses: Boolean = false
        ): Pair<String, String> {
            val value : String?
            val valueDate : String?
            if (downloadedHistoricalCourses == false) {
                val (_, response, result) = when (metal) {
                    Metal.PLATINUM -> {
                        Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PLATINUM)
                            .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                            .responseString()
                    }
                    Metal.PALLADIUM -> {
                        Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM)
                            .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                            .responseString()
                    }
                    Metal.RHODIUM -> {
                        Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_RHODIUM)
                            .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                            .responseString()
                    }
                }
                if (response.statusCode != 200) throw UnknownHostException()
                val content: List<String> = result.get().split(',')
                value = content[4].replace(',', '.')
                valueDate = content[3].split(' ')[0]
            } else {
                value = getHistoricalCourses(metal, dateHistorical, dataPathTessBaseAPI)
                valueDate = dateHistorical.toString()
            }
            if (savingToSharedPreferences) {
                when (metal) {
                    Metal.PLATINUM -> {
                        SharedPreference.setKey(SharedPreference.PLATINUM, value)
                        SharedPreference.setKey(SharedPreference.PLATINUM_DATE, valueDate)
                    }
                    Metal.PALLADIUM -> {
                        SharedPreference.setKey(SharedPreference.PALLADIUM, value)
                        SharedPreference.setKey(SharedPreference.PALLADIUM_DATE, valueDate)
                    }
                    Metal.RHODIUM -> {
                        SharedPreference.setKey(SharedPreference.RHODIUM, value)
                        SharedPreference.setKey(SharedPreference.RHODIUM_DATE, valueDate)
                    }
                }
            }
            return Pair(value, valueDate)
        }

        @Suppress("ReplaceCallWithBinaryOperator")
        fun getValues(
            database: Database,
            savingToSharedPreferences: Boolean = isCoursesSelected() == false,
            dateHistorical: LocalDate = LocalDate.now(),
            dataPathTessBaseAPI: String = "",
            downloadedHistoricalCourses: Boolean = false
        ) {
            val (usdPln, usdDate) = getCourse(
                Currency.USD,
                savingToSharedPreferences,
                dateHistorical,
                downloadedHistoricalCourses
            )
            val (eurPln, eurDate) = getCourse(
                Currency.EUR, savingToSharedPreferences,
                dateHistorical,
                downloadedHistoricalCourses
            )
            val (platinum, platinumDate) = getCourse(
                Metal.PLATINUM,
                savingToSharedPreferences,
                dateHistorical,
                dataPathTessBaseAPI,
                downloadedHistoricalCourses
            )
            val (palladium, palladiumDate) = getCourse(
                Metal.PALLADIUM,
                savingToSharedPreferences,
                dateHistorical,
                dataPathTessBaseAPI,
                downloadedHistoricalCourses
            )
            val (rhodium, rhodiumDate) = getCourse(
                Metal.RHODIUM,
                savingToSharedPreferences,
                dateHistorical,
                dataPathTessBaseAPI,
                downloadedHistoricalCourses
            )
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
            if (downloadedHistoricalCourses == false) {
                SharedPreference.setKey(
                    SharedPreference.UPDATE_COURSE_TIMESTAMP,
                    Date().time.toString()
                )
            }
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
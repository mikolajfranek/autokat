package pl.autokat.components

import com.github.kittinunf.fuel.Fuel
import com.kizitonwose.calendarview.utils.yearMonth
import org.json.JSONObject
import pl.autokat.models.ModelCourse
import java.net.UnknownHostException
import java.util.*

class MyCoursesValues {
    companion object {
        private const val MY_CATALYST_VALUES_URL_USD_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        private const val MY_CATALYST_VALUES_URL_EUR_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        private const val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM =
            "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM =
            "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM =
            "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        private const val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        //get status of selected courses
        fun isCoursesSelected(): Boolean {
            val actualCoursesChoice =
                SharedPreferences.getKeyFromFile(SharedPreferences.ACTUAL_COURSES_CHOICE)
            val actualCoursesDate =
                SharedPreferences.getKeyFromFile(SharedPreferences.ACTUAL_COURSES_DATE)
            return actualCoursesChoice.equals("0") && actualCoursesDate.isNotEmpty()
        }

        //save courses
        fun saveSelectedCourses(modelCourse: ModelCourse) {
            val date = MyConfiguration.convertStringDateToLocalDate(modelCourse.date)
            //saving about choice date
            SharedPreferences.setKeyToFile(
                SharedPreferences.ACTUAL_COURSES_DATE,
                date.toString()
            )
            //usd
            SharedPreferences.setKeyToFile(
                SharedPreferences.USD_PLN,
                modelCourse.usdPln
            )
            SharedPreferences.setKeyToFile(
                SharedPreferences.USD_PLN_DATE,
                date.toString()
            )
            //eur
            SharedPreferences.setKeyToFile(
                SharedPreferences.EUR_PLN,
                modelCourse.eurPln
            )
            SharedPreferences.setKeyToFile(
                SharedPreferences.EUR_PLN_DATE,
                date.toString()
            )
            //platinum
            SharedPreferences.setKeyToFile(
                SharedPreferences.PLATIUNUM,
                modelCourse.platinum
            )
            SharedPreferences.setKeyToFile(
                SharedPreferences.PLATIUNUM_DATE,
                date.toString()
            )
            //palladium
            SharedPreferences.setKeyToFile(
                SharedPreferences.PALLADIUM,
                modelCourse.palladium
            )
            SharedPreferences.setKeyToFile(
                SharedPreferences.PALLADIUM_DATE,
                date.toString()
            )
            //rhodium
            SharedPreferences.setKeyToFile(
                SharedPreferences.RHODIUM,
                modelCourse.rhodium
            )
            SharedPreferences.setKeyToFile(
                SharedPreferences.RHODIUM_DATE,
                date.toString()
            )
        }

        //get course usd -> pln
        private fun getCourseUsdPln(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_USD_PLN)
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                SharedPreferences.setKeyToFile(
                    SharedPreferences.USD_PLN,
                    value
                )
                SharedPreferences.setKeyToFile(
                    SharedPreferences.USD_PLN_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course eur -> pln
        private fun getCourseEurPln(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_EUR_PLN)
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                SharedPreferences.setKeyToFile(
                    SharedPreferences.EUR_PLN,
                    value
                )
                SharedPreferences.setKeyToFile(
                    SharedPreferences.EUR_PLN_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course platinum
        private fun getCoursePlatinum(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PLATINUM)
                .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                SharedPreferences.setKeyToFile(
                    SharedPreferences.PLATIUNUM,
                    value
                )
                SharedPreferences.setKeyToFile(
                    SharedPreferences.PLATIUNUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course palladium
        private fun getCoursePalladium(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM)
                .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                SharedPreferences.setKeyToFile(
                    SharedPreferences.PALLADIUM,
                    value
                )
                SharedPreferences.setKeyToFile(
                    SharedPreferences.PALLADIUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course rhodium
        private fun getCourseRhodium(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MY_CATALYST_VALUES_URL_CATALYST_RHODIUM)
                .header(mapOf("Origin" to MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                SharedPreferences.setKeyToFile(
                    SharedPreferences.RHODIUM,
                    value
                )
                SharedPreferences.setKeyToFile(
                    SharedPreferences.RHODIUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get all course
        @Suppress("ReplaceCallWithBinaryOperator")
        fun getValues(database: MyDatabase) {
            val savingToSharedPreferences: Boolean = isCoursesSelected() == false
            //course of usd -> pln
            val (usdPln, usdDate) = getCourseUsdPln(savingToSharedPreferences)
            //course of eur -> pln
            val (eurPln, eurDate) = getCourseEurPln(savingToSharedPreferences)
            //course of platinum
            val (platinum, platinumDate) = getCoursePlatinum(savingToSharedPreferences)
            //course of palladium
            val (palladium, palladiumDate) = getCoursePalladium(savingToSharedPreferences)
            //course of rhodium
            val (rhodium, rhodiumDate) = getCourseRhodium(savingToSharedPreferences)
            //always saving to database if dates are equal
            if (usdDate.equals(eurDate) && eurDate.equals(platinumDate) && platinumDate.equals(
                    palladiumDate
                ) && palladiumDate.equals(rhodiumDate)
            ) {
                val commonDate = Formatter.formatStringDate(eurDate)
                val localDate = MyConfiguration.convertStringDateToLocalDate(commonDate)
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
                    //nothing
                }
            }
            //save timestamp of update
            SharedPreferences.setKeyToFile(
                SharedPreferences.UPDATE_COURSE_TIMESTAMP,
                Date().time.toString()
            )
        }
    }
}
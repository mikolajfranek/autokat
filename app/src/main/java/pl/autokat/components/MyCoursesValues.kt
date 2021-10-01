package pl.autokat.components

import com.github.kittinunf.fuel.Fuel
import com.kizitonwose.calendarview.utils.yearMonth
import org.json.JSONObject
import java.net.UnknownHostException
import java.util.*

class MyCoursesValues {
    companion object {
        //save courses
        fun saveSelectedCourses(myCourses: MyCourses) {
            val date = MyConfiguration.formatDateToLocalDate(myCourses.date)
            //saving about choice date
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE,
                date.toString()
            )
            //usd
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN,
                myCourses.usdPln
            )
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE,
                date.toString()
            )
            //eur
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN,
                myCourses.eurPln
            )
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE,
                date.toString()
            )
            //platinum
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM,
                myCourses.platinum
            )
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE,
                date.toString()
            )
            //palladium
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM,
                myCourses.palladium
            )
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE,
                date.toString()
            )
            //rhodium
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM,
                myCourses.rhodium
            )
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE,
                date.toString()
            )
        }

        //get course usd -> pln
        private fun getCourseUsdPln(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_USD_PLN)
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN,
                    value
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course eur -> pln
        private fun getCourseEurPln(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_EUR_PLN)
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN,
                    value
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course platinum
        private fun getCoursePlatinum(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PLATINUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM,
                    value
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course palladium
        private fun getCoursePalladium(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM,
                    value
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get course rhodium
        private fun getCourseRhodium(savingToSharedPreferences: Boolean): Pair<String, String> {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_RHODIUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN))
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val content: List<String> = result.get().split(',')
            val value = content[4].replace(',', '.')
            val valueDate = content[3].split(' ')[0]
            if (savingToSharedPreferences) {
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM,
                    value
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE,
                    valueDate
                )
            }
            return Pair(value, valueDate)
        }

        //get all course
        @Suppress("ReplaceCallWithBinaryOperator")
        fun getValues(database: MyDatabase) {
            val actualCoursesChoice =
                MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_CHOICE)
            val actualCoursesDate =
                MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE)
            val gettingFromDatabase: Boolean =
                actualCoursesChoice.equals("0") && actualCoursesDate.isNotEmpty()
            val savingToSharedPreferences: Boolean = gettingFromDatabase == false
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
                val commonDate = MyConfiguration.formatDate(eurDate)
                val localDate = MyConfiguration.formatDateToLocalDate(commonDate)
                try {
                    database.insertCourses(
                        MyCourses(
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
            MySharedPreferences.setKeyToFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP,
                Date().time.toString()
            )
        }
    }
}
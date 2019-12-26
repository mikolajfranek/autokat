package pl.autokat

import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MyCatalystValues {
    companion object {

        //get course usd -> pln
        private fun getCourseUsdPln() {
            val resultFromUrl = URL(MyConfiguration.MY_CATALYST_VALUES_URL_USD_PLN).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            val rate = resultJson.getJSONArray("rates").getJSONObject(0)
            val effectiveDate = rate.getString("effectiveDate")
            val value = MyConfiguration.formatFloat(rate.getDouble("mid").toFloat())
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN, value)
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE, effectiveDate)
        }

        //get course eur -> pln
        private fun getCourseEurPln() {
            val resultFromUrl = URL(MyConfiguration.MY_CATALYST_VALUES_URL_EUR_PLN).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            val rate = resultJson.getJSONArray("rates").getJSONObject(0)
            val effectiveDate = rate.getString("effectiveDate")
            val value = MyConfiguration.formatFloat(rate.getDouble("mid").toFloat())
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN, value)
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE, effectiveDate)
        }

        //get course platinum
        private fun getCoursePlatinum() {
            with(URL(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PLATINUM).openConnection() as HttpURLConnection) {
                setRequestProperty("Origin", MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)
                val content = inputStream.bufferedReader().readText().split(',')
                val effectiveDate =  content[3].split(' ')[0]
                val value = MyConfiguration.formatFloat(content[4].toFloat())
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM, value)
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE, effectiveDate)
            }
        }

        //get course palladium
        private fun getCoursePalladium() : String {
            with(URL(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM).openConnection() as HttpURLConnection) {
                setRequestProperty("Origin", MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)
                val content = inputStream.bufferedReader().readText().split(',')
                val effectiveDate =  content[3].split(' ')[0]
                val value = MyConfiguration.formatFloat(content[4].toFloat())
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM, value)
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE, effectiveDate)
                return effectiveDate
            }
        }

        //get course rhodium
        private fun getCourseRhodium(): String {
            with(URL(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_RHODIUM).openConnection() as HttpURLConnection) {
                setRequestProperty("Origin", MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)
                val content = inputStream.bufferedReader().readText().split(',')
                val effectiveDate =  content[3].split(' ')[0]
                val value = MyConfiguration.formatFloat(content[4].toFloat())
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM, value)
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE, effectiveDate)
                return effectiveDate
            }
        }

        //get all course
        fun getValues() : String {
            //course of usd -> pln
            getCourseUsdPln()
            //course of eur -> pln
            getCourseEurPln()
            //course of platinum
            getCoursePlatinum()
            //course of palladium
            val dateEffective = getCoursePalladium()
            //course of rhoudium
            getCourseRhodium()
            //save timestamp of update
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP, Date().time.toString())
            return dateEffective
        }
    }
}
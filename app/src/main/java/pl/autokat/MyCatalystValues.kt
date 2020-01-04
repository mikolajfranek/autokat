package pl.autokat

import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.net.UnknownHostException
import java.util.*

class MyCatalystValues {
    companion object {
        //get course usd -> pln
        private fun getCourseUsdPln() {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_USD_PLN).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val rate : JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN, rate.getString("mid").replace(',','.'))
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE, rate.getString("effectiveDate"))
        }
        //get course eur -> pln
        private fun getCourseEurPln() {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_EUR_PLN).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val rate : JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN, rate.getString("mid").replace(',','.'))
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE, rate.getString("effectiveDate"))
        }
        //get course platinum
        private fun getCoursePlatinum() {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PLATINUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val content : List<String> = result.get().split(',')
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM, content[4].replace(',','.'))
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE, content[3].split(' ')[0])
        }
        //get course palladium
        private fun getCoursePalladium() {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val content : List<String> = result.get().split(',')
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM, content[4].replace(',','.'))
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE, content[3].split(' ')[0])
        }
        //get course rhodium
        private fun getCourseRhodium() {
            val (_, response, result) = Fuel.get(MyConfiguration.MY_CATALYST_VALUES_URL_CATALYST_RHODIUM)
                .header(mapOf("Origin" to MyConfiguration.MY_CATALYST_VALUES_HEADER_ORIGIN)).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val content : List<String> = result.get().split(',')
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM, content[4].replace(',','.'))
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE, content[3].split(' ')[0])
        }
        //get all course
        fun getValues(){
            //course of usd -> pln
            getCourseUsdPln()
            //course of eur -> pln
            getCourseEurPln()
            //course of platinum
            getCoursePlatinum()
            //course of palladium
            getCoursePalladium()
            //course of rhoudium
            getCourseRhodium()
            //save timestamp of update
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP, Date().time.toString())
        }
    }
}
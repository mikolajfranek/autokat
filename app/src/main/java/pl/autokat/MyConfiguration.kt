package pl.autokat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MyConfiguration {
    companion object {

        /* databases */
        val DATABASE_VERSION : Int = 1
        val DATABASE_NAME_OF_FILE : String = "autokat.db"
        val DATABASE_PAGINATE_LIMIT : String = "50"

        /* table catalyst */
        val DATABASE_TABLE_CATALYST = "catalyst"
        val DATABASE_TABLE_CATALYST_ID = "id"
        val DATABASE_TABLE_CATALYST_ID_PICTURE = "id_picture"
        val DATABASE_TABLE_CATALYST_NAME = "name"
        val DATABASE_TABLE_CATALYST_BRAND = "brand"
        val DATABASE_TABLE_CATALYST_PLATINUM = "platinum"
        val DATABASE_TABLE_CATALYST_PALLADIUM = "palladium"
        val DATABASE_TABLE_CATALYST_RHODIUM = "rhodium"
        val DATABASE_TABLE_CATALYST_TYPE = "type"
        val DATABASE_TABLE_CATALYST_WEIGHT = "weight"

        /* shared preferences */
        val MY_SHARED_PREFERENCES_NAME : String = "MyKatSharedPreferences"
        val MY_SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE
        //about user
        val MY_SHARED_PREFERENCES_KEY_LOGIN : String = "Login"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END : String = "LicenceDate"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP_CURRENT : String = "LicenceTimestampCurrent"
        val MY_SHARED_PREFERENCES_KEY_DISCOUNT : String = "Discount"
        //about courses elements
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM : String = "Palladium"
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE : String = "PalladiumDate"
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM : String = "Platinum"
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE : String = "PlatinumDate"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM : String = "Rhodium"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE : String = "RhodiumDate"
        //about courses exchanges
        val MY_SHARED_PREFERENCES_KEY_USD_PLN : String = "UsdPln"
        val MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE : String = "UsdPlnDate"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN : String = "EurPln"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE : String = "EurPlnDate"

        /* info */
        //color
        val INFO_MESSAGE_COLOR_FAILED:  Int = Color.RED
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.GRAY
        //message
        val INFO_MESSAGE_WAIT_AUTHENTICATE : String = "Trwa uwierzytelnianie..."
        val INFO_MESSAGE_UNHANDLED_EXCEPTION : String = "Wystąpił nieobsłużony błąd"
        val INFO_MESSAGE_USER_NEVER_LOGGED : String = "Wprowadź nazwę użytkownika"
        val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"

        /* spreadsheet */
        val MY_SPREADSHEET_OUTPUT_JSON : String = "out:json"
        val MY_SPREADSHEET_URL_PREFIX : String = "https://docs.google.com/a/google.com/spreadsheets/d/"
        val MY_SPREADSHEET_URL_SUFIX : String = "/gviz/tq"
        val MY_SPREADSHEET_QUERY_OUTPUT_JSON : String = "tqx"
        val MY_SPREADSHEET_QUERY_WHERE_CLAUSE: String = "tq"
        val MY_SPREADSHEET_QUERY_KEY: String = "key"

        /* courses exchange */
        val MY_CATALYST_VALUES_URL_USD_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        val MY_CATALYST_VALUES_URL_EUR_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM = "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM = "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM  = "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"


        /* helpers methods */
        //parse string to json
        fun parseResultToJson(text: String): JSONObject {
            val regex = "\\{.*\\}".toRegex()
            val resultRegex = regex.find(text)?.value
            return JSONObject(resultRegex)
        }

        //return timestamp of date in milliseconds
        @SuppressLint("SimpleDateFormat")
        fun getTimestampFromString(date: String) : Long {
            val oneDayInMilliseconds : Long = 86400000
            return ((SimpleDateFormat("yyyy-MM-dd").parse(date)!!.time) + (oneDayInMilliseconds))
        }

        //check current date if is greater than date from string
        fun cheekIfCurrentDateIsGreater(date: String) : Boolean{
            val date1 = Date().time
            val date2 = getTimestampFromString(date)
            if((date1 > date2) == false) {
                return false
            }
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP_CURRENT, date1.toString())
            return true
        }

        //check current date if is grater than timestamp saved in shared preferences
        fun checkLicence() : Boolean{
            val date1 = Date().time
            val date2 : Long = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP_CURRENT).toLong()
            if((date1 > date2) == false) {
                return false
            }
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP_CURRENT, date1.toString())
            return true
        }
    }
}
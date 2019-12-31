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
        val DATABASE_PAGINATE_LIMIT : Int = 5

        /* table catalyst */
        val DATABASE_TABLE_CATALYST = "catalyst"
        val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        val DATABASE_ELEMENT_CATALYST_ID = "id"
        val DATABASE_ELEMENT_CATALYST_ID_PICTURE = "id_picture"
        val DATABASE_ELEMENT_CATALYST_PICTURE = "picture"
        val DATABASE_ELEMENT_CATALYST_NAME = "name"
        val DATABASE_ELEMENT_CATALYST_BRAND = "brand"
        val DATABASE_ELEMENT_CATALYST_PLATINUM = "platinum"
        val DATABASE_ELEMENT_CATALYST_PALLADIUM = "palladium"
        val DATABASE_ELEMENT_CATALYST_RHODIUM = "rhodium"
        val DATABASE_ELEMENT_CATALYST_TYPE = "type"
        val DATABASE_ELEMENT_CATALYST_WEIGHT = "weight"

        /* shared preferences */
        val MY_SHARED_PREFERENCES_NAME : String = "MyKatSharedPreferences"
        val MY_SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE
        //about user
        val MY_SHARED_PREFERENCES_KEY_LOGIN : String = "Login"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END : String = "LicenceDate"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP : String = "LicenceTimestamp"
        val MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP : String = "UpdateCourseTimestamp"
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
        val INFO_MESSAGE_WAIT_UPDATE : String = "Trwa aktualizacja...."
        val INFO_UPDATE_SUCCESS : String = "Aktualizacja przebiegła pomyślnie"
        val INFO_UPDATE_FAILED : String = "Wystąpił błąd podczas aktualizacji"
        val INFO_DOWNLOAD_BITMAP_FAILED : String = "Wystąpił błąd podczas pobierania obrazu"
        val INFO_DOWNLOAD_BITMAP_WAIT: String = "Trwa pobieranie obrazu..."

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

        val MY_SPREADSHEET_USERS_COLUMN_LOGIN : String = "A"
        val MY_SPREADSHEET_USERS_COLUMN_UUID : String = "B"

        val MY_SPREADSHEET_CATALYST_COLUMN_ID : String = "A"
        val MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE : String = "I"

        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_ID : Int = 0
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_NAME : Int = 1
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_BRAND : Int = 2
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_PLATINUM : Int = 3
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_PALLADIUM : Int = 4
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_RHODIUM : Int = 5
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_TYPE : Int = 6
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_WEIGHT : Int = 7
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_ID_PICTURE : Int = 8
        val MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_PICTURE : Int = 9




        /* courses exchange */
        val MY_CATALYST_VALUES_URL_USD_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        val MY_CATALYST_VALUES_URL_EUR_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM = "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM = "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM  = "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        /* others */
        val ONE_DAY_IN_MILLISECONDS : Long = 86400000
        val REQUEST_CODE_READ_PHONE_STATE: Int = 0

        /* helpers methods */
        //parse string to json
        fun parseResultToJson(text: String): JSONObject {
            val regex = "\\{.*\\}".toRegex()
            val resultRegex = regex.find(text)!!.value
            return JSONObject(resultRegex)
        }

        //return timestamp of date in milliseconds
        @SuppressLint("SimpleDateFormat")
        fun getTimestampFromString(date: String, addDay: Boolean) : Long {
            if(addDay){
                return (SimpleDateFormat("yyyy-MM-dd").parse(date)!!.time) + ONE_DAY_IN_MILLISECONDS
            }
            return (SimpleDateFormat("yyyy-MM-dd").parse(date)!!.time)
        }

        //check if is greater and save to shared preferences if it is
        fun checkIfIsGreater(timestamp1: Long, timestamp2: Long) : Boolean{
            if((timestamp1 > timestamp2) == false) {
                return false
            }
            MySharedPreferences.setKeyToFile(MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP, timestamp1.toString())
            return true
        }

        //check current date if is greater than date from string
        fun checkIfCurrentDateIsGreater(date: String, addDay: Boolean) : Boolean{
            val timestamp1 = Date().time
            val timestamp2 = getTimestampFromString(date, addDay)
            return checkIfIsGreater(timestamp1, timestamp2)
        }

        //check current date if is grater than timestamp saved in shared preferences
        fun checkTimestamp() : Boolean{
            val timestamp1 = Date().time
            val timestamp2 : Long = MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_LICENCE_TIMESTAMP).toLong()
            return checkIfIsGreater(timestamp1, timestamp2)
        }

        //get date in format dd.mm.yyyy
        @SuppressLint("SimpleDateFormat")
        fun formatDate(date: String) : String{
            return SimpleDateFormat("dd.MM.yyyy").format((SimpleDateFormat("yyyy-MM-dd").parse(date)!!)).toString()
        }

        //get format of float from string
        fun formatStringFloat(floatString: String, precision: Int) : String{
            return String.format("%." + precision + "f", floatString.toFloat())
        }

        //get pln from dolar string
        fun getPlnFromDolar(dolar: String) : String{
            return (dolar.toFloat() * MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_USD_PLN).toFloat()).toString()
        }

        //get url to google
        fun getPictureUrlFromGoogle(urlShared: String, width: Int, height: Int) : String {
            var url = urlShared

            val regexBefore = ".*\\/d\\/".toRegex()
            val resultRegex = regexBefore.find(url)!!.value

            url = url.substring(resultRegex.length)
            val pictureIdFromGoogle = url.substring(0,url.indexOf('/'))

            return "https://lh3.googleusercontent.com/u/0/d/$pictureIdFromGoogle=w$width-h$height"
        }
    }
}
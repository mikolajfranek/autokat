package pl.autokat.components

import android.annotation.SuppressLint
import android.graphics.Color
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.enums.TimeChecking
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MyConfiguration {
    companion object {
        const val PRODUCTION: Boolean = false
        const val VERSION_APP: String = "1.0.7"
        const val DATABASE_VERSION_1_0_6: Int = 4
        const val DATABASE_VERSION: Int = 4
        const val DATABASE_NAME_OF_FILE: String = "autokat.db"
        const val DATABASE_FILE_PATH_ASSETS: String = "databases/$DATABASE_NAME_OF_FILE"

        /* color */
        val INFO_MESSAGE_COLOR_WHITE: Int = Color.parseColor("#FFFFFF")
        val INFO_MESSAGE_COLOR_FAILED: Int = Color.parseColor("#EF4836")
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.parseColor("#363636")

        /* info */
        const val INFO_MESSAGE_USER_NEVER_LOGGED: String = "Wprowadź nazwę użytkownika"
        const val INFO_MESSAGE_WAIT_AUTHENTICATE: String = "Trwa uwierzytelnianie…"
        const val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"
        const val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        const val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        const val INFO_MESSAGE_NETWORK_FAILED: String = "Brak połączenia"
        const val INFO_MESSAGE_UNHANDLED_EXCEPTION: String = "Wystąpił błąd"
        const val INFO_MESSAGE_SAVE_EMPTY_VALUE: String = "Nie można zapisać pustej wartości"
        const val INFO_MESSAGE_WAIT_UPDATE: String = "Trwa aktualizacja…"
        const val INFO_UPDATE_SUCCESS: String = "Aktualizacja przebiegła pomyślnie"
        const val INFO_UPDATE_FAILED: String = "Wystąpił błąd podczas aktualizacji"
        const val INFO_DOWNLOAD_BITMAP_WAIT: String = "Trwa pobieranie obrazu…"
        const val INFO_DOWNLOAD_BITMAP_FAILED: String = "Wystąpił błąd podczas pobierania obrazu"
        const val INFO_DOWNLOAD_BITMAP_STATUS: String = "Status pobieranych miniatur"
        const val INFO_DOWNLOAD_BITMAP_SUCCESS: String = "Baza danych jest aktualna"
        const val INFO_EMPTY_DATABASE: String = "Baza danych jest pusta"
        const val INFO_DATABASE_EXPIRE: String = "Baza danych nie jest aktualna"
        const val INFO_MESSAGE_ADDED_HISTORY_FILTER: String =
            "Pomyślnie zapisano nazwę do filtrowania"
        const val INFO_MESSAGE_DELETED_HISTORY_FILTER: String =
            "Pomyślnie usunięto nazwę do filtrowania"
        const val INFO_MESSAGE_REFRESH_COURSES: String = "Odśwież wartości kursów"
























        const val ONE_HOUR_IN_MILLISECONDS: Long = 3600000L

        //


        /* licence and time */
        private const val URL_TIMESTAMP: String =
            "https://worldtimeapi.org/api/timezone/Europe/Warsaw"
        const val ONE_DAY_IN_MILLISECONDS: Long = 86400000

        //shared preferences

        /* spreadsheet */
        //users column number
        const val MY_SPREADSHEET_USERS_ID: Int = 0
        const val MY_SPREADSHEET_USERS_LOGIN: Int = 1
        const val MY_SPREADSHEET_USERS_UUID: Int = 2
        const val MY_SPREADSHEET_USERS_LICENCE: Int = 3
        const val MY_SPREADSHEET_USERS_DISCOUNT: Int = 4
        const val MY_SPREADSHEET_USERS_VISIBILITY: Int = 5
        const val MY_SPREADSHEET_USERS_MINUS_PLATINIUM: Int = 6
        const val MY_SPREADSHEET_USERS_MINUS_PALLADIUM: Int = 7
        const val MY_SPREADSHEET_USERS_MINUS_RHODIUM: Int = 8

        //users column letter
        const val MY_SPREADSHEET_USERS_COLUMN_ID: String = "A"
        const val MY_SPREADSHEET_USERS_COLUMN_LOGIN: String = "B"
        const val MY_SPREADSHEET_USERS_COLUMN_UUID: String = "C"
        const val MY_SPREADSHEET_USERS_COLUMN_LICENCE: String = "D"
        const val MY_SPREADSHEET_USERS_COLUMN_DISCOUNT: String = "E"
        const val MY_SPREADSHEET_USERS_COLUMN_VISIBILITY: String = "F"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_PLATINIUM: String = "G"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_PALLADIUM: String = "H"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_RHODIUM: String = "I"

        //catalysts column number
        const val MY_SPREADSHEET_CATALYST_ID: Int = 0
        const val MY_SPREADSHEET_CATALYST_NAME: Int = 1
        const val MY_SPREADSHEET_CATALYST_BRAND: Int = 2
        const val MY_SPREADSHEET_CATALYST_WEIGHT: Int = 3
        const val MY_SPREADSHEET_CATALYST_PLATINUM: Int = 4
        const val MY_SPREADSHEET_CATALYST_PALLADIUM: Int = 5
        const val MY_SPREADSHEET_CATALYST_RHODIUM: Int = 6
        const val MY_SPREADSHEET_CATALYST_TYPE: Int = 7
        const val MY_SPREADSHEET_CATALYST_ID_PICTURE: Int = 8
        const val MY_SPREADSHEET_CATALYST_URL_PICTURE: Int = 9

        //catalysts column letter
        const val MY_SPREADSHEET_CATALYST_COLUMN_ID: String = "A"
        const val MY_SPREADSHEET_CATALYST_COLUMN_NAME: String = "B"
        const val MY_SPREADSHEET_CATALYST_COLUMN_BRAND: String = "C"
        const val MY_SPREADSHEET_CATALYST_COLUMN_WEIGHT: String = "D"
        const val MY_SPREADSHEET_CATALYST_COLUMN_PLATTINUM: String = "E"
        const val MY_SPREADSHEET_CATALYST_COLUMN_PALLADIUM: String = "F"
        const val MY_SPREADSHEET_CATALYST_COLUMN_RHODIUM: String = "G"
        const val MY_SPREADSHEET_CATALYST_COLUMN_TYPE: String = "H"
        const val MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE: String = "I"
        const val MY_SPREADSHEET_CATALYST_COLUMN_URL_PICTURE: String = "J"

        /* spreadsheet sheet api v4 */
        const val MY_SPREADSHEET_URL_PREFIX_SHEETS_API: String =
            "https://sheets.googleapis.com/v4/spreadsheets/"
        const val MY_SPREADSHEET_PARAMETER_INPUT: String = "valueInputOption"
        const val MY_SPREADSHEET_PARAMETER_INPUT_VALUE: String = "USER_ENTERED"

        /* spreadsheet docs api */
        private const val MY_SPREADSHEET_URL_PREFIX_DOCS_API: String =
            "https://docs.google.com/a/google.com/spreadsheets/d/"
        private const val MY_SPREADSHEET_URL_SUFIX_DOCS_API: String = "/gviz/tq"
        val MY_SPREADSHEET_LOGIN_URL_DOCS_API: String =
            MY_SPREADSHEET_URL_PREFIX_DOCS_API + Secret.getSpreadsheetIdLogin() + MY_SPREADSHEET_URL_SUFIX_DOCS_API
        val MY_SPREADSHEET_CATALYST_URL_DOCS_API: String =
            MY_SPREADSHEET_URL_PREFIX_DOCS_API + Secret.getSpreadsheetIdCatalyst() + MY_SPREADSHEET_URL_SUFIX_DOCS_API
        const val MY_SPREADSHEET_PARAMETER_JSON: String = "tqx"
        const val MY_SPREADSHEET_PARAMETER_JSON_VALUE: String = "out:json"
        const val MY_SPREADSHEET_PARAMETER_WHERE: String = "tq"










        /* database */
        const val DATABASE_PAGINATE_LIMIT_CATALYST: Int = 5
        const val DATABASE_PAGINATE_LIMIT_HISTORY_FILTER: Int = 10

        // tables
        const val DATABASE_TABLE_CATALYST = "catalyst"
        const val DATABASE_TABLE_HISTORY_FILTER = "history_filter"
        const val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        const val DATABASE_TABLE_COURSES = "courses"

        //columns courses
        const val DATABASE_ELEMENT_COURSES_ID = "id"
        const val DATABASE_ELEMENT_COURSES_DATE = "date"
        const val DATABASE_ELEMENT_COURSES_YEARMONTH = "yearmonth"
        const val DATABASE_ELEMENT_COURSES_PLATINUM = "platinum"
        const val DATABASE_ELEMENT_COURSES_PALLADIUM = "palladium"
        const val DATABASE_ELEMENT_COURSES_RHODIUM = "rhodium"
        const val DATABASE_ELEMENT_COURSES_EUR_PLN = "eur_pln"
        const val DATABASE_ELEMENT_COURSES_USD_PLN = "usd_pln"

        //columns catalyst
        const val DATABASE_ELEMENT_CATALYST_ID = "id"
        const val DATABASE_ELEMENT_CATALYST_ID_PICTURE = "id_picture"
        const val DATABASE_ELEMENT_CATALYST_URL_PICTURE = "url_picture"
        const val DATABASE_ELEMENT_CATALYST_THUMBNAIL = "thumbnail"
        const val DATABASE_ELEMENT_CATALYST_NAME = "name"
        const val DATABASE_ELEMENT_CATALYST_BRAND = "brand"
        const val DATABASE_ELEMENT_CATALYST_PLATINUM = "platinum"
        const val DATABASE_ELEMENT_CATALYST_PALLADIUM = "palladium"
        const val DATABASE_ELEMENT_CATALYST_RHODIUM = "rhodium"
        const val DATABASE_ELEMENT_CATALYST_TYPE = "type"
        const val DATABASE_ELEMENT_CATALYST_WEIGHT = "weight"
        const val DATABASE_ELEMENT_CATALYST_TEMP_HITCOUNT = "hitcount"

        //columns history_filter
        const val DATABASE_ELEMENT_HISTORY_FILTER_ID = "id"
        const val DATABASE_ELEMENT_HISTORY_FILTER_NAME = "name"

        //columns sqlite_sequence
        const val DATABASE_ELEMENT_SQLITE_SEQUENCE_SEQ = "seq"
        const val DATABASE_ELEMENT_SQLITE_SEQUENCE_NAME = "name"









        /* others */
        const val REQUEST_CODE_READ_PHONE_STATE: Int = 0
        var IS_AVAILABLE_UPDATE: Boolean = false
















        /* methods */







        //check time depends from parameter
        @SuppressLint("SimpleDateFormat")
        fun checkTimeOnPhone(dateInput: String, timeChecking: TimeChecking): Boolean {
            when (timeChecking) {
                TimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    //time from web minus 1 hour must by greater than time now
                    val json = JSONObject(URL(URL_TIMESTAMP).readText())
                    val timestampWeb: Long =
                        (json.getLong("unixtime") * 1000L) - ONE_HOUR_IN_MILLISECONDS
                    val timestampPhone: Long = Date().time
                    if (timestampPhone > timestampWeb) {
                        SharedPreferences.setKeyToFile(
                            SharedPreferences.CURRENT_TIMESTAMP,
                            timestampPhone.toString()
                        )
                        return true
                    }
                    return false
                }
                TimeChecking.PARAMETER_IS_GREATER_THAN_NOW -> {
                    val timestamp: Long = Date().time
                    val timestampInput: Long =
                        (SimpleDateFormat("yyyy-MM-dd").parse(dateInput)!!.time) + ONE_DAY_IN_MILLISECONDS
                    return timestampInput > timestamp
                }
                TimeChecking.CHECKING_LICENCE -> {
                    val timestamp: Long = Date().time
                    val timestampLicence: Long = (SimpleDateFormat("yyyy-MM-dd").parse(
                        SharedPreferences.getKeyFromFile(
                            SharedPreferences.LICENCE_DATE_OF_END
                        )
                    )!!.time) + ONE_DAY_IN_MILLISECONDS
                    val timestampFromConfiguration: Long = SharedPreferences.getKeyFromFile(
                        SharedPreferences.CURRENT_TIMESTAMP
                    ).toLong()
                    if ((timestamp > timestampFromConfiguration) && (timestampLicence > timestamp)) {
                        SharedPreferences.setKeyToFile(
                            SharedPreferences.CURRENT_TIMESTAMP,
                            timestamp.toString()
                        )
                        return true
                    }
                    return false
                }
            }
        }




























        fun convertStringDateToLocalDate(date: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return LocalDate.parse(date, formatter)
        }

        //get pln from dolar string
        fun getPlnFromDolar(dolar: String, dolarCourses: String): String {
            if (dolar.isEmpty()) return (0.00).toString()
            return (dolar.toFloat() * (if (dolarCourses.isEmpty()) (0.0F) else dolarCourses.toFloat())).toString()
        }





        //decorator


        //get url to google












    }
}
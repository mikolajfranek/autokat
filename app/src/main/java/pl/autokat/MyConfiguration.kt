package pl.autokat

import android.content.Context
import android.graphics.Color

class MyConfiguration {
    companion object {
        //databases
        val DATABASE_VERSION : Int = 1
        val DATABASE_NAME_OF_FILE : String = "autokat.db"
        val DATABASE_PAGINATE_LIMIT : String = "50"

        //table catalyst
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

        //shared preferences
        val MY_SHARED_PREFERENCES_NAME : String = "MyKatSharedPreferences"
        val MY_SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE
        val MY_SHARED_PREFERENCES_KEY_LOGIN : String = "Login"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_DATE : String = "LicenceDate"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_END : String = "LicenceEnd"
        val MY_SHARED_PREFERENCES_KEY_DISCOUNT : String = "Discount"

        //info color
        val INFO_MESSAGE_COLOR_FAILED:  Int = Color.RED
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.GRAY

        //info message
        val INFO_MESSAGE_WAIT_AUTHENTICATE : String = "Trwa uwierzytelnianie..."
        val INFO_MESSAGE_USER_NEVER_LOGGED : String = "Wprowadź nazwę użytkownika"
        val INFO_MESSAGE_UNHANDLED_EXCEPTION : String = "Wystąpił nieobsłużony błąd"
        val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"

        //spreadsheet
        val MY_SPREADSHEET_OUTPUT_JSON : String = "out:json"
        val MY_SPREADSHEET_URL_PREFIX : String = "https://docs.google.com/a/google.com/spreadsheets/d/"
        val MY_SPREADSHEET_URL_SUFIX : String = "/gviz/tq"
        val MY_SPREADSHEET_QUERY_OUTPUT_JSON : String = "tqx"
        val MY_SPREADSHEET_QUERY_WHERE_CLAUSE: String = "tq"
        val MY_SPREADSHEET_QUERY_KEY: String = "key"
    }
}
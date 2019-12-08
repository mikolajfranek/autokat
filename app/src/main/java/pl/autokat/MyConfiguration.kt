package pl.autokat

import android.content.Context

class MyConfiguration {
    companion object {
        //file
        val FILE_APPLICATION_NEW_VERSION : String = "autokat_version_" + (MyConfiguration.DATABASE_VERSION + 1) + ".apk"

        //databases
        val DATABASE_VERSION : Int = 2
        val DATABASE_NAME_OF_FILE : String = "autokat.db"
        val DATABASE_FILE_PATH_ASSETS : String = "databases/" + MyConfiguration.DATABASE_NAME_OF_FILE
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

        //info message
        val INFO_MESSAGE_WAIT_UPDATE : String = "Trwa sprawdzanie aktualizacji..."

        //exception message
        val EXCEPTION_MESSAGE_UNHANDLED_EXCEPTION : String = "Wystąpił nieobsłużony błąd"
        val EXCEPTION_MESSAGE_APPLICATION_IS_UP_TO_DATE : String = "Aplikacja jest aktualna"

        //spreadsheet
        val MY_SPREADSHEET_OUTPUT_JSON : String = "out:json"
        val MY_SPREADSHEET_URL_PREFIX : String = "https://docs.google.com/a/google.com/spreadsheets/d/"
        val MY_SPREADSHEET_URL_SUFIX : String = "/gviz/tq"
        val MY_SPREADSHEET_QUERY_OUTPUT_JSON : String = "tqx"
        val MY_SPREADSHEET_QUERY_WHERE_CLAUSE: String = "tq"
        val MY_SPREADSHEET_QUERY_KEY: String = "key"
    }
}
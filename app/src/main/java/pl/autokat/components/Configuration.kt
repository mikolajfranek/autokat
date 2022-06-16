package pl.autokat.components

import android.graphics.Color
import pl.autokat.enums.ProgramMode
import java.util.concurrent.atomic.AtomicBoolean

class Configuration {
    companion object {
        const val PRODUCTION: Boolean = false
        val PROGRAM_MODE: ProgramMode = ProgramMode.COMPANY
        const val VERSION_APK: String = "1.0.8"
        const val DATABASE_VERSION_1_0_6: Int = 4
        const val DATABASE_VERSION_NEXT: Int = 5
        const val DATABASE_VERSION: Int = 4
        const val DATABASE_NAME_OF_FILE: String = "autokat.db"
        const val DATABASE_FILE_PATH_ASSETS: String = "databases/$DATABASE_NAME_OF_FILE"

        @Volatile
        var workerDownloadThumbnail: AtomicBoolean = AtomicBoolean(false)

        @Volatile
        var workerCopyData: AtomicBoolean = AtomicBoolean(false)

        val COLOR_WHITE: Int = Color.parseColor("#FFFFFF")
        val COLOR_FAILED: Int = Color.parseColor("#EF4836")
        val COLOR_SUCCESS: Int = Color.parseColor("#363636")

        const val ONE_DAY_IN_MILLISECONDS: Long = 86400000
        const val ONE_HOUR_IN_MILLISECONDS: Long = 3600000L

        const val UNHANDLED_EXCEPTION: String = "Wystąpił błąd"
        const val NETWORK_FAILED: String = "Brak połączenia"
        const val USER_NEVER_LOGGED: String = "Wprowadź nazwę użytkownika"
        const val USER_WAIT_AUTHENTICATING: String = "Trwa uwierzytelnianie…"
        const val USER_FAILED_LICENCE: String = "Brak licencji"
        const val COMPANY_FAILED_LICENCE: String = "Brak licencji firmy"
        const val USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        const val USER_FAILED_UUID: String = "Błędne urządzenie"
        const val UPDATE_WAIT: String = "Trwa aktualizacja…"
        const val UPDATE_SUCCESS: String = "Aktualizacja przebiegła pomyślnie"
        const val UPDATE_FAILED: String = "Wystąpił błąd podczas aktualizacji"
        const val BITMAP_WAIT: String = "Trwa pobieranie obrazu…"
        const val BITMAP_FAILED: String = "Wystąpił błąd podczas pobierania obrazu"
        const val BITMAP_STATUS: String = "Status pobieranych miniatur"
        const val DATABASE_ACTUAL: String = "Baza danych jest aktualna"
        const val DATABASE_EMPTY: String = "Baza danych jest pusta"
        const val DATABASE_NOT_ACTUAL: String = "Baza danych nie jest aktualna"
        const val HISTORY_FILTER_ADDED: String = "Pomyślnie zapisano nazwę do filtrowania"
        const val HISTORY_FILTER_DELETED: String = "Pomyślnie usunięto nazwę do filtrowania"
        const val HISTORY_FILTER_CANNOT_SAVE_EMPTY: String = "Nie można zapisać pustej wartości"
        const val COURSES_REFRESH: String = "Odśwież wartości kursów"

        //region spreadsheet columns users
        const val SPREADSHEET_USERS_ID: Int = 0
        const val SPREADSHEET_USERS_LOGIN: Int = 1
        const val SPREADSHEET_USERS_UUID: Int = 2
        const val SPREADSHEET_USERS_LICENCE: Int = 3
        const val SPREADSHEET_USERS_DISCOUNT: Int = 4
        const val SPREADSHEET_USERS_VISIBILITY: Int = 5
        const val SPREADSHEET_USERS_MINUS_PLATINUM: Int = 6
        const val SPREADSHEET_USERS_MINUS_PALLADIUM: Int = 7
        const val SPREADSHEET_USERS_MINUS_RHODIUM: Int = 8

        const val SPREADSHEET_USERS_COLUMN_ID: String = "A"
        const val SPREADSHEET_USERS_COLUMN_LOGIN: String = "B"
        const val SPREADSHEET_USERS_COLUMN_UUID: String = "C"
        const val SPREADSHEET_USERS_COLUMN_LICENCE: String = "D"
        const val SPREADSHEET_USERS_COLUMN_DISCOUNT: String = "E"
        const val SPREADSHEET_USERS_COLUMN_VISIBILITY: String = "F"
        const val SPREADSHEET_USERS_COLUMN_MINUS_PLATINUM: String = "G"
        const val SPREADSHEET_USERS_COLUMN_MINUS_PALLADIUM: String = "H"
        const val SPREADSHEET_USERS_COLUMN_MINUS_RHODIUM: String = "I"
        //endregion

        //region spreadsheet columns catalyst
        const val SPREADSHEET_CATALYST_ID: Int = 0
        const val SPREADSHEET_CATALYST_NAME: Int = 1
        const val SPREADSHEET_CATALYST_BRAND: Int = 2
        const val SPREADSHEET_CATALYST_WEIGHT: Int = 3
        const val SPREADSHEET_CATALYST_PLATINUM: Int = 4
        const val SPREADSHEET_CATALYST_PALLADIUM: Int = 5
        const val SPREADSHEET_CATALYST_RHODIUM: Int = 6
        const val SPREADSHEET_CATALYST_TYPE: Int = 7
        const val SPREADSHEET_CATALYST_ID_PICTURE: Int = 8
        const val SPREADSHEET_CATALYST_URL_PICTURE: Int = 9

        const val SPREADSHEET_CATALYST_COLUMN_ID: String = "A"
        const val SPREADSHEET_CATALYST_COLUMN_NAME: String = "B"
        const val SPREADSHEET_CATALYST_COLUMN_BRAND: String = "C"
        const val SPREADSHEET_CATALYST_COLUMN_WEIGHT: String = "D"
        const val SPREADSHEET_CATALYST_COLUMN_PLATINUM: String = "E"
        const val SPREADSHEET_CATALYST_COLUMN_PALLADIUM: String = "F"
        const val SPREADSHEET_CATALYST_COLUMN_RHODIUM: String = "G"
        const val SPREADSHEET_CATALYST_COLUMN_TYPE: String = "H"
        const val SPREADSHEET_CATALYST_COLUMN_ID_PICTURE: String = "I"
        const val SPREADSHEET_CATALYST_COLUMN_URL_PICTURE: String = "J"
        //endregion

        //region spreadsheet columns company
        const val SPREADSHEET_COMPANY_ID: Int = 0
        const val SPREADSHEET_COMPANY_NAME: Int = 1
        const val SPREADSHEET_COMPANY_LOG: Int = 2
        const val SPREADSHEET_COMPANY_STATUS: Int = 3

        const val SPREADSHEET_COMPANY_COLUMN_ID: String = "A"
        const val SPREADSHEET_COMPANY_COLUMN_NAME: String = "B"
        const val SPREADSHEET_COMPANY_COLUMN_LOG: String = "C"
        const val SPREADSHEET_COMPANY_COLUMN_STATUS: String = "D"
        //endregion

        //region spreadsheet columns catalysts of companies
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_ID: Int = 0
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_ID_IN_COMPANY: Int = 1
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_ID_COMPANY: Int = 2
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_NAME: Int = 3
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_BRAND: Int = 4
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_WEIGHT: Int = 5
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_PLATINUM: Int = 6
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_PALLADIUM: Int = 7
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_RHODIUM: Int = 8
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_TYPE: Int = 9
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_URL_PICTURE: Int = 10

        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_ID: String = "A"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_ID_IN_COMPANY: String = "B"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_ID_COMPANY: String = "C"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_NAME: String = "D"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_BRAND: String = "E"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_WEIGHT: String = "F"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_PLATINUM: String = "G"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_PALLADIUM: String = "H"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_RHODIUM: String = "I"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_TYPE: String = "J"
        const val SPREADSHEET_CATALYSTS_OF_COMPANIES_COLUMN_URL_PICTURE: String = "K"
        //endregion

        //region database columns catalyst
        const val DATABASE_TABLE_CATALYST = "catalyst"
        const val DATABASE_CATALYST_ID = "id"
        const val DATABASE_CATALYST_ID_PICTURE = "id_picture"
        const val DATABASE_CATALYST_URL_PICTURE = "url_picture"
        const val DATABASE_CATALYST_THUMBNAIL = "thumbnail"
        const val DATABASE_CATALYST_NAME = "name"
        const val DATABASE_CATALYST_BRAND = "brand"
        const val DATABASE_CATALYST_PLATINUM = "platinum"
        const val DATABASE_CATALYST_PALLADIUM = "palladium"
        const val DATABASE_CATALYST_RHODIUM = "rhodium"
        const val DATABASE_CATALYST_TYPE = "type"
        const val DATABASE_CATALYST_WEIGHT = "weight"
        const val DATABASE_CATALYST_TEMP_HITCOUNT = "hitcount"
        //endregion"name"

        //region database columns catalyst client
        const val DATABASE_TABLE_CATALYST_CLIENT = "catalyst_client"
        const val DATABASE_CATALYST_CLIENT_ID = "id"
        const val DATABASE_CATALYST_CLIENT_ID_IN_COMPANY = "id_in_company"
        const val DATABASE_CATALYST_CLIENT_ID_COMPANY = "id_company"
        const val DATABASE_CATALYST_CLIENT_URL_PICTURE = "url_picture"
        const val DATABASE_CATALYST_CLIENT_THUMBNAIL = "thumbnail"
        const val DATABASE_CATALYST_CLIENT_NAME = "name"
        const val DATABASE_CATALYST_CLIENT_BRAND = "brand"
        const val DATABASE_CATALYST_CLIENT_PLATINUM = "platinum"
        const val DATABASE_CATALYST_CLIENT_PALLADIUM = "palladium"
        const val DATABASE_CATALYST_CLIENT_RHODIUM = "rhodium"
        const val DATABASE_CATALYST_CLIENT_TYPE = "type"
        const val DATABASE_CATALYST_CLIENT_WEIGHT = "weight"
        const val DATABASE_CATALYST_CLIENT_DUPLICATE = "duplicate"
        const val DATABASE_CATALYST_CLIENT_TEMP_HITCOUNT = "hitcount"
        //endregion

        //region database columns history filter
        const val DATABASE_TABLE_HISTORY_FILTER = "history_filter"
        const val DATABASE_HISTORY_FILTER_ID = "id"
        const val DATABASE_HISTORY_FILTER_NAME = "name"
        //endregion

        //region database columns courses
        const val DATABASE_TABLE_COURSES = "courses"
        const val DATABASE_COURSES_ID = "id"
        const val DATABASE_COURSES_DATE = "date"
        const val DATABASE_COURSES_YEARMONTH = "yearmonth"
        const val DATABASE_COURSES_PLATINUM = "platinum"
        const val DATABASE_COURSES_PALLADIUM = "palladium"
        const val DATABASE_COURSES_RHODIUM = "rhodium"
        const val DATABASE_COURSES_EUR_PLN = "eur_pln"
        const val DATABASE_COURSES_USD_PLN = "usd_pln"
        //endregion

        //region database columns sqlite sequence
        const val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        const val DATABASE_SQLITE_SEQUENCE_SEQ = "seq"
        const val DATABASE_SQLITE_SEQUENCE_NAME = "name"
        //endregion
    }
}
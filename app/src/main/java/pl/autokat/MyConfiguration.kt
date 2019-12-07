package pl.autokat

import android.content.Context

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
    }
}

package pl.autokat

import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import pl.autokat.ItemCatalyst
import pl.autokat.MyConfiguration

class MyDatabase(context: Context) : SQLiteAssetHelper(context, MyConfiguration.DATABASE_NAME_OF_FILE, null, MyConfiguration.DATABASE_VERSION){
    fun getDataCatalyst(nameCatalystOrBrandCar:String): ArrayList<ItemCatalyst> {
        val result : ArrayList<ItemCatalyst> = ArrayList<ItemCatalyst>()

        val fields = arrayOf(
            MyConfiguration.DATABASE_TABLE_CATALYST_ID,
            MyConfiguration.DATABASE_TABLE_CATALYST_ID_PICTURE,
            MyConfiguration.DATABASE_TABLE_CATALYST_NAME,
            MyConfiguration.DATABASE_TABLE_CATALYST_BRAND,
            MyConfiguration.DATABASE_TABLE_CATALYST_PLATINUM,
            MyConfiguration.DATABASE_TABLE_CATALYST_PALLADIUM ,
            MyConfiguration.DATABASE_TABLE_CATALYST_RHODIUM,
            MyConfiguration.DATABASE_TABLE_CATALYST_TYPE,
            MyConfiguration.DATABASE_TABLE_CATALYST_WEIGHT
        )

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyConfiguration.DATABASE_TABLE_CATALYST

        val cursor = queryBuilder.query(readableDatabase, fields, null, null, null, null, null, MyConfiguration.DATABASE_PAGINATE_LIMIT)

        if(cursor != null && cursor.moveToFirst()){
            do {
                result.add(
                    ItemCatalyst(
                        cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_ID)),
                        cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_ID_PICTURE)),
                        cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_NAME)),
                        cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_BRAND)),
                        cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_PLATINUM)),
                        cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_PALLADIUM)),
                        cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_RHODIUM)),
                        cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_TYPE)),
                        cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_TABLE_CATALYST_WEIGHT))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()

        return result
    }
}
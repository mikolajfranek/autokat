package pl.autokat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.util.*
import kotlin.collections.ArrayList


class MyDatabase(context: Context) : SQLiteAssetHelper(context, MyConfiguration.DATABASE_NAME_OF_FILE, null, MyConfiguration.DATABASE_VERSION){

    fun insertCatalysts(dataCatalyst: MutableList<ItemCatalyst>) : Boolean {
        var result : Boolean
        val db = this.writableDatabase
        try{
            db.beginTransaction()

            //truncate tables
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_CATALYST + ";VACUUM;")
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_SQLITE_SEQUENCE + ";VACUUM;")

            //insert values
            val values = ContentValues()
            for(catalyst in dataCatalyst){
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE, catalyst.idPicture)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME, catalyst.name)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND, catalyst.brand)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM, catalyst.platinum)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM, catalyst.palladium)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM, catalyst.rhodium)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE, catalyst.type)
                values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT, catalyst.weight)

                db.insert(MyConfiguration.DATABASE_TABLE_CATALYST, null, values)
            }
            db.setTransactionSuccessful()
            result = true
        }catch (e:Exception){
            result = false
        }finally {
            db.endTransaction()
        }
        return result
    }


    fun getCountCatalyst() : Int {
        val cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST, null)
        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return count
    }

    fun getDataCatalyst(nameCatalystOrBrandCar:String): ArrayList<ItemCatalyst> {
        val result : ArrayList<ItemCatalyst> = ArrayList<ItemCatalyst>()

        val fields = arrayOf(
            MyConfiguration.DATABASE_ELEMENT_CATALYST_ID,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT
        )

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyConfiguration.DATABASE_TABLE_CATALYST

        val cursor = queryBuilder.query(readableDatabase, fields,
            (MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME + " LIKE ? OR " +  MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND + " LIKE ?"),
             Array(2){ i -> ("%$nameCatalystOrBrandCar%")},
            null,
            null,
            null,
            MyConfiguration.DATABASE_PAGINATE_LIMIT)

        while (cursor.moveToNext()){
            result.add(
                ItemCatalyst(
                    cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)),
                    cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND)),
                    cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM)),
                    cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM)),
                    cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE)),
                    cursor.getFloat(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT))
                )
            )
        }

        cursor.close()
        return result
    }
}
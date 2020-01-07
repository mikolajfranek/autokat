package pl.autokat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder
import android.graphics.BitmapFactory
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import kotlin.collections.ArrayList

class MyDatabase(context: Context) : SQLiteAssetHelper(context, MyConfiguration.DATABASE_NAME_OF_FILE, null, MyConfiguration.DATABASE_VERSION){
    //reset database - truncate tables
    fun resetDatabase() : Boolean {
        var result = false
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //truncate tables
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_CATALYST + ";VACUUM;")
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_SQLITE_SEQUENCE + ";VACUUM;")
            db.setTransactionSuccessful()
            result = true
        }catch (e:Exception){
            //nothing
        }finally {
            db.endTransaction()
        }
        return result
    }
    //insert one row of element
    fun insertCatalysts(values : ContentValues) : Boolean {
        var result = false
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //do insert
            db.insert(MyConfiguration.DATABASE_TABLE_CATALYST, null, values)
            db.setTransactionSuccessful()
            result = true
        }catch (e:Exception){
            //nothing
        }finally {
            db.endTransaction()
        }
        return result
    }
    //get count of catalysts
    fun getCountCatalyst() : Int {
        val cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST, null)
        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return count
    }
    //get data catalysts
    fun getDataCatalyst(nameCatalystOrBrandCar: String, limitElements: String): ArrayList<MyItemCatalyst> {
        //set fields which will be retrieved
        val fields = arrayOf(
            MyConfiguration.DATABASE_ELEMENT_CATALYST_ID,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT
        )
        //make query
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyConfiguration.DATABASE_TABLE_CATALYST
        val cursor = queryBuilder.query(readableDatabase, fields,
            (MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME + " LIKE ? OR " +  MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND + " LIKE ?"),
             Array(2){ i -> ("%$nameCatalystOrBrandCar%")},
            null,
            null,
            null,
            limitElements)
        //iterate over data and prepare data
        val result : ArrayList<MyItemCatalyst> = ArrayList<MyItemCatalyst>()
        while (cursor.moveToNext()){
            val blobImage : ByteArray? = if(cursor.isNull(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID))) null else cursor.getBlob(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL))
            result.add(
                MyItemCatalyst(
                    cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE)),
                    if(blobImage == null) null else BitmapFactory.decodeByteArray(blobImage, 0, blobImage.size),
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
package pl.autokat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.graphics.BitmapFactory
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import org.json.JSONArray
import org.json.JSONObject

class MyDatabase(context: Context) : SQLiteAssetHelper(context, MyConfiguration.DATABASE_NAME_OF_FILE, null, MyConfiguration.DATABASE_VERSION){
    //update catalyst
    fun updateCatalyst(catalystId: Int, thumbnail: ByteArray) : Boolean {
        var result = false
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            val content = ContentValues()
            content.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL,thumbnail)
            db.updateWithOnConflict(MyConfiguration.DATABASE_TABLE_CATALYST,  content,MyConfiguration.DATABASE_ELEMENT_CATALYST_ID + "= ?", Array(1){ i -> catalystId.toString()}, SQLiteDatabase.CONFLICT_IGNORE )
            db.setTransactionSuccessful()
            result = true
        }catch (e:Exception){
            //nothing
        }finally {
            db.endTransaction()
        }
        return result
    }
    //get string of jsonarray elements which they don't have thumbnail
    fun getCatalystWithoutThumbnail(): JSONArray {
        //set fields which will be retrieved
        val fields = arrayOf(
            MyConfiguration.DATABASE_ELEMENT_CATALYST_ID,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE
        )
        //make query
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyConfiguration.DATABASE_TABLE_CATALYST
        val cursor = queryBuilder.query(readableDatabase, fields,
            MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL + " IS NULL",
            null,
            null,
            null,
            null)
        val result = JSONArray()
        while (cursor.moveToNext()){
            val json = JSONObject()
            json.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID, cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)))
            json.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE, cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE)))
            result.put(json)
        }
        cursor.close()
        return result
    }
    //get amount of elements which they don't have thumbnail
    fun getCountCatalystWithThumbnail(): Int {
        val cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST +
                " WHERE " + MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL + " IS NOT NULL", null)
        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return count
    }
    //get amount of elements which has url picture
    fun getCountCatalystWithUrlOfThumbnail(): Int {
        val cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST +
                " WHERE LENGTH(" + MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE + ") != 0", null)
        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return count
    }
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
    fun insertCatalysts(values: JSONArray) : Boolean {
        var result = false
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //iterate over elements (batch)
            for(i in 0 until values.length()) {
                val element = values.getJSONObject(i).getJSONArray("c")
                val row = ContentValues()
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE,
                    MyConfiguration.getValueStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_ID_PICTURE
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE,
                    MyConfiguration.getValueStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_URL_PICTURE
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME,
                    MyConfiguration.getValueStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_NAME
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND,
                    MyConfiguration.getValueStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_BRAND
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM,
                    MyConfiguration.getValueFloatStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_PLATINUM
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM,
                    MyConfiguration.getValueFloatStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_PALLADIUM
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM,
                    MyConfiguration.getValueFloatStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_RHODIUM
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE,
                    MyConfiguration.getValueStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_TYPE
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT,
                    MyConfiguration.getValueFloatStringFromDocsApi(
                        element,
                        MyConfiguration.MY_SPREADSHEET_CATALYST_WEIGHT
                    )
                )
                //insert element
                db.insert(MyConfiguration.DATABASE_TABLE_CATALYST, null, row)
            }
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
    fun getDataCatalyst(nameCatalystOrBrandCarInput: String, limitElements: String): ArrayList<MyItemCatalyst> {
        //change space on other sign
        val nameCatalystOrBrandCar = ("\\s{2,}").toRegex().replace(nameCatalystOrBrandCarInput.trim(), " ")
        val arrayFields = if(nameCatalystOrBrandCar.isNullOrEmpty()) mutableListOf<String>() else nameCatalystOrBrandCar.split(" ")
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
        val argumentsSelect = mutableListOf<String>()
        val argumentsWhere = mutableListOf<String>()
        var queryString = "SELECT  ${fields.joinToString()}"
        if(arrayFields.size > 0){
            var addPlus : Boolean = false
            var hitCountQuery : String = "("
            for(item in arrayFields){
                val subArrayFields = item.split("*")
                for(subItem in subArrayFields){
                    argumentsSelect.add(subItem.toLowerCase())
                    argumentsSelect.add(subItem.toLowerCase())
                    hitCountQuery += (if(addPlus) " + " else "") +  "(instr(LOWER(${MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME}), ?) > 0) + (instr(LOWER(${MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND}), ?) > 0)"
                    addPlus = true
                }
                argumentsWhere.add("%${item.replace("*", "%")}%")
                argumentsWhere.add("%${item.replace("*", "%")}%")
            }
            hitCountQuery += ") as ${MyConfiguration.DATABASE_ELEMENT_CATALYST_TEMP_HITCOUNT}"
            queryString += ", ${hitCountQuery}"
            queryString += " FROM ${MyConfiguration.DATABASE_TABLE_CATALYST}"
            queryString += " WHERE " + arrayFields.joinToString (separator = " OR ") { "${MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME} LIKE ? OR ${MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND} LIKE ?"}
            queryString += " ORDER BY ${MyConfiguration.DATABASE_ELEMENT_CATALYST_TEMP_HITCOUNT} DESC"
        }else{
            queryString += " FROM ${MyConfiguration.DATABASE_TABLE_CATALYST}"
        }
        queryString += " LIMIT ${limitElements}"
        val cursor = readableDatabase.rawQuery(queryString, (argumentsSelect + argumentsWhere).toTypedArray())
        //iterate over data and prepare data
        val result : ArrayList<MyItemCatalyst> = ArrayList<MyItemCatalyst>()
        while (cursor.moveToNext()){
            val blobImage : ByteArray? = if(cursor.isNull(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID))) null else cursor.getBlob(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL))
            val myItemCatalyst = MyItemCatalyst(
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
            result.add(myItemCatalyst)
        }
        cursor.close()
        return result
    }
}
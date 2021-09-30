package pl.autokat.components

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.graphics.BitmapFactory
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MyDatabase(context: Context) : SQLiteAssetHelper(context,
    MyConfiguration.DATABASE_NAME_OF_FILE, null,
    MyConfiguration.DATABASE_VERSION
){
    private val myContext: Context = context

    //override onupgrage of database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            when(newVersion){
                //in case, when database need to be override
                MyConfiguration.DATABASE_VERSION_1_0_6 -> {
                    createTableCourses()
                    addIndexCourses()
                }
                else -> {
                    //end transaction, unlock database
                    db.endTransaction()
                    //copy database, from assets to directory system where is database of app
                    val fileDatabaseInAssets = myContext.assets.open(MyConfiguration.DATABASE_FILE_PATH_ASSETS)
                    val fileDatabaseInSystem = FileOutputStream(myContext.getDatabasePath(
                        MyConfiguration.DATABASE_NAME_OF_FILE
                    ))
                    fileDatabaseInAssets.copyTo(fileDatabaseInSystem)
                    fileDatabaseInSystem.close()
                    fileDatabaseInAssets.close()
                    //begin new transaction
                    db.beginTransaction()
                    //recreate
                    onCreate(db)
                }
            }
        }
    }
    private fun createTableCourses() {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            val query =
                "CREATE TABLE `" + MyConfiguration.DATABASE_TABLE_COURSES + "` (\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_ID + "` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_DATE + "` TEXT NOT NULL,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_PLATINUM + "` TEXT NOT NULL,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_PALLADIUM + "` TEXT NOT NULL,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_RHODIUM + "` TEXT NOT NULL,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_EUR + "` TEXT NOT NULL,\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_USD + "` TEXT NOT NULL\n" +
                    ");"
            db.execSQL(query)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    private fun addIndexCourses() {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            val query =
                "CREATE INDEX `courses_date` ON `" + MyConfiguration.DATABASE_TABLE_COURSES + "` (\n" +
                    "`" + MyConfiguration.DATABASE_ELEMENT_COURSES_DATE + "` ASC\n" +
                    ");"
            db.execSQL(query)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //get courses
    fun getCourses(){
        //TODO
    }
    //get amount of courses in day
    @SuppressLint("Range")
    fun getCountCourses(localDate: LocalDate) : Int {
        val inputDate = MyConfiguration.formatDate(localDate.toString())
        var count = 0
        var cursor : Cursor? = null
        try {
            cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_COURSES +
                    " WHERE " + MyConfiguration.DATABASE_ELEMENT_COURSES_DATE + "=" + "'" + inputDate + "'", null)
            if(cursor.moveToFirst()){
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        }finally {
            cursor?.close()
        }
        return count
    }
    //insert courses
    fun insertCourses(){
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val row = ContentValues()
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_DATE,
                MyConfiguration.formatDate(LocalDate.now().toString())
            )
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_PLATINUM,
                "value"
            )
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_PALLADIUM,
                "value"
            )
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_RHODIUM,
                "value"
            )
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_EUR,
                "value"
            )
            row.put(
                MyConfiguration.DATABASE_ELEMENT_COURSES_USD,
                "value"
            )
            //insert element
            db.insert(MyConfiguration.DATABASE_TABLE_COURSES,null, row)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }

    //get amount of elements which they don't have thumbnail
    @SuppressLint("Range")
    fun getCountCatalystWithThumbnail(): Int {
        var count = 0
        var cursor : Cursor? = null
        try {
            cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST +
                    " WHERE " + MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL + " IS NOT NULL", null)
            if(cursor.moveToFirst()){
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        }finally {
            cursor?.close()
        }
        return count
    }
    //get amount of elements which has url picture
    @SuppressLint("Range")
    fun getCountCatalystWithUrlOfThumbnail(): Int {
        var count = 0
        var cursor : Cursor? = null
        try {
            cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST +
                    " WHERE LENGTH(" + MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE + ") != 0", null)
            if(cursor.moveToFirst()){
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        }finally {
            cursor?.close()
        }
        return count
    }
    //get count of catalysts
    @SuppressLint("Range")
    fun getCountCatalyst() : Int {
        var count = 0
        var cursor : Cursor? = null
        try {
            cursor = readableDatabase.rawQuery("SELECT count(*) as count FROM " + MyConfiguration.DATABASE_TABLE_CATALYST, null)
            if(cursor.moveToFirst()){
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        }finally {
            cursor?.close()
        }
        return count
    }
    //update catalyst
    fun updateCatalyst(catalystId: Int, thumbnail: ByteArray) {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            val content = ContentValues()
            content.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL,thumbnail)
            db.updateWithOnConflict(
                MyConfiguration.DATABASE_TABLE_CATALYST,  content,
                MyConfiguration.DATABASE_ELEMENT_CATALYST_ID + "= ?", Array(1){ i -> catalystId.toString()}, SQLiteDatabase.CONFLICT_IGNORE )
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //reset database - truncate tables
    fun resetDatabase() {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //truncate tables
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_CATALYST + ";VACUUM;")
            //delete row from sqlite sequence
            db.execSQL("DELETE FROM " + MyConfiguration.DATABASE_TABLE_SQLITE_SEQUENCE
                    + " WHERE " + MyConfiguration.DATABASE_ELEMENT_SQLITE_SEQUENCE_NAME + " LIKE '" + MyConfiguration.DATABASE_TABLE_CATALYST + "';VACUUM;")
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //insert one row of element
    fun insertCatalysts(values: JSONArray) {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //iterate over elements (batch)
            for(i in 0 until values.length()) {
                val element = values.getJSONObject(i).getJSONArray("c")
                val salt : String = MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_CATALYST_ID
                ) + MySecret.getPrivateKey()
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
                    MyConfiguration.encrypt(
                        MyConfiguration.getValueFloatStringFromDocsApi(
                            element,
                            MyConfiguration.MY_SPREADSHEET_CATALYST_PLATINUM
                        ),
                        salt
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM,
                    MyConfiguration.encrypt(
                        MyConfiguration.getValueFloatStringFromDocsApi(
                            element,
                            MyConfiguration.MY_SPREADSHEET_CATALYST_PALLADIUM
                        ),
                        salt
                    )
                )
                row.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM,
                    MyConfiguration.encrypt(
                        MyConfiguration.getValueFloatStringFromDocsApi(
                            element,
                            MyConfiguration.MY_SPREADSHEET_CATALYST_RHODIUM
                        ),
                        salt
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
                    MyConfiguration.encrypt(
                        MyConfiguration.getValueFloatStringFromDocsApi(
                            element,
                            MyConfiguration.MY_SPREADSHEET_CATALYST_WEIGHT
                        ),
                        salt
                    )
                )
                //insert element
                db.insert(MyConfiguration.DATABASE_TABLE_CATALYST, null, row)
            }
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //get string of jsonarray elements which they don't have thumbnail
    @SuppressLint("Range")
    fun getCatalystWithoutThumbnail(): JSONArray {
        val result = JSONArray()
        var cursor : Cursor? = null
        try {
            //set fields which will be retrieved
            val fields = arrayOf(
                MyConfiguration.DATABASE_ELEMENT_CATALYST_ID,
                MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE
            )
            //make query
            val queryBuilder = SQLiteQueryBuilder()
            queryBuilder.tables = MyConfiguration.DATABASE_TABLE_CATALYST
            cursor = queryBuilder.query(readableDatabase, fields,
                MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL + " IS NULL",
                null,
                null,
                null,
                null)
            while (cursor.moveToNext()){
                val json = JSONObject()
                json.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_ID, cursor.getInt(cursor.getColumnIndex(
                        MyConfiguration.DATABASE_ELEMENT_CATALYST_ID
                    )))
                json.put(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE, cursor.getString(cursor.getColumnIndex(
                        MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE
                    )))
                result.put(json)
            }
        }finally {
            cursor?.close()
        }
        return result
    }
    //get data catalysts
    @SuppressLint("Range")
    fun getDataCatalyst(nameCatalystOrBrandCarInput: String, limitElements: String): ArrayList<MyItemCatalyst> {
        val result : ArrayList<MyItemCatalyst> = ArrayList<MyItemCatalyst>()
        var cursor : Cursor? = null
        try {
            //get searching string
            val arrayFields = MyConfiguration.getSearchingString(nameCatalystOrBrandCarInput)
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
                var addPlus = false
                var hitCountQuery = "("
                for(item in arrayFields){
                    val subArrayFields = item.split("*")
                    for(subItem in subArrayFields){
                        argumentsSelect.add(subItem.toLowerCase(Locale.getDefault()))
                        argumentsSelect.add(subItem.toLowerCase(Locale.getDefault()))
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
            cursor = readableDatabase.rawQuery(queryString, (argumentsSelect + argumentsWhere).toTypedArray())
            //iterate over data and prepare data
            while (cursor.moveToNext()){
                val blobImage : ByteArray? = if(cursor.isNull(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID))) null else cursor.getBlob(cursor.getColumnIndex(
                    MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL
                ))
                val salt : String = cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)).toString() + MySecret.getPrivateKey()
                val myItemCatalyst = MyItemCatalyst(
                    cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE)),
                    if(blobImage == null) null else BitmapFactory.decodeByteArray(blobImage, 0, blobImage.size),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND)),
                    MyConfiguration.formatStringFloat(
                        MyConfiguration.decrypt(cursor.getString(cursor.getColumnIndex(
                            MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM
                        )), salt),3).toFloat(),
                    MyConfiguration.formatStringFloat(
                        MyConfiguration.decrypt(cursor.getString(cursor.getColumnIndex(
                            MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM
                        )), salt),3).toFloat(),
                    MyConfiguration.formatStringFloat(
                        MyConfiguration.decrypt(cursor.getString(cursor.getColumnIndex(
                            MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM
                        )), salt),3).toFloat(),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE)),
                    MyConfiguration.formatStringFloat(
                        MyConfiguration.decrypt(cursor.getString(cursor.getColumnIndex(
                            MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT
                        )), salt),3).toFloat()
                )
                result.add(myItemCatalyst)
            }
        }finally {
            cursor?.close()
        }
        return result
    }
    //insert history filter
    fun insertHistoryFilter(searchedText: String) {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            val row = ContentValues()
            row.put(
                MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_NAME,
                searchedText
            )
            //insert element
            db.insert(MyConfiguration.DATABASE_TABLE_HISTORY_FILTER, null, row)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //delete history filter
    fun deleteHistoryFilter(id: Int) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            //delete element
            db.delete(
                MyConfiguration.DATABASE_TABLE_HISTORY_FILTER,
                MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_ID + "=" + id, null
            )
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    fun deleteHistoryFilter(name: String) {
        val db = this.writableDatabase
        try{
            db.beginTransaction()
            //delete element
            db.delete(
                MyConfiguration.DATABASE_TABLE_HISTORY_FILTER,
                MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_NAME + " LIKE '" + name + "'", null)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }
    //get data history filter
    @SuppressLint("Range")
    fun getDataHistoryFilter(limitElements: String): ArrayList<MyItemHistoryFilter> {
        val result : ArrayList<MyItemHistoryFilter> = ArrayList<MyItemHistoryFilter>()
        var cursor : Cursor? = null
        try {
            //set fields which will be retrieved
            val fields = arrayOf(
                MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_ID,
                MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_NAME
            )
            //make query
            var queryString = "SELECT  ${fields.joinToString()}"
            queryString += " FROM ${MyConfiguration.DATABASE_TABLE_HISTORY_FILTER}"
            queryString += " ORDER BY ${MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_ID} DESC"
            queryString += " LIMIT ${limitElements}"
            cursor = readableDatabase.rawQuery(queryString, null)
            //prepare data
            while (cursor.moveToNext()){
                val myItemHistoryFilter = MyItemHistoryFilter(
                    cursor.getInt(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_ID)),
                    cursor.getString(cursor.getColumnIndex(MyConfiguration.DATABASE_ELEMENT_HISTORY_FILTER_NAME))
                )
                result.add(myItemHistoryFilter)
            }
        }finally {
            cursor?.close()
        }
        return result
    }
}
package pl.autokat.components

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.graphics.BitmapFactory
import android.util.Base64
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.models.ModelCatalyst
import pl.autokat.models.ModelCourse
import pl.autokat.models.ModelHistoryFilter
import java.io.FileOutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Database(context: Context) : SQLiteAssetHelper(
    context,
    Configuration.DATABASE_NAME_OF_FILE,
    null,
    Configuration.DATABASE_VERSION
) {
    private val myContext: Context = context
    private val transformation: String = "BLOWFISH/ECB/PKCS5Padding"

    //region upgrade, update
    private fun upgrade106(db: SQLiteDatabase) {
        try {
            db.beginTransaction()
            val queryString = "CREATE TABLE `${Configuration.DATABASE_TABLE_COURSES}` (\n" +
                    "`${Configuration.DATABASE_COURSES_ID}` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "`${Configuration.DATABASE_COURSES_DATE}` TEXT NOT NULL UNIQUE,\n" +
                    "`${Configuration.DATABASE_COURSES_YEARMONTH}` TEXT NOT NULL,\n" +
                    "`${Configuration.DATABASE_COURSES_PLATINUM}` TEXT NOT NULL,\n" +
                    "`${Configuration.DATABASE_COURSES_PALLADIUM}` TEXT NOT NULL,\n" +
                    "`${Configuration.DATABASE_COURSES_RHODIUM}` TEXT NOT NULL,\n" +
                    "`${Configuration.DATABASE_COURSES_EUR_PLN}` TEXT NOT NULL,\n" +
                    "`${Configuration.DATABASE_COURSES_USD_PLN}` TEXT NOT NULL\n" +
                    ");"
            db.execSQL(queryString)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        try {
            db.beginTransaction()
            val queryString =
                "CREATE INDEX `courses_yearmonth` ON `${Configuration.DATABASE_TABLE_COURSES}` (\n" +
                        "`${Configuration.DATABASE_COURSES_YEARMONTH}` ASC\n" +
                        ");"
            db.execSQL(queryString)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            when (newVersion) {
                Configuration.DATABASE_VERSION_1_0_6 -> {
                    upgrade106(db)
                }
                //in other case override database (copy database from assets to directory system where is database of app)
                else -> {
                    db.endTransaction()
                    val fileDatabaseInAssets =
                        myContext.assets.open(Configuration.DATABASE_FILE_PATH_ASSETS)
                    val fileDatabaseInSystem =
                        FileOutputStream(myContext.getDatabasePath(Configuration.DATABASE_NAME_OF_FILE))
                    fileDatabaseInAssets.copyTo(fileDatabaseInSystem)
                    fileDatabaseInSystem.close()
                    fileDatabaseInAssets.close()
                    db.beginTransaction()
                    onCreate(db)
                }
            }
        }
    }

    fun resetDatabase() {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.execSQL("DELETE FROM ${Configuration.DATABASE_TABLE_CATALYST};VACUUM;")
            db.execSQL("DELETE FROM ${Configuration.DATABASE_TABLE_SQLITE_SEQUENCE} WHERE ${Configuration.DATABASE_SQLITE_SEQUENCE_NAME} LIKE '${Configuration.DATABASE_TABLE_CATALYST}';VACUUM;")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    //endregion

    //region encrypt, decrypt
    @SuppressLint("GetInstance")
    private fun encrypt(input: String, salt: String): String {
        val secret = SecretKeySpec(salt.toByteArray(charset("UTF8")), "BLOWFISH")
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, secret)
        return Base64.encodeToString(
            cipher.doFinal(input.toByteArray(charset("UTF8"))),
            Base64.DEFAULT
        )
    }

    @SuppressLint("GetInstance")
    private fun decrypt(input: String, salt: String): String {
        val secret = SecretKeySpec(salt.toByteArray(charset("UTF8")), "BLOWFISH")
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, secret)
        return String(cipher.doFinal(Base64.decode(input, Base64.DEFAULT)))
    }
    //endregion

    //region catalyst
    @SuppressLint("Range")
    fun getDataCatalyst(
        nameCatalystOrBrandCarInput: String,
        limitElements: String
    ): ArrayList<ModelCatalyst> {
        val result: ArrayList<ModelCatalyst>
        var cursor: Cursor? = null
        try {
            val arrayFields = Parser.parseSearchingString(nameCatalystOrBrandCarInput)
            val fields = arrayOf(
                Configuration.DATABASE_CATALYST_ID,
                Configuration.DATABASE_CATALYST_ID_PICTURE,
                Configuration.DATABASE_CATALYST_URL_PICTURE,
                Configuration.DATABASE_CATALYST_THUMBNAIL,
                Configuration.DATABASE_CATALYST_NAME,
                Configuration.DATABASE_CATALYST_BRAND,
                Configuration.DATABASE_CATALYST_PLATINUM,
                Configuration.DATABASE_CATALYST_PALLADIUM,
                Configuration.DATABASE_CATALYST_RHODIUM,
                Configuration.DATABASE_CATALYST_TYPE,
                Configuration.DATABASE_CATALYST_WEIGHT
            )
            val argumentsSelect = mutableListOf<String>()
            val argumentsWhere = mutableListOf<String>()
            var queryString = "SELECT  ${fields.joinToString()}"
            if (arrayFields.isNotEmpty()) {
                var addPlus = false
                var hitCountQuery = "("
                for (item in arrayFields) {
                    val subArrayFields = item.split("*")
                    for (subItem in subArrayFields) {
                        argumentsSelect.add(subItem.lowercase(Locale.getDefault()))
                        argumentsSelect.add(subItem.lowercase(Locale.getDefault()))
                        hitCountQuery += (if (addPlus) " + " else "") + "(instr(LOWER(${Configuration.DATABASE_CATALYST_NAME}), ?) > 0) + (instr(LOWER(${Configuration.DATABASE_CATALYST_BRAND}), ?) > 0)"
                        addPlus = true
                    }
                    argumentsWhere.add("%${item.replace("*", "%")}%")
                    argumentsWhere.add("%${item.replace("*", "%")}%")
                }
                hitCountQuery += ") as ${Configuration.DATABASE_CATALYST_TEMP_HITCOUNT}"
                queryString += ", $hitCountQuery"
                queryString += " FROM ${Configuration.DATABASE_TABLE_CATALYST}"
                queryString += " WHERE " + arrayFields.joinToString(separator = " OR ") { "${Configuration.DATABASE_CATALYST_NAME} LIKE ? OR ${Configuration.DATABASE_CATALYST_BRAND} LIKE ?" }
                queryString += " ORDER BY ${Configuration.DATABASE_CATALYST_TEMP_HITCOUNT} DESC"
            } else {
                queryString += " FROM ${Configuration.DATABASE_TABLE_CATALYST}"
            }
            queryString += " LIMIT $limitElements"
            cursor = readableDatabase.rawQuery(
                queryString,
                (argumentsSelect + argumentsWhere).toTypedArray()
            )
            val withBlob = fields.contains(Configuration.DATABASE_CATALYST_THUMBNAIL)
            result = getCatalystFromCursor(cursor, withBlob)
        } finally {
            cursor?.close()
        }
        return result
    }

    @SuppressLint("Range")
    fun getCatalystWithoutThumbnail(): JSONArray {
        val result = JSONArray()
        var cursor: Cursor? = null
        try {
            val fields = arrayOf(
                Configuration.DATABASE_CATALYST_ID,
                Configuration.DATABASE_CATALYST_URL_PICTURE
            )
            val queryBuilder = SQLiteQueryBuilder()
            queryBuilder.tables = Configuration.DATABASE_TABLE_CATALYST
            cursor = queryBuilder.query(
                readableDatabase,
                fields,
                Configuration.DATABASE_CATALYST_THUMBNAIL + " IS NULL",
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                val json = JSONObject()
                json.put(
                    Configuration.DATABASE_CATALYST_ID,
                    cursor.getInt(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_ID))
                )
                json.put(
                    Configuration.DATABASE_CATALYST_URL_PICTURE,
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_URL_PICTURE))
                )
                result.put(json)
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    @SuppressLint("Range")
    fun getCatalystFromCursor(cursor: Cursor, withBlob: Boolean): ArrayList<ModelCatalyst> {
        val result: ArrayList<ModelCatalyst> = ArrayList()
        while (cursor.moveToNext()) {
            val blobImage: ByteArray? =
                if (withBlob == false) null else cursor.getBlob(
                    cursor.getColumnIndex(Configuration.DATABASE_CATALYST_THUMBNAIL)
                )
            val salt: String =
                cursor.getInt(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_ID))
                    .toString() + Secret.getPrivateKey()
            val modelCatalyst = ModelCatalyst(
                cursor.getInt(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_ID)),
                cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_ID_PICTURE)),
                cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_URL_PICTURE)),
                if (blobImage == null) null else BitmapFactory.decodeByteArray(
                    blobImage,
                    0,
                    blobImage.size
                ),
                cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_NAME)),
                cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_BRAND)),
                Formatter.formatStringFloat(
                    decrypt(
                        cursor.getString(
                            cursor.getColumnIndex(
                                Configuration.DATABASE_CATALYST_PLATINUM
                            )
                        ), salt
                    ), 3
                ).toFloat(),
                Formatter.formatStringFloat(
                    decrypt(
                        cursor.getString(
                            cursor.getColumnIndex(
                                Configuration.DATABASE_CATALYST_PALLADIUM
                            )
                        ), salt
                    ), 3
                ).toFloat(),
                Formatter.formatStringFloat(
                    decrypt(
                        cursor.getString(
                            cursor.getColumnIndex(
                                Configuration.DATABASE_CATALYST_RHODIUM
                            )
                        ), salt
                    ), 3
                ).toFloat(),
                cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_CATALYST_TYPE)),
                Formatter.formatStringFloat(
                    decrypt(
                        cursor.getString(
                            cursor.getColumnIndex(
                                Configuration.DATABASE_CATALYST_WEIGHT
                            )
                        ), salt
                    ), 3
                ).toFloat()
            )
            result.add(modelCatalyst)
        }
        return result
    }

    @SuppressLint("Range")
    fun getDataCatalyst(idInput: Int): ArrayList<ModelCatalyst> {
        val result: ArrayList<ModelCatalyst>
        var cursor: Cursor? = null
        try {
            val fields = arrayOf(
                Configuration.DATABASE_CATALYST_ID,
                Configuration.DATABASE_CATALYST_ID_PICTURE,
                Configuration.DATABASE_CATALYST_URL_PICTURE,
                Configuration.DATABASE_CATALYST_NAME,
                Configuration.DATABASE_CATALYST_BRAND,
                Configuration.DATABASE_CATALYST_PLATINUM,
                Configuration.DATABASE_CATALYST_PALLADIUM,
                Configuration.DATABASE_CATALYST_RHODIUM,
                Configuration.DATABASE_CATALYST_TYPE,
                Configuration.DATABASE_CATALYST_WEIGHT
            )
            val queryBuilder = SQLiteQueryBuilder()
            queryBuilder.tables = Configuration.DATABASE_TABLE_CATALYST
            cursor = queryBuilder.query(
                readableDatabase,
                fields,
                Configuration.DATABASE_CATALYST_ID + "> $idInput",
                null,
                null,
                null,
                null
            )
            val withBlob = fields.contains(Configuration.DATABASE_CATALYST_THUMBNAIL)
            result = getCatalystFromCursor(cursor, withBlob)
        } finally {
            cursor?.close()
        }
        return result
    }

    fun insertCatalysts(valuesOfBatch: JSONArray) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            for (i in 0 until valuesOfBatch.length()) {
                val element = valuesOfBatch.getJSONObject(i).getJSONArray("c")
                val salt: String = Spreadsheet.getValueStringFromDocsApi(
                    element,
                    Configuration.SPREADSHEET_CATALYST_ID
                ) + Secret.getPrivateKey()
                val row = ContentValues()
                row.put(
                    Configuration.DATABASE_CATALYST_ID_PICTURE,
                    Spreadsheet.getValueStringFromDocsApi(
                        element,
                        Configuration.SPREADSHEET_CATALYST_ID_PICTURE
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_URL_PICTURE,
                    Spreadsheet.getValueStringFromDocsApi(
                        element,
                        Configuration.SPREADSHEET_CATALYST_URL_PICTURE
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_NAME,
                    Spreadsheet.getValueStringFromDocsApi(
                        element,
                        Configuration.SPREADSHEET_CATALYST_NAME
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_BRAND,
                    Spreadsheet.getValueStringFromDocsApi(
                        element,
                        Configuration.SPREADSHEET_CATALYST_BRAND
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_PLATINUM,
                    encrypt(
                        Spreadsheet.getValueFloatStringFromDocsApi(
                            element,
                            Configuration.SPREADSHEET_CATALYST_PLATINUM
                        ),
                        salt
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_PALLADIUM,
                    encrypt(
                        Spreadsheet.getValueFloatStringFromDocsApi(
                            element,
                            Configuration.SPREADSHEET_CATALYST_PALLADIUM
                        ),
                        salt
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_RHODIUM,
                    encrypt(
                        Spreadsheet.getValueFloatStringFromDocsApi(
                            element,
                            Configuration.SPREADSHEET_CATALYST_RHODIUM
                        ),
                        salt
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_TYPE,
                    Spreadsheet.getValueStringFromDocsApi(
                        element,
                        Configuration.SPREADSHEET_CATALYST_TYPE
                    )
                )
                row.put(
                    Configuration.DATABASE_CATALYST_WEIGHT,
                    encrypt(
                        Spreadsheet.getValueFloatStringFromDocsApi(
                            element,
                            Configuration.SPREADSHEET_CATALYST_WEIGHT
                        ), salt
                    )
                )
                val countInserted = db.insert(Configuration.DATABASE_TABLE_CATALYST, null, row)
                if (countInserted == -1L) throw IllegalArgumentException()
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateCatalyst(catalystId: Int, thumbnail: ByteArray) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val content = ContentValues()
            content.put(Configuration.DATABASE_CATALYST_THUMBNAIL, thumbnail)
            db.updateWithOnConflict(
                Configuration.DATABASE_TABLE_CATALYST,
                content,
                Configuration.DATABASE_CATALYST_ID + "= ?",
                Array(1) { catalystId.toString() },
                SQLiteDatabase.CONFLICT_IGNORE
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @SuppressLint("Range")
    fun getCountCatalyst(): Int {
        var count = 0
        var cursor: Cursor? = null
        try {
            cursor = readableDatabase.rawQuery(
                "SELECT count(*) as count FROM ${Configuration.DATABASE_TABLE_CATALYST}",
                null
            )
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        } finally {
            cursor?.close()
        }
        return count
    }

    @SuppressLint("Range")
    fun getCountCatalystWithThumbnail(): Int {
        var count = 0
        var cursor: Cursor? = null
        try {
            cursor = readableDatabase.rawQuery(
                "SELECT count(*) as count FROM ${Configuration.DATABASE_TABLE_CATALYST} WHERE ${Configuration.DATABASE_CATALYST_THUMBNAIL} IS NOT NULL",
                null
            )
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        } finally {
            cursor?.close()
        }
        return count
    }

    @SuppressLint("Range")
    fun getCountCatalystWithUrlThumbnail(): Int {
        var count = 0
        var cursor: Cursor? = null
        try {
            cursor = readableDatabase.rawQuery(
                "SELECT count(*) as count FROM ${Configuration.DATABASE_TABLE_CATALYST} WHERE LENGTH(${Configuration.DATABASE_CATALYST_URL_PICTURE}) != 0",
                null
            )
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
        } finally {
            cursor?.close()
        }
        return count
    }
    //endregion

    //region courses
    @SuppressLint("Range")
    fun getCoursesOfYearMonths(setOfYearMonth: Set<String>): HashMap<String, HashMap<String, ModelCourse>> {
        val result: HashMap<String, HashMap<String, ModelCourse>> = hashMapOf()
        var cursor: Cursor? = null
        try {
            val fields = arrayOf(
                Configuration.DATABASE_COURSES_PLATINUM,
                Configuration.DATABASE_COURSES_PALLADIUM,
                Configuration.DATABASE_COURSES_RHODIUM,
                Configuration.DATABASE_COURSES_EUR_PLN,
                Configuration.DATABASE_COURSES_USD_PLN,
                Configuration.DATABASE_COURSES_DATE,
                Configuration.DATABASE_COURSES_YEARMONTH,
            )
            val queryString = "SELECT ${fields.joinToString()}\n" +
                    "FROM ${Configuration.DATABASE_TABLE_COURSES}\n" +
                    "WHERE ${Configuration.DATABASE_COURSES_YEARMONTH} IN (${
                        setOfYearMonth.joinToString(
                            prefix = "'",
                            postfix = "'",
                            separator = "','"
                        )
                    })\n"
            cursor = readableDatabase.rawQuery(queryString, null)
            while (cursor.moveToNext()) {
                val myCourses = ModelCourse(
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_PLATINUM)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_PALLADIUM)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_RHODIUM)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_EUR_PLN)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_USD_PLN)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_DATE)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_COURSES_YEARMONTH))
                )
                if (result.contains(myCourses.yearMonth) == false) {
                    result[myCourses.yearMonth] = hashMapOf()
                }
                result[myCourses.yearMonth]!![myCourses.date] = myCourses
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    fun insertCourses(modelCourse: ModelCourse) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val row = ContentValues()
            row.put(Configuration.DATABASE_COURSES_DATE, modelCourse.date)
            row.put(Configuration.DATABASE_COURSES_YEARMONTH, modelCourse.yearMonth)
            row.put(Configuration.DATABASE_COURSES_PLATINUM, modelCourse.platinum)
            row.put(Configuration.DATABASE_COURSES_PALLADIUM, modelCourse.palladium)
            row.put(Configuration.DATABASE_COURSES_RHODIUM, modelCourse.rhodium)
            row.put(Configuration.DATABASE_COURSES_EUR_PLN, modelCourse.eurPln)
            row.put(Configuration.DATABASE_COURSES_USD_PLN, modelCourse.usdPln)
            val countInserted = db.insert(Configuration.DATABASE_TABLE_COURSES, null, row)
            if (countInserted == -1L) throw IllegalArgumentException()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    //endregion

    //region history filter
    @SuppressLint("Range")
    fun getDataHistoryFilter(
        limitElements: String,
        nameCatalystOrBrandCarInput: String
    ): ArrayList<ModelHistoryFilter> {
        val result: ArrayList<ModelHistoryFilter> = ArrayList()
        var cursor: Cursor? = null
        try {
            val arrayFields = Parser.parseSearchingString(nameCatalystOrBrandCarInput)
            var whereClause = ""
            if (arrayFields.isNotEmpty()) {
                whereClause = "WHERE "
                var firstElement = true
                for (item in arrayFields) {
                    val arg = item.replace("*", "%")
                    whereClause += (if (firstElement) "" else " OR ") + "${Configuration.DATABASE_HISTORY_FILTER_NAME} LIKE '%${arg}%'"
                    firstElement = false
                }
                whereClause += "\n"
            }
            val fields = arrayOf(
                Configuration.DATABASE_HISTORY_FILTER_ID,
                Configuration.DATABASE_HISTORY_FILTER_NAME
            )
            val queryString = "SELECT  ${fields.joinToString()}\n" +
                    "FROM ${Configuration.DATABASE_TABLE_HISTORY_FILTER}\n" +
                    whereClause +
                    "ORDER BY ${Configuration.DATABASE_HISTORY_FILTER_ID} DESC\n" +
                    "LIMIT $limitElements"
            cursor = readableDatabase.rawQuery(queryString, null)
            while (cursor.moveToNext()) {
                val modelHistoryFilter = ModelHistoryFilter(
                    cursor.getInt(cursor.getColumnIndex(Configuration.DATABASE_HISTORY_FILTER_ID)),
                    cursor.getString(cursor.getColumnIndex(Configuration.DATABASE_HISTORY_FILTER_NAME))
                )
                result.add(modelHistoryFilter)
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    fun insertHistoryFilter(searchedText: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val row = ContentValues()
            row.put(Configuration.DATABASE_HISTORY_FILTER_NAME, searchedText)
            val countInserted = db.insert(Configuration.DATABASE_TABLE_HISTORY_FILTER, null, row)
            if (countInserted == -1L) throw IllegalArgumentException()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteHistoryFilter(id: Int) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(
                Configuration.DATABASE_TABLE_HISTORY_FILTER,
                Configuration.DATABASE_HISTORY_FILTER_ID + "= $id",
                null
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteHistoryFilter(name: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(
                Configuration.DATABASE_TABLE_HISTORY_FILTER,
                Configuration.DATABASE_HISTORY_FILTER_NAME + " LIKE '$name'",
                null
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    //endregion
}
package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import java.net.URL

class MySpreadsheet {
    companion object {

        //get url for query login
        fun getUrlToSpreadsheetLogin(login: String) : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_LOGINS + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_LOGIN}%3D'$login'"
        }

        //get url for query count catalyst
        fun getUrlToSpreadsheetCatalystCount() : String{
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20count%28${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID}%29"
        }

        //get url for data catalyst from defined row
        fun getUrlToSpreadsheetCatalystData(fromRow: Int) : String{
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID}%3E$fromRow"
        }

        //get url for query orygnal bitmap
        fun getUrlToSpreadsheetCatalystRow(idPicture: String) : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE}%3D'$idPicture'"
        }

        //get count catalyst
        fun getCountCatalyst(): Int {
            var count = 0
            //retrieve and parse to json data from spreadsheet
            val resultFromUrl = URL(getUrlToSpreadsheetCatalystCount()).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            //check if exists login
            val rows = resultJson.getJSONObject("table").getJSONArray("rows")
            if(rows.length() != 1) {
                return count
            }
            //get row element
            val element = rows.getJSONObject(0).getJSONArray("c")
            count = element.getJSONObject(0).getInt("v")
            return count
        }

        //get data catalyst
        fun getDataCatalyst(fromRow: Int): JSONArray {
            //retrieve and parse to json data from spreadsheet
            val resultFromUrl = URL(getUrlToSpreadsheetCatalystData(fromRow)).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            //check if exists login
            val rows = resultJson.getJSONObject("table").getJSONArray("rows")
            return rows
        }

        fun getBitmapOfIdPicture(idPicture: String): Bitmap? {
            //retrieve and parse to json data from spreadsheet
            val resultFromUrl = URL(getUrlToSpreadsheetCatalystRow(idPicture)).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            //check if exists login
            val rows = resultJson.getJSONObject("table").getJSONArray("rows")
            if(rows.length() != 1) {
                return null
            }
            //get row element
            val element = rows.getJSONObject(0).getJSONArray("c")
            val urlSharedPicture = element.getJSONObject(MyConfiguration.MY_SPREADSHEET_CATALYST_NUMBER_COLUMN_PICTURE).getString("v")
            val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 1920, 1080)

            return BitmapFactory.decodeStream(URL(urlPicture).openConnection().getInputStream())
        }
    }
}
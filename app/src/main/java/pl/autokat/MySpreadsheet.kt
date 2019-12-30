package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL

class MySpreadsheet {
    companion object {

        //get url for query login
        fun getUrlToSpreadsheetLogin(login: String) : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_LOGINS + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20A%3D'$login'"
        }

        //get url for query count catalyst
        fun getUrlToSpreadsheetCatalystCount() : String{
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20count%28A%29"
        }

        //get url for data catalyst
        fun getUrlToSpreadsheetCatalystData() : String{
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY
        }

        //get url for query orygnal bitmap
        fun getUrlToSpreadsheetCatalystRow(idPicture: String) : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_CATALYST + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20H%3D'$idPicture'"
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
        fun getDataCatalyst(): MutableList<MyItemCatalyst> {
            //retrieve and parse to json data from spreadsheet
            val resultFromUrl = URL(getUrlToSpreadsheetCatalystData()).readText()
            val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
            //check if exists login
            val rows = resultJson.getJSONObject("table").getJSONArray("rows")


            val itemsCatalyst = mutableListOf<MyItemCatalyst>()
            for(i in 0 until rows.length()){
                val element = rows.getJSONObject(i).getJSONArray("c")

                val urlSharedPicture = element.getJSONObject(8).getString("v")
                val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 128, 128)

                val itemCatalyst = MyItemCatalyst(
                    /* id */ i+1,
                    /* idPicture */ element.getJSONObject(7).getString("v"),
                    /* thumbnail */ BitmapFactory.decodeStream(URL(urlPicture).openConnection().getInputStream()),
                    /* name */  if (element.isNull(0))  "" else element.getJSONObject(0).getString("v"),
                    /* brand */ element.getJSONObject(1).getString("v"),
                    /* platinum */ element.getJSONObject(2).getDouble("v").toFloat(),
                    /* pallad */ element.getJSONObject(3).getDouble("v").toFloat(),
                    /* rhodium */ element.getJSONObject(4).getDouble("v").toFloat(),
                    /* type */ element.getJSONObject(5).getString("v"),
                    /* weight */ element.getJSONObject(6).getDouble("v").toFloat()
                    )

                itemsCatalyst.add(itemCatalyst)
            }
            return itemsCatalyst
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
            val urlSharedPicture = element.getJSONObject(8).getString("v")
            val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 1920, 1080)

            return BitmapFactory.decodeStream(URL(urlPicture).openConnection().getInputStream())
        }
    }
}
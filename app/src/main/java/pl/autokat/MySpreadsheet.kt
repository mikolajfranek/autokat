package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MySpreadsheet {
    companion object {

        //get login from database login
        fun getDataLogin(login: String) : JSONArray? {
            val (request, response, result) = Fuel.get(MyConfiguration.MY_SPREADSHEET_URL_LOGIN)
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw Exception()
            val rows : JSONArray = JSONObject(result.get()).getJSONArray("values")
            (0..rows.length()).forEach { i ->
                if(login.equals(rows.getJSONArray(i).getString(MyConfiguration.MY_SPREADSHEET_LOGIN_USERNAME))) return rows.getJSONArray(i)
            }
            return null
        }

        //save serial id in database login
        fun saveSerialId(userId: Int, serialId: String) {
            val sheetCell : String  = "Arkusz1!" + MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_UUID + ((userId+1).toString())
            val bodyJson = """{"range": "$sheetCell", "majorDimension": "ROWS", "values": [["$serialId"]]}"""
            val (request, response, result) = Fuel.put(MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.getSpreadsheetIdLogin() + "/values/$sheetCell",
                    listOf(
                        MyConfiguration.MY_SPREADSHEET_VALUE_INPUT_OPTION_NAME to MyConfiguration.MY_SPREADSHEET_VALUE_INPUT_OPTION_VALUE
                    )
                )
                .body(bodyJson)
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw Exception()
            val resultJson = JSONObject(result.get())
            if((resultJson.getInt("updatedRows") == 1 && resultJson.getInt("updatedColumns") == 1 && resultJson.getInt("updatedCells") == 1) == false) throw Exception()
        }

        //get count catalyst from database catalyst
        fun getCountCatalyst(): Int {
            val (request, response, result) = Fuel.get(MyConfiguration.MY_SPREADSHEET_URL_CATALYST)
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw Exception()
            return JSONObject(result.get()).getJSONArray("values").length()
        }

        //get catalysts from database catalyst
        fun getDataCatalyst(fromRow: Int): JSONArray {
            val (request, response, result) = Fuel.get(MyConfiguration.MY_SPREADSHEET_URL_CATALYST)
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw Exception()
            val rows : JSONArray = JSONObject(result.get()).getJSONArray("values")
            if(fromRow == 0) return rows
            val dataCatalyst : JSONArray = JSONArray()
            ((fromRow+1)..rows.length()).forEach{i ->
                dataCatalyst.put(rows[i])
            }
            return dataCatalyst
        }

    }
}
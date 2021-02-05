package pl.autokat.components

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException

class MySpreadsheet {
    companion object {
        /* sheet api v4 */
        //save serial id in database login
        fun saveSerialId(userId: Int, serialId: String) {
            val sheetCell : String  = "użytkownicy!" + MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_UUID + ((userId+1).toString())
            val bodyJson = """{"range": "$sheetCell", "majorDimension": "ROWS", "values": [["$serialId"]]}"""
            val (_, response, result) = Fuel.put(
                MyConfiguration.MY_SPREADSHEET_URL_PREFIX_SHEETS_API + MySecret.getSpreadsheetIdLogin() + "/values/$sheetCell",
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_INPUT to MyConfiguration.MY_SPREADSHEET_PARAMETER_INPUT_VALUE
                )
            )
                .body(bodyJson)
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val resultJson = JSONObject(result.get())
            if(resultJson.getInt("updatedRows") != 1 || resultJson.getInt("updatedColumns") != 1 || resultJson.getInt("updatedCells") != 1) throw Exception()
        }
        /* docs api */
        //get login from database login
        fun getDataLogin(login: String) : JSONArray? {
            val (_, response, result) = Fuel.get(
                MyConfiguration.MY_SPREADSHEET_LOGIN_URL_DOCS_API,
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON to MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON_VALUE,
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_WHERE to "select * where ${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_LOGIN}='$login' AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_ID} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_LOGIN} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_LICENCE} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_DISCOUNT} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_VISIBILITY} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_MINUS_PLATINIUM} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_MINUS_PALLADIUM} IS NOT NULL AND " +
                            "${MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_MINUS_RHODIUM} IS NOT NULL"
                ))
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val rows : JSONArray = MyConfiguration.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table").getJSONArray("rows")
            if(rows.length() != 1) return null
            val user = JSONArray()
            val element : JSONArray = rows.getJSONObject(0).getJSONArray("c")
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_ID
                )
            )
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_LOGIN
                )
            )
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_UUID
                )
            )
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_LICENCE
                )
            )
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT
                )
            )
            user.put(
                MyConfiguration.getValueStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY
                )
            )
            user.put(
                MyConfiguration.getValueFloatStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PLATINIUM
                )
            )
            user.put(
                MyConfiguration.getValueFloatStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PALLADIUM
                )
            )
            user.put(
                MyConfiguration.getValueFloatStringFromDocsApi(
                    element,
                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_RHODIUM
                )
            )
            return user
        }
        //get count catalyst from database catalyst
        fun getCountCatalyst(): Int {
            val (_, response, result) = Fuel.get(
                MyConfiguration.MY_SPREADSHEET_CATALYST_URL_DOCS_API,
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON to MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON_VALUE,
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_WHERE to "select count(${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID}) where " +
                            "${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID} IS NOT NULL"
                ))
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val rows : JSONArray = MyConfiguration.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table").getJSONArray("rows")
            if(rows.length() != 1) throw Exception()
            return rows.getJSONObject(0).getJSONArray("c").getJSONObject(0).getInt("v")
        }
        //get catalysts from database catalyst
        fun getDataCatalyst(fromRow: Int): JSONArray {
            val (_, response, result) = Fuel.get(
                MyConfiguration.MY_SPREADSHEET_CATALYST_URL_DOCS_API,
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON to MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON_VALUE,
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_WHERE to "select * where ${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID}>$fromRow"
                ))
                .authentication().bearer(MyConfiguration.getAccessToken()).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val rows  = MyConfiguration.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table").getJSONArray("rows")
            return rows
        }
    }
}
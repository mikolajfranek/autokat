package pl.autokat

import org.json.JSONObject
import java.net.URL

class MySpreadsheet {
    companion object {
        fun parseResultToJson(text: String): JSONObject {
            val regex = "\\{.*\\}".toRegex()
            val resultRegex = regex.find(text)?.value
            return JSONObject(resultRegex)
        }

        //retrieve url for version
        fun getUrlVersion() : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_VERSIONS + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20A%3D" + (MyConfiguration.DATABASE_VERSION + 1)
        }
    }
}
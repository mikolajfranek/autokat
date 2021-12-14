package pl.autokat.components

import android.util.Base64
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class MySpreadsheet {
    companion object {
        @Suppress("RegExpRedundantEscape")
        fun parseToJsonFromResultDocsApi(text: String): JSONObject {
            return JSONObject("\\{.*\\}".toRegex().find(text)!!.value)
        }

        private const val GOOGLE_TOKEN_URL: String = "https://oauth2.googleapis.com/token"
        private const val GOOGLE_PARAMETER_SCOPE: String = "scope"
        private const val GOOGLE_PARAMETER_SCOPE_VALUE: String =
            "https://www.googleapis.com/auth/spreadsheets"

        //generate new access token
        private fun generateNewAccessToken() {
            val privateKey: RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(
                PKCS8EncodedKeySpec(
                    Base64.decode(
                        Secret.getPrivateKey(), Base64.DEFAULT
                    )
                )
            ) as RSAPrivateKey
            val timestamp: Long = Date().time
            val signedJwt = Jwts.builder()
                .setClaims(
                    mapOf(
                        GOOGLE_PARAMETER_SCOPE to GOOGLE_PARAMETER_SCOPE_VALUE,
                        Claims.ISSUER to Secret.getEmail(),
                        Claims.AUDIENCE to GOOGLE_TOKEN_URL,
                        Claims.ISSUED_AT to Date(timestamp),
                        Claims.EXPIRATION to Date(timestamp + MyConfiguration.ONE_HOUR_IN_MILLISECONDS)
                    )
                )
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact()
            val bodyJson =
                """{"grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer","assertion" : "$signedJwt"}"""
            val (_, response, result) = Fuel.post(GOOGLE_TOKEN_URL).body(bodyJson).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val accessToken = JSONObject(result.get()).getString("access_token")
            SharedPreferences.setKeyToFile(SharedPreferences.ACCESS_TOKEN, accessToken)
            SharedPreferences.setKeyToFile(
                SharedPreferences.ACCESS_TOKEN_TIMESTAMP,
                timestamp.toString()
            )
        }

        //get access token
        fun getAccessToken(): String {
            val accessTokenTimestamp: String =
                SharedPreferences.getKeyFromFile(SharedPreferences.ACCESS_TOKEN_TIMESTAMP)
            val generateNewAccessToken: Boolean =
                (Date().time - (if (accessTokenTimestamp.isEmpty()) (0).toLong() else accessTokenTimestamp.toLong())) > MyConfiguration.ONE_HOUR_IN_MILLISECONDS
            if (generateNewAccessToken) {
                generateNewAccessToken()
            }
            return SharedPreferences.getKeyFromFile(SharedPreferences.ACCESS_TOKEN)
        }
        /* sheet api v4 */
        //save serial id in database login
        fun saveSerialId(userId: Int, serialId: String) {
            val sheetCell: String =
                "użytkownicy!" + MyConfiguration.MY_SPREADSHEET_USERS_COLUMN_UUID + ((userId + 1).toString())
            val bodyJson =
                """{"range": "$sheetCell", "majorDimension": "ROWS", "values": [["$serialId"]]}"""
            val (_, response, result) = Fuel.put(
                MyConfiguration.MY_SPREADSHEET_URL_PREFIX_SHEETS_API + Secret.getSpreadsheetIdLogin() + "/values/$sheetCell",
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_INPUT to MyConfiguration.MY_SPREADSHEET_PARAMETER_INPUT_VALUE
                )
            )
                .body(bodyJson)
                .authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val resultJson = JSONObject(result.get())
            if (resultJson.getInt("updatedRows") != 1 || resultJson.getInt("updatedColumns") != 1 || resultJson.getInt(
                    "updatedCells"
                ) != 1
            ) throw Exception()
        }

        /* docs api */
        //get login from database login
        fun getDataLogin(login: String): JSONArray? {
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
                )
            )
                .authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rows: JSONArray =
                parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                    .getJSONArray("rows")
            if (rows.length() != 1) return null
            val user = JSONArray()
            val element: JSONArray = rows.getJSONObject(0).getJSONArray("c")
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
                )
            )
                .authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rows: JSONArray =
                parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                    .getJSONArray("rows")
            if (rows.length() != 1) throw Exception()
            return rows.getJSONObject(0).getJSONArray("c").getJSONObject(0).getInt("v")
        }

        //get catalysts from database catalyst
        fun getDataCatalyst(fromRow: Int): JSONArray {
            val (_, response, result) = Fuel.get(
                MyConfiguration.MY_SPREADSHEET_CATALYST_URL_DOCS_API,
                listOf(
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON to MyConfiguration.MY_SPREADSHEET_PARAMETER_JSON_VALUE,
                    MyConfiguration.MY_SPREADSHEET_PARAMETER_WHERE to "select * where ${MyConfiguration.MY_SPREADSHEET_CATALYST_COLUMN_ID}>$fromRow"
                )
            )
                .authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            return parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                .getJSONArray("rows")
        }
    }
}
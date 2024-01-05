package pl.autokat.components

import android.util.Base64
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.enums.ProgramMode
import pl.autokat.models.ModelCatalyst
import java.net.UnknownHostException
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDateTime
import java.util.*

/* (sheet, docs) api v4 */
class Spreadsheet {
    companion object {
        private const val TOKEN_URL: String = "https://oauth2.googleapis.com/token"
        private const val TOKEN_SCOPE: String = "scope"
        private const val TOKEN_SCOPE_VALUE: String = "https://www.googleapis.com/auth/spreadsheets"
        private const val SHEET_API_URL: String = "https://sheets.googleapis.com/v4/spreadsheets/"
        private const val SHEET_API_PARAMETER_INPUT: String = "valueInputOption"
        private const val SHEET_API_PARAMETER_INPUT_VALUE: String = "USER_ENTERED"
        private const val DOCS_API_URL: String =
            "https://docs.google.com/a/google.com/spreadsheets/d/"
        private const val DOCS_API_URL_SUFFIX: String = "/gviz/tq"
        private val DOCS_API_URL_LOGIN: String =
            DOCS_API_URL + Secret.getSpreadsheetIdLogin() + DOCS_API_URL_SUFFIX
        private val DOCS_API_URL_CATALYST: String =
            DOCS_API_URL + Secret.getSpreadsheetIdCatalyst() + DOCS_API_URL_SUFFIX
        private const val DOCS_API_PARAMETER_JSON: String = "tqx"
        private const val DOCS_API_PARAMETER_JSON_VALUE: String = "out:json"
        private const val DOCS_API_PARAMETER_WHERE: String = "tq"

        private fun generateNewAccessToken() {
            val privateKey: RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(
                PKCS8EncodedKeySpec(
                    Base64.decode(
                        Secret.getPrivateKey(),
                        Base64.DEFAULT
                    )
                )
            ) as RSAPrivateKey
            val timestamp: Long = Date().time
            val signedJwt = Jwts.builder().setClaims(
                mapOf(
                    TOKEN_SCOPE to TOKEN_SCOPE_VALUE,
                    Claims.ISSUER to Secret.getEmail(),
                    Claims.AUDIENCE to TOKEN_URL,
                    Claims.ISSUED_AT to Date(timestamp),
                    Claims.EXPIRATION to Date(timestamp + Configuration.ONE_HOUR_IN_MILLISECONDS)
                )
            ).signWith(privateKey, SignatureAlgorithm.RS256).compact()
            val bodyJson =
                """{"grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer","assertion" : "$signedJwt"}"""
            val (_, response, result) = Fuel.post(TOKEN_URL).body(bodyJson).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val accessToken = JSONObject(result.get()).getString("access_token")
            SharedPreference.setKey(
                SharedPreference.ACCESS_TOKEN,
                accessToken
            )
            SharedPreference.setKey(
                SharedPreference.ACCESS_TOKEN_TIMESTAMP,
                timestamp.toString()
            )
        }

        private fun getAccessToken(): String {
            val accessTokenTimestamp: String =
                SharedPreference.getKey(SharedPreference.ACCESS_TOKEN_TIMESTAMP)
            val generateNewAccessToken: Boolean =
                (Date().time - (if (accessTokenTimestamp.isEmpty()) (0).toLong() else accessTokenTimestamp.toLong())) > Configuration.ONE_HOUR_IN_MILLISECONDS
            if (generateNewAccessToken) {
                generateNewAccessToken()
            }
            return SharedPreference.getKey(SharedPreference.ACCESS_TOKEN)
        }

        fun getValueStringFromDocsApi(jsonArray: JSONArray, index: Int): String {
            if (jsonArray.isNull(index)) return ""
            val jsonObject: JSONObject = jsonArray.getJSONObject(index)
            if (jsonObject.isNull("f") == false) {
                return jsonObject.getString("f").replace(',', '.')
            }
            if (jsonObject.isNull("v")) return ""
            return jsonObject.getString("v").trim()
        }

        fun getValueFloatStringFromDocsApi(jsonArray: JSONArray, index: Int): String {
            var stringField = getValueStringFromDocsApi(jsonArray, index)
            stringField = ("\\s+").toRegex().replace(stringField, "")
            stringField.replace(',', '.')
            if (stringField.isEmpty()) return "0.0"
            return stringField
        }

        fun saveSerialId(userId: Int, serialId: String) {
            val sheetCell: String =
                "użytkownicy!" + Configuration.SPREADSHEET_USERS_COLUMN_UUID + ((userId + 1).toString())
            val bodyJson =
                """{"range": "$sheetCell", "majorDimension": "ROWS", "values": [["$serialId"]]}"""
            val (_, response, result) = Fuel.put(
                SHEET_API_URL + Secret.getSpreadsheetIdLogin() + "/values/$sheetCell",
                listOf(SHEET_API_PARAMETER_INPUT to SHEET_API_PARAMETER_INPUT_VALUE)
            ).body(bodyJson).authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val resultJson = JSONObject(result.get())
            if (resultJson.getInt("updatedRows") != 1 || resultJson.getInt("updatedColumns") != 1 || resultJson.getInt(
                    "updatedCells"
                ) != 1
            ) throw Exception()
        }

        fun getDataLogin(login: String): JSONArray? {
            val (_, response, result) = Fuel.get(
                DOCS_API_URL_LOGIN,
                listOf(
                    DOCS_API_PARAMETER_JSON to DOCS_API_PARAMETER_JSON_VALUE,
                    DOCS_API_PARAMETER_WHERE to "select * where ${Configuration.SPREADSHEET_USERS_COLUMN_LOGIN}='$login' AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_ID} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_LOGIN} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_LICENCE} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_DISCOUNT} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_VISIBILITY} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_MINUS_PLATINUM} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_MINUS_PALLADIUM} IS NOT NULL AND " +
                            "${Configuration.SPREADSHEET_USERS_COLUMN_MINUS_RHODIUM} IS NOT NULL"
                )
            ).authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rows: JSONArray =
                Parser.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                    .getJSONArray("rows")
            if (rows.length() != 1) return null
            val user = JSONArray()
            val element: JSONArray = rows.getJSONObject(0).getJSONArray("c")
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_ID))
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_LOGIN))
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_UUID))
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_LICENCE))
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_DISCOUNT))
            user.put(getValueStringFromDocsApi(element, Configuration.SPREADSHEET_USERS_VISIBILITY))
            user.put(
                getValueFloatStringFromDocsApi(
                    element,
                    Configuration.SPREADSHEET_USERS_MINUS_PLATINUM
                )
            )
            user.put(
                getValueFloatStringFromDocsApi(
                    element,
                    Configuration.SPREADSHEET_USERS_MINUS_PALLADIUM
                )
            )
            user.put(
                getValueFloatStringFromDocsApi(
                    element,
                    Configuration.SPREADSHEET_USERS_MINUS_RHODIUM
                )
            )
            return user
        }

        fun getDataCatalyst(fromRow: Int): JSONArray {
            val (_, response, result) = Fuel.get(
                DOCS_API_URL_CATALYST,
                listOf(
                    DOCS_API_PARAMETER_JSON to DOCS_API_PARAMETER_JSON_VALUE,
                    DOCS_API_PARAMETER_WHERE to "select * where ${Configuration.SPREADSHEET_CATALYST_COLUMN_ID}>$fromRow"
                )
            ).authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            return Parser.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                .getJSONArray("rows")
        }

        fun getCountCatalyst(): Int {
            val (_, response, result) = Fuel.get(
                DOCS_API_URL_CATALYST,
                listOf(
                    DOCS_API_PARAMETER_JSON to DOCS_API_PARAMETER_JSON_VALUE,
                    DOCS_API_PARAMETER_WHERE to "select count(${Configuration.SPREADSHEET_CATALYST_COLUMN_ID}) where " +
                            "${Configuration.SPREADSHEET_CATALYST_COLUMN_ID} IS NOT NULL and " +
                            "${Configuration.SPREADSHEET_CATALYST_COLUMN_ID}>=1 "
                )
            ).authentication().bearer(getAccessToken()).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rows: JSONArray =
                Parser.parseToJsonFromResultDocsApi(result.get()).getJSONObject("table")
                    .getJSONArray("rows")
            if (rows.length() != 1) return 0
            return rows.getJSONObject(0).getJSONArray("c").getJSONObject(0).getInt("v")
        }
    }
}
package pl.autokat.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Base64
import com.github.kittinunf.fuel.Fuel
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.net.UnknownHostException
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MyConfiguration {
    companion object {
        /* production mode = true / development mode = false */
        const val PRODUCTION: Boolean = false
        const val VERSION_APP: String = "1.0.6"
        const val DATABASE_VERSION_1_0_6: Int = 4
        const val DATABASE_VERSION: Int = 4
        const val DATABASE_NAME_OF_FILE: String = "autokat.db"
        const val DATABASE_FILE_PATH_ASSETS: String = "databases/$DATABASE_NAME_OF_FILE"

        /* creating access token */
        private const val GOOGLE_TOKEN_URL: String = "https://oauth2.googleapis.com/token"
        private const val GOOGLE_PARAMETER_SCOPE: String = "scope"
        private const val GOOGLE_PARAMETER_SCOPE_VALUE: String =
            "https://www.googleapis.com/auth/spreadsheets"
        private const val ONE_HOUR_IN_MILLISECONDS: Long = 3600000L

        //shared preferences
        private const val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN: String = "AccessToken"
        private const val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP: String =
            "AccessTokenTimestamp"

        //generate new access token
        private fun generateNewAccessToken() {
            val privateKey: RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(
                PKCS8EncodedKeySpec(
                    Base64.decode(
                        MySecret.getPrivateKey(), Base64.DEFAULT
                    )
                )
            ) as RSAPrivateKey
            val timestamp: Long = Date().time
            val signedJwt = Jwts.builder()
                .setClaims(
                    mapOf(
                        GOOGLE_PARAMETER_SCOPE to GOOGLE_PARAMETER_SCOPE_VALUE,
                        Claims.ISSUER to MySecret.getEmail(),
                        Claims.AUDIENCE to GOOGLE_TOKEN_URL,
                        Claims.ISSUED_AT to Date(timestamp),
                        Claims.EXPIRATION to Date(timestamp + ONE_HOUR_IN_MILLISECONDS)
                    )
                )
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact()
            val bodyJson =
                """{"grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer","assertion" : "$signedJwt"}"""
            val (_, response, result) = Fuel.post(GOOGLE_TOKEN_URL).body(bodyJson).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val accessToken = JSONObject(result.get()).getString("access_token")
            MySharedPreferences.setKeyToFile(MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN, accessToken)
            MySharedPreferences.setKeyToFile(
                MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP,
                timestamp.toString()
            )
        }

        //get access token
        fun getAccessToken(): String {
            val accessTokenTimestamp: String =
                MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP)
            val generateNewAccessToken: Boolean =
                (Date().time - (if (accessTokenTimestamp.isEmpty()) (0).toLong() else accessTokenTimestamp.toLong())) > ONE_HOUR_IN_MILLISECONDS
            if (generateNewAccessToken) {
                generateNewAccessToken()
            }
            return MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN)
        }

        /* licence and time */
        private const val URL_TIMESTAMP: String =
            "https://worldtimeapi.org/api/timezone/Europe/Warsaw"
        const val ONE_DAY_IN_MILLISECONDS: Long = 86400000

        //shared preferences
        private const val MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP: String = "CurrentTimestamp"

        //check time depends from parameter
        @SuppressLint("SimpleDateFormat")
        fun checkTimeOnPhone(dateInput: String, timeChecking: MyTimeChecking): Boolean {
            when (timeChecking) {
                MyTimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    //time from web minus 1 hour must by greater than time now
                    val json = JSONObject(URL(URL_TIMESTAMP).readText())
                    val timestampWeb: Long =
                        (json.getLong("unixtime") * 1000L) - ONE_HOUR_IN_MILLISECONDS
                    val timestampPhone: Long = Date().time
                    if (timestampPhone > timestampWeb) {
                        MySharedPreferences.setKeyToFile(
                            MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP,
                            timestampPhone.toString()
                        )
                        return true
                    }
                    return false
                }
                MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW -> {
                    val timestamp: Long = Date().time
                    val timestampInput: Long =
                        (SimpleDateFormat("yyyy-MM-dd").parse(dateInput)!!.time) + ONE_DAY_IN_MILLISECONDS
                    return timestampInput > timestamp
                }
                MyTimeChecking.CHECKING_LICENCE -> {
                    val timestamp: Long = Date().time
                    val timestampLicence: Long = (SimpleDateFormat("yyyy-MM-dd").parse(
                        MySharedPreferences.getKeyFromFile(
                            MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END
                        )
                    )!!.time) + ONE_DAY_IN_MILLISECONDS
                    val timestampFromConfiguration: Long = MySharedPreferences.getKeyFromFile(
                        MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP
                    ).toLong()
                    if ((timestamp > timestampFromConfiguration) && (timestampLicence > timestamp)) {
                        MySharedPreferences.setKeyToFile(
                            MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP,
                            timestamp.toString()
                        )
                        return true
                    }
                    return false
                }
            }
        }

        /* spreadsheet */
        //users column number
        const val MY_SPREADSHEET_USERS_ID: Int = 0
        const val MY_SPREADSHEET_USERS_LOGIN: Int = 1
        const val MY_SPREADSHEET_USERS_UUID: Int = 2
        const val MY_SPREADSHEET_USERS_LICENCE: Int = 3
        const val MY_SPREADSHEET_USERS_DISCOUNT: Int = 4
        const val MY_SPREADSHEET_USERS_VISIBILITY: Int = 5
        const val MY_SPREADSHEET_USERS_MINUS_PLATINIUM: Int = 6
        const val MY_SPREADSHEET_USERS_MINUS_PALLADIUM: Int = 7
        const val MY_SPREADSHEET_USERS_MINUS_RHODIUM: Int = 8

        //users column letter
        const val MY_SPREADSHEET_USERS_COLUMN_ID: String = "A"
        const val MY_SPREADSHEET_USERS_COLUMN_LOGIN: String = "B"
        const val MY_SPREADSHEET_USERS_COLUMN_UUID: String = "C"
        const val MY_SPREADSHEET_USERS_COLUMN_LICENCE: String = "D"
        const val MY_SPREADSHEET_USERS_COLUMN_DISCOUNT: String = "E"
        const val MY_SPREADSHEET_USERS_COLUMN_VISIBILITY: String = "F"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_PLATINIUM: String = "G"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_PALLADIUM: String = "H"
        const val MY_SPREADSHEET_USERS_COLUMN_MINUS_RHODIUM: String = "I"

        //catalysts column number
        const val MY_SPREADSHEET_CATALYST_ID: Int = 0
        const val MY_SPREADSHEET_CATALYST_NAME: Int = 1
        const val MY_SPREADSHEET_CATALYST_BRAND: Int = 2
        const val MY_SPREADSHEET_CATALYST_WEIGHT: Int = 3
        const val MY_SPREADSHEET_CATALYST_PLATINUM: Int = 4
        const val MY_SPREADSHEET_CATALYST_PALLADIUM: Int = 5
        const val MY_SPREADSHEET_CATALYST_RHODIUM: Int = 6
        const val MY_SPREADSHEET_CATALYST_TYPE: Int = 7
        const val MY_SPREADSHEET_CATALYST_ID_PICTURE: Int = 8
        const val MY_SPREADSHEET_CATALYST_URL_PICTURE: Int = 9

        //catalysts column letter
        const val MY_SPREADSHEET_CATALYST_COLUMN_ID: String = "A"
        const val MY_SPREADSHEET_CATALYST_COLUMN_NAME: String = "B"
        const val MY_SPREADSHEET_CATALYST_COLUMN_BRAND: String = "C"
        const val MY_SPREADSHEET_CATALYST_COLUMN_WEIGHT: String = "D"
        const val MY_SPREADSHEET_CATALYST_COLUMN_PLATTINUM: String = "E"
        const val MY_SPREADSHEET_CATALYST_COLUMN_PALLADIUM: String = "F"
        const val MY_SPREADSHEET_CATALYST_COLUMN_RHODIUM: String = "G"
        const val MY_SPREADSHEET_CATALYST_COLUMN_TYPE: String = "H"
        const val MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE: String = "I"
        const val MY_SPREADSHEET_CATALYST_COLUMN_URL_PICTURE: String = "J"

        /* spreadsheet sheet api v4 */
        const val MY_SPREADSHEET_URL_PREFIX_SHEETS_API: String =
            "https://sheets.googleapis.com/v4/spreadsheets/"
        const val MY_SPREADSHEET_PARAMETER_INPUT: String = "valueInputOption"
        const val MY_SPREADSHEET_PARAMETER_INPUT_VALUE: String = "USER_ENTERED"

        /* spreadsheet docs api */
        private const val MY_SPREADSHEET_URL_PREFIX_DOCS_API: String =
            "https://docs.google.com/a/google.com/spreadsheets/d/"
        private const val MY_SPREADSHEET_URL_SUFIX_DOCS_API: String = "/gviz/tq"
        val MY_SPREADSHEET_LOGIN_URL_DOCS_API: String =
            MY_SPREADSHEET_URL_PREFIX_DOCS_API + MySecret.getSpreadsheetIdLogin() + MY_SPREADSHEET_URL_SUFIX_DOCS_API
        val MY_SPREADSHEET_CATALYST_URL_DOCS_API: String =
            MY_SPREADSHEET_URL_PREFIX_DOCS_API + MySecret.getSpreadsheetIdCatalyst() + MY_SPREADSHEET_URL_SUFIX_DOCS_API
        const val MY_SPREADSHEET_PARAMETER_JSON: String = "tqx"
        const val MY_SPREADSHEET_PARAMETER_JSON_VALUE: String = "out:json"
        const val MY_SPREADSHEET_PARAMETER_WHERE: String = "tq"

        @Suppress("RegExpRedundantEscape")
        fun parseToJsonFromResultDocsApi(text: String): JSONObject {
            return JSONObject("\\{.*\\}".toRegex().find(text)!!.value)
        }

        fun getValueStringFromDocsApi(jsonArrary: JSONArray, index: Int): String {
            if (jsonArrary.isNull(index)) return ""
            val jsonObject: JSONObject = jsonArrary.getJSONObject(index)
            if (jsonObject.isNull("f") == false) {
                return jsonObject.getString("f").replace(',', '.')
            }
            if (jsonObject.isNull("v")) return ""
            return jsonObject.getString("v").trim()
        }

        fun getValueFloatStringFromDocsApi(jsonArrary: JSONArray, index: Int): String {
            var stringField = getValueStringFromDocsApi(jsonArrary, index)
            stringField = ("\\s+").toRegex().replace(stringField, "")
            stringField.replace(',', '.')
            if (stringField.isEmpty()) return "0.0"
            return stringField
        }

        /* courses exchange */
        const val MY_CATALYST_VALUES_URL_USD_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        const val MY_CATALYST_VALUES_URL_EUR_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        const val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM =
            "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        const val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM =
            "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        const val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM =
            "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        const val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        /* shared preferences */
        const val MY_SHARED_PREFERENCES_NAME: String = "MyKatSharedPreferences"
        const val MY_SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE

        //about user
        const val MY_SHARED_PREFERENCES_KEY_LOGIN: String = "Login"
        const val MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END: String = "LicenceDate"
        const val MY_SHARED_PREFERENCES_KEY_DISCOUNT: String = "Discount"
        const val MY_SHARED_PREFERENCES_KEY_VISIBILITY: String = "Visibility"
        const val MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP: String =
            "UpdateCourseTimestamp"
        const val MY_SHARED_PREFERENCES_KEY_MINUS_PLATINIUM: String = "MinusPlatinium"
        const val MY_SHARED_PREFERENCES_KEY_MINUS_PALLADIUM: String = "MinusPalladium"
        const val MY_SHARED_PREFERENCES_KEY_MINUS_RHODIUM: String = "MinusRhodium"
        const val MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT: String = "LastSearchedText"

        //about courses elements
        const val MY_SHARED_PREFERENCES_KEY_PLATIUNUM: String = "Platinum"
        const val MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE: String = "PlatinumDate"
        const val MY_SHARED_PREFERENCES_KEY_PALLADIUM: String = "Palladium"
        const val MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE: String = "PalladiumDate"
        const val MY_SHARED_PREFERENCES_KEY_RHODIUM: String = "Rhodium"
        const val MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE: String = "RhodiumDate"
        const val MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE: String = "ActualCoursesDate"
        const val MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_CHOICE: String = "ActualCoursesChoice"

        //about courses exchanges
        const val MY_SHARED_PREFERENCES_KEY_USD_PLN: String = "UsdPln"
        const val MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE: String = "UsdPlnDate"
        const val MY_SHARED_PREFERENCES_KEY_EUR_PLN: String = "EurPln"
        const val MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE: String = "EurPlnDate"

        /* database */
        const val DATABASE_PAGINATE_LIMIT_CATALYST: Int = 5
        const val DATABASE_PAGINATE_LIMIT_HISTORY_FILTER: Int = 10

        // tables
        const val DATABASE_TABLE_CATALYST = "catalyst"
        const val DATABASE_TABLE_HISTORY_FILTER = "history_filter"
        const val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        const val DATABASE_TABLE_COURSES = "courses"

        //columns courses
        const val DATABASE_ELEMENT_COURSES_ID = "id"
        const val DATABASE_ELEMENT_COURSES_DATE = "date"
        const val DATABASE_ELEMENT_COURSES_YEARMONTH = "yearmonth"
        const val DATABASE_ELEMENT_COURSES_PLATINUM = "platinum"
        const val DATABASE_ELEMENT_COURSES_PALLADIUM = "palladium"
        const val DATABASE_ELEMENT_COURSES_RHODIUM = "rhodium"
        const val DATABASE_ELEMENT_COURSES_EUR_PLN = "eur_pln"
        const val DATABASE_ELEMENT_COURSES_USD_PLN = "usd_pln"

        //columns catalyst
        const val DATABASE_ELEMENT_CATALYST_ID = "id"
        const val DATABASE_ELEMENT_CATALYST_ID_PICTURE = "id_picture"
        const val DATABASE_ELEMENT_CATALYST_URL_PICTURE = "url_picture"
        const val DATABASE_ELEMENT_CATALYST_THUMBNAIL = "thumbnail"
        const val DATABASE_ELEMENT_CATALYST_NAME = "name"
        const val DATABASE_ELEMENT_CATALYST_BRAND = "brand"
        const val DATABASE_ELEMENT_CATALYST_PLATINUM = "platinum"
        const val DATABASE_ELEMENT_CATALYST_PALLADIUM = "palladium"
        const val DATABASE_ELEMENT_CATALYST_RHODIUM = "rhodium"
        const val DATABASE_ELEMENT_CATALYST_TYPE = "type"
        const val DATABASE_ELEMENT_CATALYST_WEIGHT = "weight"
        const val DATABASE_ELEMENT_CATALYST_TEMP_HITCOUNT = "hitcount"

        //columns history_filter
        const val DATABASE_ELEMENT_HISTORY_FILTER_ID = "id"
        const val DATABASE_ELEMENT_HISTORY_FILTER_NAME = "name"

        //columns sqlite_sequence
        const val DATABASE_ELEMENT_SQLITE_SEQUENCE_SEQ = "seq"
        const val DATABASE_ELEMENT_SQLITE_SEQUENCE_NAME = "name"

        /* others */
        const val REQUEST_CODE_READ_PHONE_STATE: Int = 0
        var IS_AVAILABLE_UPDATE: Boolean = false
        fun getIntFromString(input: String): Int {
            var result = ("\\s+").toRegex().replace(input, "")
            result = result.replace(',', '.')
            result =
                if (result.indexOf(",") != -1) result.substring(0, result.indexOf(",")) else result
            var resultInt = 0
            try {
                resultInt = result.toInt()
            } catch (e: Exception) {
                //noting
            }
            resultInt = if (resultInt < 0) 0 else resultInt
            return resultInt
        }

        @Suppress("ReplaceCallWithBinaryOperator")
        fun getIntFromEnumBoolean(input: String): Int {
            val result = ("[^a-zA-Z]").toRegex().replace(input, "")
            if (result.equals("tak")) return 1
            return 0
        }

        /* info */
        //color
        val INFO_MESSAGE_COLOR_WHITE: Int = Color.parseColor("#FFFFFF")
        val INFO_MESSAGE_COLOR_FAILED: Int = Color.parseColor("#EF4836")
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.parseColor("#363636")

        //message
        const val INFO_MESSAGE_USER_NEVER_LOGGED: String = "Wprowadź nazwę użytkownika"
        const val INFO_MESSAGE_WAIT_AUTHENTICATE: String = "Trwa uwierzytelnianie…"
        const val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"
        const val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        const val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        const val INFO_MESSAGE_NETWORK_FAILED: String = "Brak połączenia"
        const val INFO_MESSAGE_UNHANDLED_EXCEPTION: String = "Wystąpił błąd"
        const val INFO_MESSAGE_SAVE_EMPTY_VALUE: String = "Nie można zapisać pustej wartości"
        const val INFO_MESSAGE_WAIT_UPDATE: String = "Trwa aktualizacja…"
        const val INFO_UPDATE_SUCCESS: String = "Aktualizacja przebiegła pomyślnie"
        const val INFO_UPDATE_FAILED: String = "Wystąpił błąd podczas aktualizacji"
        const val INFO_DOWNLOAD_BITMAP_WAIT: String = "Trwa pobieranie obrazu…"
        const val INFO_DOWNLOAD_BITMAP_FAILED: String = "Wystąpił błąd podczas pobierania obrazu"
        const val INFO_DOWNLOAD_BITMAP_STATUS: String = "Status pobieranych miniatur"
        const val INFO_DOWNLOAD_BITMAP_SUCCESS: String = "Baza danych jest aktualna"
        const val INFO_EMPTY_DATABASE: String = "Baza danych jest pusta"
        const val INFO_DATABASE_EXPIRE: String = "Baza danych nie jest aktualna"
        const val INFO_MESSAGE_ADDED_HISTORY_FILTER: String =
            "Pomyślnie zapisano nazwę do filtrowania"
        const val INFO_MESSAGE_DELETED_HISTORY_FILTER: String =
            "Pomyślnie usunięto nazwę do filtrowania"
        const val INFO_MESSAGE_REFRESH_COURSES: String = "Odśwież wartości kursów"

        /* methods */
        //decorator for delete others signs
        fun decoratorIdentificatorOfUser(applicationContext: Context): String {
            var identificator: String = getIdentificatorOfUser(applicationContext)
            identificator = ("[^A-Za+-z0-9]+").toRegex().replace(identificator, "")
            //return if is not empty
            if (identificator.isEmpty() == false) return identificator
            throw Exception()
        }

        //get serial if of phone if is empty then return phone number
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getIdentificatorOfUser(applicationContext: Context): String {
            //serial id section
            var serialId = ""
            try {
                serialId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    @Suppress("DEPRECATION")
                    Build.SERIAL
                }
            } catch (e: Exception) {
                //nothing
            }
            //return if serial id is not empty
            if (serialId.isEmpty() == false) return serialId
            //phone number section
            try {
                serialId =
                    (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number.toString()
            } catch (e: Exception) {
                //nothing
            }
            //return if phone number is not empty
            if (serialId.isEmpty() == false) return serialId
            //phone number section
            try {
                serialId =
                    (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
            } catch (e: Exception) {
                //nothing
            }
            //return if sim id is not empty
            if (serialId.isEmpty() == false) return serialId
            //android id for android > 10
            try {
                serialId = Settings.Secure.getString(
                    applicationContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            } catch (e: Exception) {
                //nothing
            }
            //return if is not empty
            if (serialId.isEmpty() == false) return serialId
            throw Exception()
        }

        //get date in format dd.mm.yyyy
        @SuppressLint("SimpleDateFormat")
        fun formatDate(date: String): String {
            if (date.isEmpty()) return ""
            return SimpleDateFormat("dd.MM.yyyy").format((SimpleDateFormat("yyyy-MM-dd").parse(date)!!))
                .toString()
        }

        fun formatDateToLocalDate(date: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return LocalDate.parse(date, formatter)
        }

        //get format of float from string
        fun formatStringFloat(floatString: String, precision: Int): String {
            if (floatString.isEmpty()) return (String.format(
                "%." + precision + "f",
                (0.00).toFloat()
            )).replace(",", ".")
            return String.format("%." + precision + "f", floatString.toFloat()).replace(",", ".")
        }

        //get pln from dolar string
        fun getPlnFromDolar(dolar: String, dolarCourses: String): String {
            if (dolar.isEmpty()) return (0.00).toString()
            return (dolar.toFloat() * (if (dolarCourses.isEmpty()) (0.0F) else dolarCourses.toFloat())).toString()
        }

        //get url to google
        fun getPictureUrlFromGoogle(urlShared: String, width: Int, height: Int): String {
            var url = urlShared
            val resultRegex = ".*/d/".toRegex().find(url)!!.value
            url = url.substring(resultRegex.length)
            val pictureIdFromGoogle = url.substring(0, url.indexOf('/'))
            return "https://lh3.googleusercontent.com/u/0/d/$pictureIdFromGoogle=w$width-h$height"
        }

        private const val CIPHER_TRANSFORMATION: String = "BLOWFISH/ECB/PKCS5Padding"

        //get encrypted
        @SuppressLint("GetInstance")
        fun encrypt(input: String, salt: String): String {
            val secret = SecretKeySpec(salt.toByteArray(charset("UTF8")), "BLOWFISH")
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secret)
            return Base64.encodeToString(
                cipher.doFinal(input.toByteArray(charset("UTF8"))),
                Base64.DEFAULT
            )
        }

        //get decrypted
        @SuppressLint("GetInstance")
        fun decrypt(input: String, salt: String): String {
            val secret = SecretKeySpec(salt.toByteArray(charset("UTF8")), "BLOWFISH")
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secret)
            return String(cipher.doFinal(Base64.decode(input, Base64.DEFAULT)))
        }

        //get list from string searching
        fun getSearchingString(input: String): List<String> {
            var searchString = ("\\*{2,}").toRegex().replace(input.trim(), "*")
            searchString = ("\\s{2,}").toRegex().replace(searchString, " ")
            return if (searchString.isEmpty()) mutableListOf() else searchString.split(" ")
        }

        //get colored text
        fun getColoredText(input: String, search: String): SpannableString {
            val spannable = SpannableString(input)
            for (item in getSearchingString(search)) {
                val regex = item.replace("*", ".*")
                var startIndex = 0
                regex.toRegex(
                    options = mutableSetOf(
                        RegexOption.IGNORE_CASE,
                        RegexOption.MULTILINE,
                        RegexOption.DOT_MATCHES_ALL
                    )
                ).findAll(input, 0).toList().forEach { x ->
                    val i = input.indexOf(x.value, startIndex, true)
                    startIndex = i + 1
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#FF00FF")),
                        i,
                        i + x.value.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannable
        }
    }
}
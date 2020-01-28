package pl.autokat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
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
import java.util.*


class MyConfiguration {
    companion object {
        /* production mode = true / development mode = false */
        val PRODUCTION : Boolean = false
        val VERSION_APP : String = "1.0.0"

        /* creating access token */
        private val GOOGLE_TOKEN_URL : String = "https://oauth2.googleapis.com/token"
        private val GOOGLE_PARAMETER_SCOPE : String = "scope"
        private val GOOGLE_PARAMETER_SCOPE_VALUE : String = "https://www.googleapis.com/auth/spreadsheets"
        private val ONE_HOUR_IN_MILLISECONDS : Long = 3600 * 1000L
        //shared preferences
        private val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN : String = "AccessToken"
        private val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP : String = "AccessTokenTimestamp"
        //generate new access token
        private fun generateNewAccessToken(){
            val privateKey : RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(Base64.decode(MySecret.getPrivateKey(), Base64.DEFAULT))) as RSAPrivateKey
            val timestamp : Long = Date().time
            val signedJwt = Jwts.builder()
                .setClaims(
                    mapOf(
                        this.GOOGLE_PARAMETER_SCOPE to this.GOOGLE_PARAMETER_SCOPE_VALUE,
                        Claims.ISSUER to MySecret.getEmail(),
                        Claims.AUDIENCE to this.GOOGLE_TOKEN_URL,
                        Claims.ISSUED_AT to Date(timestamp),
                        Claims.EXPIRATION to Date(timestamp + this.ONE_HOUR_IN_MILLISECONDS)
                    )
                )
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact()
            val bodyJson = """{"grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer","assertion" : "$signedJwt"}"""
            val (_, response, result) = Fuel.post(this.GOOGLE_TOKEN_URL).body(bodyJson).responseString()
            if(response.statusCode != 200) throw UnknownHostException()
            val accessToken = JSONObject(result.get()).getString("access_token")
            MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN, accessToken)
            MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP, timestamp.toString())
        }
        //get access token
        fun getAccessToken() : String{
            val accessTokenTimestamp : String = MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP)
            val generateNewAccessToken : Boolean = (Date().time - (if (accessTokenTimestamp.isEmpty()) (0).toLong() else accessTokenTimestamp.toLong())) > this.ONE_HOUR_IN_MILLISECONDS
            if(generateNewAccessToken){
                this.generateNewAccessToken()
            }
            return MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN)
        }

        /* licence and time */
        private val URL_TIMESTAMP : String = "https://worldtimeapi.org/api/timezone/Europe/Warsaw"
        val ONE_DAY_IN_MILLISECONDS : Long = 86400000
        //shared preferences
        private val MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP : String = "CurrentTimestamp"
        //check time depends from parameter
        @SuppressLint("SimpleDateFormat")
        fun checkTimeOnPhone(dateInput : String, timeChecking : MyTimeChecking) : Boolean{
            when(timeChecking){
                MyTimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    //time from web minus 1 hour must by greater than time now
                    val json = JSONObject(URL(this.URL_TIMESTAMP).readText())
                    val timestampWeb : Long = (json.getLong("unixtime") * 1000L) - this.ONE_HOUR_IN_MILLISECONDS
                    val timestampPhone : Long = Date().time
                    if(timestampPhone > timestampWeb){
                        MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP, timestampPhone.toString())
                        return true
                    }
                    return false
                }
                MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW -> {
                    val timestamp : Long = Date().time
                    val timestampInput : Long = (SimpleDateFormat("yyyy-MM-dd").parse(dateInput)!!.time) + ONE_DAY_IN_MILLISECONDS
                    return timestampInput > timestamp
                }
                MyTimeChecking.CHECKING_LICENCE -> {
                    val timestamp : Long = Date().time
                    val timestampLicence : Long = (SimpleDateFormat("yyyy-MM-dd").parse(MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END))!!.time) + ONE_DAY_IN_MILLISECONDS
                    val timestampFromConfiguration : Long =  MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP).toLong()
                    if((timestamp > timestampFromConfiguration) && (timestampLicence > timestamp)){
                        MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP, timestamp.toString())
                        return true
                    }
                    return false
                }
            }
        }

        /* spreadsheet */
        //users column number
        val MY_SPREADSHEET_USERS_ID : Int = 0
        val MY_SPREADSHEET_USERS_LOGIN : Int = 1
        val MY_SPREADSHEET_USERS_UUID : Int = 2
        val MY_SPREADSHEET_USERS_LICENCE : Int = 3
        val MY_SPREADSHEET_USERS_DISCOUNT : Int = 4
        val MY_SPREADSHEET_USERS_VISIBILITY : Int = 5
        //users column letter
        val MY_SPREADSHEET_USERS_COLUMN_ID : String = "A"
        val MY_SPREADSHEET_USERS_COLUMN_LOGIN : String = "B"
        val MY_SPREADSHEET_USERS_COLUMN_UUID : String = "C"
        val MY_SPREADSHEET_USERS_COLUMN_LICENCE : String = "D"
        val MY_SPREADSHEET_USERS_COLUMN_DISCOUNT : String = "E"
        val MY_SPREADSHEET_USERS_COLUMN_VISIBILITY : String = "F"
        //catalysts column number
        val MY_SPREADSHEET_CATALYST_ID : Int = 0
        val MY_SPREADSHEET_CATALYST_NAME : Int = 1
        val MY_SPREADSHEET_CATALYST_BRAND : Int = 2
        val MY_SPREADSHEET_CATALYST_PLATINUM : Int = 3
        val MY_SPREADSHEET_CATALYST_PALLADIUM : Int = 4
        val MY_SPREADSHEET_CATALYST_RHODIUM : Int = 5
        val MY_SPREADSHEET_CATALYST_WEIGHT : Int = 6
        val MY_SPREADSHEET_CATALYST_TYPE : Int = 7
        val MY_SPREADSHEET_CATALYST_ID_PICTURE : Int = 8
        val MY_SPREADSHEET_CATALYST_URL_PICTURE : Int = 9
        //catalysts column letter
        val MY_SPREADSHEET_CATALYST_COLUMN_ID : String = "A"
        val MY_SPREADSHEET_CATALYST_COLUMN_NAME : String = "B"
        val MY_SPREADSHEET_CATALYST_COLUMN_BRAND : String = "C"
        val MY_SPREADSHEET_CATALYST_COLUMN_PLATTINUM : String = "D"
        val MY_SPREADSHEET_CATALYST_COLUMN_PALLADIUM : String = "E"
        val MY_SPREADSHEET_CATALYST_COLUMN_RHODIUM : String = "F"
        val MY_SPREADSHEET_CATALYST_COLUMN_WEIGHT : String = "G"
        val MY_SPREADSHEET_CATALYST_COLUMN_TYPE : String = "H"
        val MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE : String = "I"
        val MY_SPREADSHEET_CATALYST_COLUMN_URL_PICTURE : String = "J"

        /* spreadsheet sheet api v4 */
        val MY_SPREADSHEET_URL_PREFIX_SHEETS_API : String = "https://sheets.googleapis.com/v4/spreadsheets/"
        val MY_SPREADSHEET_PARAMETER_INPUT: String = "valueInputOption"
        val MY_SPREADSHEET_PARAMETER_INPUT_VALUE : String = "USER_ENTERED"

        /* spreadsheet docs api */
        private val MY_SPREADSHEET_URL_PREFIX_DOCS_API : String = "https://docs.google.com/a/google.com/spreadsheets/d/"
        private val MY_SPREADSHEET_URL_SUFIX_DOCS_API : String = "/gviz/tq"
        val MY_SPREADSHEET_LOGIN_URL_DOCS_API : String = this.MY_SPREADSHEET_URL_PREFIX_DOCS_API + MySecret.getSpreadsheetIdLogin() + this.MY_SPREADSHEET_URL_SUFIX_DOCS_API
        val MY_SPREADSHEET_CATALYST_URL_DOCS_API : String = this.MY_SPREADSHEET_URL_PREFIX_DOCS_API + MySecret.getSpreadsheetIdCatalyst() + this.MY_SPREADSHEET_URL_SUFIX_DOCS_API
        val MY_SPREADSHEET_PARAMETER_JSON : String = "tqx"
        val MY_SPREADSHEET_PARAMETER_JSON_VALUE : String = "out:json"
        val MY_SPREADSHEET_PARAMETER_WHERE: String = "tq"
        fun parseToJsonFromResultDocsApi(text: String): JSONObject {
            return JSONObject("\\{.*\\}".toRegex().find(text)!!.value)
        }
        fun getValueStringFromDocsApi(jsonArrary: JSONArray, index : Int) : String{
            if(jsonArrary.isNull(index)) return ""
            val jsonObject : JSONObject = jsonArrary.getJSONObject(index)
            if(jsonObject.isNull("f") == false){
                return jsonObject.getString("f").replace(',', '.')
            }
            if(jsonObject.isNull("v")) return ""
            return jsonObject.getString("v").replace("\n", " ").trim()
        }
        fun getValueFloatStringFromDocsApi(jsonArrary: JSONArray, index : Int) : String{
            var stringField = this.getValueStringFromDocsApi(jsonArrary, index)
            stringField = ("\\s+").toRegex().replace(stringField, "")
            stringField.replace(',', '.')
            if(stringField.isEmpty()) return "0.0"
            return stringField
        }

        /* courses exchange */
        val MY_CATALYST_VALUES_URL_USD_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        val MY_CATALYST_VALUES_URL_EUR_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM = "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM = "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM  = "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        /* shared preferences */
        val MY_SHARED_PREFERENCES_NAME : String = "MyKatSharedPreferences"
        val MY_SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE
        //about user
        val MY_SHARED_PREFERENCES_KEY_LOGIN : String = "Login"
        val MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END : String = "LicenceDate"
        val MY_SHARED_PREFERENCES_KEY_DISCOUNT : String = "Discount"
        val MY_SHARED_PREFERENCES_KEY_VISIBILITY : String = "Visibility"
        val MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP : String = "UpdateCourseTimestamp"
        //about courses elements
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM : String = "Platinum"
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE : String = "PlatinumDate"
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM : String = "Palladium"
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE : String = "PalladiumDate"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM : String = "Rhodium"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE : String = "RhodiumDate"
        //about courses exchanges
        val MY_SHARED_PREFERENCES_KEY_USD_PLN : String = "UsdPln"
        val MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE : String = "UsdPlnDate"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN : String = "EurPln"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE : String = "EurPlnDate"

        /* database */
        val DATABASE_VERSION : Int = 1
        val DATABASE_NAME_OF_FILE : String = "autokat.db"
        val DATABASE_PAGINATE_LIMIT : Int = 5
        // tables
        val DATABASE_TABLE_CATALYST = "catalyst"
        val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        //columns catalyst
        val DATABASE_ELEMENT_CATALYST_ID = "id"
        val DATABASE_ELEMENT_CATALYST_ID_PICTURE = "id_picture"
        val DATABASE_ELEMENT_CATALYST_URL_PICTURE = "url_picture"
        val DATABASE_ELEMENT_CATALYST_THUMBNAIL = "thumbnail"
        val DATABASE_ELEMENT_CATALYST_NAME = "name"
        val DATABASE_ELEMENT_CATALYST_BRAND = "brand"
        val DATABASE_ELEMENT_CATALYST_PLATINUM = "platinum"
        val DATABASE_ELEMENT_CATALYST_PALLADIUM = "palladium"
        val DATABASE_ELEMENT_CATALYST_RHODIUM = "rhodium"
        val DATABASE_ELEMENT_CATALYST_TYPE = "type"
        val DATABASE_ELEMENT_CATALYST_WEIGHT = "weight"

        /* others */
        val REQUEST_CODE_READ_PHONE_STATE: Int = 0
        var IS_AVAILABLE_UPDATE : Boolean = false
        fun getIntFromString(input : String) : Int{
            var result = ("\\s+").toRegex().replace(input, "")
            result = result.replace(',', '.')
            result = if(result.indexOf(",") != -1) result.substring(0, result.indexOf(",")) else result
            var resultInt = 0
            try{
                resultInt = result.toInt()
            }catch(e:Exception){
                //noting
            }
            resultInt = if(resultInt < 0) 0 else resultInt
            return resultInt
        }
        fun getIntFromEnumBoolean(input : String) : Int{
            val result = ("[^a-zA-Z]").toRegex().replace(input, "")
            if(result.equals("tak")) return 1
            return 0
        }


        /* info */
        //color
        val INFO_MESSAGE_COLOR_FAILED:  Int = Color.RED
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.GRAY
        //message
        val INFO_MESSAGE_USER_NEVER_LOGGED : String = "Wprowadź nazwę użytkownika"
        val INFO_MESSAGE_WAIT_AUTHENTICATE : String = "Trwa uwierzytelnianie..."
        val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"
        val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        val INFO_MESSAGE_NETWORK_FAILED : String = "Brak połączenia"
        val INFO_MESSAGE_UNHANDLED_EXCEPTION : String = "Wystąpił błąd"
        val INFO_MESSAGE_WAIT_UPDATE : String = "Trwa aktualizacja...."
        val INFO_UPDATE_SUCCESS : String = "Aktualizacja przebiegła pomyślnie"
        val INFO_UPDATE_FAILED : String = "Wystąpił błąd podczas aktualizacji"
        val INFO_DOWNLOAD_BITMAP_WAIT: String = "Trwa pobieranie obrazu..."
        val INFO_DOWNLOAD_BITMAP_FAILED : String = "Wystąpił błąd podczas pobierania obrazu"
        val INFO_DOWNLOAD_BITMAP_STATUS : String = "Status pobieranych miniatur"
        val INFO_DOWNLOAD_BITMAP_SUCCESS : String = "Baza danych jest aktualna"
        val INFO_EMPTY_DATABASE : String = "Baza danych jest pusta"
        val INFO_DATABASE_EXPIRE : String = "Baza danych nie jest aktualna"

        /* methods */
        //decorator for delete others signs
        fun decoratorIdentificatorOfUser(applicationContext: Context): String{
            var identificator : String = this.getIdentificatorOfUser(applicationContext)
            identificator = ("[^A-Za+-z0-9]+").toRegex().replace(identificator, "")
            //return if is not empty
            if(identificator.isEmpty() == false) return identificator
            throw Exception()
        }
        //get serial if of phone if is empty then return phone number
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getIdentificatorOfUser(applicationContext: Context): String{
            //serial id section
            var serialId = ""
            try{
                serialId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
            }catch(e: Exception){
                //nothing
            }
            //return if serial id is not empty
            if(serialId.isEmpty() == false) return serialId
            //phone number section
            try{
                serialId = (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number.toString()
            }catch(e: Exception){
                //nothing
            }
            //return if phone number is not empty
            if(serialId.isEmpty() == false) return serialId
            //phone number section
            try{
                serialId = (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
            }catch(e: Exception){
                //nothing
            }
            //return if sim id is not empty
            if(serialId.isEmpty() == false) return serialId
            //android id for android > 10
            try{
                serialId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            }catch(e: Exception){
                //nothing
            }
            //return if is not empty
            if(serialId.isEmpty() == false) return serialId
            throw Exception()
        }
        //get date in format dd.mm.yyyy
        @SuppressLint("SimpleDateFormat")
        fun formatDate(date: String) : String{
            if(date.isEmpty()) return ""
            return SimpleDateFormat("dd.MM.yyyy").format((SimpleDateFormat("yyyy-MM-dd").parse(date)!!)).toString()
        }
        //get format of float from string
        fun formatStringFloat(floatString: String, precision: Int) : String{
            if(floatString.isEmpty()) return (String.format("%." + precision + "f", (0.00).toFloat()))
            return String.format("%." + precision + "f", floatString.toFloat())
        }
        //get pln from dolar string
        fun getPlnFromDolar(dolar: String) : String{
            if(dolar.isEmpty()) return (0.00).toString()
            val dolarFromConfiguration : String = MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_USD_PLN)
            return (dolar.toFloat() * (if(dolarFromConfiguration.isEmpty()) (0.0F) else dolarFromConfiguration.toFloat())).toString()
        }
        //get url to google
        fun getPictureUrlFromGoogle(urlShared: String, width: Int, height: Int) : String {
            var url = urlShared
            val resultRegex = ".*/d/".toRegex().find(url)!!.value
            url = url.substring(resultRegex.length)
            val pictureIdFromGoogle = url.substring(0,url.indexOf('/'))
            return "https://lh3.googleusercontent.com/u/0/d/$pictureIdFromGoogle=w$width-h$height"
        }
    }
}
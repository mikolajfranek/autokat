package pl.autokat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*

class MyConfiguration {
    companion object {
        val PRODUCTION : Boolean = false

        /* creating access token */
        val GOOGLE_TOKEN_URL : String = "https://oauth2.googleapis.com/token"
        val GOOGLE_SCOPE_NAME : String = "scope"
        val GOOGLE_SCOPE_VALUE : String = "https://www.googleapis.com/auth/spreadsheets"
        val ONE_HOUR : Long = 3600 * 1000L
        //shared preferences
        val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN : String = "AccessToken"
        val MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP : String = "AccessTokenTimestamp"
        //generate new access token
        fun generateNewAccessToken(){
            val privateKey: RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(Base64.decode(MySecret.getPrivateKey(), Base64.DEFAULT))) as RSAPrivateKey
            val timestamp = Date().time
            MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP, timestamp.toString())
            val signedJwt = JWT.create()
                .withIssuer(MySecret.getEmail())
                .withAudience(this.GOOGLE_TOKEN_URL)
                .withClaim(this.GOOGLE_SCOPE_NAME, this.GOOGLE_SCOPE_VALUE)
                .withIssuedAt(Date(timestamp))
                .withExpiresAt(Date(timestamp + this.ONE_HOUR))
                .sign(Algorithm.RSA256(null, privateKey))
            val bodyJson = """{"grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer","assertion" : "$signedJwt"}"""
            val (request, response, result) = Fuel.post(this.GOOGLE_TOKEN_URL).body(bodyJson).responseString()
            if(response.statusCode != 200) throw Exception()
            val accessToken = JSONObject(result.get()).getString("access_token")
            MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN, accessToken)
        }
        //get access token
        fun getAccessToken() : String{
            val accessTokenTimestamp : String = MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN_TIMESTAMP)
            val generateNewAccessToken : Boolean = (Date().time - (if (accessTokenTimestamp.isEmpty()) (0).toLong() else accessTokenTimestamp.toLong())) > this.ONE_HOUR
            if(generateNewAccessToken){
                this.generateNewAccessToken()
            }
            return MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_ACCESS_TOKEN)
        }

        /* licence and time */
        val URL_TIMESTAMP : String = "https://worldtimeapi.org/api/timezone/Europe/Warsaw"
        val ONE_DAY_IN_MILLISECONDS : Long = 86400000
        //shared preferences
        val MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP : String = "CurrentTimestamp"
        //check time depends from parameter
        fun checkTimeOnPhone(dateInput : String, timeChecking : MyTimeChecking) : Boolean{
            when(timeChecking){
                MyTimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET -> {
                    //time from web minus 1 hour must by greater than time now
                    val json : JSONObject = JSONObject(URL(this.URL_TIMESTAMP).readText())
                    val timestampWeb : Long = (json.getLong("unixtime") * 1000L) - this.ONE_HOUR
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
                    val timestampLicence : Long = (SimpleDateFormat("yyyy-MM-dd").parse(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END))!!.time) + ONE_DAY_IN_MILLISECONDS
                    val timestampFromConfiguration : Long =  MySharedPreferences.getKeyFromFile(this.MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP).toLong()

                    if((timestamp > timestampFromConfiguration) && (timestampLicence > timestamp)){
                        MySharedPreferences.setKeyToFile(this.MY_SHARED_PREFERENCES_KEY_CURRENT_TIMESTAMP, timestamp.toString())
                        return true
                    }
                    return false
                }
            }
        }
























        /* database */
        val DATABASE_VERSION : Int = 1
        val DATABASE_NAME_OF_FILE : String = "autokat.db"
        val DATABASE_PAGINATE_LIMIT : Int = 5


        /* table catalyst */
        val DATABASE_TABLE_CATALYST = "catalyst"
        val DATABASE_TABLE_SQLITE_SEQUENCE = "sqlite_sequence"
        val DATABASE_ELEMENT_CATALYST_ID = "id"
        val DATABASE_ELEMENT_CATALYST_ID_PICTURE = "id_picture"
        val DATABASE_ELEMENT_CATALYST_URL_PICTURE = "url_picture"
        val DATABASE_ELEMENT_CATALYST_PICTURE = "picture"
        val DATABASE_ELEMENT_CATALYST_NAME = "name"
        val DATABASE_ELEMENT_CATALYST_BRAND = "brand"
        val DATABASE_ELEMENT_CATALYST_PLATINUM = "platinum"
        val DATABASE_ELEMENT_CATALYST_PALLADIUM = "palladium"
        val DATABASE_ELEMENT_CATALYST_RHODIUM = "rhodium"
        val DATABASE_ELEMENT_CATALYST_TYPE = "type"
        val DATABASE_ELEMENT_CATALYST_WEIGHT = "weight"

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
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM : String = "Palladium"
        val MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE : String = "PalladiumDate"
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM : String = "Platinum"
        val MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE : String = "PlatinumDate"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM : String = "Rhodium"
        val MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE : String = "RhodiumDate"
        //about courses exchanges
        val MY_SHARED_PREFERENCES_KEY_USD_PLN : String = "UsdPln"
        val MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE : String = "UsdPlnDate"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN : String = "EurPln"
        val MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE : String = "EurPlnDate"


        /* info */
        //color
        val INFO_MESSAGE_COLOR_FAILED:  Int = Color.RED
        val INFO_MESSAGE_COLOR_SUCCESS: Int = Color.GRAY
        //message
        val INFO_MESSAGE_WAIT_AUTHENTICATE : String = "Trwa uwierzytelnianie..."
        val INFO_MESSAGE_WAIT_UPDATE : String = "Trwa aktualizacja...."
        val INFO_UPDATE_SUCCESS : String = "Aktualizacja przebiegła pomyślnie"
        val INFO_UPDATE_FAILED : String = "Wystąpił błąd podczas aktualizacji"
        val INFO_DOWNLOAD_BITMAP_FAILED : String = "Wystąpił błąd podczas pobierania obrazu"
        val INFO_DOWNLOAD_BITMAP_WAIT: String = "Trwa pobieranie obrazu..."

        val INFO_MESSAGE_UNHANDLED_EXCEPTION : String = "Wystąpił nieobsłużony błąd"
        val INFO_MESSAGE_USER_NEVER_LOGGED : String = "Wprowadź nazwę użytkownika"
        val INFO_MESSAGE_USER_FAILED_LOGIN: String = "Błędna nazwa użytkownika"
        val INFO_MESSAGE_USER_FAILED_SERIAL: String = "Błędne urządzenie"
        val INFO_MESSAGE_USER_FAILED_LICENCE: String = "Brak licencji"









        /* spreadsheet */
        val MY_SPREADSHEET_URL_PREFIX : String = "https://sheets.googleapis.com/v4/spreadsheets/"

        /* spreadsheet login */
        val MY_SPREADSHEET_URL_SUFIX_LOGIN : String = "/values/Arkusz1!A2:F"
        val MY_SPREADSHEET_URL_LOGIN : String = this.MY_SPREADSHEET_URL_PREFIX + MySecret.getSpreadsheetIdLogin() + this.MY_SPREADSHEET_URL_SUFIX_LOGIN
        val MY_SPREADSHEET_VALUE_INPUT_OPTION_NAME: String = "valueInputOption"
        val MY_SPREADSHEET_VALUE_INPUT_OPTION_VALUE : String = "USER_ENTERED"
        //column number
        val MY_SPREADSHEET_LOGIN_ID : Int = 0
        val MY_SPREADSHEET_LOGIN_USERNAME : Int = 1
        val MY_SPREADSHEET_LOGIN_UUID : Int = 2
        val MY_SPREADSHEET_LOGIN_LICENCE : Int = 3
        val MY_SPREADSHEET_LOGIN_DISCOUNT : Int = 4
        val MY_SPREADSHEET_LOGIN_VISIBILITY : Int = 5
        //column letter
        val MY_SPREADSHEET_USERS_COLUMN_ID : String = "A"
        val MY_SPREADSHEET_USERS_COLUMN_USERNAME : String = "B"
        val MY_SPREADSHEET_USERS_COLUMN_UUID : String = "C"
        val MY_SPREADSHEET_USERS_COLUMN_LICENCE : String = "D"
        val MY_SPREADSHEET_USERS_COLUMN_DISCOUNT : String = "E"
        val MY_SPREADSHEET_USERS_COLUMN_VISIBILITY : String = "F"
        @SuppressLint("MissingPermission")
        fun getSerialId() : String{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
        }

        /* spreadsheet catalyst */
        val MY_SPREADSHEET_URL_SUFIX_CATALYST : String = "/values/Arkusz1!A2:J"
        val MY_SPREADSHEET_URL_CATALYST : String = this.MY_SPREADSHEET_URL_PREFIX + MySecret.getSpreadsheetIdCatalyst() + this.MY_SPREADSHEET_URL_SUFIX_CATALYST
        //column number
        val MY_SPREADSHEET_CATALYST_ID : Int = 0
        val MY_SPREADSHEET_CATALYST_NAME : Int = 1
        val MY_SPREADSHEET_CATALYST_BRAND : Int = 2
        val MY_SPREADSHEET_CATALYST_PLATINUM : Int = 3
        val MY_SPREADSHEET_CATALYST_PALLADIUM : Int = 4
        val MY_SPREADSHEET_CATALYST_RHODIUM : Int = 5
        val MY_SPREADSHEET_CATALYST_TYPE : Int = 6
        val MY_SPREADSHEET_CATALYST_WEIGHT : Int = 7
        val MY_SPREADSHEET_CATALYST_ID_PICTURE : Int = 8
        val MY_SPREADSHEET_CATALYST_URL_PICTURE : Int = 9
        //column letter
        val MY_SPREADSHEET_CATALYST_COLUMN_ID : String = "A"
        val MY_SPREADSHEET_CATALYST_COLUMN_NAME : String = "B"
        val MY_SPREADSHEET_CATALYST_COLUMN_BRAND : String = "C"
        val MY_SPREADSHEET_CATALYST_COLUMN_PLATTINUM : String = "D"
        val MY_SPREADSHEET_CATALYST_COLUMN_PALLADIUM : String = "E"
        val MY_SPREADSHEET_CATALYST_COLUMN_RHODIUM : String = "F"
        val MY_SPREADSHEET_CATALYST_COLUMN_TYPE : String = "G"
        val MY_SPREADSHEET_CATALYST_COLUMN_WEIGHT : String = "H"
        val MY_SPREADSHEET_CATALYST_COLUMN_ID_PICTURE : String = "I"
        val MY_SPREADSHEET_CATALYST_COLUMN_URL_PICTURE : String = "J"

























        /* courses exchange */
        val MY_CATALYST_VALUES_URL_USD_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json"
        val MY_CATALYST_VALUES_URL_EUR_PLN = "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json"
        val MY_CATALYST_VALUES_URL_CATALYST_PLATINUM = "https://proxy.kitco.com/getPM?symbol=PT&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_PALLADIUM = "https://proxy.kitco.com/getPM?symbol=PD&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_URL_CATALYST_RHODIUM  = "https://proxy.kitco.com/getPM?symbol=RH&unit=gram&currency=USD"
        val MY_CATALYST_VALUES_HEADER_ORIGIN = "https://www.kitco.com"

        /* others */
        val REQUEST_CODE_READ_PHONE_STATE: Int = 0





        //get date in format dd.mm.yyyy
        @SuppressLint("SimpleDateFormat")
        fun formatDate(date: String) : String{
            if(date.isEmpty()) return ""
            return SimpleDateFormat("dd.MM.yyyy").format((SimpleDateFormat("yyyy-MM-dd").parse(date)!!)).toString()
        }

        //get format of float from string
        fun formatStringFloat(floatString: String, precision: Int) : String{
            if(floatString.isEmpty()) return (0.0F).toString()
            return String.format("%." + precision + "f", floatString.toFloat())
        }

        //get pln from dolar string
        fun getPlnFromDolar(dolar: String) : String{
            if(dolar.isEmpty()) return (0.0F).toString()
            return (dolar.toFloat() * MySharedPreferences.getKeyFromFile(MY_SHARED_PREFERENCES_KEY_USD_PLN).toFloat()).toString()
        }







        //get url to google
        fun getPictureUrlFromGoogle(urlShared: String, width: Int, height: Int) : String {
            var url = urlShared

            val regexBefore = ".*\\/d\\/".toRegex()
            val resultRegex = regexBefore.find(url)!!.value

            url = url.substring(resultRegex.length)
            val pictureIdFromGoogle = url.substring(0,url.indexOf('/'))

            return "https://lh3.googleusercontent.com/u/0/d/$pictureIdFromGoogle=w$width-h$height"
        }


    }
}
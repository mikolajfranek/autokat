package pl.autokat

import android.annotation.SuppressLint
import android.os.Build
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MySpreadsheet {
    companion object {

        //parse string to json
        fun parseResultToJson(text: String): JSONObject {
            val regex = "\\{.*\\}".toRegex()
            val resultRegex = regex.find(text)?.value
            return JSONObject(resultRegex)
        }

        //retrieve element from login
        fun getUrlToSpreadsheetLogin(login: String) : String {
            return MyConfiguration.MY_SPREADSHEET_URL_PREFIX + MySecret.MY_SPREADSHEET_ID_LOGINS + MyConfiguration.MY_SPREADSHEET_URL_SUFIX +
                    "?" + MyConfiguration.MY_SPREADSHEET_QUERY_OUTPUT_JSON + "=" + MyConfiguration.MY_SPREADSHEET_OUTPUT_JSON +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_KEY + "=" + MySecret.MY_SPREADSHEET_KEY +
                    "&" + MyConfiguration.MY_SPREADSHEET_QUERY_WHERE_CLAUSE + "=" + "select%20*%20where%20A%3D'$login'"
        }

        //checking if licence end
        @SuppressLint("SimpleDateFormat")
        fun checkIfLicenceEnd(licenceDateInput: String) : Boolean{
            val licenceDate : Long = SimpleDateFormat("yyyy-MM-dd").parse(licenceDateInput)!!.time
            val todayDate : Long =  Date().time

            if(todayDate > licenceDate) return true
            return false
        }

        @SuppressLint("MissingPermission", "HardwareIds")
        fun authenticate(login: String, licenceDate: String) : MyProcessStep{

            //check licence if is not empty
            if(licenceDate.isNullOrEmpty() == false){
                if(this.checkIfLicenceEnd(licenceDate)){
                    //save licence date as empty
                    MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE, "")
                    MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_END, "1")
                    return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                }else{
                    return MyProcessStep.SUCCESS
                }
            }

            //retrieve and parse to json data from spreadsheet
            val resultFromUrl = URL(this.getUrlToSpreadsheetLogin(login)).readText()
            val resultJson = this.parseResultToJson(resultFromUrl)

            //check if exists login
            val rows = resultJson.getJSONObject("table").getJSONArray("rows")
            if(rows.length() != 1) return MyProcessStep.USER_FAILED_LOGIN

            //get row element
            val element = rows.getJSONObject(0).getJSONArray("c")

            //read serial id from phone
            val serialId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }

            //check serial id of element
            val elementSerialId : String = element.getJSONObject(1).getString("v")
            if(elementSerialId.isNullOrEmpty()){
                //save serial id to spreadsheet
                //save flag that save successful
            }else{
                //check current serial id with element serial id
                if(serialId.equals(elementSerialId) == false) return MyProcessStep.USER_FAILED_SERIAL
            }

            //save licence date
            val elementLicenceDate = element.getJSONObject(2).getString("v")
            if(this.checkIfLicenceEnd(elementLicenceDate)) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE, elementLicenceDate)
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_END, "0")

            //save discount
            val discount = element.getJSONObject(3).getString("v")
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, discount)

            //save login
            MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN, login)

            return MyProcessStep.SUCCESS
        }

    }
}
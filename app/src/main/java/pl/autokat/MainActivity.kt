package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), MyConfiguration.REQUEST_CODE_READ_PHONE_STATE)
        }else{
            //try login
            this.tryLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN), false)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MyConfiguration.REQUEST_CODE_READ_PHONE_STATE -> {
                // permission was granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //try login
                    this.tryLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN), false)
                } else {
                    finish()
                }
                return
            }

            else -> {
                finish()
                return
            }
        }
    }


    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //open about activity
    fun openAboutActivity(){
        val intent = Intent(applicationContext, AboutActivity::class.java)
        startActivity(intent)
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        return when(id){
            R.id.toolbar_list_about -> {
                this.openAboutActivity()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }

    //open result activity
    fun openResultActivity(){
        val intent = Intent(applicationContext, ResultActivity::class.java)
        startActivity(intent)
        finish()
    }

    //click button
    fun activityMainButtonOnClick(view: View?) {
        this.tryLogin(activity_main_edittext.text.toString(), true)
    }

    //process login
    fun tryLogin(login: String, hasClickedButton: Boolean){
        //make async task and execute
        val task = TryLogin(login, hasClickedButton)
        task.execute()
    }

    //async class which make all job - when job is finished then go to next activity in success or set view application and unblock user interface
    @SuppressLint("StaticFieldLeak")
    private inner class TryLogin(loginInput: String, hasClickedButtonInput : Boolean) : AsyncTask<Void, Void, MyProcessStep>() {

        //field
        private var login : String = loginInput
        private var hasClickedButton : Boolean = hasClickedButtonInput
        private var licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()

            //disable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, false)

            //set edit text (user not click on button, trying auto login)
            if(hasClickedButton == false){
                this@MainActivity.activity_main_edittext.setText(this.login)
            }

            //set info message
            this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_WAIT_AUTHENTICATE)
        }

        //do in async mode - in here can't modify user interface
        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): MyProcessStep {

            try{
                //user never logged (not click on button, trying auto login)
                if(hasClickedButton == false && login.isEmpty()) {
                    return MyProcessStep.USER_NEVER_LOGGED
                }

                //check licence
                if(licenceDateOfEnd.isEmpty() == false){
                    //check if licence if end
                    if(MyConfiguration.checkIfCurrentDateIsGreater(licenceDateOfEnd, true) == true){
                        return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                    }
                    //check if user has good time on phone
                    if(MyConfiguration.checkTimestamp() == false){
                        return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                    }
                    return MyProcessStep.SUCCESS
                }

                /* download courses */
                val dateEffective = MyCatalystValues.getValues()
                //check if date of phone is greater than date from request
                if(MyConfiguration.checkIfCurrentDateIsGreater(dateEffective, false) == false){
                    return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                }

                /* authentication */
                //retrieve and parse to json data from spreadsheet
                val resultFromUrl = URL(MySpreadsheet.getUrlToSpreadsheetLogin(login)).readText()
                val resultJson = MyConfiguration.parseResultToJson(resultFromUrl)
                //check if exists login
                val rows = resultJson.getJSONObject("table").getJSONArray("rows")
                if(rows.length() != 1) {
                    return MyProcessStep.USER_FAILED_LOGIN
                }
                //get row element
                val element = rows.getJSONObject(0).getJSONArray("c")
                //read serial id from phone

                val serialId =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
                //check serial id of element
                val elementSerialId : String = element.getJSONObject(1).getString("v")
                if(elementSerialId.isEmpty()){
                    //save serial id to spreadsheet                 //save flag that save was successful
                }else{
                    //check current serial id with element serial id
                    if(serialId.equals(elementSerialId) == false) {
                        return MyProcessStep.USER_FAILED_SERIAL
                    }
                }
                //check date of licence
                val elementLicenceDate = element.getJSONObject(2).getString("v")
                if(MyConfiguration.checkIfCurrentDateIsGreater(elementLicenceDate, true) == true){
                    return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                }
                //save licence date of end
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, elementLicenceDate)
                //save discount
                val discount = element.getJSONObject(3).getString("v")
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, discount)
                //save login
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN, login)

                /* success */
                return MyProcessStep.SUCCESS

            }catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
        }

        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)

            //do job depends on situation
            when(result){
                MyProcessStep.SUCCESS -> {
                    this@MainActivity.openResultActivity()
                }

                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION
                }

                MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_LICENCE
                    MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, "")
                }

                MyProcessStep.USER_NEVER_LOGGED -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_NEVER_LOGGED
                }

                MyProcessStep.USER_FAILED_LOGIN -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_LOGIN
                }

                MyProcessStep.USER_FAILED_SERIAL -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_SERIAL
                }
            }

            //enable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, true)
        }
    }
}
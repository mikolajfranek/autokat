package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.net.UnknownHostException

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
    }
    //onresume
    override fun onResume() {
        super.onResume()
        //check permission about phone state (required for getting serial id)
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //ask about permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), MyConfiguration.REQUEST_CODE_READ_PHONE_STATE)
        }else{
            //has permission and try login
            this.tryLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN), false)
        }
    }
    //on request permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MyConfiguration.REQUEST_CODE_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
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
    private fun openAboutActivity(){
        startActivity(Intent(applicationContext, AboutActivity::class.java))
    }
    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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
        startActivity(Intent(applicationContext, ResultActivity::class.java))
        finish()
    }
    //click button
    fun activityMainButtonOnClick() {
        this.tryLogin(activity_main_edittext.text.toString(), true)
    }
    //process login
    fun tryLogin(login: String, hasClickedButton: Boolean){
        //make async task and execute
        val task = TryLogin(login, hasClickedButton)
        task.execute()
    }
    //async class which make all job - when job is finished then go to next activity in success otherwise set view application with message and unblock user interface
    @SuppressLint("StaticFieldLeak")
    private inner class TryLogin(loginInput: String, hasClickedButtonInput : Boolean) : AsyncTask<Void, Void, MyProcessStep>() {
        //field
        private var login : String = loginInput.trim()
        private var hasClickedButton : Boolean = hasClickedButtonInput
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            //disable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, false)
            //set edit text (user not click on button, trying auto login)
            if(hasClickedButton == false) this@MainActivity.activity_main_edittext.setText(this.login)
            //set info message
            this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_WAIT_AUTHENTICATE)
        }
        //do in async mode - in here can't modify user interface
        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //user never logged (not click on button, trying auto login)
                if(login.isEmpty()) return MyProcessStep.USER_NEVER_LOGGED
                //check licence without connection to internet
                if(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END).isEmpty() == false){
                    /* checking time */
                    if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                    return MyProcessStep.SUCCESS
                }
                /* checking time */
                if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                /* authentication */
                //get user from database
                val user : JSONArray = MySpreadsheet.getDataLogin(login) ?: return MyProcessStep.USER_FAILED_LOGIN
                /* checking time */
                if(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE).isEmpty() || MyConfiguration.checkTimeOnPhone(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE), MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                //read serial id from phone
                val serialId : String = MyConfiguration.decoratorIdentificatorOfUser(applicationContext)
                //check if serial id is correct or save serial id to database
                if(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_UUID).isEmpty()){
                    //save serial id
                    MySpreadsheet.saveSerialId(user.getInt(MyConfiguration.MY_SPREADSHEET_USERS_ID), serialId)
                }else{
                    //check if current serial id is the same as in database
                    if(serialId.equals(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_UUID)) == false) return MyProcessStep.USER_FAILED_SERIAL
                }
                /* save configuration */
                //save licence date
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE))
                //save discount
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, MyConfiguration.getIntFromString(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT)).toString())
                //save visibility
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY, MyConfiguration.getIntFromEnumBoolean(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY)).toString())
                //save minus elements
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PLATINIUM, MyConfiguration.getIntFromString(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PLATINIUM)).toString())
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PALLADIUM, MyConfiguration.getIntFromString(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PALLADIUM)).toString())
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_RHODIUM, MyConfiguration.getIntFromString(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_MINUS_RHODIUM)).toString())
                //save login
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN, login)
                //success
                return MyProcessStep.SUCCESS
            }
            catch(e: UnknownHostException){
                return MyProcessStep.NETWORK_FAILED
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //do job depends on situation
            when(result){
                MyProcessStep.USER_NEVER_LOGGED -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_NEVER_LOGGED
                }
                MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_LICENCE
                    /* set licence as empty */
                    MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, "")
                }
                MyProcessStep.USER_FAILED_LOGIN -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_LOGIN
                }
                MyProcessStep.USER_FAILED_SERIAL -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_USER_FAILED_SERIAL
                }
                MyProcessStep.NETWORK_FAILED -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_NETWORK_FAILED
                }
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.text = MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION
                }
                MyProcessStep.SUCCESS -> {
                    this@MainActivity.openResultActivity()
                }
            }
            //enable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, true)
        }
    }
}
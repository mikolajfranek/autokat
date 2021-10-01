package pl.autokat

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityMainBinding
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var bindingActivityMain: ActivityMainBinding

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityMain = ActivityMainBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityMain.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityMain.toolbar)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //listeners
        bindingActivityMain.loginButton.setOnClickListener {
            this.tryLogin(this.bindingActivityMain.editText.text.toString(), true)
        }
    }

    //onresume
    override fun onResume() {
        super.onResume()
        //check permission about phone state (required for getting serial id)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //ask about permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                MyConfiguration.REQUEST_CODE_READ_PHONE_STATE
            )
        } else {
            //has permission and try login
            this.tryLogin(
                MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN),
                false
            )
        }
    }

    //on request permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MyConfiguration.REQUEST_CODE_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    this.tryLogin(
                        MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN),
                        false
                    )
                } else {
                    this.finish()
                }
                return
            }
            else -> {
                this.finish()
                return
            }
        }
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.toolbar_list_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //open about activity
    private fun openAboutActivity() {
        this.startActivity(Intent(this.applicationContext, AboutActivity::class.java))
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_about -> {
                this.openAboutActivity()
                true
            }
            else -> {
                this.finish()
                true
            }
        }
    }

    //open result activity
    fun openResultActivity() {
        this.startActivity(Intent(this.applicationContext, ResultActivity::class.java))
        this.finish()
    }

    //process login
    private fun tryLogin(login: String, hasClickedButton: Boolean) {
        //make async task and execute
        Thread(this.TaskTryLogin(login, hasClickedButton)).start()
    }

    //async class which make all job - when job is finished then go to next activity in success otherwise set view application with message and unblock user interface
    inner class TaskTryLogin(loginInput: String, hasClickedButtonInput: Boolean) : Runnable {
        //fields
        private var login: String = loginInput.trim()
        private var hasClickedButton: Boolean = hasClickedButtonInput

        //run
        @Suppress("ReplaceCallWithBinaryOperator")
        override fun run() {
            //--- onPreExecute
            this@MainActivity.runOnUiThread {
                //disable user interface on process application
                MyUserInterface.enableActivity(
                    this@MainActivity.bindingActivityMain.linearLayout,
                    false
                )
                //set edit text (user not click on button, trying auto login)
                if (this.hasClickedButton == false) {
                    this@MainActivity.bindingActivityMain.editText.setText(this.login)
                }
                //set info message
                this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                this@MainActivity.bindingActivityMain.textView.text =
                    MyConfiguration.INFO_MESSAGE_WAIT_AUTHENTICATE
            }
            //--- doInBackground
            var myProcessStep: MyProcessStep = MyProcessStep.NONE
            try {
                //user never logged (not click on button, trying auto login)
                if (this.login.isEmpty()) {
                    myProcessStep = MyProcessStep.USER_NEVER_LOGGED
                } else {
                    //check licence without connection to internet
                    if (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
                            .isEmpty() == false
                    ) {
                        /* checking time */
                        myProcessStep = if (MyConfiguration.checkTimeOnPhone(
                                "",
                                MyTimeChecking.CHECKING_LICENCE
                            ) == false
                        ) {
                            MyProcessStep.USER_ELAPSED_DATE_LICENCE
                        } else {
                            MyProcessStep.SUCCESS
                        }
                    } else {
                        /* checking time */
                        if (MyConfiguration.checkTimeOnPhone(
                                "",
                                MyTimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET
                            ) == false
                        ) {
                            myProcessStep = MyProcessStep.USER_ELAPSED_DATE_LICENCE
                        } else {
                            /* authentication */
                            //get user from database
                            val user: JSONArray? = MySpreadsheet.getDataLogin(this.login)
                            if (user == null) {
                                myProcessStep = MyProcessStep.USER_FAILED_LOGIN
                            } else {
                                /* checking time */
                                if (user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE)
                                        .isEmpty()
                                    || MyConfiguration.checkTimeOnPhone(
                                        user.getString(
                                            MyConfiguration.MY_SPREADSHEET_USERS_LICENCE
                                        ), MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                                    ) == false
                                ) {
                                    myProcessStep = MyProcessStep.USER_ELAPSED_DATE_LICENCE
                                } else {
                                    //read serial id from phone
                                    val serialId: String =
                                        MyConfiguration.decoratorIdentificatorOfUser(this@MainActivity.applicationContext)
                                    //check if serial id is correct or save serial id to database
                                    if (user.getString(MyConfiguration.MY_SPREADSHEET_USERS_UUID)
                                            .isEmpty()
                                    ) {
                                        //save serial id
                                        MySpreadsheet.saveSerialId(
                                            user.getInt(MyConfiguration.MY_SPREADSHEET_USERS_ID),
                                            serialId
                                        )
                                    } else {
                                        //check if current serial id is the same as in database
                                        if (serialId.equals(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_UUID)) == false) {
                                            myProcessStep = MyProcessStep.USER_FAILED_SERIAL
                                        }
                                    }
                                    if (myProcessStep == MyProcessStep.NONE) {
                                        /* save configuration */
                                        //save licence date
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END,
                                            user.getString(
                                                MyConfiguration.MY_SPREADSHEET_USERS_LICENCE
                                            )
                                        )
                                        //save discount
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT,
                                            MyConfiguration.getIntFromString(
                                                user.getString(
                                                    MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT
                                                )
                                            ).toString()
                                        )
                                        //save visibility
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY,
                                            MyConfiguration.getIntFromEnumBoolean(
                                                user.getString(
                                                    MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY
                                                )
                                            ).toString()
                                        )
                                        //save minus elements
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PLATINIUM,
                                            MyConfiguration.getIntFromString(
                                                user.getString(
                                                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PLATINIUM
                                                )
                                            ).toString()
                                        )
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PALLADIUM,
                                            MyConfiguration.getIntFromString(
                                                user.getString(
                                                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PALLADIUM
                                                )
                                            ).toString()
                                        )
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_RHODIUM,
                                            MyConfiguration.getIntFromString(
                                                user.getString(
                                                    MyConfiguration.MY_SPREADSHEET_USERS_MINUS_RHODIUM
                                                )
                                            ).toString()
                                        )
                                        //save login
                                        MySharedPreferences.setKeyToFile(
                                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN,
                                            this.login
                                        )
                                        //success
                                        myProcessStep = MyProcessStep.SUCCESS
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                myProcessStep = MyProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                myProcessStep = MyProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@MainActivity.runOnUiThread {
                //do job depends on situation
                when (myProcessStep) {
                    MyProcessStep.USER_NEVER_LOGGED -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_USER_NEVER_LOGGED
                    }
                    MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_USER_FAILED_LICENCE
                        /* set licence as empty */
                        MySharedPreferences.setKeyToFile(
                            MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END,
                            ""
                        )
                    }
                    MyProcessStep.USER_FAILED_LOGIN -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_USER_FAILED_LOGIN
                    }
                    MyProcessStep.USER_FAILED_SERIAL -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_USER_FAILED_SERIAL
                    }
                    MyProcessStep.NETWORK_FAILED -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_NETWORK_FAILED
                    }
                    MyProcessStep.UNHANDLED_EXCEPTION -> {
                        this@MainActivity.bindingActivityMain.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                        this@MainActivity.bindingActivityMain.textView.text =
                            MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION
                    }
                    MyProcessStep.SUCCESS -> {
                        this@MainActivity.openResultActivity()
                    }
                    else -> {
                        //nothing
                    }
                }
                //enable user interface on process application
                MyUserInterface.enableActivity(
                    this@MainActivity.bindingActivityMain.linearLayout,
                    true
                )
            }
        }
    }
}
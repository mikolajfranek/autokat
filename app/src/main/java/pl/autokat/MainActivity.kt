package pl.autokat

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

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

        //try login
        this.tryLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN), false)
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //open result activity
    fun openResultActivity(){
        val intent = Intent(applicationContext, ResultActivity::class.java)
        startActivity(intent)
        finish()
    }

    //click button
    fun activityMainButtonOnClick(view: View) {
        this.tryLogin(activity_main_edittext.text.toString(), true)
    }

    //process login
    fun tryLogin(login: String, hasClickedButton: Boolean){
        //make async task and execute
        val task = TryLogin(login, hasClickedButton)
        task.execute()
    }

    //async class which make all job - when job is finished then go to next activity in success or set view application and unblock user interface
    private inner class TryLogin(loginInput: String, hasClickedButtonInput : Boolean) : AsyncTask<Void, Int, MyProcessStep>() {

        //field
        private var login : String = loginInput
        private var hasClickedButton : Boolean = hasClickedButtonInput

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()

            //disable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, false)

            //set edit text (user not click on button, trying auto login)
            if(hasClickedButton == false){
                this@MainActivity.activity_main_edittext.setText(login)
            }

            //set info text
            this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_WAIT_AUTHENTICATE)
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //user never logged (user not click on button, trying auto login)
                if(hasClickedButton == false && login.isEmpty()) {
                    return MyProcessStep.USER_NEVER_LOGGED
                }

                //send request to spreadsheet and check login, licenceDate, serialId
                var status : MyProcessStep = MySpreadsheet.authenticate(login, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE))
                if(status != MyProcessStep.SUCCESS) return status

                //try get values of courses
                MyCatalystValues.tryGetValues()

                //success
                return MyProcessStep.SUCCESS

            }catch(e: Exception){

                //if licence end then show user information about it
                if(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_END).equals("1")){
                    return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                }

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
                    this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION)
                }

                MyProcessStep.USER_NEVER_LOGGED -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                    this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_USER_NEVER_LOGGED)
                }

                MyProcessStep.USER_FAILED_LOGIN -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_USER_FAILED_LOGIN)
                }

                MyProcessStep.USER_FAILED_SERIAL -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_USER_FAILED_SERIAL)
                }

                MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    this@MainActivity.activity_main_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@MainActivity.activity_main_textview.setText(MyConfiguration.INFO_MESSAGE_USER_FAILED_LICENCE)
                }
            }

            //enable user interface on process application
            MyUserInterface.enableActivity(this@MainActivity.activity_main_linearlayout, true)
        }
    }
}

package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.RSAKeyProvider
import com.github.kittinunf.fuel.Fuel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


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

    //on request permission
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

            val profile =
                JSONObject(applicationContext.assets.open("profile.json").bufferedReader().use { it.readText() })

            var privateKey = profile.getString("private_key")
            privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "");
            privateKey = privateKey.replace("-----END PRIVATE KEY-----", "");
            privateKey = privateKey.replace("\n", "");


            val pkcs8EncodedBytes: ByteArray = Base64.decode(privateKey, Base64.DEFAULT)

            val pKCS8EncodedKeySpec: PKCS8EncodedKeySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)

            val kf: KeyFactory = KeyFactory.getInstance("RSA")

            val privKey: RSAPrivateKey = kf.generatePrivate(pKCS8EncodedKeySpec) as RSAPrivateKey


            val now = System.currentTimeMillis()
            val algorithm: Algorithm = Algorithm.RSA256(null, privKey)

            val signedJwt = JWT.create()
                .withIssuer(profile.getString("client_email"))
                .withAudience(profile.getString("token_uri"))
                .withClaim("scope", "https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/spreadsheets")
                .withIssuedAt(Date(now))
                .withExpiresAt(Date(now + 3600 * 1000L))
                .sign(algorithm)


            val bodyJson = """
              { "grant_type" : "urn:ietf:params:oauth:grant-type:jwt-bearer",
                "assertion" : "$signedJwt"
              }
            """
            val url = "https://oauth2.googleapis.com/token"
            val (request, response, result) = Fuel.post(url).body(bodyJson).responseString()

            val access_token = JSONObject(result.get()).getString("access_token")

            print("")





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
                val rows = MySpreadsheet.getDataLogin(login)
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
                    //save serial id to spreadsheet
                    // save flag that save was successful
                }else{
                    //check current serial id with element serial id
                    if(serialId.equals(elementSerialId) == false) {
                        return MyProcessStep.USER_FAILED_SERIAL
                    }
                }


                //save data
                val elementLicenceDate = element.getJSONObject(2).getString("v")
                val discount = element.getJSONObject(3).getString("v")
                val visibility = element.getJSONObject(4).getString("v")

                //check date of licence
                if(MyConfiguration.checkIfCurrentDateIsGreater(elementLicenceDate, true) == true){
                    return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                }
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, elementLicenceDate)
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, discount)
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY, visibility)



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
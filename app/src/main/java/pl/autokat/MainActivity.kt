package pl.autokat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityMainBinding
import pl.autokat.enums.ProcessStep
import pl.autokat.enums.TimeChecking
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val requestCodeReadPhoneState: Int = 0

    //region methods of override
    private fun authenticate(login: String, hasClickedButton: Boolean) {
        Thread(RunnableAuthentication(login, hasClickedButton)).start()
    }

    private fun init() {
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = activityMainBinding.root
        setContentView(view)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
    }

    private fun setClickListeners() {
        activityMainBinding.loginButton.setOnClickListener {
            authenticate(activityMainBinding.editText.text.toString(), true)
        }
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                requestCodeReadPhoneState
            )
        } else {
            authenticate(
                SharedPreference.getKeyFromFile(SharedPreference.LOGIN),
                false
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodeReadPhoneState -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    authenticate(
                        SharedPreference.getKeyFromFile(SharedPreference.LOGIN),
                        false
                    )
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_about -> {
                openAboutActivity()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }
    //endregion

    //region open activities
    private fun openAboutActivity() {
        startActivity(Intent(applicationContext, AboutActivity::class.java))
    }

    fun openResultActivity() {
        startActivity(Intent(applicationContext, ResultActivity::class.java))
        finish()
    }
    //endregion

    //region id of user
    fun decoratorIdOfUser(): String {
        var id: String = getIdOfUser()
        id = ("[^A-Za+-z0-9]+").toRegex().replace(id, "")
        if (id.isEmpty() == false) return id
        throw Exception()
    }

    @SuppressLint("HardwareIds")
    private fun getIdForSdkGreaterOrEqualTo26(): String {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                @Suppress("DEPRECATION")
                Build.SERIAL
            }
        } catch (e: Exception) {
            //
        }
        return ""
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getIdPhoneNumber(): String {
        try {
            return (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number.toString()
        } catch (e: Exception) {
            //
        }
        return ""
    }

    @SuppressLint("HardwareIds")
    private fun getIdSimNumber(): String {
        try {
            return (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
        } catch (e: Exception) {
            //
        }
        return ""
    }

    @SuppressLint("HardwareIds")
    private fun getIdAndroidId(): String {
        try {
            return Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } catch (e: Exception) {
            //
        }
        return ""
    }

    private fun getIdOfUser(): String {
        var serialId = ""
        serialId = getIdForSdkGreaterOrEqualTo26()
        if (serialId.isEmpty() == false) return serialId
        serialId = getIdPhoneNumber()
        if (serialId.isEmpty() == false) return serialId
        serialId = getIdSimNumber()
        if (serialId.isEmpty() == false) return serialId
        serialId = getIdAndroidId()
        if (serialId.isEmpty() == false) return serialId
        throw Exception()
    }
    //endregion



    inner class RunnableAuthentication(loginInput: String, hasClickedButtonInput: Boolean) : Runnable {
        private var login: String = loginInput.trim()
        private var hasClickedButton: Boolean = hasClickedButtonInput

        @Suppress("ReplaceCallWithBinaryOperator")
        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityMainBinding.linearLayout,
                    false
                )
                if (hasClickedButton == false) {
                    activityMainBinding.editText.setText(login)
                }
                activityMainBinding.textView.setTextColor(Configuration.COLOR_SUCCESS)
                activityMainBinding.textView.text =
                    Configuration.USER_WAIT_AUTHENTICATING
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                if (login.isEmpty()) {
                    processStep = ProcessStep.USER_NEVER_LOGGED
                } else {
                    if (SharedPreference.getKeyFromFile(SharedPreference.LICENCE_DATE_OF_END)
                            .isEmpty() == false
                    ) {
                        processStep = if (Checker.checkTimeOnPhone(
                                "",
                                TimeChecking.CHECKING_LICENCE
                            ) == false
                        ) {
                            ProcessStep.USER_ELAPSED_DATE_LICENCE
                        } else {
                            ProcessStep.SUCCESS
                        }
                    } else {
                        if (Checker.checkTimeOnPhone(
                                "",
                                TimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET
                            ) == false
                        ) {
                            processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                        } else {
                            val user: JSONArray? = Spreadsheet.getDataLogin(login)
                            if (user == null) {
                                processStep = ProcessStep.USER_FAILED_LOGIN
                            } else {
                                if (user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
                                        .isEmpty()
                                    || Checker.checkTimeOnPhone(
                                        user.getString(
                                            Configuration.SPREADSHEET_USERS_LICENCE
                                        ), TimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                                    ) == false
                                ) {
                                    processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                                } else {
                                    val serialId: String =
                                        decoratorIdOfUser()
                                    if (user.getString(Configuration.SPREADSHEET_USERS_UUID)
                                            .isEmpty()
                                    ) {
                                        Spreadsheet.saveSerialId(
                                            user.getInt(Configuration.SPREADSHEET_USERS_ID),
                                            serialId
                                        )
                                    } else {
                                        if (serialId.equals(user.getString(Configuration.SPREADSHEET_USERS_UUID)) == false) {
                                            processStep = ProcessStep.USER_FAILED_SERIAL
                                        }
                                    }
                                    if (processStep == ProcessStep.NONE) {
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.LICENCE_DATE_OF_END,
                                            user.getString(
                                                Configuration.SPREADSHEET_USERS_LICENCE
                                            )
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.DISCOUNT,
                                            Parser.parseStringToInt(
                                                user.getString(
                                                    Configuration.SPREADSHEET_USERS_DISCOUNT
                                                )
                                            ).toString()
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.VISIBILITY,
                                            Parser.parseStringBooleanToInt(
                                                user.getString(
                                                    Configuration.SPREADSHEET_USERS_VISIBILITY
                                                )
                                            ).toString()
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.MINUS_PLATINUM,
                                            Parser.parseStringToInt(
                                                user.getString(
                                                    Configuration.SPREADSHEET_USERS_MINUS_PLATINUM
                                                )
                                            ).toString()
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.MINUS_PALLADIUM,
                                            Parser.parseStringToInt(
                                                user.getString(
                                                    Configuration.SPREADSHEET_USERS_MINUS_PALLADIUM
                                                )
                                            ).toString()
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.MINUS_RHODIUM,
                                            Parser.parseStringToInt(
                                                user.getString(
                                                    Configuration.SPREADSHEET_USERS_MINUS_RHODIUM
                                                )
                                            ).toString()
                                        )
                                        SharedPreference.setKeyToFile(
                                            SharedPreference.LOGIN,
                                            login
                                        )
                                        processStep = ProcessStep.SUCCESS
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                processStep = ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.USER_NEVER_LOGGED -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_SUCCESS)
                        activityMainBinding.textView.text =
                            Configuration.USER_NEVER_LOGGED
                    }
                    ProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_FAILED)
                        activityMainBinding.textView.text =
                            Configuration.USER_FAILED_LICENCE
                        SharedPreference.setKeyToFile(
                            SharedPreference.LICENCE_DATE_OF_END,
                            ""
                        )
                    }
                    ProcessStep.USER_FAILED_LOGIN -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_FAILED)
                        activityMainBinding.textView.text =
                            Configuration.USER_FAILED_LOGIN
                    }
                    ProcessStep.USER_FAILED_SERIAL -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_FAILED)
                        activityMainBinding.textView.text =
                            Configuration.USER_FAILED_UUID
                    }
                    ProcessStep.NETWORK_FAILED -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_FAILED)
                        activityMainBinding.textView.text =
                            Configuration.NETWORK_FAILED
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        activityMainBinding.textView.setTextColor(Configuration.COLOR_FAILED)
                        activityMainBinding.textView.text =
                            Configuration.UNHANDLED_EXCEPTION
                    }
                    ProcessStep.SUCCESS -> {
                        openResultActivity()
                    }
                    else -> {
                        //
                    }
                }
                UserInterface.changeStatusLayout(
                    activityMainBinding.linearLayout,
                    true
                )
            }
        }
    }
}
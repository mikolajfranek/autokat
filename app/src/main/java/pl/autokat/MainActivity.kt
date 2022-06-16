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
import androidx.core.view.get
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityMainBinding
import pl.autokat.enums.ProcessStep
import pl.autokat.enums.ProgramMode
import pl.autokat.enums.TimeChecking
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val requestCodeReadPhoneState: Int = 0

    //region methods used in override
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
        activityMainBinding.buttonLogin.setOnClickListener {
            authenticate(activityMainBinding.login.text.toString(), true)
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
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
                return
            }
        }
        authenticate(SharedPreference.getKey(SharedPreference.LOGIN), false)
    }

    private fun handleRequestCode(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            requestCodeReadPhoneState -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    authenticate(SharedPreference.getKey(SharedPreference.LOGIN), false)
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
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleRequestCode(requestCode, grantResults)
    }

    override fun onCreateOptionsMenu(menuInput: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menuInput)
        if (Configuration.PROGRAM_MODE == ProgramMode.CLIENT) {
            menuInput.get(menuInput.size() - 1).isVisible = false
        }
        return super.onCreateOptionsMenu(menuInput)
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

    //region inner classes
    inner class RunnableAuthentication(loginInput: String, hasClickedButtonInput: Boolean) :
        Runnable {

        private var login: String = loginInput.trim()
        private var hasClickedButton: Boolean = hasClickedButtonInput

        //region id of user
        private fun getSerialId(): String {
            var id: String = getId()
            id = ("[^A-Za-z0-9]+").toRegex().replace(id, "")
            if (id.isEmpty() == false) return id
            throw Exception()
        }

        private fun getId(): String {
            var id: String = getIdForSdkGreaterOrEqualTo26()
            if (id.isEmpty() == false) return id
            id = getIdPhoneNumber()
            if (id.isEmpty() == false) return id
            id = getIdSimNumber()
            if (id.isEmpty() == false) return id
            id = getIdAndroidId()
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
        //endregion

        //region methods used in doInBackground
        private fun checkLicence(): ProcessStep {
            if (Spreadsheet.isExpiredLicenceOfCompany(true) == true) {
                return ProcessStep.COMPANY_ELAPSED_LICENCE
            }
            if (SharedPreference.getKey(SharedPreference.LICENCE_DATE_OF_END).isEmpty() == false) {
                if (Checker.checkTimeOnPhone("", TimeChecking.CHECKING_LICENCE) == false) {
                    return ProcessStep.USER_ELAPSED_DATE_LICENCE
                }
                return ProcessStep.SUCCESS
            }
            return ProcessStep.NONE
        }

        private fun checkTimeOnPhoneWithTimeOnTheInternet(): ProcessStep {
            if (Checker.checkTimeOnPhone(
                    "",
                    TimeChecking.NOW_GREATER_THAN_TIME_FROM_INTERNET
                ) == false
            ) {
                ProcessStep.USER_ELAPSED_DATE_LICENCE
            }
            return ProcessStep.NONE
        }

        private fun checkTimeOfUserLicenceWithTimeOnPhone(user: JSONArray): ProcessStep {
            val licenceDate = user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
            if (licenceDate.isEmpty() || Checker.checkTimeOnPhone(
                    licenceDate,
                    TimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                ) == false
            ) {
                return ProcessStep.USER_ELAPSED_DATE_LICENCE
            }
            return ProcessStep.NONE
        }

        private fun checkSerialId(user: JSONArray): ProcessStep {
            val serialId: String = getSerialId()
            val uuid: String = user.getString(Configuration.SPREADSHEET_USERS_UUID)
            if (uuid.isEmpty()) {
                Spreadsheet.saveSerialId(user.getInt(Configuration.SPREADSHEET_USERS_ID), serialId)
            } else {
                if ((serialId == uuid) == false) {
                    return ProcessStep.USER_FAILED_SERIAL
                }
            }
            return ProcessStep.NONE
        }

        private fun setSharedPreferencesOfUser(user: JSONArray) {
            SharedPreference.setKey(
                SharedPreference.LICENCE_DATE_OF_END,
                user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
            )
            SharedPreference.setKey(
                SharedPreference.DISCOUNT,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_DISCOUNT))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.VISIBILITY,
                Parser.parseStringBooleanToInt(user.getString(Configuration.SPREADSHEET_USERS_VISIBILITY))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_PLATINUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_PLATINUM))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_PALLADIUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_PALLADIUM))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_RHODIUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_RHODIUM))
                    .toString()
            )
            SharedPreference.setKey(SharedPreference.LOGIN, login)
        }
        //endregion

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityMainBinding.linearLayout, false)
            if (hasClickedButton == false) {
                activityMainBinding.login.setText(login)
            }
            activityMainBinding.notification.setTextColor(Configuration.COLOR_SUCCESS)
            activityMainBinding.notification.text = Configuration.USER_WAIT_AUTHENTICATING
        }

        private fun doInBackground(): ProcessStep {
            var processStep: ProcessStep
            try {
                if (login.isEmpty()) return ProcessStep.USER_NEVER_LOGGED
                processStep = checkLicence()
                if (processStep != ProcessStep.NONE) return processStep
                processStep = checkTimeOnPhoneWithTimeOnTheInternet()
                if (processStep != ProcessStep.NONE) return processStep
                val user: JSONArray =
                    Spreadsheet.getDataLogin(login) ?: return ProcessStep.USER_FAILED_LOGIN
                processStep = checkTimeOfUserLicenceWithTimeOnPhone(user)
                if (processStep != ProcessStep.NONE) return processStep
                processStep = checkSerialId(user)
                if (processStep != ProcessStep.NONE) return processStep
                setSharedPreferencesOfUser(user)
                return ProcessStep.SUCCESS
            } catch (e: UnknownHostException) {
                return ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                return ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.USER_NEVER_LOGGED -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_SUCCESS)
                    activityMainBinding.notification.text = Configuration.USER_NEVER_LOGGED
                }
                ProcessStep.COMPANY_ELAPSED_LICENCE -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.COMPANY_FAILED_LICENCE
                }
                ProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.USER_FAILED_LICENCE
                    SharedPreference.setKey(SharedPreference.LICENCE_DATE_OF_END, "")
                }
                ProcessStep.USER_FAILED_LOGIN -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.USER_FAILED_LOGIN
                }
                ProcessStep.USER_FAILED_SERIAL -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.USER_FAILED_UUID
                }
                ProcessStep.NETWORK_FAILED -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.NETWORK_FAILED
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    activityMainBinding.notification.setTextColor(Configuration.COLOR_FAILED)
                    activityMainBinding.notification.text = Configuration.UNHANDLED_EXCEPTION
                }
                ProcessStep.SUCCESS -> {
                    openResultActivity()
                }
                else -> {
                    //
                }
            }
            UserInterface.changeStatusLayout(activityMainBinding.linearLayout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }
    //endregion
}
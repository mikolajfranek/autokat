package pl.autokat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.json.JSONArray
import pl.autokat.components.Checker
import pl.autokat.components.Configuration
import pl.autokat.components.Course
import pl.autokat.components.Parser
import pl.autokat.components.SharedPreference
import pl.autokat.components.Spreadsheet
import pl.autokat.components.UserInterface
import pl.autokat.databinding.ActivityBottomNavigationBinding
import pl.autokat.enums.ProcessStep
import pl.autokat.enums.ProgramMode
import pl.autokat.enums.ScrollRefresh
import pl.autokat.enums.TimeChecking
import pl.autokat.workers.WorkerCopyData
import pl.autokat.workers.WorkerDownloadThumbnail
import java.lang.Exception
import java.time.LocalDate
import java.util.Date

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var activityBottomNavigationBinding: ActivityBottomNavigationBinding

    fun badgeOn(r_id_bottom_menu: Int) {
        val badge =
            activityBottomNavigationBinding.bottomNavigation.getOrCreateBadge(r_id_bottom_menu)
        badge.isVisible = true
    }

    fun badgeOff(r_id_bottom_menu: Int) {
        val badge =
            activityBottomNavigationBinding.bottomNavigation.getOrCreateBadge(r_id_bottom_menu)
        badge.isVisible = false
    }

    fun layoutOn() {
        UserInterface.changeStatusLayout(activityBottomNavigationBinding.layout, true)
    }

    fun layoutOff() {
        UserInterface.changeStatusLayout(activityBottomNavigationBinding.layout, false)
    }

    private fun openMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    //region inner classes
    inner class RunnableWorkBackground : Runnable {

        private var colorIconUpdateCatalyst: Boolean = false
        private var isTableCatalystEmpty: Boolean = false

        //region methods used in doInBackground
        private fun updateUserInformation(): ProcessStep {
            val user: JSONArray =
                Spreadsheet.getDataLogin(SharedPreference.getKey(SharedPreference.LOGIN))
                    ?: return ProcessStep.USER_ELAPSED_DATE_LICENCE
            if (Checker.checkTimeIsGreaterThanNow(
                    user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
                ) == false
            ) {
                return ProcessStep.USER_ELAPSED_DATE_LICENCE
            }
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
            return ProcessStep.NONE
        }

        private fun checkLicence(): ProcessStep {
            if (Spreadsheet.isExpiredLicenceOfCompany(false) == true) {
                return ProcessStep.COMPANY_ELAPSED_LICENCE
            }
            if (Checker.checkTimeOnPhone(TimeChecking.CHECKING_LICENCE) == true) {
                return ProcessStep.SUCCESS
            }
            return ProcessStep.USER_ELAPSED_DATE_LICENCE
        }
        //endregion


        //region methods of run
        private fun onPreExecute() {
            //
        }

        private fun doInBackground(): ProcessStep {
            try {
                var processStep = checkLicence()
                if (processStep != ProcessStep.NONE) {
                    return processStep
                }
                processStep = updateUserInformation()
                if (processStep != ProcessStep.NONE) {
                    return processStep
                }
                return ProcessStep.SUCCESS
            } catch (e: Exception) {
                return ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            if (processStep == ProcessStep.USER_ELAPSED_DATE_LICENCE || processStep == ProcessStep.COMPANY_ELAPSED_LICENCE) {
                SharedPreference.setKey(SharedPreference.LICENCE_DATE_OF_END, "")
                openMainActivity()
            } else {
                activityBottomNavigationBinding.bottomNavigation.visibility = View.VISIBLE
                activityBottomNavigationBinding.fragmentWaiting.visibility = View.GONE
                activityBottomNavigationBinding.bottomNavigation.setOnItemSelectedListener {
                    var selectedFragment: Fragment? = null
                    when (it.itemId) {
                        R.id.bottom_menu_result -> {
                            selectedFragment = ResultsFragment()
                        }

                        R.id.bottom_menu_courses -> {
                            selectedFragment = CoursesFragment()
                        }

                        R.id.bottom_menu_update -> {
                            selectedFragment = UpdatesFragment()
                        }

                        R.id.bottom_menu_settings -> {
                            selectedFragment = SettingsFragment()
                        }
                    }


                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment!!)
                        .commit()
                    return@setOnItemSelectedListener true
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ResultsFragment()).commit()
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreference.init(applicationContext)
        activityBottomNavigationBinding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(activityBottomNavigationBinding.root)


        Thread(RunnableWorkBackground()).start()
    }
}
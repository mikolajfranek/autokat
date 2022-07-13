package pl.autokat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.*
import pl.autokat.databinding.ActivityCoursesBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException
import java.time.LocalDate

class CoursesActivity : AppCompatActivity() {

    private lateinit var activityCoursesBinding: ActivityCoursesBinding
    private lateinit var database: Database

    //region methods used in override
    private fun init() {
        activityCoursesBinding = ActivityCoursesBinding.inflate(layoutInflater)
        val view = activityCoursesBinding.root
        setContentView(view)
        setSupportActionBar(activityCoursesBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        database = Database(applicationContext)
    }

    private fun setClickListeners() {
        activityCoursesBinding.switchTypeCourses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val lastCoursesDate = SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
                activityCoursesBinding.buttonSelectDateCourses.visibility = View.GONE
                SharedPreference.setKey(SharedPreference.ACTUAL_COURSES_DATE, "")
                SharedPreference.setKey(SharedPreference.ACTUAL_COURSES_CHOICE, "1")
                if (lastCoursesDate.isNotEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        Configuration.COURSES_REFRESH,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                activityCoursesBinding.buttonSelectDateCourses.visibility = View.VISIBLE
                SharedPreference.setKey(SharedPreference.ACTUAL_COURSES_CHOICE, "0")
            }
        }
        activityCoursesBinding.buttonSelectDateCourses.setOnClickListener {
            openCalendarActivity()
        }
    }

    private fun setInViewSelectingCourses() {
        if (Course.isCoursesSelected()) {
            activityCoursesBinding.switchTypeCourses.isChecked = false
            activityCoursesBinding.buttonSelectDateCourses.visibility = View.VISIBLE
        } else {
            activityCoursesBinding.switchTypeCourses.isChecked = true
            activityCoursesBinding.buttonSelectDateCourses.visibility = View.GONE
        }
    }

    private fun setInViewCourses() {
        val platinum: String = Course.calculateCoursesToPln(
            SharedPreference.getKey(SharedPreference.PLATINUM),
            SharedPreference.getKey(SharedPreference.USD_PLN)
        )
        val platinumDate: String = SharedPreference.getKey(SharedPreference.PLATINUM_DATE)
        val platinumText = (Formatter.formatStringFloat(platinum, 2) + " zł/g")
        activityCoursesBinding.platinum.text = platinumText
        activityCoursesBinding.platinumDate.text = Formatter.formatStringDate(platinumDate)
        val palladium: String = Course.calculateCoursesToPln(
            SharedPreference.getKey(SharedPreference.PALLADIUM),
            SharedPreference.getKey(SharedPreference.USD_PLN)
        )
        val palladiumDate: String = SharedPreference.getKey(SharedPreference.PALLADIUM_DATE)
        val palladiumText = (Formatter.formatStringFloat(palladium, 2) + " zł/g")
        activityCoursesBinding.palladium.text = palladiumText
        activityCoursesBinding.palladiumDate.text = Formatter.formatStringDate(palladiumDate)
        val rhodium: String = Course.calculateCoursesToPln(
            SharedPreference.getKey(SharedPreference.RHODIUM),
            SharedPreference.getKey(SharedPreference.USD_PLN)
        )
        val rhodiumDate: String = SharedPreference.getKey(SharedPreference.RHODIUM_DATE)
        val rhodiumText = (Formatter.formatStringFloat(rhodium, 2) + " zł/g")
        activityCoursesBinding.rhodium.text = rhodiumText
        activityCoursesBinding.rhodiumDate.text = Formatter.formatStringDate(rhodiumDate)
        val courseEurPln: String = SharedPreference.getKey(SharedPreference.EUR_PLN)
        val courseEurPlnDate: String = SharedPreference.getKey(SharedPreference.EUR_PLN_DATE)
        val eurPlnText = (Formatter.formatStringFloat(courseEurPln, 2) + " zł")
        activityCoursesBinding.eurPln.text = eurPlnText
        activityCoursesBinding.eurPlnDate.text = Formatter.formatStringDate(courseEurPlnDate)
        val courseUsdPln: String = SharedPreference.getKey(SharedPreference.USD_PLN)
        val courseUsdPlnDate: String = SharedPreference.getKey(SharedPreference.USD_PLN_DATE)
        val usdPlnText = (Formatter.formatStringFloat(courseUsdPln, 2) + " zł")
        activityCoursesBinding.usdPln.text = usdPlnText
        activityCoursesBinding.usdPlnDate.text = Formatter.formatStringDate(courseUsdPlnDate)
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setClickListeners()
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    override fun onResume() {
        super.onResume()
        setInViewSelectingCourses()
        setInViewCourses()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.courses, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_refresh_courses -> {
                Thread(RunnableUpdateCourses()).start()
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
    private fun openCalendarActivity() {
        startActivity(Intent(applicationContext, CalendarActivity::class.java))
    }
    //endregion

    //region inner classes
    inner class RunnableUpdateCourses : Runnable {

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityCoursesBinding.linearLayout, false)
            Toast.makeText(applicationContext, Configuration.UPDATE_WAIT, Toast.LENGTH_SHORT).show()
        }

        private fun doInBackground(): ProcessStep {
            return try {
                Course.getValues(database, LocalDate.now())
                ProcessStep.SUCCESS
            } catch (e: UnknownHostException) {
                ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.NETWORK_FAILED -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.NETWORK_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UPDATE_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UPDATE_SUCCESS,
                        Toast.LENGTH_SHORT
                    ).show()
                    setInViewCourses()
                }
                else -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UPDATE_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            UserInterface.changeStatusLayout(activityCoursesBinding.linearLayout, true)
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

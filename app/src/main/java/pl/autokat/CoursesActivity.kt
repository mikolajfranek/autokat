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

class CoursesActivity : AppCompatActivity() {

    private lateinit var activityCoursesBinding: ActivityCoursesBinding
    private lateinit var database: Database

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCoursesBinding =
            ActivityCoursesBinding.inflate(layoutInflater)
        val view = activityCoursesBinding.root
        setContentView(view)
        setSupportActionBar(activityCoursesBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        database = Database(applicationContext)
        activityCoursesBinding.switchCourses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val lastCoursesDate =
                    SharedPreference.getKeyFromFile(SharedPreference.ACTUAL_COURSES_DATE)
                activityCoursesBinding.actualDateCoursesButton.visibility =
                    View.GONE
                SharedPreference.setKeyToFile(
                    SharedPreference.ACTUAL_COURSES_DATE,
                    ""
                )
                SharedPreference.setKeyToFile(
                    SharedPreference.ACTUAL_COURSES_CHOICE,
                    "1"
                )
                if (lastCoursesDate.isNotEmpty()) {
                    Toast.makeText(
                        this@CoursesActivity.applicationContext,
                        Configuration.COURSES_REFRESH,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                activityCoursesBinding.actualDateCoursesButton.visibility =
                    View.VISIBLE
                SharedPreference.setKeyToFile(
                    SharedPreference.ACTUAL_COURSES_CHOICE,
                    "0"
                )
            }
        }
        activityCoursesBinding.actualDateCoursesButton.setOnClickListener {
            startActivity(Intent(applicationContext, CalendarActivity::class.java))
        }
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    override fun onResume() {
        super.onResume()
        if (Course.isCoursesSelected()) {
            activityCoursesBinding.switchCourses.isChecked = false
            activityCoursesBinding.actualDateCoursesButton.visibility =
                View.VISIBLE
        } else {
            activityCoursesBinding.switchCourses.isChecked = true
            activityCoursesBinding.actualDateCoursesButton.visibility = View.GONE
        }
        setValuesInView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_configurationvalues, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_refresh_courses -> {
                Thread(TaskUpdateCourses()).start()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }
    //endregion

    private fun setValuesInView() {
        val platinum: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.PLATINUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val platinumDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.PLATINUM_DATE)
        val platinumText = (Formatter.formatStringFloat(platinum, 2) + " zł/g")
        activityCoursesBinding.platinum.text = platinumText
        activityCoursesBinding.platinumDate.text =
            Formatter.formatStringDate(platinumDate)
        val palladium: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val palladDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM_DATE)
        val palladiumText = (Formatter.formatStringFloat(palladium, 2) + " zł/g")
        activityCoursesBinding.palladium.text = palladiumText
        activityCoursesBinding.palladiumDate.text =
            Formatter.formatStringDate(palladDate)
        val rhodium: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val rhodiumDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM_DATE)
        val rhodiumText = (Formatter.formatStringFloat(rhodium, 2) + " zł/g")
        activityCoursesBinding.rhodium.text = rhodiumText
        activityCoursesBinding.rhodiumDate.text =
            Formatter.formatStringDate(rhodiumDate)
        val courseEurPln: String =
            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN)
        val courseEurPlnDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN_DATE)
        val eurPlnText = (Formatter.formatStringFloat(courseEurPln, 2) + " zł")
        activityCoursesBinding.eurPln.text = eurPlnText
        activityCoursesBinding.eurPlnDate.text =
            Formatter.formatStringDate(courseEurPlnDate)
        val courseUsdPln: String =
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        val courseUsdPlnDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN_DATE)
        val usdPlnText = (Formatter.formatStringFloat(courseUsdPln, 2) + " zł")
        activityCoursesBinding.usdPln.text = usdPlnText
        activityCoursesBinding.usdPlnDate.text =
            Formatter.formatStringDate(courseUsdPlnDate)
    }

    inner class TaskUpdateCourses : Runnable {
        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityCoursesBinding.linearLayout,
                    false
                )
                Toast.makeText(
                    this@CoursesActivity.applicationContext,
                    Configuration.UPDATE_WAIT,
                    Toast.LENGTH_SHORT
                ).show()
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.SUCCESS
            try {
                Course.getValues(database)
            } catch (e: UnknownHostException) {
                processStep = ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.NETWORK_FAILED -> {
                        Toast.makeText(
                            this@CoursesActivity.applicationContext,
                            Configuration.NETWORK_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@CoursesActivity.applicationContext,
                            Configuration.UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@CoursesActivity.applicationContext,
                            Configuration.UPDATE_SUCCESS,
                            Toast.LENGTH_SHORT
                        ).show()
                        setValuesInView()
                    }
                    else -> {
                        Toast.makeText(
                            this@CoursesActivity.applicationContext,
                            Configuration.UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                UserInterface.changeStatusLayout(
                    activityCoursesBinding.linearLayout,
                    true
                )
            }
        }
    }
}

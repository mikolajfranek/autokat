package pl.autokat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.*
import pl.autokat.databinding.ActivityConfigurationValuesBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException


class ConfigurationValuesActivity : AppCompatActivity() {

    private lateinit var bindingActivityConfigurationValues: ActivityConfigurationValuesBinding
    private lateinit var database: Database

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityConfigurationValues =
            ActivityConfigurationValuesBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityConfigurationValues.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityConfigurationValues.toolbar)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        SharedPreference.init(this)
        //init database object
        this.database = Database(this.applicationContext)
        //switch listener
        this.bindingActivityConfigurationValues.switchCourses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val lastCoursesDate =
                    SharedPreference.getKeyFromFile(SharedPreference.ACTUAL_COURSES_DATE)
                this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility =
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
                        this@ConfigurationValuesActivity.applicationContext,
                        MyConfiguration.COURSES_REFRESH,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility =
                    View.VISIBLE
                SharedPreference.setKeyToFile(
                    SharedPreference.ACTUAL_COURSES_CHOICE,
                    "0"
                )
            }
        }
        //listener
        this.bindingActivityConfigurationValues.actualDateCoursesButton.setOnClickListener {
            this.startActivity(Intent(this.applicationContext, CalendarViewActivity::class.java))
        }
    }

    //onresume
    @Suppress("ReplaceCallWithBinaryOperator")
    override fun onResume() {
        super.onResume()
        if (Course.isCoursesSelected()) {
            this.bindingActivityConfigurationValues.switchCourses.isChecked = false
            this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility =
                View.VISIBLE
        } else {
            this.bindingActivityConfigurationValues.switchCourses.isChecked = true
            this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility = View.GONE
        }
        //set values in view
        this.setValuesInView()
    }

    //set all values in view
    private fun setValuesInView() {
        //platinum
        val platinum: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.PLATINUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val platinumDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.PLATINUM_DATE)
        val platiniumText = (Formatter.formatStringFloat(platinum, 2) + " zł/g")
        this.bindingActivityConfigurationValues.platinum.text = platiniumText
        this.bindingActivityConfigurationValues.platinumDate.text =
            Formatter.formatStringDate(platinumDate)
        //pallad
        val pallad: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val palladDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM_DATE)
        val palladiumText = (Formatter.formatStringFloat(pallad, 2) + " zł/g")
        this.bindingActivityConfigurationValues.palladium.text = palladiumText
        this.bindingActivityConfigurationValues.palladiumDate.text =
            Formatter.formatStringDate(palladDate)
        //rhodium
        val rhodium: String = Course.calculateCoursesToPln(
            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM),
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        )
        val rhodiumDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM_DATE)
        val rhodiumText = (Formatter.formatStringFloat(rhodium, 2) + " zł/g")
        this.bindingActivityConfigurationValues.rhodium.text = rhodiumText
        this.bindingActivityConfigurationValues.rhodiumDate.text =
            Formatter.formatStringDate(rhodiumDate)
        //euro
        val courseEurPln: String =
            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN)
        val courseEurPlnDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN_DATE)
        val eurPlnText = (Formatter.formatStringFloat(courseEurPln, 2) + " zł")
        this.bindingActivityConfigurationValues.eurPln.text = eurPlnText
        this.bindingActivityConfigurationValues.eurPlnDate.text =
            Formatter.formatStringDate(courseEurPlnDate)
        //dolar
        val courseUsdPln: String =
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        val courseUsdPlnDate: String =
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN_DATE)
        val usdPlnText = (Formatter.formatStringFloat(courseUsdPln, 2) + " zł")
        this.bindingActivityConfigurationValues.usdPln.text = usdPlnText
        this.bindingActivityConfigurationValues.usdPlnDate.text =
            Formatter.formatStringDate(courseUsdPlnDate)
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.toolbar_list_configurationvalues, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_refresh_courses -> {
                //make async task and execute
                Thread(this.TaskUpdateCourses()).start()
                true
            }
            else -> {
                this.finish()
                true
            }
        }
    }

    //async class which update values of courses
    inner class TaskUpdateCourses : Runnable {
        //fields
        //run
        override fun run() {
            //--- onPreExecute
            this@ConfigurationValuesActivity.runOnUiThread {
                //disable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ConfigurationValuesActivity.bindingActivityConfigurationValues.linearLayout,
                    false
                )
                Toast.makeText(
                    this@ConfigurationValuesActivity.applicationContext,
                    MyConfiguration.UPDATE_WAIT,
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
            this@ConfigurationValuesActivity.runOnUiThread {
                when (processStep) {
                    ProcessStep.NETWORK_FAILED -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.NETWORK_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.UPDATE_SUCCESS,
                            Toast.LENGTH_SHORT
                        ).show()
                        this@ConfigurationValuesActivity.setValuesInView()
                    }
                    else -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                //enable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ConfigurationValuesActivity.bindingActivityConfigurationValues.linearLayout,
                    true
                )
            }
        }
    }
}

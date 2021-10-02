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
import java.net.UnknownHostException


class ConfigurationValuesActivity : AppCompatActivity() {

    private lateinit var bindingActivityConfigurationValues: ActivityConfigurationValuesBinding
    private lateinit var database: MyDatabase

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
        MySharedPreferences.init(this)
        //init database object
        this.database = MyDatabase(this.applicationContext)
        //switch listener
        this.bindingActivityConfigurationValues.switchCourses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val lastCoursesDate =
                    MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE)
                this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility =
                    View.GONE
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE,
                    ""
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_CHOICE,
                    "1"
                )
                if (lastCoursesDate.isNotEmpty()) {
                    Toast.makeText(
                        this@ConfigurationValuesActivity.applicationContext,
                        MyConfiguration.INFO_MESSAGE_REFRESH_COURSES,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                this.bindingActivityConfigurationValues.actualDateCoursesButton.visibility =
                    View.VISIBLE
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_CHOICE,
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
        if (MyCoursesValues.isCoursesSelected()) {
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
        val platinum: String = MyConfiguration.getPlnFromDolar(
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM),
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        )
        val platinumDate: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE)
        val platiniumText = (MyConfiguration.formatStringFloat(platinum, 2) + " zł/g")
        this.bindingActivityConfigurationValues.platinum.text = platiniumText
        this.bindingActivityConfigurationValues.platinumDate.text =
            MyConfiguration.formatDate(platinumDate)
        //pallad
        val pallad: String = MyConfiguration.getPlnFromDolar(
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM),
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        )
        val palladDate: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE)
        val palladiumText = (MyConfiguration.formatStringFloat(pallad, 2) + " zł/g")
        this.bindingActivityConfigurationValues.palladium.text = palladiumText
        this.bindingActivityConfigurationValues.palladiumDate.text =
            MyConfiguration.formatDate(palladDate)
        //rhodium
        val rhodium: String = MyConfiguration.getPlnFromDolar(
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM),
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        )
        val rhodiumDate: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE)
        val rhodiumText = (MyConfiguration.formatStringFloat(rhodium, 2) + " zł/g")
        this.bindingActivityConfigurationValues.rhodium.text = rhodiumText
        this.bindingActivityConfigurationValues.rhodiumDate.text =
            MyConfiguration.formatDate(rhodiumDate)
        //euro
        val courseEurPln: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN)
        val courseEurPlnDate: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE)
        val eurPlnText = (MyConfiguration.formatStringFloat(courseEurPln, 2) + " zł")
        this.bindingActivityConfigurationValues.eurPln.text = eurPlnText
        this.bindingActivityConfigurationValues.eurPlnDate.text =
            MyConfiguration.formatDate(courseEurPlnDate)
        //dolar
        val courseUsdPln: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        val courseUsdPlnDate: String =
            MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE)
        val usdPlnText = (MyConfiguration.formatStringFloat(courseUsdPln, 2) + " zł")
        this.bindingActivityConfigurationValues.usdPln.text = usdPlnText
        this.bindingActivityConfigurationValues.usdPlnDate.text =
            MyConfiguration.formatDate(courseUsdPlnDate)
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
                MyUserInterface.enableActivity(
                    this@ConfigurationValuesActivity.bindingActivityConfigurationValues.linearLayout,
                    false
                )
                Toast.makeText(
                    this@ConfigurationValuesActivity.applicationContext,
                    MyConfiguration.INFO_MESSAGE_WAIT_UPDATE,
                    Toast.LENGTH_SHORT
                ).show()
            }
            //--- doInBackground
            var myProcessStep: MyProcessStep = MyProcessStep.SUCCESS
            try {
                MyCoursesValues.getValues(database)
            } catch (e: UnknownHostException) {
                myProcessStep = MyProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                myProcessStep = MyProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@ConfigurationValuesActivity.runOnUiThread {
                when (myProcessStep) {
                    MyProcessStep.NETWORK_FAILED -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_NETWORK_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    MyProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.INFO_UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    MyProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.INFO_UPDATE_SUCCESS,
                            Toast.LENGTH_SHORT
                        ).show()
                        this@ConfigurationValuesActivity.setValuesInView()
                    }
                    else -> {
                        Toast.makeText(
                            this@ConfigurationValuesActivity.applicationContext,
                            MyConfiguration.INFO_UPDATE_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                //enable user interface on process application
                MyUserInterface.enableActivity(
                    this@ConfigurationValuesActivity.bindingActivityConfigurationValues.linearLayout,
                    true
                )
            }
        }
    }
}

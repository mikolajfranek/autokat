package pl.autokat

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import pl.autokat.databinding.ActivityConfigurationValuesBinding
import java.net.UnknownHostException

class ConfigurationValuesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigurationValuesBinding

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationValuesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //set toolbar
        setSupportActionBar(binding.toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //set values in view
        this.setValuesInView()
    }

    //set all values in view
    private fun setValuesInView(){
        //pallad
        val pallad : String = MyConfiguration.getPlnFromDolar((MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM)))
        val palladDate : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE)
        binding.activityConfigurationValuesElementPalladium.text = (MyConfiguration.formatStringFloat(pallad, 2) + " zł/g")
        binding.activityConfigurationValuesElementPalladiumDate.text = MyConfiguration.formatDate(palladDate)
        //platinum
        val platinum : String = MyConfiguration.getPlnFromDolar(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM))
        val platinumDate : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE)
        binding.activityConfigurationValuesElementPlatinum.text = (MyConfiguration.formatStringFloat(platinum, 2) + " zł/g")
        binding.activityConfigurationValuesElementPlatinumDate.text = MyConfiguration.formatDate(platinumDate)
        //rhodium
        val rhodium : String = MyConfiguration.getPlnFromDolar(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM))
        val rhodiumDate : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE)
        binding.activityConfigurationValuesElementRhodium.text = (MyConfiguration.formatStringFloat(rhodium, 2) + " zł/g")
        binding.activityConfigurationValuesElementRhodiumDate.text = MyConfiguration.formatDate(rhodiumDate)
        //euro
        val courseEurPln : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN)
        val courseEurPlnDate : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE)
        binding.activityConfigurationValuesElementEurPln.text = (MyConfiguration.formatStringFloat(courseEurPln, 2) + " zł")
        binding.activityConfigurationValuesElementEurPlnDate.text = MyConfiguration.formatDate(courseEurPlnDate)
        //dolar
        val courseUsdPln : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        val courseUsdPlnDate : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE)
        binding.activityConfigurationValuesElementUsdPln.text = (MyConfiguration.formatStringFloat(courseUsdPln, 2) + " zł")
        binding.activityConfigurationValuesElementUsdPlnDate.text = MyConfiguration.formatDate(courseUsdPlnDate)
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_configurationvalues, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.toolbar_list_refresh_courses -> {
                //make async task and execute
                val task = UpdateCourses()
                task.execute()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }

    //async class which update values of courses
    @SuppressLint("StaticFieldLeak")
    private inner class UpdateCourses() : AsyncTask<Void, Void, MyProcessStep>() {
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(this@ConfigurationValuesActivity.applicationContext, MyConfiguration.INFO_MESSAGE_WAIT_UPDATE, Toast.LENGTH_SHORT).show()
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                MyCatalystValues.getValues()
            }
            catch(e: UnknownHostException){
                return MyProcessStep.NETWORK_FAILED
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
            return MyProcessStep.SUCCESS
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //do job depends on situation
            when(result){
                MyProcessStep.NETWORK_FAILED -> {
                    Toast.makeText(this@ConfigurationValuesActivity.applicationContext, MyConfiguration.INFO_MESSAGE_NETWORK_FAILED, Toast.LENGTH_SHORT).show()
                }
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(this@ConfigurationValuesActivity.applicationContext, MyConfiguration.INFO_UPDATE_FAILED, Toast.LENGTH_SHORT).show()
                }
                MyProcessStep.SUCCESS -> {
                    Toast.makeText(this@ConfigurationValuesActivity.applicationContext, MyConfiguration.INFO_UPDATE_SUCCESS, Toast.LENGTH_SHORT).show()
                    this@ConfigurationValuesActivity.setValuesInView()
                }
                else -> {
                    Toast.makeText(this@ConfigurationValuesActivity.applicationContext, MyConfiguration.INFO_UPDATE_FAILED, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

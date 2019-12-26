package pl.autokat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_configuration_values.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class ConfigurationValuesActivity : AppCompatActivity() {

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration_values)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)

        //about courses elements
        activity_configuration_values_element_palladium.text = ((MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM)) + " USD")
        activity_configuration_values_element_palladium_date.text = MyConfiguration.formatDate(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM_DATE))
        activity_configuration_values_element_platinum.text = (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM) + " USD")
        activity_configuration_values_element_platinum_date.text = MyConfiguration.formatDate(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM_DATE))
        activity_configuration_values_element_rhodium.text = (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM) + " USD")
        activity_configuration_values_element_rhodium_date.text = MyConfiguration.formatDate(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM_DATE))
        //about courses exchanges
        activity_configuration_values_element_usd_pln.text = (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN) + " PLN")
        activity_configuration_values_element_usd_pln_date.text = MyConfiguration.formatDate(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN_DATE))
        activity_configuration_values_element_eur_pln.text = (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN) + " PLN")
        activity_configuration_values_element_eur_pln_date.text = MyConfiguration.formatDate(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN_DATE))
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

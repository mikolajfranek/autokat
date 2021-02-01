package pl.autokat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    //oncreate
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //change visibility about licence if licence exists
        val licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
        if(licenceDateOfEnd.isEmpty() == false) {
            activity_about_licence.visibility = VISIBLE
            activity_about_licence.text = ("Licencja ważna do: " + MyConfiguration.formatDate(licenceDateOfEnd))
        }
        activity_about_version.text = ("Wersja aplikacji: " + MyConfiguration.VERSION_APP)
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

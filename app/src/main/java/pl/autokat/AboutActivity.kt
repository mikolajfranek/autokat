package pl.autokat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import pl.autokat.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    //oncreate
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //set toolbar
        setSupportActionBar(binding.toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //change visibility about licence if licence exists
        val licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
        if(licenceDateOfEnd.isEmpty() == false) {
            binding.activityAboutLicence.visibility = VISIBLE
            binding.activityAboutLicence.text = ("Licencja ważna do: " + MyConfiguration.formatDate(
                licenceDateOfEnd
            ))
        }
        binding.activityAboutVersion.text = ("Wersja aplikacji: " + MyConfiguration.VERSION_APP)
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

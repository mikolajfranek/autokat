package pl.autokat

import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MySharedPreferences
import pl.autokat.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var bindingActivityAbout: ActivityAboutBinding

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityAbout = ActivityAboutBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityAbout.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityAbout.toolbar as Toolbar?)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //change visibility about licence if licence exists
        val licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
        if(licenceDateOfEnd.isEmpty() == false) {
            this.bindingActivityAbout.licence.visibility = VISIBLE
            val licenceText = "Licencja ważna do: " + MyConfiguration.formatDate(licenceDateOfEnd)
            this.bindingActivityAbout.licence.text = licenceText
        }
        val versionText = "Wersja aplikacji: " + MyConfiguration.VERSION_APP
        this.bindingActivityAbout.version.text = versionText
    }
    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
}

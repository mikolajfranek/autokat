package pl.autokat

import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.Formatter
import pl.autokat.components.MyConfiguration
import pl.autokat.components.SharedPreference
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
        this.setSupportActionBar(this.bindingActivityAbout.toolbar)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        SharedPreference.init(this)
        //change visibility about licence if licence exists
        val licenceDateOfEnd: String =
            SharedPreference.getKeyFromFile(SharedPreference.LICENCE_DATE_OF_END)
        if (licenceDateOfEnd.isEmpty() == false) {
            this.bindingActivityAbout.licence.visibility = VISIBLE
            val licenceText = "Licencja ważna do: " + Formatter.formatStringDate(licenceDateOfEnd)
            this.bindingActivityAbout.licence.text = licenceText
        }
        val versionText = "Wersja aplikacji: " + MyConfiguration.VERSION_APK
        this.bindingActivityAbout.version.text = versionText
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
}

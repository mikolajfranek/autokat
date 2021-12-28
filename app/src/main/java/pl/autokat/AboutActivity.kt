package pl.autokat

import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.Formatter
import pl.autokat.components.Configuration
import pl.autokat.components.SharedPreference
import pl.autokat.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var activityAboutBinding: ActivityAboutBinding

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        val view = activityAboutBinding.root
        setContentView(view)
        setSupportActionBar(activityAboutBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        val licenceDateOfEnd: String =
            SharedPreference.getKeyFromFile(SharedPreference.LICENCE_DATE_OF_END)
        if (licenceDateOfEnd.isEmpty() == false) {
            activityAboutBinding.licence.visibility = VISIBLE
            val licenceText = "Licencja ważna do: " + Formatter.formatStringDate(licenceDateOfEnd)
            activityAboutBinding.licence.text = licenceText
        }
        val versionText = "Wersja aplikacji: " + Configuration.VERSION_APK
        activityAboutBinding.version.text = versionText
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    //endregion
}

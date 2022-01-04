package pl.autokat

import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.Configuration
import pl.autokat.components.Formatter
import pl.autokat.components.SharedPreference
import pl.autokat.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var activityAboutBinding: ActivityAboutBinding

    //region methods used in override
    private fun init() {
        activityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        val view = activityAboutBinding.root
        setContentView(view)
        setSupportActionBar(activityAboutBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
    }

    private fun setInViewInformation() {
        val licenceDateOfEnd: String =
            SharedPreference.getKey(SharedPreference.LICENCE_DATE_OF_END)
        if (licenceDateOfEnd.isEmpty() == false) {
            val licenceText = "Licencja ważna do: " + Formatter.formatStringDate(licenceDateOfEnd)
            activityAboutBinding.licence.text = licenceText
            activityAboutBinding.licence.visibility = VISIBLE
        }
        val versionText = "Wersja aplikacji: " + Configuration.VERSION_APK
        activityAboutBinding.version.text = versionText
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setInViewInformation()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    //endregion
}

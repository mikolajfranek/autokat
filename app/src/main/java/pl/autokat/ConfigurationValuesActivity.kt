package pl.autokat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class ConfigurationValuesActivity : AppCompatActivity() {

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration_values)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, ResultActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}

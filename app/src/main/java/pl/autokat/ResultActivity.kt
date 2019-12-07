package pl.autokat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class ResultActivity : AppCompatActivity() {
    //fields
    private lateinit var database: MyDatabase

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init databases
        database = MyDatabase(applicationContext)



        var result = database.getDataCatalyst("")
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //open configuration values activity
    fun openConfigurationValuesActivity(){
        val intent = Intent(applicationContext, ConfigurationValuesActivity::class.java)
        startActivity(intent)
        finish()
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        when(id){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
                return true
            }
            R.id.toolbar_list_update -> {
                Toast.makeText(applicationContext, "Aktualizacja", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                finish()
                return true
            }
        }
    }
}

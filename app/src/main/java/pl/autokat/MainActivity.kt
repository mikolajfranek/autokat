package pl.autokat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //init shared preferences
        MySharedPreferences.init(this)


        this.openResultActivity()
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //open result activity
    fun openResultActivity(){
        val intent = Intent(applicationContext, ResultActivity::class.java)
        startActivity(intent)
        finish()
    }

    //click button
    fun activityMainButtonOnClick(view: View) {
        this.openResultActivity()
    }
}

package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import java.util.*


class ResultActivity : AppCompatActivity() {

    //fields
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapter: ArrayAdapter<ItemCatalyst>

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)

        //init database object
        database = MyDatabase(applicationContext)
        //init listener on change text
        activity_result_edittext.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@ResultActivity.refreshListView()
            }
        })
        //init database adapter
        databaseAdapter = object : ArrayAdapter<ItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            override fun isEnabled(position: Int): Boolean {
                return false
            }

            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view : View = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                val itemCatalyst = getItem(position)!!

                view.item_id_picture.text = itemCatalyst.idPicture.toString()
                view.item_brand.text = itemCatalyst.brand
                view.item_type.text = itemCatalyst.type
                view.item_name.text = itemCatalyst.name
                view.item_weight.text = (itemCatalyst.weight.toString() + " kg")
                view.item_platinum.text = (itemCatalyst.platinum.toString() + " g")
                view.item_palladium.text = (itemCatalyst.palladium.toString() + " g")
                view.item_rhodium.text = (itemCatalyst.rhodium.toString() + " g")

                view.item_price_pl.text = MyConfiguration.formatFloat(itemCatalyst.countPricePln())

                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //onresume
    override fun onResume() {
        super.onResume()

        //check if licence if end
        val licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
        if(MyConfiguration.checkIfCurrentDateIsGreater(licenceDateOfEnd, true) == true){
            this.openMainActivity()
        }

        //check if user has good time on phone
        if(MyConfiguration.checkTimestamp() == false){
            this.openMainActivity()
        }

        //make async task and execute
        val task = TryUpdate()
        task.execute()
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_result, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //open main activity
    fun openMainActivity(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //open configuration values activity
    fun openConfigurationValuesActivity(){
        val intent = Intent(applicationContext, ConfigurationValuesActivity::class.java)
        startActivity(intent)
    }

    //open about activity
    fun openAboutActivity(){
        val intent = Intent(applicationContext, AboutActivity::class.java)
        startActivity(intent)
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        return when(id){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
                true
            }
            R.id.toolbar_list_about -> {
                this.openAboutActivity()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }

    //refresh list view
    fun refreshListView(){
        val result = database.getDataCatalyst(activity_result_edittext.text.toString())
        databaseAdapter.clear()
        databaseAdapter.addAll(result)
    }

    //async class which check if exists update of app
    @SuppressLint("StaticFieldLeak")
    private inner class TryUpdate : AsyncTask<Void, Void, Void>() {

        private var updateCourses : Boolean = false
        private var updateCatalyst : Boolean = false

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()

            //disable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, false)
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): Void? {
            try{
                //modify flag if could be update courses - if from last update passed 6h
                updateCourses = (Date().time - MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP).toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS/4)

                //modify flag if could be update catalyst - if amount in spreadsheet is greater than in local database
                updateCatalyst = MySpreadsheet.getCountCatalyst() > database.getCountCatalyst()
            }catch(e: Exception){
                //nothing
            }
            return null
        }

        //post execute
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if(updateCourses || updateCatalyst){
                //make async task and execute
                val task = Update(updateCourses, updateCatalyst)
                task.execute()
            }else{
                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
                this@ResultActivity.refreshListView()
            }
        }
    }

    //async class which check if exists update of app
    @SuppressLint("StaticFieldLeak")
    private inner class Update(updateCoursesInput: Boolean, updateCatalystInput : Boolean) : AsyncTask<Void, Void, Boolean>() {

        private var updateCourses : Boolean = updateCoursesInput
        private var updateCatalyst : Boolean = updateCatalystInput

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(applicationContext, "Trwa aktualizacja....", Toast.LENGTH_SHORT).show()
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): Boolean {
            var result = false
            try{
                if(updateCourses){
                    MyCatalystValues.getValues()
                }

                if(updateCatalyst){
                    result = database.insertCatalysts(MySpreadsheet.getDataCatalyst())
                }
            }catch(e: Exception){
                result = false
            }
            return result
        }

        //post execute
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)

            if(result){
                Toast.makeText(applicationContext, "Aktualizacja przebiegła pomyślnie", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Wystąpił błąd podczas aktualizacji", Toast.LENGTH_SHORT).show()
            }
            //disable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
            this@ResultActivity.refreshListView()
        }
    }
}

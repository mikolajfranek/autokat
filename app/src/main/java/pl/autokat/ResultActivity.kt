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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import kotlin.math.roundToInt


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
        //init databases
        database = MyDatabase(applicationContext)
        //init listener on change text
        activity_result_edittext.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@ResultActivity.refreshListView()
            }
        })






        databaseAdapter = object : ArrayAdapter<ItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view : View = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                val itemCatalyst = getItem(position)!!

                view.item_id_picture.text = itemCatalyst.idPicture.toString()
                view.item_brand.text = itemCatalyst.brand
                view.item_type.text = itemCatalyst.type
                view.item_name.text = itemCatalyst.name
                view.item_weight.text = itemCatalyst.weight.toString() + " kg"
                view.item_platinum.text = itemCatalyst.platinum.toString() + " g (1g/kg)"
                view.item_palladium.text = itemCatalyst.palladium.toString() + " g (1g/kg)"
                view.item_rhodium.text = itemCatalyst.rhodium.toString() + " g (1g/kg)"

                itemCatalyst.countPrice(itemCatalyst.platinum, itemCatalyst.palladium, itemCatalyst.rhodium)
                view.item_price_euro.text = (String.format("%.2f", itemCatalyst.priceEuro) + " EUR")
                view.item_price_pl.text = (String.format("%.2f", itemCatalyst.pricePln) + " PLN")

                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)


        val result = database.getDataCatalyst("")
        databaseAdapter.clear()
        databaseAdapter.addAll(result)

    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //onresume
    override fun onResume() {
        super.onResume()

        //check if licence ends
        if(MySpreadsheet.checkIfLicenceEnd(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE))){
            this.openMainActivity()
        }
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list, menu)
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
        finish()
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        return when(id){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
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
    private inner class SearchCatalystOrBrandCar() : AsyncTask<Void, Int, MyProcessStep>() {

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{

                return MyProcessStep.SUCCESS
            }catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
        }

        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)

            when(result){
                MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    this@ResultActivity.openMainActivity()
                }

            }
        }
    }
}

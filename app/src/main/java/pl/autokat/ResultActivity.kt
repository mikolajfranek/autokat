package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import java.util.*


class ResultActivity : AppCompatActivity() {

    //fields
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapter: ArrayAdapter<MyItemCatalyst>
    private var scrollPreLast: Int = 0
    private var scrollLimit : Int = MyConfiguration.DATABASE_PAGINATE_LIMIT

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
        databaseAdapter = object : ArrayAdapter<MyItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            override fun isEnabled(position: Int): Boolean {
                return false
            }

            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view : View = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                val itemCatalyst = getItem(position)!!

                view.item_id_picture.text = itemCatalyst.idPicture.toString()

                view.item_picture.setImageBitmap(itemCatalyst.thumbnail)

                view.item_picture.setOnClickListener {
                    val intent = Intent(applicationContext, PictureActivity::class.java)
                    intent.putExtra("idPicture", itemCatalyst.idPicture)
                    startActivity(intent)
                }

                view.item_brand.text = itemCatalyst.brand
                view.item_type.text = itemCatalyst.type
                view.item_name.text = itemCatalyst.name
                view.item_weight.text = (MyConfiguration.formatStringFloat(itemCatalyst.weight.toString(), 3) + " kg")
                view.item_platinum.text = (MyConfiguration.formatStringFloat(itemCatalyst.platinum.toString(), 3) + " g/kg")
                view.item_palladium.text = (MyConfiguration.formatStringFloat(itemCatalyst.palladium.toString(), 3) + " g/kg")
                view.item_rhodium.text = (MyConfiguration.formatStringFloat(itemCatalyst.rhodium.toString(), 3) + " g/kg")

                val pricePl = itemCatalyst.countPricePln()
                val priceEur = pricePl / MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN).toFloat()

                view.item_price_eur.text = (MyConfiguration.formatStringFloat(priceEur.toString(), 2) + " €")
                view.item_price_pl.text = (MyConfiguration.formatStringFloat(pricePl.toString(), 2) + " zł")
                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)


        activity_result_listView.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastItem : Int = firstVisibleItem + visibleItemCount
                if (lastItem == totalItemCount && scrollPreLast != lastItem) {
                    scrollLimit += MyConfiguration.DATABASE_PAGINATE_LIMIT
                    this@ResultActivity.refreshListView(scrollLimit)
                    scrollPreLast = lastItem
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
        })


        this.refreshListView()
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //onresume
    override fun onResume() {
        super.onResume()

        //check if user has good time on phone
        if(MyConfiguration.checkTimestamp() == false){
            this.openMainActivity()
        }
        //check if licence if end
        val licenceDateOfEnd : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END)
        if(MyConfiguration.checkIfCurrentDateIsGreater(licenceDateOfEnd, true) == true){
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
    fun refreshListView(limit: Int = MyConfiguration.DATABASE_PAGINATE_LIMIT){
        if(limit == MyConfiguration.DATABASE_PAGINATE_LIMIT){
            this.scrollPreLast = 0
            this.scrollLimit = MyConfiguration.DATABASE_PAGINATE_LIMIT
        }

        val result = database.getDataCatalyst(activity_result_edittext.text.toString(), limit)
        databaseAdapter.clear()
        databaseAdapter.addAll(result)
    }

    //async class which check if exists update of app
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
            database.insertCatalysts(MySpreadsheet.getDataCatalyst())

            return null
        }

        //post execute
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)


            this@ResultActivity.refreshListView()

            if(updateCourses || updateCatalyst){
                //make async task and execute
                //val task = Update(applicationContext, database, updateCourses, updateCatalyst)
                //task.execute()


                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
            }else{
                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
                this@ResultActivity.refreshListView()
            }
        }
    }


}

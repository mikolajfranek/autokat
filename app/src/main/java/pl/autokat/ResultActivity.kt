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
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import org.json.JSONArray
import java.util.*

class ResultActivity : AppCompatActivity() {
    //fields
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapter: ArrayAdapter<MyItemCatalyst>
    private var scrollPreLast: Int = 0
    private var scrollLimit : Int = MyConfiguration.DATABASE_PAGINATE_LIMIT
    private var menu : Menu? = null
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
        //text listener on change text
        activity_result_edittext.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@ResultActivity.refreshListView()
            }
        })
        //init database adapter
        databaseAdapter = object : ArrayAdapter<MyItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                val view : View = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                //get element
                val itemCatalyst = getItem(position)!!
                //visibility of feature of element
                val visibilityCatalystFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY)
                val visibilityCatalyst : Boolean = if(visibilityCatalystFromConfiguration.isEmpty()) false  else visibilityCatalystFromConfiguration.toInt() != 0
                //item id picture
                view.item_id_picture.text = itemCatalyst.idPicture
                //item thumbnail
                view.item_picture.setImageBitmap(itemCatalyst.thumbnail)
                view.item_picture.setOnClickListener {
                    val intent = Intent(applicationContext, PictureActivity::class.java)
                    intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                    startActivity(intent)
                }
                //item brand
                view.item_brand.text = itemCatalyst.brand
                //item type
                view.item_type.text = itemCatalyst.type
                //item name
                view.item_name.text = itemCatalyst.name
                //item weight
                view.item_weight.text = (MyConfiguration.formatStringFloat(itemCatalyst.weight.toString(), 3) + " kg")
                //item platinum
                view.item_platinum.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0", 3) + " g/kg")
                //item palladium
                view.item_palladium.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0", 3) + " g/kg")
                //item rhodium
                view.item_rhodium.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.rhodium.toString() else "0.0", 3) + " g/kg")
                //count price
                val pricePl = itemCatalyst.countPricePln()
                val courseUsdPlnFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
                val courseUsdPln : Float = if(courseUsdPlnFromConfiguration.isEmpty()) 0.0F else courseUsdPlnFromConfiguration.toFloat()
                val priceEur = if(courseUsdPln != 0.0F) (pricePl / courseUsdPln) else 0.0F
                view.item_price_eur.text = (MyConfiguration.formatStringFloat(priceEur.toString(), 2) + " €")
                view.item_price_pl.text = (MyConfiguration.formatStringFloat(pricePl.toString(), 2) + " zł")
                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)
        //scroll listener
        activity_result_listView.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                //helper variable which equals first visible item on list plus many of item which can be on the screen
                val lastItem : Int = firstVisibleItem + visibleItemCount
                //if helper equals total elements on list and helper is different that the last helper then refresh list (add elements)
                if(lastItem == totalItemCount && lastItem != scrollPreLast) {
                    scrollLimit += MyConfiguration.DATABASE_PAGINATE_LIMIT
                    this@ResultActivity.refreshListView(scrollLimit)
                    scrollPreLast = lastItem
                }
            }
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
        })
    }
    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    //onresume
    override fun onResume() {
        super.onResume()
        /* checking time */
        if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) this.openMainActivity()
        //make async task and execute
        val task = CheckUpdate()
        task.execute()
    }
    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_result, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }
    //open main activity
    fun openMainActivity(){
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
    //open configuration values activity
    fun openConfigurationValuesActivity(){
        startActivity(Intent(applicationContext, ConfigurationValuesActivity::class.java))
    }
    //open update activity
    private fun openUpdateActivity() {
        startActivity(Intent(applicationContext, UpdateActivity::class.java))
    }
    //open about activity
    fun openAboutActivity(){
        startActivity(Intent(applicationContext, AboutActivity::class.java))
    }
    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
                true
            }
            R.id.toolbar_list_update -> {
                this.openUpdateActivity()
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
            //reset variable of scroll
            this.scrollPreLast = 0
            this.scrollLimit = MyConfiguration.DATABASE_PAGINATE_LIMIT
            //get data from database
            val result = database.getDataCatalyst(activity_result_edittext.text.toString(), this.scrollLimit.toString())
            databaseAdapter.clear()
            databaseAdapter.addAll(result)
        }else{
            //limit as offset in format: skip elements, count elements to get
            val limitWithOffset : String = (limit-MyConfiguration.DATABASE_PAGINATE_LIMIT).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT
            //get data from database
            val result = database.getDataCatalyst(activity_result_edittext.text.toString(), limitWithOffset)
            //add to list
            databaseAdapter.addAll(result)
        }
    }
    //async class which check if exists update of app and update it
    @SuppressLint("StaticFieldLeak")
    private inner class CheckUpdate : AsyncTask<Void, Void, MyProcessStep>() {
        //fields
        private var updateCatalyst : Boolean = false
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            //disable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, false)
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //update value of courses - if from last update passed 6h
                val lastTimestampUpdateCourseFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP)
                if(lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS/4))){
                    MyCatalystValues.getValues()
                }
                //flag update catalyst - if amount in spreadsheet is greater than in local database
                val databaseCatalystCount : Int = database.getCountCatalyst()
                val spreadsheetCatalystCount : Int = MySpreadsheet.getCountCatalyst()
                updateCatalyst = databaseCatalystCount == 0 || spreadsheetCatalystCount > databaseCatalystCount
                /* authentication */
                val user : JSONArray = MySpreadsheet.getDataLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN)) ?: return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                /* save configuration */
                //save licence date
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE))
                //save discount
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, user.getString(MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT))
                //save visibility
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY, user.getString(MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY))
                /* checking time */
                if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
            }catch(e: Exception){
                //nothing
            }
            return MyProcessStep.SUCCESS
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)

            //if elapsed time then go to main activity
            if(result == MyProcessStep.USER_ELAPSED_DATE_LICENCE) this@ResultActivity.openMainActivity()

            //set visibility of ability update catalyst
            if(this@ResultActivity.menu != null){
                if(updateCatalyst){
                    this@ResultActivity.menu!!.getItem(1).setIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_action_update_catalyst_yellow))
                }else{
                    this@ResultActivity.menu!!.getItem(1).setIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_action_update_catalyst))
                }
            }
            //refresh list view
            this@ResultActivity.refreshListView()
            //enable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
        }
    }
}

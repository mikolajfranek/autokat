package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.*
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityResultBinding
import pl.autokat.databinding.MyItemCatalystBinding
import pl.autokat.databinding.MyItemHistoryFilterBinding
import java.lang.Runnable
import java.util.*

class ResultActivity : AppCompatActivity()  {

    private lateinit var bindingActivityResult: ActivityResultBinding
    private lateinit var bindingMyItemCatalyst : MyItemCatalystBinding
    private lateinit var bindingMyItemHistoryFilter: MyItemHistoryFilterBinding
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapterCatalysts: ArrayAdapter<MyItemCatalyst>
    private var scrollPreLastCatalyst: Int = 0
    private var scrollLimitCatalyst : Int = MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
    private lateinit var databaseAdapterHistoryFilter: ArrayAdapter<MyItemHistoryFilter>
    private var scrollPreLastHistoryFilter: Int = 0
    private var scrollLimitHistoryFilter : Int = MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
    private var menu : Menu? = null

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityResult = ActivityResultBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityResult.root
        this.setContentView(view)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityResult.toolbar as Toolbar?)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //init database object
        this.database = MyDatabase(this.applicationContext)
        //text listener on change text
        this.bindingActivityResult.editText.setText(
            MySharedPreferences.getKeyFromFile(
                MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT))
        this.bindingActivityResult.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //save last searched text
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT,
                    s.toString()
                )
                this@ResultActivity.refreshCatalystListView(MyScrollRefresh.RESET_LIST)
            }
        })
        //init database adapter catalyst
        this.databaseAdapterCatalysts = object : ArrayAdapter<MyItemCatalyst>(
            this.applicationContext,
            R.layout.my_item_catalyst
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                this@ResultActivity.bindingMyItemCatalyst = MyItemCatalystBinding.inflate(this@ResultActivity.layoutInflater, parent,  false)
                val viewItem = this@ResultActivity.bindingMyItemCatalyst.root
                //get element
                val itemCatalyst = this.getItem(position)!!
                //visibility of feature of element
                val visibilityCatalyst : Boolean = MySharedPreferences.getKeyFromFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY
                ).toInt() == 1
                //item thumbnail
                this@ResultActivity.bindingMyItemCatalyst.imageView.setImageBitmap(itemCatalyst.thumbnail)
                this@ResultActivity.bindingMyItemCatalyst.imageView.setOnLongClickListener(OnLongClickListener {
                    Toast.makeText(this@ResultActivity.applicationContext, itemCatalyst.idPicture, Toast.LENGTH_LONG)
                        .show()
                    return@OnLongClickListener true
                })
                this@ResultActivity.bindingMyItemCatalyst.imageView.setOnClickListener {
                    val intent = Intent(this@ResultActivity.applicationContext, PictureActivity::class.java)
                    intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                    this@ResultActivity.startActivity(intent)
                }
                //item brand
                this@ResultActivity.bindingMyItemCatalyst.brand.text = itemCatalyst.brand
                //item type
                this@ResultActivity.bindingMyItemCatalyst.type.text = itemCatalyst.type
                //item name
                this@ResultActivity.bindingMyItemCatalyst.name.text = itemCatalyst.name
                //item weight
                this@ResultActivity.bindingMyItemCatalyst.weight.text = (MyConfiguration.formatStringFloat(
                    itemCatalyst.weight.toString(),
                    3
                ) + " kg")
                //item platinum
                this@ResultActivity.bindingMyItemCatalyst.platinum.text = (MyConfiguration.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0",
                    3
                ) + " g/kg")
                //item palladium
                this@ResultActivity.bindingMyItemCatalyst.palladium.text = (MyConfiguration.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0",
                    3
                ) + " g/kg")
                //item rhodium
                this@ResultActivity.bindingMyItemCatalyst.rhodium.text = (MyConfiguration.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.rhodium.toString() else "0.0",
                    3
                ) + " g/kg")
                //count price
                var pricePl = itemCatalyst.countPricePln()
                val courseEurlnFromConfiguration : String = MySharedPreferences.getKeyFromFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN
                )
                val courseEurPln : Float = if(courseEurlnFromConfiguration.isEmpty()) 0.0F else courseEurlnFromConfiguration.toFloat()
                var priceEur = if(courseEurPln != 0.0F) (pricePl / courseEurPln) else 0.0F
                pricePl = if(pricePl < 0) 0.0F else pricePl
                priceEur = if(priceEur < 0) 0.0F else priceEur
                val resultPriceEur : String = (MyConfiguration.formatStringFloat(
                    priceEur.toString(),
                    2
                ) + " €")
                val resultPricePln : String = (MyConfiguration.formatStringFloat(
                    pricePl.toString(),
                    2
                ) + " zł")
                if(visibilityCatalyst){
                    this@ResultActivity.bindingMyItemCatalyst.priceEur.text = resultPriceEur
                    this@ResultActivity.bindingMyItemCatalyst.pricePln.text = resultPricePln
                    this@ResultActivity.bindingMyItemCatalyst.rowPlattinum.visibility = VISIBLE
                    this@ResultActivity.bindingMyItemCatalyst.rowPalladium.visibility = VISIBLE
                    this@ResultActivity.bindingMyItemCatalyst.rowRhodium.visibility = VISIBLE
                }else{
                    this@ResultActivity.bindingMyItemCatalyst.priceEurWithoutMetal.text = resultPriceEur
                    this@ResultActivity.bindingMyItemCatalyst.pricePlnWithoutMetal.text = resultPricePln
                }
                return viewItem
            }
        }
        this.bindingActivityResult.catalystListView.setAdapter(this.databaseAdapterCatalysts)
        //scroll listener
        this.bindingActivityResult.catalystListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                //helper variable which equals first visible item on list plus many of item which can be on the screen
                val lastItem: Int = firstVisibleItem + visibleItemCount
                //if helper equals total elements on list and helper is different that the last helper then refresh list (add elements)
                if (lastItem == totalItemCount && lastItem != this@ResultActivity.scrollPreLastCatalyst) {
                    this@ResultActivity.scrollLimitCatalyst += MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                    this@ResultActivity.refreshCatalystListView(MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    this@ResultActivity.scrollPreLastCatalyst = lastItem
                }
            }
        })
        //init database adapter history filter
        this.databaseAdapterHistoryFilter = object : ArrayAdapter<MyItemHistoryFilter>(
            this.applicationContext,
            R.layout.my_item_history_filter
        ){
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                this@ResultActivity.bindingMyItemHistoryFilter = MyItemHistoryFilterBinding.inflate(
                    this@ResultActivity.layoutInflater, parent,  false)
                val viewItem = this@ResultActivity.bindingMyItemHistoryFilter.root
                //get element
                val itemHistoryFilter = this.getItem(position)!!
                //item name
                this@ResultActivity.bindingMyItemHistoryFilter.name.text = itemHistoryFilter.name
                //click on name history filter
                viewItem.setOnClickListener {
                    this@ResultActivity.bindingActivityResult.editText.setText(itemHistoryFilter.name)
                    this@ResultActivity.bindingActivityResult.drawerLayout.closeDrawers()
                }
                //click for delete history filter
                this@ResultActivity.bindingMyItemHistoryFilter.crossDelete.setOnClickListener {
                    this@ResultActivity.deleteRecordHistoryOfSearch(itemHistoryFilter.id)
                }
                return viewItem
            }
        }
        this.bindingActivityResult.historyFilterListView.setAdapter(this.databaseAdapterHistoryFilter)
        this.bindingActivityResult.historyFilterListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                //helper variable which equals first visible item on list plus many of item which can be on the screen
                val lastItem: Int = firstVisibleItem + visibleItemCount
                //if helper equals total elements on list and helper is different that the last helper then refresh list (add elements)
                if (lastItem == totalItemCount && lastItem != this@ResultActivity.scrollPreLastHistoryFilter) {
                    this@ResultActivity.scrollLimitHistoryFilter += MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                    this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    this@ResultActivity.scrollPreLastHistoryFilter = lastItem
                }
            }
        })
        //drawer layout
        this.bindingActivityResult.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {
                //when opening drawer is on final position
                if(newState == DrawerLayout.STATE_SETTLING && this@ResultActivity.bindingActivityResult.drawerLayout.isDrawerOpen(
                        this@ResultActivity.bindingActivityResult.navigationHistoryFilter) == false) {
                    this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.RESET_LIST)
                }
            }
        })
    }
    //onresume
    override fun onResume() {
        super.onResume()
        /* checking time */
        if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) this.openMainActivity()
        //make async task and execute
        Thread(this.TaskUpdate()).start()
    }
    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.toolbar_list_result, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }
    //open main activity
    fun openMainActivity(){
        this.startActivity(Intent(this.applicationContext, MainActivity::class.java))
        this.finish()
    }
    //open configuration values activity
    fun openConfigurationValuesActivity(){
        this.startActivity(Intent(this.applicationContext, ConfigurationValuesActivity::class.java))
    }
    //open update activity
    fun openUpdateActivity() {
        this.startActivity(Intent(this.applicationContext, UpdateActivity::class.java))
    }
    //open about activity
    fun openAboutActivity(){
        this.startActivity(Intent(this.applicationContext, AboutActivity::class.java))
    }
    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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
                this.finish()
                true
            }
        }
    }
    //click button adding new record history of search
    fun addRecordHistoryOfSearch(view: View?) {
        Thread(this.TaskAddRecordHistoryFilter()).start()
    }
    //click button delete record history of search
    fun deleteRecordHistoryOfSearch(id: Int) {
        Thread(this.TaskDeleteRecordHistoryFilter(id)).start()
    }
    //refresh catalyst list view
    fun refreshCatalystListView(myScrollRefresh: MyScrollRefresh){
        val searchedText = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT)
        when(myScrollRefresh){
            MyScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                this.scrollPreLastCatalyst = 0
                this.scrollLimitCatalyst = MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result = this.database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                this.databaseAdapterCatalysts.clear()
                this.databaseAdapterCatalysts.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result = this.database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                this.databaseAdapterCatalysts.clear()
                this.databaseAdapterCatalysts.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String = (this.scrollLimitCatalyst - MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result = this.database.getDataCatalyst(searchedText, limitWithOffset)
                //add to list
                this.databaseAdapterCatalysts.addAll(result)
            }
        }
        if(this.databaseAdapterCatalysts.count == 0 && searchedText.isEmpty() == false){
            this.bindingActivityResult.catalystTextViewEmptyList.visibility = VISIBLE
        }else{
            this.bindingActivityResult.catalystTextViewEmptyList.visibility = GONE
        }
    }
    //refresh history filter list view
    fun refreshHistoryFilterListView(myScrollRefresh: MyScrollRefresh){
        when(myScrollRefresh){
            MyScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                this.scrollPreLastHistoryFilter = 0
                this.scrollLimitHistoryFilter = MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result = this.database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString())
                this.databaseAdapterHistoryFilter.clear()
                this.databaseAdapterHistoryFilter.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result = this.database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString())
                this.databaseAdapterHistoryFilter.clear()
                this.databaseAdapterHistoryFilter.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (this.scrollLimitHistoryFilter - MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result = this.database.getDataHistoryFilter(limitWithOffset)
                //add to list
                this.databaseAdapterHistoryFilter.addAll(result)
            }
        }
        if(this.databaseAdapterHistoryFilter.count == 0){
            this.bindingActivityResult.historyFilterTextViewWaiting.visibility = GONE
            this.bindingActivityResult.historyFilterTextViewEmptyList.visibility = VISIBLE
            this.bindingActivityResult.historyFilterListView.visibility = GONE
        }else{
            this.bindingActivityResult.historyFilterTextViewWaiting.visibility = GONE
            this.bindingActivityResult.historyFilterTextViewEmptyList.visibility = GONE
            this.bindingActivityResult.historyFilterListView.visibility = VISIBLE
        }
    }
    //update of app
    inner class TaskUpdate : Runnable {
        //fields
        private var updateCatalyst : Boolean = false
        private var databaseEmpty : Boolean = false
        //run
        override fun run() {
            //--- onPreExecute
            this@ResultActivity.runOnUiThread {
                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, false)
                //visibility of content
                this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility = VISIBLE
                this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility = GONE
                this@ResultActivity.bindingActivityResult.catalystListView.visibility = GONE
            }
            //--- doInBackground
            var myProcessStep : MyProcessStep = MyProcessStep.NONE
            try{
                //update value of courses - if from last update passed 6h
                val lastTimestampUpdateCourseFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP)
                if(lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS/4))){
                    MyCoursesValues.getValues(database)
                }
                //flag update catalyst - if amount in spreadsheet is greater than in local database
                val databaseCatalystCount : Int = this@ResultActivity.database.getCountCatalyst()
                this.databaseEmpty = databaseCatalystCount == 0
                val spreadsheetCatalystCount : Int = MySpreadsheet.getCountCatalyst()
                this.updateCatalyst = databaseCatalystCount == 0 || spreadsheetCatalystCount > databaseCatalystCount
                /* authentication */
                val user : JSONArray? = MySpreadsheet.getDataLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN))
                if(user == null){
                    myProcessStep = MyProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                /* checking time */
                if(MyConfiguration.checkTimeOnPhone(user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE),MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW) == false){
                    myProcessStep = MyProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                /* save configuration */
                //save licence date
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END,
                    user.getString(
                        MyConfiguration.MY_SPREADSHEET_USERS_LICENCE
                    )
                )
                //save discount
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT
                        )
                    ).toString()
                )
                //save visibility
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY,
                    MyConfiguration.getIntFromEnumBoolean(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY
                        )
                    ).toString()
                )
                //save minus elements
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PLATINIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PLATINIUM
                        )
                    ).toString()
                )
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PALLADIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PALLADIUM
                        )
                    ).toString()
                )
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_RHODIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_RHODIUM
                        )
                    ).toString()
                )
                //can run service - assuming that app has connection to internet
                ServiceOfThumbnail.enqueueWork(this@ResultActivity.applicationContext)
                myProcessStep = MyProcessStep.SUCCESS
            }catch (e: Exception){
                //nothing
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //if elapsed time then go to main activity
                if(myProcessStep == MyProcessStep.USER_ELAPSED_DATE_LICENCE) {
                    /* set licence as empty */
                    MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END,
                        ""
                    )
                    this@ResultActivity.openMainActivity()
                }
                //visibility of content
                if(this.databaseEmpty){
                    this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility = GONE
                    this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility = VISIBLE
                    this@ResultActivity.bindingActivityResult.catalystListView.visibility = GONE
                }else{
                    this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility = GONE
                    this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility = GONE
                    this@ResultActivity.bindingActivityResult.catalystListView.visibility = VISIBLE
                }
                //set visibility of ability update catalyst
                if(this@ResultActivity.menu != null){
                    if(this.updateCatalyst){
                        MyConfiguration.IS_AVAILABLE_UPDATE = true
                        this@ResultActivity.menu!!.getItem(1).setIcon(
                            ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_update_catalyst_color
                            )
                        )
                    }else{
                        this@ResultActivity.menu!!.getItem(1).setIcon(
                            ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_update_catalyst
                            )
                        )
                    }
                }
                //refresh list view
                this@ResultActivity.refreshCatalystListView(MyScrollRefresh.UPDATE_LIST)
                //enable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, true)
            }
        }
    }
    //add history filter
    inner class TaskAddRecordHistoryFilter : Runnable {
        //fields
        //run
        override fun run() {
            //--- onPreExecute
            this@ResultActivity.runOnUiThread {
                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, false)
            }
            //--- doInBackground
            var myProcessStep : MyProcessStep = MyProcessStep.NONE
            try{
                var searchedText = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT)
                searchedText = ("\\s{2,}").toRegex().replace(searchedText.trim(), " ")
                if(searchedText.isEmpty() == false) {
                    this@ResultActivity.database.deleteHistoryFilter(searchedText)
                    this@ResultActivity.database.insertHistoryFilter(searchedText)
                    myProcessStep = MyProcessStep.SUCCESS
                }
            }catch (e: Exception){
                //nothing
                myProcessStep = MyProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //do job depends on situation
                when(myProcessStep){
                    MyProcessStep.NONE -> {
                        Toast.makeText(this@ResultActivity.applicationContext, MyConfiguration.INFO_MESSAGE_SAVE_EMPTY_VALUE, Toast.LENGTH_LONG).show()
                    }
                    MyProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(this@ResultActivity.applicationContext, MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_LONG).show()
                    }
                    MyProcessStep.SUCCESS -> {
                        Toast.makeText(this@ResultActivity.applicationContext, MyConfiguration.INFO_MESSAGE_ADDED_HISTORY_FILTER, Toast.LENGTH_LONG).show()
                    }
                }
                //enable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, true)
            }
        }
    }
    //delete history filter
    inner class TaskDeleteRecordHistoryFilter(idInput: Int): Runnable {
        //fields
        private var id : Int = idInput
        //run
        override fun run() {
            //--- onPreExecute
            this@ResultActivity.runOnUiThread {
                //disable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, false)
            }
            //--- doInBackground
            var myProcessStep : MyProcessStep = MyProcessStep.SUCCESS
            try{
                this@ResultActivity.database.deleteHistoryFilter(this.id)
            }catch (e: Exception){
                //nothing
                myProcessStep = MyProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //do job depends on situation
                when(myProcessStep){
                    MyProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(this@ResultActivity.applicationContext, MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_LONG).show()
                    }
                    MyProcessStep.SUCCESS -> {
                        Toast.makeText(this@ResultActivity.applicationContext, MyConfiguration.INFO_MESSAGE_DELETED_HISTORY_FILTER, Toast.LENGTH_LONG).show()
                    }
                }
                //refresh list
                this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.UPDATE_LIST)
                //enable user interface on process application
                MyUserInterface.enableActivity(this@ResultActivity.bindingActivityResult.drawerLayout, true)
            }
        }
    }
}
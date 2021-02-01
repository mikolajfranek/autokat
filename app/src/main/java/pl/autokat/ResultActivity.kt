package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
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
import pl.autokat.databinding.ActivityResultBinding
import pl.autokat.databinding.MyItemCatalystBinding
import pl.autokat.databinding.MyItemHistoryFilterBinding
import java.util.*


class ResultActivity : AppCompatActivity()  {

    private lateinit var bindingResult: ActivityResultBinding
    private lateinit var bindingCatalyst : MyItemCatalystBinding
    private lateinit var bindingHistoryFilter: MyItemHistoryFilterBinding
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
        bindingResult = ActivityResultBinding.inflate(layoutInflater)
        val view = bindingResult.root
        setContentView(view)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //set toolbar
        setSupportActionBar(bindingResult.toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //init database object
        database = MyDatabase(applicationContext)
        //text listener on change text
        bindingResult.activityResultEdittext.setText(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT))
        bindingResult.activityResultEdittext.addTextChangedListener(object : TextWatcher {
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
        databaseAdapterCatalysts = object : ArrayAdapter<MyItemCatalyst>(
            applicationContext,
            R.layout.my_item_catalyst
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                bindingCatalyst = MyItemCatalystBinding.inflate(layoutInflater, parent,  false)
                val view = bindingCatalyst.root
                //get element
                val itemCatalyst = getItem(position)!!
                //visibility of feature of element
                val visibilityCatalyst : Boolean = MySharedPreferences.getKeyFromFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY
                ).toInt() == 1
                //item thumbnail
                bindingCatalyst.itemPicture.setImageBitmap(itemCatalyst.thumbnail)
                bindingCatalyst.itemPicture.setOnLongClickListener(OnLongClickListener {
                    Toast.makeText(applicationContext, itemCatalyst.idPicture, Toast.LENGTH_LONG)
                        .show()
                    return@OnLongClickListener true
                })
                bindingCatalyst.itemPicture.setOnClickListener {
                    val intent = Intent(applicationContext, PictureActivity::class.java)
                    intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                    startActivity(intent)
                }
                //item brand
                bindingCatalyst.itemBrand.text = itemCatalyst.brand
                //item type
                bindingCatalyst.itemType.text = itemCatalyst.type
                //item name
                bindingCatalyst.itemName.text = itemCatalyst.name
                //item weight
                bindingCatalyst.itemWeight.text = (MyConfiguration.formatStringFloat(
                    itemCatalyst.weight.toString(),
                    3
                ) + " kg")
                //item platinum
                bindingCatalyst.itemPlatinum.text = (MyConfiguration.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0",
                    3
                ) + " g/kg")
                //item palladium
                bindingCatalyst.itemPalladium.text = (MyConfiguration.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0",
                    3
                ) + " g/kg")
                //item rhodium
                bindingCatalyst.itemRhodium.text = (MyConfiguration.formatStringFloat(
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
                    bindingCatalyst.itemPriceVisibilityEur.text = resultPriceEur
                    bindingCatalyst.itemPriceVisibilityPln.text = resultPricePln
                    bindingCatalyst.itemTableRowPlattinum.visibility = VISIBLE
                    bindingCatalyst.itemTableRowPalladium.visibility = VISIBLE
                    bindingCatalyst.itemTableRowRhodium.visibility = VISIBLE
                }else{
                    bindingCatalyst.itemPriceNotvisibilityEur.text = resultPriceEur
                    bindingCatalyst.itemPriceNotvisibilityPln.text = resultPricePln
                }
                return view
            }
        }
        bindingResult.activityResultCatalystList.setAdapter(databaseAdapterCatalysts)
        //scroll listener
        bindingResult.activityResultCatalystList.setOnScrollListener(object : AbsListView.OnScrollListener {
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
                if (lastItem == totalItemCount && lastItem != scrollPreLastCatalyst) {
                    scrollLimitCatalyst += MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                    this@ResultActivity.refreshCatalystListView(MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    scrollPreLastCatalyst = lastItem
                }
            }
        })
        //init database adapter history filter
        databaseAdapterHistoryFilter = object : ArrayAdapter<MyItemHistoryFilter>(
            applicationContext,
            R.layout.my_item_history_filter
        ){
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                bindingHistoryFilter = MyItemHistoryFilterBinding.inflate(layoutInflater, parent,  false)
                val view = bindingHistoryFilter.root
                //get element
                val itemHistoryFilter = getItem(position)!!
                //item name
                bindingHistoryFilter.itemName.text = itemHistoryFilter.name
                //click on name history filter
                view.setOnClickListener {
                    bindingResult.activityResultEdittext.setText(itemHistoryFilter.name)
                    bindingResult.activityResultDrawerlayout.closeDrawers()
                }
                //click for delete history filter
                bindingHistoryFilter.itemDelete.setOnClickListener {
                    this@ResultActivity.deleteRecordHistoryOfSearch(itemHistoryFilter.id)
                }
                return view
            }
        }
        bindingResult.activityResultHistoryFilterList.setAdapter(databaseAdapterHistoryFilter)
        bindingResult.activityResultHistoryFilterList.setOnScrollListener(object : AbsListView.OnScrollListener {
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
                if (lastItem == totalItemCount && lastItem != scrollPreLastHistoryFilter) {
                    scrollLimitHistoryFilter += MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                    this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    scrollPreLastHistoryFilter = lastItem
                }
            }
        })
        //drawer layout
        bindingResult.activityResultDrawerlayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {
                //when opening drawer is on final position
                if(newState == DrawerLayout.STATE_SETTLING && bindingResult.activityResultDrawerlayout.isDrawerOpen(bindingResult.activityResultNavigationHistoryFilter) == false) {
                    this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.RESET_LIST)
                }
            }
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
    fun openUpdateActivity() {
        startActivity(Intent(applicationContext, UpdateActivity::class.java))
    }
    //open about activity
    fun openAboutActivity(){
        startActivity(Intent(applicationContext, AboutActivity::class.java))
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
                finish()
                true
            }
        }
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
                val result = database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                databaseAdapterCatalysts.clear()
                databaseAdapterCatalysts.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result = database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                databaseAdapterCatalysts.clear()
                databaseAdapterCatalysts.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (this.scrollLimitCatalyst - MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result = database.getDataCatalyst(searchedText, limitWithOffset)
                //add to list
                databaseAdapterCatalysts.addAll(result)
            }
        }
        if(databaseAdapterCatalysts.count == 0 && searchedText.isEmpty() == false){
            bindingResult.activityResultCatalystTextviewEmptyList.visibility = VISIBLE
        }else{
            bindingResult.activityResultCatalystTextviewEmptyList.visibility = GONE
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
                val result = database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString())
                databaseAdapterHistoryFilter.clear()
                databaseAdapterHistoryFilter.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result = database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString())
                databaseAdapterHistoryFilter.clear()
                databaseAdapterHistoryFilter.addAll(result)
            }
            MyScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (this.scrollLimitHistoryFilter - MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result = database.getDataHistoryFilter(limitWithOffset)
                //add to list
                databaseAdapterHistoryFilter.addAll(result)
            }
        }
        if(databaseAdapterHistoryFilter.count == 0){
            bindingResult.activityResultHistoryFilterTextviewWaiting.visibility = GONE
            bindingResult.activityResultHistoryFilterTextviewEmptyList.visibility = VISIBLE
            bindingResult.activityResultHistoryFilterList.visibility = GONE
        }else{
            bindingResult.activityResultHistoryFilterTextviewWaiting.visibility = GONE
            bindingResult.activityResultHistoryFilterTextviewEmptyList.visibility = GONE
            bindingResult.activityResultHistoryFilterList.visibility = VISIBLE
        }
    }
    //async class which check if exists update of app and update it
    @SuppressLint("StaticFieldLeak")
    private inner class CheckUpdate : AsyncTask<Void, Void, MyProcessStep>() {
        //fields
        private var updateCatalyst : Boolean = false
        private var databaseEmpty : Boolean = false
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            //disable user interface on process application
            MyUserInterface.enableActivity(bindingResult.activityResultDrawerlayout, false)
            //visibility of content
            bindingResult.activityResultCatalystTextviewWaiting.visibility = VISIBLE
            bindingResult.activityResultCatalystTextviewEmptyDatabase.visibility = GONE
            bindingResult.activityResultCatalystList.visibility = GONE
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //update value of courses - if from last update passed 6h
                val lastTimestampUpdateCourseFromConfiguration : String = MySharedPreferences.getKeyFromFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP
                )
                if(lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS/4))){
                    MyCatalystValues.getValues()
                }
                //flag update catalyst - if amount in spreadsheet is greater than in local database
                val databaseCatalystCount : Int = database.getCountCatalyst()
                databaseEmpty = databaseCatalystCount == 0
                val spreadsheetCatalystCount : Int = MySpreadsheet.getCountCatalyst()
                updateCatalyst = databaseCatalystCount == 0 || spreadsheetCatalystCount > databaseCatalystCount
                /* authentication */
                val user : JSONArray = MySpreadsheet.getDataLogin(
                    MySharedPreferences.getKeyFromFile(
                        MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN
                    )
                ) ?: return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                /* checking time */
                if(MyConfiguration.checkTimeOnPhone(
                        user.getString(MyConfiguration.MY_SPREADSHEET_USERS_LICENCE),
                        MyTimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                    ) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE
                /* save configuration */
                //save licence date
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, user.getString(
                        MyConfiguration.MY_SPREADSHEET_USERS_LICENCE
                    )
                )
                //save discount
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_DISCOUNT
                        )
                    ).toString()
                )
                //save visibility
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY,
                    MyConfiguration.getIntFromEnumBoolean(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_VISIBILITY
                        )
                    ).toString()
                )
                //save minus elements
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PLATINIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PLATINIUM
                        )
                    ).toString()
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_PALLADIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_PALLADIUM
                        )
                    ).toString()
                )
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_MINUS_RHODIUM,
                    MyConfiguration.getIntFromString(
                        user.getString(
                            MyConfiguration.MY_SPREADSHEET_USERS_MINUS_RHODIUM
                        )
                    ).toString()
                )
                //can run service - assuming that app has connection to internet
                ServiceOfThumbnail.enqueueWork(applicationContext)
            }catch (e: Exception){
                //nothing
            }
            return MyProcessStep.SUCCESS
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //if elapsed time then go to main activity
            if(result == MyProcessStep.USER_ELAPSED_DATE_LICENCE) {
                /* set licence as empty */
                MySharedPreferences.setKeyToFile(
                    MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END,
                    ""
                )
                this@ResultActivity.openMainActivity()
            }
            //visibility of content
            if(databaseEmpty){
                bindingResult.activityResultCatalystTextviewWaiting.visibility = GONE
                bindingResult.activityResultCatalystTextviewEmptyDatabase.visibility = VISIBLE
                bindingResult.activityResultCatalystList.visibility = GONE
            }else{
                bindingResult.activityResultCatalystTextviewWaiting.visibility = GONE
                bindingResult.activityResultCatalystTextviewEmptyDatabase.visibility = GONE
                bindingResult.activityResultCatalystList.visibility = VISIBLE
            }
            //set visibility of ability update catalyst
            if(this@ResultActivity.menu != null){
                if(updateCatalyst){
                    MyConfiguration.IS_AVAILABLE_UPDATE = true
                    this@ResultActivity.menu!!.getItem(1).setIcon(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.mipmap.ic_action_update_catalyst_color
                        )
                    )
                }else{
                    this@ResultActivity.menu!!.getItem(1).setIcon(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.mipmap.ic_action_update_catalyst
                        )
                    )
                }
            }
            //refresh list view
            this@ResultActivity.refreshCatalystListView(MyScrollRefresh.UPDATE_LIST)
            //enable user interface on process application
            MyUserInterface.enableActivity(bindingResult.activityResultDrawerlayout, true)
        }
    }










    //TODO

    //click button adding new record history of search
    fun addRecordHistoryOfSearch(view: View?) {
        val searchedText = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LAST_SEARCHED_TEXT)






        if(searchedText.isEmpty() == false && database.deleteHistoryFilter(searchedText) != -1 && database.insertHistoryFilter(searchedText) == true){
            Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_ADDED_HISTORY_FILTER, Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_LONG).show()
        }






        //znowu asynchronicznie?
        //zablokuj widok
            //dodaj
                //usun taki sam jeśli jest
        //odkryj widok


        //TODO

    }
    //click button delete record history of search
    fun deleteRecordHistoryOfSearch(id: Int) {


        //znowu asynchronicznie?
        //zablokuj widok
            //usun
            //odśwież listę - długa operacja
        //odkryj widok



        if(database.deleteHistoryFilter(id) != -1){
            Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_DELETED_HISTORY_FILTER, Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_LONG).show()
        }
        this@ResultActivity.refreshHistoryFilterListView(MyScrollRefresh.UPDATE_LIST)


        //TODO

    }


    //async class which prepare drawer layout for searching
        //ustaw 'trwa pobieranie danych w drawer'
            //pobierz rekordy
        //ustaw tekst lub elementy do adaptera






}

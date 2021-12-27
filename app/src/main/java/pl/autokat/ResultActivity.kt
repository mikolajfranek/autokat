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
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.components.Formatter
import pl.autokat.databinding.ActivityResultBinding
import pl.autokat.databinding.MyItemCatalystBinding
import pl.autokat.databinding.MyItemHistoryFilterBinding
import pl.autokat.enums.ProcessStep
import pl.autokat.enums.ScrollRefresh
import pl.autokat.enums.TimeChecking
import pl.autokat.models.ModelCatalyst
import pl.autokat.models.ModelHistoryFilter
import pl.autokat.workers.WorkerUpload
import java.time.LocalDate
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var bindingActivityResult: ActivityResultBinding
    private lateinit var bindingMyItemCatalyst: MyItemCatalystBinding
    private lateinit var bindingMyItemHistoryFilter: MyItemHistoryFilterBinding
    private lateinit var database: Database
    private lateinit var databaseAdapterCatalysts: ArrayAdapter<ModelCatalyst>
    private var scrollPreLastCatalyst: Int = 0
    private var scrollLimitCatalyst: Int = MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
    private lateinit var databaseAdapterHistoryFilter: ArrayAdapter<ModelHistoryFilter>
    private var scrollPreLastHistoryFilter: Int = 0
    private var scrollLimitHistoryFilter: Int =
        MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
    private var menu: Menu? = null

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityResult = ActivityResultBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityResult.root
        this.setContentView(view)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityResult.toolbar)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        SharedPreference.init(this)
        //init database object
        this.database = Database(this.applicationContext)
        //text listener on change text
        this.bindingActivityResult.editText.setText(
            SharedPreference.getKeyFromFile(
                SharedPreference.LAST_SEARCHED_TEXT
            )
        )
        this.bindingActivityResult.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //save last searched text
                SharedPreference.setKeyToFile(
                    SharedPreference.LAST_SEARCHED_TEXT,
                    s.toString()
                )
                this@ResultActivity.refreshCatalystListView(ScrollRefresh.RESET_LIST)
            }
        })
        //init database adapter catalyst
        this.databaseAdapterCatalysts = object : ArrayAdapter<ModelCatalyst>(
            this.applicationContext,
            R.layout.my_item_catalyst
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                this@ResultActivity.bindingMyItemCatalyst =
                    MyItemCatalystBinding.inflate(this@ResultActivity.layoutInflater, parent, false)
                val viewItem = this@ResultActivity.bindingMyItemCatalyst.root
                //get element
                val itemCatalyst = this.getItem(position)!!
                //visibility of feature of element
                val visibilityCatalyst: Boolean = SharedPreference.getKeyFromFile(
                    SharedPreference.VISIBILITY
                ).toInt() == 1
                //item thumbnail
                this@ResultActivity.bindingMyItemCatalyst.imageView.setImageBitmap(itemCatalyst.thumbnail)
                this@ResultActivity.bindingMyItemCatalyst.imageView.setOnLongClickListener(
                    OnLongClickListener {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            itemCatalyst.idPicture,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@OnLongClickListener true
                    })
                this@ResultActivity.bindingMyItemCatalyst.imageView.setOnClickListener {
                    val intent =
                        Intent(this@ResultActivity.applicationContext, PictureActivity::class.java)
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
                val weightText = Formatter.formatStringFloat(
                    itemCatalyst.weight.toString(),
                    3
                ) + " kg"
                this@ResultActivity.bindingMyItemCatalyst.weight.text = weightText
                //item platinum
                val platinumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0",
                    3
                ) + " g/kg"
                this@ResultActivity.bindingMyItemCatalyst.platinum.text = platinumText
                //item palladium
                val palladiumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0",
                    3
                ) + " g/kg"
                this@ResultActivity.bindingMyItemCatalyst.palladium.text = palladiumText
                //item rhodium
                val rhodiumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.rhodium.toString() else "0.0",
                    3
                ) + " g/kg"
                this@ResultActivity.bindingMyItemCatalyst.rhodium.text = rhodiumText
                //count price
                var pricePl = itemCatalyst.countPricePln()
                val courseEurlnFromConfiguration: String = SharedPreference.getKeyFromFile(
                    SharedPreference.EUR_PLN
                )
                val courseEurPln: Float =
                    if (courseEurlnFromConfiguration.isEmpty()) 0.0F else courseEurlnFromConfiguration.toFloat()
                var priceEur = if (courseEurPln != 0.0F) (pricePl / courseEurPln) else 0.0F
                pricePl = if (pricePl < 0) 0.0F else pricePl
                priceEur = if (priceEur < 0) 0.0F else priceEur
                val resultPriceEur: String = (Formatter.formatStringFloat(
                    priceEur.toString(),
                    2
                ) + " €")
                val resultPricePln: String = (Formatter.formatStringFloat(
                    pricePl.toString(),
                    2
                ) + " zł")
                if (visibilityCatalyst) {
                    this@ResultActivity.bindingMyItemCatalyst.priceEur.text = resultPriceEur
                    this@ResultActivity.bindingMyItemCatalyst.pricePln.text = resultPricePln
                    this@ResultActivity.bindingMyItemCatalyst.rowPlattinum.visibility = VISIBLE
                    this@ResultActivity.bindingMyItemCatalyst.rowPalladium.visibility = VISIBLE
                    this@ResultActivity.bindingMyItemCatalyst.rowRhodium.visibility = VISIBLE
                } else {
                    this@ResultActivity.bindingMyItemCatalyst.priceEurWithoutMetal.text =
                        resultPriceEur
                    this@ResultActivity.bindingMyItemCatalyst.pricePlnWithoutMetal.text =
                        resultPricePln
                }
                return viewItem
            }
        }
        this.bindingActivityResult.catalystListView.adapter = this.databaseAdapterCatalysts
        //scroll listener
        this.bindingActivityResult.catalystListView.setOnScrollListener(object :
            AbsListView.OnScrollListener {
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
                    this@ResultActivity.refreshCatalystListView(ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    this@ResultActivity.scrollPreLastCatalyst = lastItem
                }
            }
        })
        //init database adapter history filter
        this.databaseAdapterHistoryFilter = object : ArrayAdapter<ModelHistoryFilter>(
            this.applicationContext,
            R.layout.my_item_history_filter
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //set layout of element
                this@ResultActivity.bindingMyItemHistoryFilter = MyItemHistoryFilterBinding.inflate(
                    this@ResultActivity.layoutInflater, parent, false
                )
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
        this.bindingActivityResult.historyFilterListView.adapter = this.databaseAdapterHistoryFilter
        this.bindingActivityResult.historyFilterListView.setOnScrollListener(object :
            AbsListView.OnScrollListener {
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
                    this@ResultActivity.refreshHistoryFilterListView(ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
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
                if (newState == DrawerLayout.STATE_SETTLING && this@ResultActivity.bindingActivityResult.drawerLayout.isDrawerOpen(
                        this@ResultActivity.bindingActivityResult.navigationHistoryFilter
                    ) == false
                ) {
                    this@ResultActivity.refreshHistoryFilterListView(ScrollRefresh.RESET_LIST)
                }
            }
        })
        //listeners
        this.bindingActivityResult.adderButtonHistoryOfSearch.setOnClickListener {
            //click button adding new record history of search
            Thread(this.TaskAddRecordHistoryFilter()).start()
        }
    }

    //onresume
    override fun onResume() {
        super.onResume()
        /* checking time */
        if (MyConfiguration.checkTimeOnPhone(
                "",
                TimeChecking.CHECKING_LICENCE
            ) == false
        ) this.openMainActivity()
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
    fun openMainActivity() {
        this.startActivity(Intent(this.applicationContext, MainActivity::class.java))
        this.finish()
    }

    //open configuration values activity
    private fun openConfigurationValuesActivity() {
        this.startActivity(Intent(this.applicationContext, ConfigurationValuesActivity::class.java))
    }

    //open update activity
    private fun openUpdateActivity() {
        this.startActivity(Intent(this.applicationContext, UpdateActivity::class.java))
    }

    //open about activity
    private fun openAboutActivity() {
        this.startActivity(Intent(this.applicationContext, AboutActivity::class.java))
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    //click button delete record history of search
    fun deleteRecordHistoryOfSearch(id: Int) {
        Thread(this.TaskDeleteRecordHistoryFilter(id)).start()
    }

    //refresh catalyst list view
    fun refreshCatalystListView(scrollRefresh: ScrollRefresh) {
        val searchedText =
            SharedPreference.getKeyFromFile(SharedPreference.LAST_SEARCHED_TEXT)
        when (scrollRefresh) {
            ScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                this.scrollPreLastCatalyst = 0
                this.scrollLimitCatalyst = MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result =
                    this.database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                this.databaseAdapterCatalysts.clear()
                this.databaseAdapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result =
                    this.database.getDataCatalyst(searchedText, this.scrollLimitCatalyst.toString())
                this.databaseAdapterCatalysts.clear()
                this.databaseAdapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (this.scrollLimitCatalyst - MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result = this.database.getDataCatalyst(searchedText, limitWithOffset)
                //add to list
                this.databaseAdapterCatalysts.addAll(result)
            }
        }
        if (this.databaseAdapterCatalysts.count == 0 && searchedText.isEmpty() == false) {
            this.bindingActivityResult.catalystTextViewEmptyList.visibility = VISIBLE
        } else {
            this.bindingActivityResult.catalystTextViewEmptyList.visibility = GONE
        }
    }

    //refresh history filter list view
    fun refreshHistoryFilterListView(scrollRefresh: ScrollRefresh) {
        val nameCatalystOrBrandCarInput = this@ResultActivity.bindingActivityResult.editText.text.toString()
        when (scrollRefresh) {
            ScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                this.scrollPreLastHistoryFilter = 0
                this.scrollLimitHistoryFilter =
                    MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result =
                    this.database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString(), nameCatalystOrBrandCarInput)
                this.databaseAdapterHistoryFilter.clear()
                this.databaseAdapterHistoryFilter.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result =
                    this.database.getDataHistoryFilter(this.scrollLimitHistoryFilter.toString(), nameCatalystOrBrandCarInput)
                this.databaseAdapterHistoryFilter.clear()
                this.databaseAdapterHistoryFilter.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (this.scrollLimitHistoryFilter - MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER).toString() + "," + MyConfiguration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result = this.database.getDataHistoryFilter(limitWithOffset, nameCatalystOrBrandCarInput)
                //add to list
                this.databaseAdapterHistoryFilter.addAll(result)
            }
        }
        if (this.databaseAdapterHistoryFilter.count == 0) {
            this.bindingActivityResult.historyFilterTextViewWaiting.visibility = GONE
            this.bindingActivityResult.historyFilterTextViewEmptyList.visibility = VISIBLE
            this.bindingActivityResult.historyFilterListView.visibility = GONE
        } else {
            this.bindingActivityResult.historyFilterTextViewWaiting.visibility = GONE
            this.bindingActivityResult.historyFilterTextViewEmptyList.visibility = GONE
            this.bindingActivityResult.historyFilterListView.visibility = VISIBLE
        }
    }

    //update of app
    inner class TaskUpdate : Runnable {
        //fields
        private var updateCatalyst: Boolean = false
        private var databaseEmpty: Boolean = false

        //run
        @Suppress("ReplaceCallWithBinaryOperator")
        override fun run() {
            //--- onPreExecute
            this@ResultActivity.runOnUiThread {
                //disable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    false
                )
                //visibility of content
                this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility =
                    VISIBLE
                this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility =
                    GONE
                this@ResultActivity.bindingActivityResult.catalystListView.visibility = GONE
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                //update value of courses - if from last update passed 6h
                val lastTimestampUpdateCourseFromConfiguration: String =
                    SharedPreference.getKeyFromFile(SharedPreference.UPDATE_COURSE_TIMESTAMP)
                if (lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS / 4))) {
                    Course.getValues(database)
                }
                //flag update catalyst - if amount in spreadsheet is greater than in local database
                val databaseCatalystCount: Int = this@ResultActivity.database.getCountCatalyst()
                this.databaseEmpty = databaseCatalystCount == 0
                val spreadsheetCatalystCount: Int = Spreadsheet.getCountCatalyst()
                this.updateCatalyst =
                    databaseCatalystCount == 0 || spreadsheetCatalystCount > databaseCatalystCount
                /* authentication */
                val user: JSONArray? =
                    Spreadsheet.getDataLogin(SharedPreference.getKeyFromFile(SharedPreference.LOGIN))
                if (user == null) {
                    processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                /* checking time */
                if (MyConfiguration.checkTimeOnPhone(
                        user.getString(MyConfiguration.SPREADSHEET_USERS_LICENCE),
                        TimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                    ) == false
                ) {
                    processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                /* save configuration */
                //save licence date
                SharedPreference.setKeyToFile(
                    SharedPreference.LICENCE_DATE_OF_END,
                    user.getString(
                        MyConfiguration.SPREADSHEET_USERS_LICENCE
                    )
                )
                //save discount
                SharedPreference.setKeyToFile(
                    SharedPreference.DISCOUNT,
                    Parser.parseStringToInt(
                        user.getString(
                            MyConfiguration.SPREADSHEET_USERS_DISCOUNT
                        )
                    ).toString()
                )
                //save visibility
                SharedPreference.setKeyToFile(
                    SharedPreference.VISIBILITY,
                    Parser.parseStringBooleanToInt(
                        user.getString(
                            MyConfiguration.SPREADSHEET_USERS_VISIBILITY
                        )
                    ).toString()
                )
                //save minus elements
                SharedPreference.setKeyToFile(
                    SharedPreference.MINUS_PLATINUM,
                    Parser.parseStringToInt(
                        user.getString(
                            MyConfiguration.SPREADSHEET_USERS_MINUS_PLATINUM
                        )
                    ).toString()
                )
                SharedPreference.setKeyToFile(
                    SharedPreference.MINUS_PALLADIUM,
                    Parser.parseStringToInt(
                        user.getString(
                            MyConfiguration.SPREADSHEET_USERS_MINUS_PALLADIUM
                        )
                    ).toString()
                )
                SharedPreference.setKeyToFile(
                    SharedPreference.MINUS_RHODIUM,
                    Parser.parseStringToInt(
                        user.getString(
                            MyConfiguration.SPREADSHEET_USERS_MINUS_RHODIUM
                        )
                    ).toString()
                )
                //can run service - assuming that app has connection to internet
                val workRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<WorkerUpload>().build()
                WorkManager
                    .getInstance(this@ResultActivity.applicationContext)
                    .enqueue(workRequest)
                processStep = ProcessStep.SUCCESS
            } catch (e: Exception) {
                //nothing
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //if elapsed time then go to main activity
                if (processStep == ProcessStep.USER_ELAPSED_DATE_LICENCE) {
                    /* set licence as empty */
                    SharedPreference.setKeyToFile(
                        SharedPreference.LICENCE_DATE_OF_END,
                        ""
                    )
                    this@ResultActivity.openMainActivity()
                }
                //visibility of content
                if (this.databaseEmpty) {
                    this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility =
                        GONE
                    this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility =
                        VISIBLE
                    this@ResultActivity.bindingActivityResult.catalystListView.visibility = GONE
                } else {
                    this@ResultActivity.bindingActivityResult.catalystTextViewWaiting.visibility =
                        GONE
                    this@ResultActivity.bindingActivityResult.catalystTextViewEmptyDatabase.visibility =
                        GONE
                    this@ResultActivity.bindingActivityResult.catalystListView.visibility = VISIBLE
                }
                if (this@ResultActivity.menu != null) {
                    //set visibility of ability update catalyst
                    if (this.updateCatalyst) {
                        MyConfiguration.IS_AVAILABLE_UPDATE = true
                        this@ResultActivity.menu!!.getItem(1).icon = ContextCompat.getDrawable(
                            this@ResultActivity.applicationContext,
                            R.mipmap.ic_action_update_catalyst_color
                        )
                    } else {
                        this@ResultActivity.menu!!.getItem(1).icon = ContextCompat.getDrawable(
                            this@ResultActivity.applicationContext,
                            R.mipmap.ic_action_update_catalyst
                        )
                    }
                    //set visibility of status update courses
                    if (Course.isCoursesSelected()) {
                        val actualCoursesDate =
                            SharedPreference.getKeyFromFile(SharedPreference.ACTUAL_COURSES_DATE)
                        if (LocalDate.now().toString().equals(actualCoursesDate)) {
                            this@ResultActivity.menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values
                            )
                        } else {
                            this@ResultActivity.menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values_color
                            )
                        }
                    } else {
                        val usdDate =
                            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN_DATE)
                        val eurDate =
                            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN_DATE)
                        val platinumDate =
                            SharedPreference.getKeyFromFile(SharedPreference.PLATINUM_DATE)
                        val palladiumDate =
                            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM_DATE)
                        val rhodiumDate =
                            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM_DATE)
                        //yellow when dates are different
                        if (LocalDate.now().toString()
                                .equals(usdDate) && usdDate.equals(eurDate) && eurDate.equals(
                                platinumDate
                            ) && platinumDate.equals(
                                palladiumDate
                            ) && palladiumDate.equals(rhodiumDate)
                        ) {
                            this@ResultActivity.menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values
                            )
                        } else {
                            this@ResultActivity.menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values_color
                            )
                        }
                    }
                }
                //refresh list view
                this@ResultActivity.refreshCatalystListView(ScrollRefresh.UPDATE_LIST)
                //enable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    true
                )
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
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    false
                )
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                var searchedText =
                    SharedPreference.getKeyFromFile(SharedPreference.LAST_SEARCHED_TEXT)
                searchedText = ("\\s{2,}").toRegex().replace(searchedText.trim(), " ")
                if (searchedText.isEmpty() == false) {
                    this@ResultActivity.database.deleteHistoryFilter(searchedText)
                    this@ResultActivity.database.insertHistoryFilter(searchedText)
                    processStep = ProcessStep.SUCCESS
                }
            } catch (e: Exception) {
                //nothing
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //do job depends on situation
                when (processStep) {
                    ProcessStep.NONE -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_SAVE_EMPTY_VALUE,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_ADDED_HISTORY_FILTER,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        //nothing
                    }
                }
                //enable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    true
                )
            }
        }
    }

    //delete history filter
    inner class TaskDeleteRecordHistoryFilter(idInput: Int) : Runnable {
        //fields
        private var id: Int = idInput

        //run
        override fun run() {
            //--- onPreExecute
            this@ResultActivity.runOnUiThread {
                //disable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    false
                )
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.SUCCESS
            try {
                this@ResultActivity.database.deleteHistoryFilter(this.id)
            } catch (e: Exception) {
                //nothing
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@ResultActivity.runOnUiThread {
                //do job depends on situation
                when (processStep) {
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            MyConfiguration.INFO_MESSAGE_DELETED_HISTORY_FILTER,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        //nothing
                    }
                }
                //refresh list
                this@ResultActivity.refreshHistoryFilterListView(ScrollRefresh.UPDATE_LIST)
                //enable user interface on process application
                UserInterface.changeStatusLayout(
                    this@ResultActivity.bindingActivityResult.drawerLayout,
                    true
                )
            }
        }
    }
}
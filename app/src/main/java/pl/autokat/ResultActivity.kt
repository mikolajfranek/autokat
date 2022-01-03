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
import pl.autokat.databinding.CatalystBinding
import pl.autokat.databinding.HistoryFilterBinding
import pl.autokat.enums.ProcessStep
import pl.autokat.enums.ScrollRefresh
import pl.autokat.enums.TimeChecking
import pl.autokat.models.ModelCatalyst
import pl.autokat.models.ModelHistoryFilter
import pl.autokat.workers.WorkerUpload
import java.time.LocalDate
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var activityResultBinding: ActivityResultBinding
    private lateinit var catalystBinding: CatalystBinding
    private lateinit var historyFilterBinding: HistoryFilterBinding
    private lateinit var database: Database
    private lateinit var databaseAdapterCatalysts: ArrayAdapter<ModelCatalyst>
    private var scrollPreLastCatalyst: Int = 0
    private var scrollLimitCatalyst: Int = Configuration.DATABASE_PAGINATE_LIMIT_CATALYST
    private lateinit var databaseAdapterHistoryFilter: ArrayAdapter<ModelHistoryFilter>
    private var scrollPreLastHistoryFilter: Int = 0
    private var scrollLimitHistoryFilter: Int =
        Configuration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
    private var menu: Menu? = null

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = activityResultBinding.root
        setContentView(view)
        setSupportActionBar(activityResultBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        database = Database(applicationContext)
        activityResultBinding.editText.setText(
            SharedPreference.getKey(
                SharedPreference.LAST_SEARCHED_TEXT
            )
        )
        activityResultBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                SharedPreference.setKey(
                    SharedPreference.LAST_SEARCHED_TEXT,
                    s.toString()
                )
                refreshDatabaseAdapterCatalysts(ScrollRefresh.RESET_LIST)
            }
        })
        databaseAdapterCatalysts = object : ArrayAdapter<ModelCatalyst>(
            applicationContext,
            R.layout.catalyst
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                catalystBinding =
                    CatalystBinding.inflate(this@ResultActivity.layoutInflater, parent, false)
                val viewItem = catalystBinding.root
                val itemCatalyst = getItem(position)!!
                val visibilityCatalyst: Boolean = SharedPreference.getKey(
                    SharedPreference.VISIBILITY
                ).toInt() == 1
                catalystBinding.imageView.setImageBitmap(itemCatalyst.thumbnail)
                catalystBinding.imageView.setOnLongClickListener(
                    OnLongClickListener {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            itemCatalyst.idPicture,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@OnLongClickListener true
                    })
                catalystBinding.imageView.setOnClickListener {
                    val intent =
                        Intent(this@ResultActivity.applicationContext, PictureActivity::class.java)
                    intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                    this@ResultActivity.startActivity(intent)
                }
                catalystBinding.brand.text = itemCatalyst.brand
                catalystBinding.type.text = itemCatalyst.type
                catalystBinding.name.text = itemCatalyst.name
                val weightText = Formatter.formatStringFloat(
                    itemCatalyst.weight.toString(),
                    3
                ) + " kg"
                catalystBinding.weight.text = weightText
                val platinumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0",
                    3
                ) + " g/kg"
                catalystBinding.platinum.text = platinumText
                val palladiumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0",
                    3
                ) + " g/kg"
                catalystBinding.palladium.text = palladiumText
                val rhodiumText = Formatter.formatStringFloat(
                    if (visibilityCatalyst) itemCatalyst.rhodium.toString() else "0.0",
                    3
                ) + " g/kg"
                catalystBinding.rhodium.text = rhodiumText
                var pricePl = itemCatalyst.countPricePln()
                val courseEurlnFromConfiguration: String = SharedPreference.getKey(
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
                    catalystBinding.priceEur.text = resultPriceEur
                    catalystBinding.pricePln.text = resultPricePln
                    catalystBinding.rowPlattinum.visibility = VISIBLE
                    catalystBinding.rowPalladium.visibility = VISIBLE
                    catalystBinding.rowRhodium.visibility = VISIBLE
                } else {
                    catalystBinding.priceEurWithoutMetal.text =
                        resultPriceEur
                    catalystBinding.pricePlnWithoutMetal.text =
                        resultPricePln
                }
                return viewItem
            }
        }
        activityResultBinding.catalystListView.adapter = databaseAdapterCatalysts
        activityResultBinding.catalystListView.setOnScrollListener(object :
            AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                val lastItem: Int = firstVisibleItem + visibleItemCount
                if (lastItem == totalItemCount && lastItem != scrollPreLastCatalyst) {
                    scrollLimitCatalyst += Configuration.DATABASE_PAGINATE_LIMIT_CATALYST
                    refreshDatabaseAdapterCatalysts(ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    scrollPreLastCatalyst = lastItem
                }
            }
        })
        databaseAdapterHistoryFilter = object : ArrayAdapter<ModelHistoryFilter>(
            applicationContext,
            R.layout.history_filter
        ) {
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                historyFilterBinding = HistoryFilterBinding.inflate(
                    this@ResultActivity.layoutInflater, parent, false
                )
                val viewItem = historyFilterBinding.root
                val itemHistoryFilter = getItem(position)!!
                historyFilterBinding.name.text = itemHistoryFilter.name
                viewItem.setOnClickListener {
                    activityResultBinding.editText.setText(itemHistoryFilter.name)
                    activityResultBinding.drawerLayout.closeDrawers()
                }
                historyFilterBinding.crossDelete.setOnClickListener {
                    deleteRecordHistoryOfSearch(itemHistoryFilter.id)
                }
                return viewItem
            }
        }
        activityResultBinding.historyFilterListView.adapter = databaseAdapterHistoryFilter
        activityResultBinding.historyFilterListView.setOnScrollListener(object :
            AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                val lastItem: Int = firstVisibleItem + visibleItemCount
                if (lastItem == totalItemCount && lastItem != scrollPreLastHistoryFilter) {
                    scrollLimitHistoryFilter += Configuration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                    refreshDataBaseAdapterHistoryFilter(ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    scrollPreLastHistoryFilter = lastItem
                }
            }
        })
        activityResultBinding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_SETTLING && activityResultBinding.drawerLayout.isDrawerOpen(
                        activityResultBinding.navigationHistoryFilter
                    ) == false
                ) {
                    refreshDataBaseAdapterHistoryFilter(ScrollRefresh.RESET_LIST)
                }
            }
        })
        activityResultBinding.adderButtonHistoryOfSearch.setOnClickListener {
            Thread(TaskAddRecordHistoryFilter()).start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Checker.checkTimeOnPhone(
                "",
                TimeChecking.CHECKING_LICENCE
            ) == false
        ) openMainActivity()
        Thread(TaskUpdate()).start()
    }

    override fun onCreateOptionsMenu(menuInput: Menu): Boolean {
        menuInflater.inflate(R.menu.result, menuInput)
        menu = menuInput
        return super.onCreateOptionsMenu(menuInput)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_configuration_values -> {
                openConfigurationValuesActivity()
                true
            }
            R.id.toolbar_list_update -> {
                openUpdateActivity()
                true
            }
            R.id.toolbar_list_about -> {
                openAboutActivity()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }
    //endregion

    //region open activities
    private fun openMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun openConfigurationValuesActivity() {
        startActivity(Intent(applicationContext, CoursesActivity::class.java))
    }

    private fun openUpdateActivity() {
        startActivity(Intent(applicationContext, UpdateActivity::class.java))
    }

    private fun openAboutActivity() {
        startActivity(Intent(applicationContext, AboutActivity::class.java))
    }
    //endregion

    //region refresh database adapter
    fun refreshDatabaseAdapterCatalysts(scrollRefresh: ScrollRefresh) {
        val searchedText =
            SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT)
        when (scrollRefresh) {
            ScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                scrollPreLastCatalyst = 0
                scrollLimitCatalyst = Configuration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result =
                    database.getDataCatalyst(searchedText, scrollLimitCatalyst.toString())
                databaseAdapterCatalysts.clear()
                databaseAdapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result =
                    database.getDataCatalyst(searchedText, scrollLimitCatalyst.toString())
                databaseAdapterCatalysts.clear()
                databaseAdapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (scrollLimitCatalyst - Configuration.DATABASE_PAGINATE_LIMIT_CATALYST).toString() + "," + Configuration.DATABASE_PAGINATE_LIMIT_CATALYST
                //get data from database
                val result = database.getDataCatalyst(searchedText, limitWithOffset)
                //add to list
                databaseAdapterCatalysts.addAll(result)
            }
        }
        if (databaseAdapterCatalysts.count == 0 && searchedText.isEmpty() == false) {
            activityResultBinding.catalystTextViewEmptyList.visibility = VISIBLE
        } else {
            activityResultBinding.catalystTextViewEmptyList.visibility = GONE
        }
    }

    fun refreshDataBaseAdapterHistoryFilter(scrollRefresh: ScrollRefresh) {
        val nameCatalystOrBrandCarInput =
            activityResultBinding.editText.text.toString()
        when (scrollRefresh) {
            ScrollRefresh.RESET_LIST -> {
                //reset variable of scroll
                scrollPreLastHistoryFilter = 0
                scrollLimitHistoryFilter =
                    Configuration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result =
                    database.getDataHistoryFilter(
                        scrollLimitHistoryFilter.toString(),
                        nameCatalystOrBrandCarInput
                    )
                databaseAdapterHistoryFilter.clear()
                databaseAdapterHistoryFilter.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST -> {
                //get data from database
                val result =
                    database.getDataHistoryFilter(
                        scrollLimitHistoryFilter.toString(),
                        nameCatalystOrBrandCarInput
                    )
                databaseAdapterHistoryFilter.clear()
                databaseAdapterHistoryFilter.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                //limit as offset in format: skip elements, count elements to get
                val limitWithOffset: String =
                    (scrollLimitHistoryFilter - Configuration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER).toString() + "," + Configuration.DATABASE_PAGINATE_LIMIT_HISTORY_FILTER
                //get data from database
                val result =
                    database.getDataHistoryFilter(limitWithOffset, nameCatalystOrBrandCarInput)
                //add to list
                databaseAdapterHistoryFilter.addAll(result)
            }
        }
        if (databaseAdapterHistoryFilter.count == 0) {
            activityResultBinding.historyFilterTextViewWaiting.visibility = GONE
            activityResultBinding.historyFilterTextViewEmptyList.visibility = VISIBLE
            activityResultBinding.historyFilterListView.visibility = GONE
        } else {
            activityResultBinding.historyFilterTextViewWaiting.visibility = GONE
            activityResultBinding.historyFilterTextViewEmptyList.visibility = GONE
            activityResultBinding.historyFilterListView.visibility = VISIBLE
        }
    }
    //endregion

    fun deleteRecordHistoryOfSearch(id: Int) {
        Thread(TaskDeleteRecordHistoryFilter(id)).start()
    }

    inner class TaskUpdate : Runnable {
        private var updateCatalyst: Boolean = false
        private var databaseEmpty: Boolean = false

        @Suppress("ReplaceCallWithBinaryOperator")
        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    false
                )
                activityResultBinding.catalystTextViewWaiting.visibility =
                    VISIBLE
                activityResultBinding.catalystTextViewEmptyDatabase.visibility =
                    GONE
                activityResultBinding.catalystListView.visibility = GONE
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                val lastTimestampUpdateCourseFromConfiguration: String =
                    SharedPreference.getKey(SharedPreference.UPDATE_COURSE_TIMESTAMP)
                if (lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (Configuration.ONE_DAY_IN_MILLISECONDS / 4))) {
                    Course.getValues(database)
                }
                val databaseCatalystCount: Int = database.getCountCatalyst()
                databaseEmpty = databaseCatalystCount == 0
                val spreadsheetCatalystCount: Int = Spreadsheet.getCountCatalyst()
                updateCatalyst =
                    databaseCatalystCount == 0 || spreadsheetCatalystCount > databaseCatalystCount
                val user: JSONArray? =
                    Spreadsheet.getDataLogin(SharedPreference.getKey(SharedPreference.LOGIN))
                if (user == null) {
                    processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                if (Checker.checkTimeOnPhone(
                        user.getString(Configuration.SPREADSHEET_USERS_LICENCE),
                        TimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                    ) == false
                ) {
                    processStep = ProcessStep.USER_ELAPSED_DATE_LICENCE
                    throw Exception()
                }
                SharedPreference.setKey(
                    SharedPreference.LICENCE_DATE_OF_END,
                    user.getString(
                        Configuration.SPREADSHEET_USERS_LICENCE
                    )
                )
                SharedPreference.setKey(
                    SharedPreference.DISCOUNT,
                    Parser.parseStringToInt(
                        user.getString(
                            Configuration.SPREADSHEET_USERS_DISCOUNT
                        )
                    ).toString()
                )
                SharedPreference.setKey(
                    SharedPreference.VISIBILITY,
                    Parser.parseStringBooleanToInt(
                        user.getString(
                            Configuration.SPREADSHEET_USERS_VISIBILITY
                        )
                    ).toString()
                )
                SharedPreference.setKey(
                    SharedPreference.MINUS_PLATINUM,
                    Parser.parseStringToInt(
                        user.getString(
                            Configuration.SPREADSHEET_USERS_MINUS_PLATINUM
                        )
                    ).toString()
                )
                SharedPreference.setKey(
                    SharedPreference.MINUS_PALLADIUM,
                    Parser.parseStringToInt(
                        user.getString(
                            Configuration.SPREADSHEET_USERS_MINUS_PALLADIUM
                        )
                    ).toString()
                )
                SharedPreference.setKey(
                    SharedPreference.MINUS_RHODIUM,
                    Parser.parseStringToInt(
                        user.getString(
                            Configuration.SPREADSHEET_USERS_MINUS_RHODIUM
                        )
                    ).toString()
                )
                val workRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<WorkerUpload>().build()
                WorkManager
                    .getInstance(this@ResultActivity.applicationContext)
                    .enqueue(workRequest)
                processStep = ProcessStep.SUCCESS
            } catch (e: Exception) {
                //
            }
            //--- onPostExecute
            runOnUiThread {
                if (processStep == ProcessStep.USER_ELAPSED_DATE_LICENCE) {
                    SharedPreference.setKey(
                        SharedPreference.LICENCE_DATE_OF_END,
                        ""
                    )
                    openMainActivity()
                }
                if (databaseEmpty) {
                    activityResultBinding.catalystTextViewWaiting.visibility =
                        GONE
                    activityResultBinding.catalystTextViewEmptyDatabase.visibility =
                        VISIBLE
                    activityResultBinding.catalystListView.visibility = GONE
                } else {
                    activityResultBinding.catalystTextViewWaiting.visibility =
                        GONE
                    activityResultBinding.catalystTextViewEmptyDatabase.visibility =
                        GONE
                    activityResultBinding.catalystListView.visibility = VISIBLE
                }
                if (menu != null) {
                    if (updateCatalyst) {
                        Dynamic.IS_AVAILABLE_UPDATE = true
                        menu!!.getItem(1).icon = ContextCompat.getDrawable(
                            this@ResultActivity.applicationContext,
                            R.mipmap.ic_action_update_catalyst_color
                        )
                    } else {
                        menu!!.getItem(1).icon = ContextCompat.getDrawable(
                            this@ResultActivity.applicationContext,
                            R.mipmap.ic_action_update_catalyst
                        )
                    }
                    if (Course.isCoursesSelected()) {
                        val actualCoursesDate =
                            SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
                        if (LocalDate.now().toString().equals(actualCoursesDate)) {
                            menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values
                            )
                        } else {
                            menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values_color
                            )
                        }
                    } else {
                        val usdDate =
                            SharedPreference.getKey(SharedPreference.USD_PLN_DATE)
                        val eurDate =
                            SharedPreference.getKey(SharedPreference.EUR_PLN_DATE)
                        val platinumDate =
                            SharedPreference.getKey(SharedPreference.PLATINUM_DATE)
                        val palladiumDate =
                            SharedPreference.getKey(SharedPreference.PALLADIUM_DATE)
                        val rhodiumDate =
                            SharedPreference.getKey(SharedPreference.RHODIUM_DATE)
                        if (LocalDate.now().toString()
                                .equals(usdDate) && usdDate.equals(eurDate) && eurDate.equals(
                                platinumDate
                            ) && platinumDate.equals(
                                palladiumDate
                            ) && palladiumDate.equals(rhodiumDate)
                        ) {
                            menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values
                            )
                        } else {
                            menu!!.getItem(0).icon = ContextCompat.getDrawable(
                                this@ResultActivity.applicationContext,
                                R.mipmap.ic_action_values_color
                            )
                        }
                    }
                }
                refreshDatabaseAdapterCatalysts(ScrollRefresh.UPDATE_LIST)
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    true
                )
            }
        }
    }

    inner class TaskAddRecordHistoryFilter : Runnable {
        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    false
                )
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                var searchedText =
                    SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT)
                searchedText = ("\\s{2,}").toRegex().replace(searchedText.trim(), " ")
                if (searchedText.isEmpty() == false) {
                    database.deleteHistoryFilter(searchedText)
                    database.insertHistoryFilter(searchedText)
                    processStep = ProcessStep.SUCCESS
                }
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.NONE -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            Configuration.HISTORY_FILTER_CANNOT_SAVE_EMPTY,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            Configuration.UNHANDLED_EXCEPTION,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            Configuration.HISTORY_FILTER_ADDED,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        //
                    }
                }
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    true
                )
            }
        }
    }

    inner class TaskDeleteRecordHistoryFilter(idInput: Int) : Runnable {
        private var id: Int = idInput

        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    false
                )
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.SUCCESS
            try {
                database.deleteHistoryFilter(id)
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            Configuration.UNHANDLED_EXCEPTION,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ProcessStep.SUCCESS -> {
                        Toast.makeText(
                            this@ResultActivity.applicationContext,
                            Configuration.HISTORY_FILTER_DELETED,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        //
                    }
                }
                refreshDataBaseAdapterHistoryFilter(ScrollRefresh.UPDATE_LIST)
                UserInterface.changeStatusLayout(
                    activityResultBinding.drawerLayout,
                    true
                )
            }
        }
    }
}
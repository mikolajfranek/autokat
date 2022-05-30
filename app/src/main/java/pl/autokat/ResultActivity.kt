package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import pl.autokat.enums.ProgramMode
import pl.autokat.enums.ScrollRefresh
import pl.autokat.enums.TimeChecking
import pl.autokat.models.ModelCatalyst
import pl.autokat.models.ModelHistoryFilter
import pl.autokat.workers.WorkerCopyData
import pl.autokat.workers.WorkerDownloadThumbnail
import java.time.LocalDate
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var activityResultBinding: ActivityResultBinding
    private lateinit var catalystBinding: CatalystBinding
    private lateinit var historyFilterBinding: HistoryFilterBinding
    private lateinit var database: Database
    private lateinit var menu: Menu
    private lateinit var adapterCatalysts: ArrayAdapter<ModelCatalyst>
    private var scrollPreLastCatalyst: Int = 0
    private val paginateLimitCatalyst: Int = 5
    private var scrollLimitCatalyst: Int = paginateLimitCatalyst
    private lateinit var adapterHistoryFilter: ArrayAdapter<ModelHistoryFilter>
    private val paginateLimitHistoryFilter: Int = 10

    @Volatile
    private var isAvailableUpdateCatalyst: Boolean = false

    //region methods used in override
    private fun init() {
        activityResultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = activityResultBinding.root
        setContentView(view)
        setSupportActionBar(activityResultBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        database = Database(applicationContext)
    }

    private fun deleteHistoryFilter(id: Int) {
        Thread(RunnableDeleteHistoryFilter(id)).start()
    }

    private fun addHistoryFilter() {
        Thread(RunnableAddHistoryFilter()).start()
    }

    private fun setFilterField() {
        activityResultBinding.filter.setText(SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT))
        activityResultBinding.filter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                SharedPreference.setKey(SharedPreference.LAST_SEARCHED_TEXT, s.toString())
                refreshAdapterCatalysts(ScrollRefresh.RESET_LIST)
                refreshAdapterHistoryFilter()
            }
        })
    }

    private fun setCatalystListView() {
        adapterCatalysts =
            object : ArrayAdapter<ModelCatalyst>(applicationContext, R.layout.catalyst) {
                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    catalystBinding = CatalystBinding.inflate(layoutInflater, parent, false)
                    val viewItem = catalystBinding.root
                    val itemCatalyst = getItem(position)!!
                    val visibilityCatalyst: Boolean =
                        SharedPreference.getKey(SharedPreference.VISIBILITY).toInt() == 1
                    catalystBinding.thumbnail.setImageBitmap(itemCatalyst.thumbnail)
                    if (Configuration.PROGRAM_MODE == ProgramMode.COMPANY) {
                        catalystBinding.thumbnail.setOnLongClickListener {
                            Toast.makeText(
                                applicationContext,
                                itemCatalyst.idPicture,
                                Toast.LENGTH_LONG
                            ).show()
                            true
                        }
                    }
                    catalystBinding.thumbnail.setOnClickListener {
                        val intent = Intent(applicationContext, PictureActivity::class.java)
                        intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                        startActivity(intent)
                    }
                    catalystBinding.brand.text = itemCatalyst.brand
                    catalystBinding.type.text = itemCatalyst.type
                    catalystBinding.name.text = itemCatalyst.name
                    val weightText =
                        Formatter.formatStringFloat(itemCatalyst.weight.toString(), 3) + " kg"
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
                    val courseEurPlnFromConfiguration: String = SharedPreference.getKey(
                        SharedPreference.EUR_PLN
                    )
                    val courseEurPln: Float =
                        if (courseEurPlnFromConfiguration.isEmpty()) 0.0F else courseEurPlnFromConfiguration.toFloat()
                    var priceEur = if (courseEurPln != 0.0F) (pricePl / courseEurPln) else 0.0F
                    pricePl = if (pricePl < 0) 0.0F else pricePl
                    priceEur = if (priceEur < 0) 0.0F else priceEur
                    val resultPriceEur: String =
                        (Formatter.formatStringFloat(priceEur.toString(), 2) + " €")
                    val resultPricePln: String =
                        (Formatter.formatStringFloat(pricePl.toString(), 2) + " zł")
                    if (visibilityCatalyst) {
                        catalystBinding.priceEur.text = resultPriceEur
                        catalystBinding.pricePln.text = resultPricePln
                        catalystBinding.rowPlatinum.visibility = VISIBLE
                        catalystBinding.rowPalladium.visibility = VISIBLE
                        catalystBinding.rowRhodium.visibility = VISIBLE
                    } else {
                        catalystBinding.priceEurWithoutMetal.text = resultPriceEur
                        catalystBinding.pricePlnWithoutMetal.text = resultPricePln
                    }
                    return viewItem
                }
            }
        activityResultBinding.catalystListView.adapter = adapterCatalysts
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
                    scrollLimitCatalyst += paginateLimitCatalyst
                    refreshAdapterCatalysts(ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS)
                    scrollPreLastCatalyst = lastItem
                }
            }
        })
    }

    private fun setHistoryFilterListView() {
        adapterHistoryFilter =
            object : ArrayAdapter<ModelHistoryFilter>(applicationContext, R.layout.history_filter) {
                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    historyFilterBinding =
                        HistoryFilterBinding.inflate(layoutInflater, parent, false)
                    val viewItem = historyFilterBinding.root
                    val itemHistoryFilter = getItem(position)
                    historyFilterBinding.name.text = UserInterface.colorText(
                        itemHistoryFilter.name,
                        activityResultBinding.filter.text.toString()
                    )
                    viewItem.setOnClickListener {
                        activityResultBinding.filter.setText(itemHistoryFilter.name)
                        activityResultBinding.filter.dismissDropDown()
                    }
                    historyFilterBinding.buttonDeleteHistoryFilter.setOnClickListener {
                        deleteHistoryFilter(itemHistoryFilter.id)
                    }
                    return viewItem
                }

                var items: ArrayList<ModelHistoryFilter> = ArrayList()
                private val mLock = Any()

                override fun clear() {
                    synchronized(mLock) {
                        items.clear()
                    }
                }

                override fun addAll(collection: MutableCollection<out ModelHistoryFilter>) {
                    synchronized(mLock) {
                        items.addAll(collection)
                    }
                }

                override fun getCount(): Int {
                    return items.size
                }

                override fun getItem(index: Int): ModelHistoryFilter {
                    return items[index]
                }

                override fun getFilter(): Filter {
                    return object : Filter() {
                        override fun performFiltering(constraint: CharSequence?): FilterResults {
                            val filterResults = FilterResults()
                            filterResults.values = items
                            filterResults.count = items.size
                            return filterResults
                        }

                        override fun publishResults(
                            constraint: CharSequence?,
                            results: FilterResults?
                        ) {
                            if (results != null && results.count > 0) {
                                notifyDataSetChanged()
                            } else {
                                notifyDataSetInvalidated()
                            }
                        }
                    }
                }
            }
        activityResultBinding.filter.threshold = 1
        activityResultBinding.filter.setAdapter(adapterHistoryFilter)
    }

    private fun setClickListeners() {
        activityResultBinding.buttonAddHistoryFilter.setOnClickListener {
            addHistoryFilter()
        }
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setFilterField()
        setCatalystListView()
        setHistoryFilterListView()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        if (Checker.checkTimeOnPhone("", TimeChecking.CHECKING_LICENCE) == false) openMainActivity()
        Thread(RunnableWorkBackground()).start()
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
        val intent = Intent(applicationContext, UpdateActivity::class.java)
        intent.putExtra("isAvailableUpdateCatalyst", isAvailableUpdateCatalyst)
        startActivity(intent)
    }

    private fun openAboutActivity() {
        startActivity(Intent(applicationContext, AboutActivity::class.java))
    }
    //endregion

    //region refresh database adapter
    fun refreshAdapterCatalysts(scrollRefresh: ScrollRefresh) {
        val searchedText = SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT)
        when (scrollRefresh) {
            ScrollRefresh.RESET_LIST -> {
                scrollPreLastCatalyst = 0
                scrollLimitCatalyst = paginateLimitCatalyst
                val result = database.getDataCatalyst(searchedText, scrollLimitCatalyst.toString())
                adapterCatalysts.clear()
                adapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST -> {
                val result = database.getDataCatalyst(searchedText, scrollLimitCatalyst.toString())
                adapterCatalysts.clear()
                adapterCatalysts.addAll(result)
            }
            ScrollRefresh.UPDATE_LIST_WITH_NEW_ITEMS -> {
                val skip = (scrollLimitCatalyst - paginateLimitCatalyst).toString()
                val limitWithOffset = "$skip,$paginateLimitCatalyst"
                val result = database.getDataCatalyst(searchedText, limitWithOffset)
                adapterCatalysts.addAll(result)
            }
        }
        if (adapterCatalysts.count == 0 && searchedText.isEmpty() == false) {
            activityResultBinding.catalystEmptyList.visibility = VISIBLE
        } else {
            activityResultBinding.catalystEmptyList.visibility = GONE
        }
    }

    fun refreshAdapterHistoryFilter() {
        val result = database.getDataHistoryFilter(
            paginateLimitHistoryFilter.toString(),
            activityResultBinding.filter.text.toString()
        )
        adapterHistoryFilter.clear()
        adapterHistoryFilter.addAll(result)
    }
    //endregion

    //region inner classes
    inner class RunnableWorkBackground : Runnable {

        private var colorIconUpdateCatalyst: Boolean = false
        private var isTableCatalystEmpty: Boolean = false

        //region methods used in doInBackground
        private fun getCourses() {
            val lastTimestampUpdateCourseFromConfiguration: String =
                SharedPreference.getKey(SharedPreference.UPDATE_COURSE_TIMESTAMP)
            if (lastTimestampUpdateCourseFromConfiguration.isEmpty() || ((Date().time - lastTimestampUpdateCourseFromConfiguration.toLong()) > (Configuration.ONE_DAY_IN_MILLISECONDS / 4))) {
                Course.getValues(database)
            }
        }

        private fun checkCountCatalyst() {
            val databaseCatalystCount: Int = database.getCountCatalyst()
            isTableCatalystEmpty = databaseCatalystCount == 0
            val spreadsheetCatalystCount: Int = Spreadsheet.getCountCatalyst()
            colorIconUpdateCatalyst = spreadsheetCatalystCount > databaseCatalystCount
            isAvailableUpdateCatalyst = colorIconUpdateCatalyst
        }

        private fun updateUserInformation(): ProcessStep {
            val user: JSONArray =
                Spreadsheet.getDataLogin(SharedPreference.getKey(SharedPreference.LOGIN))
                    ?: return ProcessStep.USER_ELAPSED_DATE_LICENCE
            if (Checker.checkTimeOnPhone(
                    user.getString(Configuration.SPREADSHEET_USERS_LICENCE),
                    TimeChecking.PARAMETER_IS_GREATER_THAN_NOW
                ) == false
            ) {
                return ProcessStep.USER_ELAPSED_DATE_LICENCE
            }
            SharedPreference.setKey(
                SharedPreference.LICENCE_DATE_OF_END,
                user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
            )
            SharedPreference.setKey(
                SharedPreference.DISCOUNT,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_DISCOUNT))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.VISIBILITY,
                Parser.parseStringBooleanToInt(user.getString(Configuration.SPREADSHEET_USERS_VISIBILITY))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_PLATINUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_PLATINUM))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_PALLADIUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_PALLADIUM))
                    .toString()
            )
            SharedPreference.setKey(
                SharedPreference.MINUS_RHODIUM,
                Parser.parseStringToInt(user.getString(Configuration.SPREADSHEET_USERS_MINUS_RHODIUM))
                    .toString()
            )
            return ProcessStep.NONE
        }

        private fun runWorkerDownloadThumbnail() {
            if (Configuration.workerDownloadThumbnail.compareAndSet(false, true)) {
                val workRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<WorkerDownloadThumbnail>().build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
        }

        private fun runWorkerCopyData() {
            if (Configuration.PROGRAM_MODE == ProgramMode.COMPANY) {
                if (Configuration.workerCopyData.compareAndSet(false, true)) {
                    val workRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<WorkerCopyData>().build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                }
            }
        }
        //endregion

        //region methods used in onPostExecute
        private fun handleElapsedLicence(processStep: ProcessStep) {
            if (processStep == ProcessStep.USER_ELAPSED_DATE_LICENCE || processStep == ProcessStep.COMPANY_ELAPSED_LICENCE) {
                SharedPreference.setKey(SharedPreference.LICENCE_DATE_OF_END, "")
                openMainActivity()
            }
        }

        private fun setVisibility() {
            if (isTableCatalystEmpty) {
                activityResultBinding.catalystWaiting.visibility = GONE
                activityResultBinding.catalystEmpty.visibility = VISIBLE
                activityResultBinding.catalystListView.visibility = GONE
            } else {
                activityResultBinding.catalystWaiting.visibility = GONE
                activityResultBinding.catalystEmpty.visibility = GONE
                activityResultBinding.catalystListView.visibility = VISIBLE
            }
        }

        private fun setColorIconUpdateCatalyst() {
            if (colorIconUpdateCatalyst) {
                menu.getItem(1).icon = ContextCompat.getDrawable(
                    applicationContext,
                    R.mipmap.ic_action_update_catalyst_color
                )
            } else {
                menu.getItem(1).icon = ContextCompat.getDrawable(
                    applicationContext,
                    R.mipmap.ic_action_update_catalyst
                )
            }
        }

        private fun setColorIconUpdateCourses() {
            if (Course.isCoursesSelected()) {
                val actualCoursesDate =
                    SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
                if (LocalDate.now().toString() == actualCoursesDate) {
                    menu.getItem(0).icon = ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_action_update_courses
                    )
                } else {
                    menu.getItem(0).icon = ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_action_update_courses_color
                    )
                }
            } else {
                val usdDate = SharedPreference.getKey(SharedPreference.USD_PLN_DATE)
                val eurDate = SharedPreference.getKey(SharedPreference.EUR_PLN_DATE)
                val platinumDate = SharedPreference.getKey(SharedPreference.PLATINUM_DATE)
                val palladiumDate = SharedPreference.getKey(SharedPreference.PALLADIUM_DATE)
                val rhodiumDate = SharedPreference.getKey(SharedPreference.RHODIUM_DATE)
                if (LocalDate.now()
                        .toString() == usdDate && usdDate == eurDate && eurDate == platinumDate && platinumDate == palladiumDate && palladiumDate == rhodiumDate
                ) {
                    menu.getItem(0).icon = ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_action_update_courses
                    )
                } else {
                    menu.getItem(0).icon = ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_action_update_courses_color
                    )
                }
            }
        }
        //endregion

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityResultBinding.linearLayout, false)
            activityResultBinding.catalystWaiting.visibility = VISIBLE
            activityResultBinding.catalystEmpty.visibility = GONE
            activityResultBinding.catalystListView.visibility = GONE
        }

        private fun doInBackground(): ProcessStep {
            try {
                if (Spreadsheet.isExpiredLicenceOfCompany(false) == true) {
                    return ProcessStep.COMPANY_ELAPSED_LICENCE
                }
                getCourses()
                checkCountCatalyst()
                val processStep = updateUserInformation()
                if (processStep != ProcessStep.NONE) {
                    return processStep
                }
                runWorkerDownloadThumbnail()
                runWorkerCopyData()
                return ProcessStep.SUCCESS
            } catch (e: Exception) {
                return ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            handleElapsedLicence(processStep)
            setVisibility()
            setColorIconUpdateCatalyst()
            setColorIconUpdateCourses()
            refreshAdapterCatalysts(ScrollRefresh.UPDATE_LIST)
            UserInterface.changeStatusLayout(
                activityResultBinding.linearLayout,
                true
            )
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }

    inner class RunnableAddHistoryFilter : Runnable {

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityResultBinding.linearLayout, false)
        }

        private fun doInBackground(): ProcessStep {
            return try {
                var searchedText = SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT)
                searchedText = ("\\s{2,}").toRegex().replace(searchedText.trim(), " ")
                if (searchedText.isEmpty() == false) {
                    database.deleteHistoryFilter(searchedText)
                    database.insertHistoryFilter(searchedText)
                    ProcessStep.SUCCESS
                } else {
                    ProcessStep.NONE
                }
            } catch (e: Exception) {
                ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.NONE -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.HISTORY_FILTER_CANNOT_SAVE_EMPTY,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UNHANDLED_EXCEPTION,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.HISTORY_FILTER_ADDED,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    //
                }
            }
            UserInterface.changeStatusLayout(activityResultBinding.linearLayout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }

    inner class RunnableDeleteHistoryFilter(idInput: Int) : Runnable {

        private var id: Int = idInput

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityResultBinding.linearLayout, false)
        }

        private fun doInBackground(): ProcessStep {
            return try {
                database.deleteHistoryFilter(id)
                ProcessStep.SUCCESS
            } catch (e: Exception) {
                ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UNHANDLED_EXCEPTION,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.HISTORY_FILTER_DELETED,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    //
                }
            }
            activityResultBinding.filter.setText(activityResultBinding.filter.text.toString())
            UserInterface.changeStatusLayout(activityResultBinding.linearLayout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }
    //endregion
}
package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.components.Formatter
import pl.autokat.databinding.CatalystBinding
import pl.autokat.databinding.FragmentResultsBinding
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

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val fragmentResultBinding get() = _binding!!
    private lateinit var catalystBinding: CatalystBinding
    private lateinit var historyFilterBinding: HistoryFilterBinding
    private lateinit var database: Database
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
        SharedPreference.init(requireActivity().applicationContext)
        database = Database(requireActivity().applicationContext)
    }

    private fun deleteHistoryFilter(id: Int) {
        Thread(RunnableDeleteHistoryFilter(id)).start()
    }

    private fun addHistoryFilter() {
        Thread(RunnableAddHistoryFilter()).start()
    }

    private fun setFilterField() {
        fragmentResultBinding.filter.setText(SharedPreference.getKey(SharedPreference.LAST_SEARCHED_TEXT))
        fragmentResultBinding.filter.addTextChangedListener(object : TextWatcher {
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
            object : ArrayAdapter<ModelCatalyst>(
                requireActivity().applicationContext,
                R.layout.catalyst
            ) {
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
                                requireActivity().applicationContext,
                                itemCatalyst.idPicture,
                                Toast.LENGTH_LONG
                            ).show()
                            true
                        }
                    }
                    catalystBinding.thumbnail.setOnClickListener {
                        val intent = Intent(
                            requireActivity().applicationContext,
                            PictureActivity::class.java
                        )
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
                    pricePl = if (pricePl < 0) 0.0F else pricePl
                    val courseEurPlnFromConfiguration: String = SharedPreference.getKey(
                        SharedPreference.EUR_PLN
                    )
                    val courseEurPln: Float =
                        if (courseEurPlnFromConfiguration.isEmpty()) 0.0F else courseEurPlnFromConfiguration.toFloat()
                    var priceEur = if (courseEurPln != 0.0F) (pricePl / courseEurPln) else 0.0F
                    priceEur = if (priceEur < 0) 0.0F else priceEur
                    val courseUsdPlnFromConfiguration: String = SharedPreference.getKey(
                        SharedPreference.USD_PLN
                    )
                    val courseUsdPln: Float =
                        if (courseUsdPlnFromConfiguration.isEmpty()) 0.0F else courseUsdPlnFromConfiguration.toFloat()
                    var priceUsd = if (courseUsdPln != 0.0F) (pricePl / courseUsdPln) else 0.0F
                    priceUsd = if (priceUsd < 0) 0.0F else priceUsd
                    val resultPriceUsd: String =
                        (Formatter.formatStringFloat(priceUsd.toString(), 2) + " $")
                    val resultPriceEur: String =
                        (Formatter.formatStringFloat(priceEur.toString(), 2) + " €")
                    val resultPricePln: String =
                        (Formatter.formatStringFloat(pricePl.toString(), 2) + " zł")
                    if (visibilityCatalyst) {
                        catalystBinding.priceUsd.text = resultPriceUsd
                        catalystBinding.priceEur.text = resultPriceEur
                        catalystBinding.pricePln.text = resultPricePln
                        catalystBinding.rowPlatinum.visibility = View.VISIBLE
                        catalystBinding.rowPalladium.visibility = View.VISIBLE
                        catalystBinding.rowRhodium.visibility = View.VISIBLE
                    } else {
                        catalystBinding.priceUsdWithoutMetal.text = resultPriceUsd
                        catalystBinding.priceEurWithoutMetal.text = resultPriceEur
                        catalystBinding.pricePlnWithoutMetal.text = resultPricePln
                    }
                    return viewItem
                }
            }
        fragmentResultBinding.catalystListView.adapter = adapterCatalysts
        fragmentResultBinding.catalystListView.setOnScrollListener(object :
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
            object : ArrayAdapter<ModelHistoryFilter>(
                requireActivity().applicationContext,
                R.layout.history_filter
            ) {
                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    historyFilterBinding =
                        HistoryFilterBinding.inflate(layoutInflater, parent, false)
                    val viewItem = historyFilterBinding.root
                    val itemHistoryFilter = getItem(position)
                    historyFilterBinding.name.text = UserInterface.colorText(
                        itemHistoryFilter.name,
                        fragmentResultBinding.filter.text.toString()
                    )
                    viewItem.setOnClickListener {
                        fragmentResultBinding.filter.setText(itemHistoryFilter.name)
                        fragmentResultBinding.filter.dismissDropDown()
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
        fragmentResultBinding.filter.threshold = 1
        fragmentResultBinding.filter.setAdapter(adapterHistoryFilter)
    }

    private fun setClickListeners() {
        fragmentResultBinding.buttonAddHistoryFilter.setOnClickListener {
            addHistoryFilter()
        }
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        setFilterField()
        setCatalystListView()
        setHistoryFilterListView()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        if (Checker.checkTimeOnPhone(TimeChecking.CHECKING_LICENCE) == false) {
            openMainActivity()
        } else {
            Thread(RunnableWorkBackground()).start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return fragmentResultBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //TODO
    //region open activities
    private fun openMainActivity() {
        startActivity(Intent(requireActivity().applicationContext, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun openConfigurationValuesActivity() {
        startActivity(Intent(requireActivity().applicationContext, CoursesActivity::class.java))
    }

    private fun openUpdateActivity() {
        val intent = Intent(requireActivity().applicationContext, UpdateActivity::class.java)
        intent.putExtra("isAvailableUpdateCatalyst", isAvailableUpdateCatalyst)
        startActivity(intent)
    }

    private fun openAboutActivity() {
        startActivity(Intent(requireActivity().applicationContext, AboutActivity::class.java))
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
            fragmentResultBinding.catalystEmptyList.visibility = View.VISIBLE
        } else {
            fragmentResultBinding.catalystEmptyList.visibility = View.GONE
        }
    }

    fun refreshAdapterHistoryFilter() {
        val result = database.getDataHistoryFilter(
            paginateLimitHistoryFilter.toString(),
            fragmentResultBinding.filter.text.toString()
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
            if (Checker.checkTimeIsGreaterThanNow(
                    user.getString(Configuration.SPREADSHEET_USERS_LICENCE)
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
                WorkManager.getInstance(requireActivity().applicationContext).enqueue(workRequest)
            }
        }

        private fun runWorkerCopyData() {
            if (Configuration.PROGRAM_MODE == ProgramMode.COMPANY) {
                if (Configuration.workerCopyData.compareAndSet(false, true)) {
                    val workRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<WorkerCopyData>().build()
                    WorkManager.getInstance(requireActivity().applicationContext)
                        .enqueue(workRequest)
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
                fragmentResultBinding.catalystWaiting.visibility = View.GONE
                fragmentResultBinding.catalystEmpty.visibility = View.VISIBLE
                fragmentResultBinding.catalystListView.visibility = View.GONE
            } else {
                fragmentResultBinding.catalystWaiting.visibility = View.GONE
                fragmentResultBinding.catalystEmpty.visibility = View.GONE
                fragmentResultBinding.catalystListView.visibility = View.VISIBLE
            }
        }

        private fun setColorIconUpdateCatalyst() {
            if (colorIconUpdateCatalyst) {
                (activity as BottomNavigationActivity).badgeOn(R.id.bottom_menu_update)
            } else {
                (activity as BottomNavigationActivity).badgeOff(R.id.bottom_menu_update)
            }
        }

        private fun setColorIconUpdateCourses() {
            if (Course.isCoursesSelected()) {
                val actualCoursesDate =
                    SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
                if (LocalDate.now().toString() == actualCoursesDate) {
                    (activity as BottomNavigationActivity).badgeOff(R.id.bottom_menu_courses)
                } else {
                    (activity as BottomNavigationActivity).badgeOn(R.id.bottom_menu_courses)
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
                    (activity as BottomNavigationActivity).badgeOff(R.id.bottom_menu_courses)
                } else {
                    (activity as BottomNavigationActivity).badgeOn(R.id.bottom_menu_courses)
                }
            }
        }
        //endregion

        //region methods of run
        private fun onPreExecute() {
            (activity as BottomNavigationActivity).layoutOff()
            fragmentResultBinding.catalystWaiting.visibility = View.VISIBLE
            fragmentResultBinding.catalystEmpty.visibility = View.GONE
            fragmentResultBinding.catalystListView.visibility = View.GONE
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
            (activity as BottomNavigationActivity).layoutOn()
        }
        //endregion

        override fun run() {
            requireActivity().runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            requireActivity().runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }

    inner class RunnableAddHistoryFilter : Runnable {

        //region methods of run
        private fun onPreExecute() {
            (activity as BottomNavigationActivity).layoutOff()
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
                        requireActivity().applicationContext,
                        Configuration.HISTORY_FILTER_CANNOT_SAVE_EMPTY,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        Configuration.UNHANDLED_EXCEPTION,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        Configuration.HISTORY_FILTER_ADDED,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    //
                }
            }
            (activity as BottomNavigationActivity).layoutOn()
        }
        //endregion

        override fun run() {
            requireActivity().runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            requireActivity().runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }

    inner class RunnableDeleteHistoryFilter(idInput: Int) : Runnable {

        private var id: Int = idInput

        //region methods of run
        private fun onPreExecute() {
            (activity as BottomNavigationActivity).layoutOff()
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
                        requireActivity().applicationContext,
                        Configuration.UNHANDLED_EXCEPTION,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        Configuration.HISTORY_FILTER_DELETED,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    //
                }
            }
            fragmentResultBinding.filter.setText(fragmentResultBinding.filter.text.toString())
            (activity as BottomNavigationActivity).layoutOn()
        }
        //endregion

        override fun run() {
            requireActivity().runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            requireActivity().runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }
    //endregion
}
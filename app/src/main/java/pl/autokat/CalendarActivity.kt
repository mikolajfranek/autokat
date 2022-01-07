package pl.autokat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import pl.autokat.components.*
import pl.autokat.databinding.ActivityCalendarBinding
import pl.autokat.databinding.CalendarDayBinding
import pl.autokat.databinding.CalendarHeaderBinding
import pl.autokat.models.ModelCourse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarActivity : AppCompatActivity() {

    private lateinit var activityCalendarBinding: ActivityCalendarBinding
    private lateinit var database: Database
    private lateinit var menuItemCheck: MenuItem
    private var selectedDate: LocalDate? = null
    private var mapCoursesOfYearMonths = HashMap<String, HashMap<String, ModelCourse>>()

    //region methods used in override
    private fun chooseDay(): Boolean {
        selectedDate ?: return false
        val keyYearMonth = selectedDate!!.yearMonth.toString()
        val keyDate = selectedDate.toString().let { it1 -> Formatter.formatStringDate(it1) }
        val courses = mapCoursesOfYearMonths[keyYearMonth]!![keyDate]
        if (courses != null) {
            Course.saveSelectedCourses(courses)
            Toast.makeText(
                this@CalendarActivity.applicationContext,
                "Wybrano $keyDate",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@CalendarActivity.applicationContext,
                Configuration.UNHANDLED_EXCEPTION,
                Toast.LENGTH_SHORT
            ).show()
        }
        finish()
        return true
    }

    private fun init() {
        activityCalendarBinding = ActivityCalendarBinding.inflate(layoutInflater)
        val view = activityCalendarBinding.root
        setContentView(view)
        setSupportActionBar(activityCalendarBinding.toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        database = Database(applicationContext)
    }

    private fun initCalendarDayBinder() {
        activityCalendarBinding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visibility = View.VISIBLE
                    when (day.date) {
                        selectedDate -> {
                            textView.setTextColor(Configuration.COLOR_WHITE)
                            textView.setBackgroundResource(R.drawable.selected_day)
                        }
                        else -> {
                            textView.setTextColor(Configuration.COLOR_SUCCESS)
                            val keyYearMonth = day.date.yearMonth.toString()
                            if (mapCoursesOfYearMonths.contains(keyYearMonth)) {
                                val keyDate = Formatter.formatStringDate(day.date.toString())
                                if (mapCoursesOfYearMonths[keyYearMonth]!!.contains(keyDate)) {
                                    textView.setBackgroundResource(R.drawable.course_day)
                                } else {
                                    textView.background = null
                                }
                            } else {
                                textView.background = null
                            }
                            if (day.date == LocalDate.now()) {
                                textView.setTextColor(Configuration.COLOR_FAILED)
                            }
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initCalendarMonthHeaderBinder() {
        activityCalendarBinding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                private val mapOfMonthNames = mapOf(
                    1 to "Styczeń",
                    2 to "Luty",
                    3 to "Marzec",
                    4 to "Kwiecień",
                    5 to "Maj",
                    6 to "Czerwiec",
                    7 to "Lipiec",
                    8 to "Sierpień",
                    9 to "Wrzesień",
                    10 to "Październik",
                    11 to "Listopad",
                    12 to "Grudzień"
                )

                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthName = "${mapOfMonthNames[month.yearMonth.month.value]} ${month.year}"
                    container.textView.text = monthName
                }
            }
    }

    private fun setupCalendar() {
        val featureStart = LocalDate.parse("01-10-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        activityCalendarBinding.calendarView.setup(
            featureStart.yearMonth,
            YearMonth.now(),
            DayOfWeek.MONDAY
        )
        val actualCoursesDate =
            SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
        if (actualCoursesDate.isEmpty()) {
            activityCalendarBinding.calendarView.scrollToDate(LocalDate.now())
        } else {
            val date = Formatter.formatStringDate(actualCoursesDate)
            val localDate = Parser.parseStringDateToLocalDate(date)
            activityCalendarBinding.calendarView.scrollToDate(localDate)
        }
        activityCalendarBinding.calendarView.scrollMode = ScrollMode.PAGED
    }

    private fun setMonthScrollListener() {
        activityCalendarBinding.calendarView.monthScrollListener = { actualMonth ->
            val setOfYearMonth = hashSetOf<String>()
            setOfYearMonth.add(actualMonth.yearMonth.toString())
            val firstMonth = activityCalendarBinding.calendarView.findFirstVisibleMonth()
            if (firstMonth != null) {
                setOfYearMonth.add(firstMonth.yearMonth.toString())
            }
            val lastMonth = activityCalendarBinding.calendarView.findLastVisibleMonth()
            if (lastMonth != null) {
                setOfYearMonth.add(lastMonth.yearMonth.toString())
            }
            mapCoursesOfYearMonths = database.getCoursesOfYearMonths(setOfYearMonth)
            for ((_, hashMap) in mapCoursesOfYearMonths) {
                for ((keyDate, _) in hashMap) {
                    val localDate = Parser.parseStringDateToLocalDate(keyDate)
                    activityCalendarBinding.calendarView.notifyDateChanged(localDate)
                }
            }
        }
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initCalendarDayBinder()
        initCalendarMonthHeaderBinder()
        setupCalendar()
        setMonthScrollListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.calendar, menu)
        menuItemCheck = menu.getItem(0)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_list_calendar_choose -> {
                return chooseDay()
            }
            else -> {
                finish()
                true
            }
        }
    }
    //endregion

    //region inner classes
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val textView = CalendarDayBinding.bind(view).textView

        private fun resetView() {
            menuItemCheck.isVisible = false
            activityCalendarBinding.footer.visibility = View.GONE
            activityCalendarBinding.footerActualDate.text = ""
            activityCalendarBinding.footerActualPlatinum.text = ""
            activityCalendarBinding.footerActualPalladium.text = ""
            activityCalendarBinding.footerActualRhodium.text = ""
            activityCalendarBinding.footerActualEurPln.text = ""
            activityCalendarBinding.footerActualUsdPln.text = ""
        }

        private fun handleChangeDay() {
            if (selectedDate == day.date) {
                selectedDate = null
                activityCalendarBinding.calendarView.notifyDayChanged(day)
            } else {
                val oldDate = selectedDate
                selectedDate = day.date
                activityCalendarBinding.calendarView.notifyDateChanged(day.date)
                oldDate?.let {
                    activityCalendarBinding.calendarView.notifyDateChanged(
                        oldDate
                    )
                }
            }
        }

        private fun markSelectedDate() {
            val keyYearMonth = selectedDate?.yearMonth.toString()
            val keyDate = selectedDate?.toString()?.let { it1 -> Formatter.formatStringDate(it1) }
            if (mapCoursesOfYearMonths.contains(keyYearMonth)) {
                menuItemCheck.isVisible =
                    mapCoursesOfYearMonths[keyYearMonth]!!.contains(keyDate)
                if (menuItemCheck.isVisible) {
                    val courses = mapCoursesOfYearMonths[keyYearMonth]!![keyDate]
                    setView(courses)
                }
            }
        }

        private fun setView(courses: ModelCourse?) {
            if (courses != null) {
                activityCalendarBinding.footer.visibility =
                    View.VISIBLE
                val text =
                    "Z dnia ${Formatter.formatStringDate(day.date.toString())}"
                activityCalendarBinding.footerActualDate.text = text
                val platinum = Course.calculateCoursesToPln(
                    courses.platinum,
                    courses.usdPln
                )
                val platinumText =
                    (Formatter.formatStringFloat(platinum, 2) + " zł/g")
                activityCalendarBinding.footerActualPlatinum.text =
                    platinumText
                val palladium = Course.calculateCoursesToPln(
                    courses.palladium,
                    courses.usdPln
                )
                val palladiumText =
                    (Formatter.formatStringFloat(palladium, 2) + " zł/g")
                activityCalendarBinding.footerActualPalladium.text =
                    palladiumText
                val rhodium =
                    Course.calculateCoursesToPln(courses.rhodium, courses.usdPln)
                val rhodiumText =
                    (Formatter.formatStringFloat(rhodium, 2) + " zł/g")
                activityCalendarBinding.footerActualRhodium.text =
                    rhodiumText
                val eurText =
                    (Formatter.formatStringFloat(courses.eurPln, 2) + " zł")
                activityCalendarBinding.footerActualEurPln.text = eurText
                val usdText =
                    (Formatter.formatStringFloat(courses.usdPln, 2) + " zł")
                activityCalendarBinding.footerActualUsdPln.text = usdText
            } else {
                menuItemCheck.isVisible = false
            }
        }

        init {
            textView.setOnClickListener {
                resetView()
                if (day.owner == DayOwner.THIS_MONTH) {
                    handleChangeDay()
                    markSelectedDate()
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarHeaderBinding.bind(view).textView
    }
    //endregion
}
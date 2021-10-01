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
import pl.autokat.databinding.ActivityCalendarViewBinding
import pl.autokat.databinding.CalendarDayBinding
import pl.autokat.databinding.CalendarHeaderBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewActivity : AppCompatActivity() {

    private lateinit var bindingActivityCalendarView : ActivityCalendarViewBinding
    private lateinit var menuItemCalendarCheck: MenuItem
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val mapOfMonthNames = mapOf(1 to "Styczeń", 2 to "Luty", 3 to "Marzec", 4 to "Kwiecień", 5 to "Maj", 6 to "Czerwiec", 7 to "Lipiec", 8 to "Sierpień", 9 to "Wrzesień", 10 to "Październik", 11 to "Listopad", 12 to "Grudzień")
    private val featureStart = LocalDate.parse("01-01-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    private var mapDaysCoursesOfYearMonth = HashMap<String, HashMap<String, MyCourses>>()
    private lateinit var myDatabase: MyDatabase

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityCalendarView = ActivityCalendarViewBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityCalendarView.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityCalendarView.toolbar as Toolbar?)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //init database object
        this.myDatabase = MyDatabase(this.applicationContext)
        //calendar day binder
        bindingActivityCalendarView.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visibility = View.VISIBLE
                    when (day.date) {
                        selectedDate -> {
                            textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_WHITE)
                            textView.setBackgroundResource(R.drawable.drawable_selectday_background)
                        }
                        else -> {
                            textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                            val keyYearMonth = day.date.yearMonth.toString()
                            if(mapDaysCoursesOfYearMonth.contains(keyYearMonth)){
                                val keyDate = MyConfiguration.formatDate(day.date.toString())
                                if(mapDaysCoursesOfYearMonth[keyYearMonth]!!.contains(keyDate)){
                                    textView.setBackgroundResource(R.drawable.drawable_selectedday_background)
                                }else{
                                    textView.background = null
                                }
                            }else{
                                if(day.date == today){
                                    textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                                }
                                textView.background = null
                            }
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }
        }
        //calendar month binder
        bindingActivityCalendarView.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthName = "${mapOfMonthNames[month.yearMonth.month.value]} ${month.year}"
                container.textView.text = monthName
            }
        }
        //calendar setup
        bindingActivityCalendarView.calendarView.setup(featureStart.yearMonth, YearMonth.now(), DayOfWeek.MONDAY)
        val actualCoursesDate = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_ACTUAL_COURSES_DATE)
        if(actualCoursesDate.isEmpty()){
            bindingActivityCalendarView.calendarView.scrollToDate(today)
        }else{
            val localDate = MyConfiguration.formatDateToLocalDate(actualCoursesDate)
            bindingActivityCalendarView.calendarView.scrollToDate(localDate)
        }
        //calendar listeners
        bindingActivityCalendarView.calendarView.scrollMode = ScrollMode.PAGED
        bindingActivityCalendarView.calendarView.monthScrollListener = {actualMonth ->
            val setOfYearMonth = hashSetOf<String>()
            setOfYearMonth.add(actualMonth.yearMonth.toString())
            val firstMonth = bindingActivityCalendarView.calendarView.findFirstVisibleMonth()
            if (firstMonth != null) {
                setOfYearMonth.add(firstMonth.yearMonth.toString())
            }
            val lastMonth = bindingActivityCalendarView.calendarView.findLastVisibleMonth()
            if (lastMonth != null) {
                setOfYearMonth.add(lastMonth.yearMonth.toString())
            }
            mapDaysCoursesOfYearMonth = this.myDatabase.getCoursesOfYearMonths(setOfYearMonth)
            for ((_, hashMap) in mapDaysCoursesOfYearMonth) {
                for((keyDate, _) in hashMap){
                    val localDate = MyConfiguration.formatDateToLocalDate(keyDate)
                    bindingActivityCalendarView.calendarView.notifyDateChanged(localDate)
                }
            }
        }
    }
    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.toolbar_list_calendar, menu)
        menuItemCalendarCheck = menu.getItem(0)
        return super.onCreateOptionsMenu(menu)
    }
    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.toolbar_list_calendar_check -> {
                selectedDate ?: return false
                val keyYearMonth = selectedDate!!.yearMonth.toString()
                val keyDate = selectedDate.toString().let { it1 -> MyConfiguration.formatDate(it1) }
                val courses = mapDaysCoursesOfYearMonth[keyYearMonth]!![keyDate]
                if(courses != null){
                    //saving courses values
                    MyCoursesValues.saveSelectedCourses(courses)
                    Toast.makeText(this@CalendarViewActivity.applicationContext, "Wybrano: $keyDate", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@CalendarViewActivity.applicationContext, MyConfiguration.INFO_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_SHORT).show()
                }
                this.finish()
                true
            }
            else -> {
                this.finish()
                true
            }
        }
    }
    //containers
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val textView = CalendarDayBinding.bind(view).calendarDayTextView
        init {
            textView.setOnClickListener {
                //reset actual values
                bindingActivityCalendarView.calendarViewCourses.visibility = View.GONE
                bindingActivityCalendarView.calendarViewActualDate.text = ""
                bindingActivityCalendarView.calendarViewActualPlatinum.text = ""
                bindingActivityCalendarView.calendarViewActualPalladium.text = ""
                bindingActivityCalendarView.calendarViewActualRhodium.text = ""
                bindingActivityCalendarView.calendarViewActualEurPln.text = ""
                bindingActivityCalendarView.calendarViewActualUsdPln.text = ""
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (selectedDate == day.date) {
                        selectedDate = null
                        bindingActivityCalendarView.calendarView.notifyDayChanged(day)
                    } else {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        bindingActivityCalendarView.calendarView.notifyDateChanged(day.date)
                        oldDate?.let { bindingActivityCalendarView.calendarView.notifyDateChanged(oldDate) }
                    }
                    val keyYearMonth = selectedDate?.yearMonth.toString()
                    val keyDate = selectedDate?.toString()?.let { it1 -> MyConfiguration.formatDate(it1) }
                    if(mapDaysCoursesOfYearMonth.contains(keyYearMonth)){
                        menuItemCalendarCheck.isVisible = mapDaysCoursesOfYearMonth[keyYearMonth]!!.contains(keyDate)
                        if(menuItemCalendarCheck.isVisible){
                            val courses = mapDaysCoursesOfYearMonth[keyYearMonth]!![keyDate]
                            if (courses != null) {
                                bindingActivityCalendarView.calendarViewCourses.visibility = View.VISIBLE
                                val text = "Z dnia ${MyConfiguration.formatDate(day.date.toString())}"
                                bindingActivityCalendarView.calendarViewActualDate.text = text
                                val platinum = MyConfiguration.getPlnFromDolar(courses.platinum, courses.usdPln)
                                val platinumText = (MyConfiguration.formatStringFloat(platinum, 2) + " zł/g")
                                bindingActivityCalendarView.calendarViewActualPlatinum.text = platinumText
                                val palladium = MyConfiguration.getPlnFromDolar(courses.palladium, courses.usdPln)
                                val palladiumText = (MyConfiguration.formatStringFloat(palladium, 2) + " zł/g")
                                bindingActivityCalendarView.calendarViewActualPalladium.text = palladiumText
                                val rhodium = MyConfiguration.getPlnFromDolar(courses.rhodium, courses.usdPln)
                                val rhodiumText = (MyConfiguration.formatStringFloat(rhodium, 2) + " zł/g")
                                bindingActivityCalendarView.calendarViewActualRhodium.text = rhodiumText
                                val eurText = (MyConfiguration.formatStringFloat(courses.eurPln, 2) + " zł")
                                bindingActivityCalendarView.calendarViewActualEurPln.text = eurText
                                val usdText = (MyConfiguration.formatStringFloat(courses.usdPln, 2) + " zł")
                                bindingActivityCalendarView.calendarViewActualUsdPln.text = usdText
                            }
                        }
                    } else {
                        menuItemCalendarCheck.isVisible = false
                    }
                }
            }
        }
    }
    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarHeaderBinding.bind(view).calendarHeaderTextView
    }
}
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
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MyDatabase
import pl.autokat.components.MySharedPreferences
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
    private val mapDaysCoursesOfYearMonth = hashMapOf<String, MutableSet<String>>()
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

        //this.myDatabase.insertCourses()
        val zzz = this.myDatabase.getCourses(LocalDate.now())

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
                        today -> {
                            textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                            textView.background = null
                        }
                        else -> {
                            textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                            val key = day.date.yearMonth.toString()
                            if(mapDaysCoursesOfYearMonth.contains(key)){
                                val keyDay = day.date.dayOfMonth.toString()
                                if(mapDaysCoursesOfYearMonth[key]!!.contains(keyDay)){
                                    textView.setBackgroundResource(R.drawable.drawable_selectedday_background)
                                }else{
                                    textView.background = null
                                }
                            }else{
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
        //TODO today lub data pobrana z konfiguracji (ostatnia wybrana data tzn. data kursów)
        bindingActivityCalendarView.calendarView.scrollToDate(today)

//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------





        //calendar listeners
        bindingActivityCalendarView.calendarView.scrollMode = ScrollMode.PAGED
        bindingActivityCalendarView.calendarView.monthScrollListener = {actualMonth ->

            val firstMonth = bindingActivityCalendarView.calendarView.findFirstVisibleMonth()
            val lastMonth = bindingActivityCalendarView.calendarView.findLastVisibleMonth()

            val title = "=> ${mapOfMonthNames[firstMonth?.month]}, ${mapOfMonthNames[actualMonth.month]}, ${mapOfMonthNames[lastMonth?.month]}"
            Toast.makeText(this@CalendarViewActivity.applicationContext, title, Toast.LENGTH_LONG).show()


            val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")
            val localDate = LocalDate.parse("5-9-2021", formatter)
            val month = "2021-09"
            if(mapDaysCoursesOfYearMonth.contains(month)){
                mapDaysCoursesOfYearMonth.get(month)!!.add("5")
            }else{
                mapDaysCoursesOfYearMonth.put(month, mutableSetOf())
                mapDaysCoursesOfYearMonth.get(month)!!.add("5")
            }
            bindingActivityCalendarView.calendarView.notifyDateChanged(localDate)


            val localDate2 = LocalDate.parse("10-9-2021", formatter)
            if(mapDaysCoursesOfYearMonth.contains(month)){
                mapDaysCoursesOfYearMonth.get(month)!!.add("10")
            }else{
                mapDaysCoursesOfYearMonth.put(month, mutableSetOf())
            }
            bindingActivityCalendarView.calendarView.notifyDateChanged(localDate2)


            val localDate3 = LocalDate.parse("31-8-2021", formatter)
            if(mapDaysCoursesOfYearMonth.contains("2021-08")){
                mapDaysCoursesOfYearMonth.get("2021-08")!!.add("31")
            }else{
                mapDaysCoursesOfYearMonth.put("2021-08", mutableSetOf())
                mapDaysCoursesOfYearMonth.get("2021-08")!!.add("31")
            }
            bindingActivityCalendarView.calendarView.notifyDateChanged(localDate3)

/*
            // Select the first day of the month when
            // we scroll to a new month.
            first?.yearMonth?.let {
                val key = it.month.value.toString().plus("-").plus(it.year.toString())
                if(setOfMonthYear.contains(key) == false){
                    setOfMonthYear.add(key)
                    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")
                    val localDate = LocalDate.parse("1-".plus(key), formatter)
                    selectDate(localDate)
                }
            }
*/


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
                val date = selectedDate ?: return false

                //TODO
                val text = "Selected: ${DateTimeFormatter.ofPattern("d MMMM yyyy").format(date)}"
                Toast.makeText(this@CalendarViewActivity.applicationContext, text, Toast.LENGTH_LONG).show()


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
                    val key = selectedDate?.yearMonth.toString()
                    val keyDay = selectedDate?.dayOfMonth.toString()
                    if(mapDaysCoursesOfYearMonth.contains(key)){
                        menuItemCalendarCheck.isVisible = mapDaysCoursesOfYearMonth.get(key)!!.contains(keyDay)
                        if(menuItemCalendarCheck.isVisible){
                            bindingActivityCalendarView.calendarViewCourses.visibility = View.VISIBLE
                            //TODO wypisać użytkownikowi ceny kursów z tego dnia
                            bindingActivityCalendarView.calendarViewActualDate.text = "Z dnia ".plus(MyConfiguration.formatDate(day.date.toString()))
                            bindingActivityCalendarView.calendarViewActualPlatinum.text = "1"
                            bindingActivityCalendarView.calendarViewActualPalladium.text = "2"
                            bindingActivityCalendarView.calendarViewActualRhodium.text = "3"
                            bindingActivityCalendarView.calendarViewActualEurPln.text = "4"
                            bindingActivityCalendarView.calendarViewActualUsdPln.text = "5"
                        }
                    }else{
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
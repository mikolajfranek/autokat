package pl.autokat

import android.app.usage.UsageEvents
import android.media.metrics.Event
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MySharedPreferences
import pl.autokat.databinding.ActivityCalendarViewBinding
import pl.autokat.databinding.CalendarDayBinding
import pl.autokat.databinding.CalendarEventItemViewBinding
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
                            textView.background = null
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
        bindingActivityCalendarView.calendarView.setup(YearMonth.now().minusYears(100), YearMonth.now(), DayOfWeek.MONDAY)
        bindingActivityCalendarView.calendarView.scrollToDate(today)

//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------





        /*
        TODO
        - zaznaczyć pierwszą datę z miesiąca projekt Example3!

         */



        //calendar listeners
        bindingActivityCalendarView.calendarView.monthScrollListener = {month ->

            val first = bindingActivityCalendarView.calendarView.findFirstVisibleMonth()
            val last = bindingActivityCalendarView.calendarView.findLastVisibleMonth()

            val title = "=> ${mapOfMonthNames[first?.month]}$, ${mapOfMonthNames[month.month]}$, ${mapOfMonthNames[last?.month]}$"
            Toast.makeText(this@CalendarViewActivity.applicationContext, title, Toast.LENGTH_LONG).show()




            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(month.yearMonth.atDay(1))



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
                val text = "Selected: ${DateTimeFormatter.ofPattern("d MMMM yyyy").format(date)}"
                Toast.makeText(this@CalendarViewActivity.applicationContext, text, Toast.LENGTH_LONG).show()
                //TODO
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
                    menuItemCalendarCheck.isVisible = selectedDate != null
                }
            }
        }
    }
    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarHeaderBinding.bind(view).calendarHeaderTextView
    }


//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------

















    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { bindingActivityCalendarView.calendarView.notifyDateChanged(it) }
            bindingActivityCalendarView.calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }



    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.example_3_dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }


    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(events[date].orEmpty())
            notifyDataSetChanged()
        }
    }







    inner class Example3EventsAdapter(val onClick: (UsageEvents.Event) -> Unit) : RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {
        val events = mutableListOf<UsageEvents.Event>()
        override fun getItemCount(): Int = events.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
            return Example3EventsViewHolder(
                CalendarEventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
            )
        }
        override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
            viewHolder.bind(events[position])
        }



        inner class Example3EventsViewHolder(private val binding: CalendarEventItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    onClick(events[bindingAdapterPosition])
                }
            }
            fun bind(event: Event) {
                binding.itemEventText.text = event.text
            }
        }
    }










}
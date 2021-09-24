package pl.autokat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import pl.autokat.components.MySharedPreferences
import pl.autokat.databinding.ActivityCalendarViewBinding
import pl.autokat.databinding.ActivityConfigurationValuesBinding
import pl.autokat.databinding.CalendarDayLayoutBinding
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class CalendarViewActivity : AppCompatActivity() {

    private lateinit var bindingActivityCalendarView : ActivityCalendarViewBinding

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



        //TODO - wybrany Example2Fragment.kt, robić!

        bindingActivityCalendarView.calendarView.dayBinder = object : DayBinder<MyDayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = MyDayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: MyDayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                if(day.day == 2){
                    container.textView.setBackgroundColor(Color.RED)
                }

                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(Color.WHITE)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = DayOfWeek.MONDAY
        bindingActivityCalendarView.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        bindingActivityCalendarView.calendarView.monthFooterResource
        bindingActivityCalendarView.calendarView.scrollToMonth(currentMonth)
    }

    inner class MyDayViewContainer (view: View) : ViewContainer(view) {
        val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

    }
}
package pl.autokat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import pl.autokat.databinding.ActivityBottomNavigationBinding


class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var activityBottomNavigationBinding: ActivityBottomNavigationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBottomNavigationBinding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(activityBottomNavigationBinding.root)


        activityBottomNavigationBinding.bottomNavigation.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null


            when (it.itemId) {
                R.id.bottom_menu_result -> {
                    selectedFragment = ResultFragment()
                }
                R.id.bottom_menu_courses -> {
                    selectedFragment = CoursesFragment()

                }
                R.id.bottom_menu_catalyst -> {
                    selectedFragment = UpdateFragment()
                }
            }


            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .commit()
            return@setOnItemSelectedListener true
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ResultFragment()).commit()


        val badge = activityBottomNavigationBinding.bottomNavigation.getOrCreateBadge(R.id.bottom_menu_catalyst)
        badge.isVisible = true
    }
}
package pl.autokat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import pl.autokat.components.UserInterface
import pl.autokat.databinding.ActivityBottomNavigationBinding

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var activityBottomNavigationBinding: ActivityBottomNavigationBinding

    fun badgeOn(r_id_bottom_menu: Int) {
        val badge =
            activityBottomNavigationBinding.bottomNavigation.getOrCreateBadge(r_id_bottom_menu)
        badge.isVisible = true
    }

    fun badgeOff(r_id_bottom_menu: Int) {
        val badge =
            activityBottomNavigationBinding.bottomNavigation.getOrCreateBadge(r_id_bottom_menu)
        badge.isVisible = false
    }

    fun layoutOn() {
        UserInterface.changeStatusLayout(activityBottomNavigationBinding.layout, true)
    }

    fun layoutOff() {
        UserInterface.changeStatusLayout(activityBottomNavigationBinding.layout, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBottomNavigationBinding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(activityBottomNavigationBinding.root)


        activityBottomNavigationBinding.bottomNavigation.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null


            when (it.itemId) {
                R.id.bottom_menu_result -> {
                    selectedFragment = ResultsFragment()
                }
                R.id.bottom_menu_courses -> {
                    selectedFragment = CoursesFragment()
                }
                R.id.bottom_menu_update -> {
                    selectedFragment = UpdatesFragment()
                }
                R.id.bottom_menu_settings -> {
                    selectedFragment = SettingsFragment()
                }
            }


            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .commit()
            return@setOnItemSelectedListener true
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ResultsFragment()).commit()


    }
}
package pl.autokat

import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout

class MyUserInterface {
    companion object {
        //enable or disable user interface
        fun enableActivity(linearLayout: LinearLayout, isEnabled: Boolean){
            //for elements in view set element as enabled or disabled (depends from parameter)
            for(i in 0 until linearLayout.childCount){
                val view = linearLayout.getChildAt(i)
                view.isEnabled = isEnabled
            }
        }
        fun enableActivity(drawerLayout: DrawerLayout, isEnabled: Boolean){
            //for elements in view set element as enabled or disabled (depends from parameter)
            for(i in 0 until drawerLayout.childCount){
                val view = drawerLayout.getChildAt(i)
                view.isEnabled = isEnabled
            }
        }
    }
}
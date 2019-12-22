package pl.autokat

import android.widget.LinearLayout

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
    }
}
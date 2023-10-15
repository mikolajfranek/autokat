package pl.autokat

import android.app.Application
import org.opencv.android.OpenCVLoader
import pl.autokat.components.Tesseracter

class Main : Application() {
    override fun onCreate() {
        super.onCreate()
        OpenCVLoader.initDebug()
        Tesseracter.init(applicationContext)
    }
}

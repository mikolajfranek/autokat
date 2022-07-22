package pl.autokat

import android.app.Application
import org.opencv.android.OpenCVLoader

class Main : Application() {
    override fun onCreate() {
        super.onCreate()
        OpenCVLoader.initDebug()
    }
}

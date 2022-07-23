package pl.autokat.components

import android.content.Context

class Tesseracter {
    companion object {
        fun init(context: Context) {
            val relativePathOfFileInAssets = "tessdata/digits.traineddata"
            Assetser.copyAssetFileToInternal(context, relativePathOfFileInAssets)
        }
    }
}
package pl.autokat.components

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class Assetser {
    companion object {
        fun getPathInternal(context: Context): String {
            var pathInternal = context.getFileStreamPath("").path
            pathInternal = pathInternal.substring(0, pathInternal.lastIndexOf('/'))
            return pathInternal
        }

        fun copyAssetFileToInternal(
            context: Context,
            relativePathOfFileInAssets: String
        ) {
            val directoryPathInternal = getPathInternal(context)
            val filePathInternal = directoryPathInternal.plus("/$relativePathOfFileInAssets")
            if (File(filePathInternal).exists() == false) {
                Path(filePathInternal).parent.createDirectories()
                val inputStream = context.assets.open(relativePathOfFileInAssets)
                val outputStream = FileOutputStream(filePathInternal)
                inputStream.copyTo(outputStream)
                outputStream.close()
                inputStream.close()
            }
        }
    }
}
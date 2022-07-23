package pl.autokat.components

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class Assetser {
    companion object {
        private fun getPathInternal(context: Context): String {
            var pathInternal = context.getDatabasePath(Configuration.DATABASE_NAME_OF_FILE).path
            pathInternal = pathInternal.substring(0, pathInternal.lastIndexOf('/'))
            pathInternal = pathInternal.substring(0, pathInternal.lastIndexOf('/'))
            return pathInternal
        }

        fun copyAssetFileToInternal(
            context: Context,
            relativePathOfFileInAssets: String
        ) {

            //TODO - decide, where copy tessdata?
            //val f = File(context.getFileStreamPath("").path)
            //f.listFiles()

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
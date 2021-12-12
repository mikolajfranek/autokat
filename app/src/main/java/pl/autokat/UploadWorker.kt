package pl.autokat

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MyDatabase
import java.net.URL

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        //assuming that it is atomic (change variable) - in this case will be only one job which something do
        if (jobExists == false) {
            jobExists = true
            uploadImages()
        }
        return Result.success()
    }

    private var jobExists: Boolean = false

    private fun uploadImages() {
        try {
            val database = MyDatabase(applicationContext)
            val items: JSONArray = database.getCatalystWithoutThumbnail()
            for (i in 0 until items.length()) {
                val id: Int =
                    (items[i] as JSONObject).getInt(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)
                val urlSharedPicture: String =
                    (items[i] as JSONObject).getString(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE)
                if (urlSharedPicture.isEmpty()) continue
                val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 128, 128)
                database.updateCatalyst(id, URL(urlPicture).readBytes())
            }
        } catch (e: Exception) {
            //nothing
        } finally {
            jobExists = false
        }
    }
}
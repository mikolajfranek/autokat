package pl.autokat.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MyDatabase
import pl.autokat.components.Parser
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

class WorkerUpload(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @Volatile
    private var workerExists: AtomicBoolean = AtomicBoolean(false)

    override fun doWork(): Result {
        if (workerExists.compareAndSet(false, true)) {
            uploadImages()
        }
        return Result.success()
    }

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
                val urlThumbnail = Parser.parseUrlOfPicture(urlSharedPicture, 128, 128)
                database.updateCatalyst(id, URL(urlThumbnail).readBytes())
            }
        } catch (e: Exception) {
            //
        } finally {
            workerExists.set(false)
        }
    }
}
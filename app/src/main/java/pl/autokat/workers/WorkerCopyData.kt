package pl.autokat.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONArray
import org.json.JSONObject
import pl.autokat.components.Configuration
import pl.autokat.components.Database
import pl.autokat.components.Parser
import pl.autokat.components.Spreadsheet
import java.net.URL

class WorkerCopyData(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        copyData()
        return Result.success()
    }

    private fun copyData() {
        try {
            val database = Database(applicationContext)
            val countDatabase = database.getCountCatalyst()
            var countSpreadsheet = Spreadsheet.getCountCatalystsOfCompanies()

            //TODO

            //sprawdź max (id, SECRET.company_name) z spreadsheet

            //jeśli max == count w bazie danych, wtedy koniec

            //w innym przypadku weź większe od id rekordy z bazy danych i wrzucaj do spredsheeta



            val items: JSONArray = database.getCatalystWithoutThumbnail()
            for (i in 0 until items.length()) {
                val id: Int = (items[i] as JSONObject).getInt(Configuration.DATABASE_CATALYST_ID)
                val urlSharedPicture: String =
                    (items[i] as JSONObject).getString(Configuration.DATABASE_CATALYST_URL_PICTURE)
                if (urlSharedPicture.isEmpty()) continue
                val urlThumbnail = Parser.parseUrlOfPicture(urlSharedPicture, 128, 128)
                database.updateCatalyst(id, URL(urlThumbnail).readBytes())
            }
        } catch (e: Exception) {
            //
            Configuration.workerCopyData.set(false)
        } finally {
            Configuration.workerCopyData.set(false)
        }
    }
}
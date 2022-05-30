package pl.autokat.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.autokat.components.Configuration
import pl.autokat.components.Database
import pl.autokat.components.Spreadsheet

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
            val countSpreadsheet = Spreadsheet.getCountCatalystsOfCompaniesGrouped()
            if (countDatabase == countSpreadsheet) return
            val data = database.getDataCatalyst(countSpreadsheet).chunked(10)
            for (part in data) {
                Spreadsheet.saveRowCatalystsOfCompanies(part)
            }
        } catch (e: Exception) {
            //
        } finally {
            Configuration.workerCopyData.set(false)
        }
    }
}
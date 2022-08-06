package pl.autokat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityUpdateBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {

    private lateinit var activityUpdateBinding: ActivityUpdateBinding
    private lateinit var database: Database
    private var isAvailableUpdateCatalyst: Boolean = false

    @Volatile
    private var threadUpdateProgressOfDownloadThumbnail: Thread? = null

    //region methods used in override
    private fun init() {
        activityUpdateBinding = ActivityUpdateBinding.inflate(layoutInflater)
        val view = activityUpdateBinding.root
        setContentView(view)
        setSupportActionBar(activityUpdateBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //SharedPreference.init(this)
        database = Database(applicationContext)

    }

    private fun setClickListeners() {
        activityUpdateBinding.buttonUpdateNew.setOnClickListener {
            Thread(RunnableUpdate(false)).start()
        }
        activityUpdateBinding.buttonUpdateFull.setOnClickListener {
            Thread(RunnableUpdate(true)).start()
        }
    }

    private fun receiveExtraAndSet() {
        isAvailableUpdateCatalyst = intent.getBooleanExtra("isAvailableUpdateCatalyst", false)
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setClickListeners()
        receiveExtraAndSet()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        stopThreadUpdateProgressOfDownloadThumbnail()
    }

    override fun onResume() {
        super.onResume()
        Thread(RunnableWorkBackground()).start()
    }
    //endregion

    //region methods of turing threads
    private fun stopThreadUpdateProgressOfDownloadThumbnail() {
        try {
            threadUpdateProgressOfDownloadThumbnail?.interrupt()
        } catch (e: Exception) {
            //
        }
    }

    private fun startThreadUpdateProgressOfDownloadThumbnail() {
        if (threadUpdateProgressOfDownloadThumbnail == null) {
            threadUpdateProgressOfDownloadThumbnail =
                Thread(RunnableUpdateProgressOfDownloadThumbnail())
            threadUpdateProgressOfDownloadThumbnail!!.start()
        }
    }
    //endregion

    //region inner classes
    inner class RunnableWorkBackground : Runnable {

        private var itemsWithThumbnail: Int = 0
        private var itemsWithUrlThumbnail: Int = 0
        private var itemsFromDatabase: Int = 0

        private fun setInView() {
            activityUpdateBinding.notification.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.color_main
                )
            )
            if (itemsFromDatabase != 0) {
                if (isAvailableUpdateCatalyst) {
                    activityUpdateBinding.progressBar.progress = 0
                    activityUpdateBinding.notification.text = Configuration.DATABASE_NOT_ACTUAL
                } else {
                    if (itemsWithUrlThumbnail != 0) {
                        activityUpdateBinding.progressBar.progress =
                            ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
                    } else {
                        activityUpdateBinding.progressBar.progress = 0
                    }
                    if (itemsWithThumbnail / itemsFromDatabase != 1) {
                        val textView =
                            Configuration.BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase + ")"
                        activityUpdateBinding.notification.text = textView
                        startThreadUpdateProgressOfDownloadThumbnail()
                    } else {
                        activityUpdateBinding.notification.text = Configuration.DATABASE_ACTUAL
                    }
                }
            } else {
                activityUpdateBinding.progressBar.progress = 0
                activityUpdateBinding.notification.text = Configuration.DATABASE_EMPTY
            }
        }

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityUpdateBinding.layout, false)
        }

        private fun doInBackground(): ProcessStep {
            return try {
                itemsWithThumbnail = database.getCountCatalystWithThumbnail()
                itemsWithUrlThumbnail = database.getCountCatalystWithUrlThumbnail()
                itemsFromDatabase = database.getCountCatalyst()
                ProcessStep.SUCCESS
            } catch (e: Exception) {
                ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.UNHANDLED_EXCEPTION,
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProcessStep.SUCCESS -> {
                    setInView()
                }
                else -> {
                    //
                }
            }
            UserInterface.changeStatusLayout(activityUpdateBinding.layout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }

    inner class RunnableUpdateProgressOfDownloadThumbnail : Runnable {

        private var itemsWithThumbnail: Int = 0
        private var itemsWithUrlThumbnail: Int = 0
        private var itemsFromDatabase: Int = 0

        //region methods used in doInBackground
        private fun onProgressUpdate() {
            val textView =
                Configuration.BITMAP_STATUS + " (" + itemsWithThumbnail.toString() + "/" + itemsWithUrlThumbnail.toString() + "/" + itemsFromDatabase.toString() + ")"
            activityUpdateBinding.notification.text = textView
            activityUpdateBinding.progressBar.progress =
                ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
        }
        //endregion

        override fun run() {
            try {
                while (Thread.currentThread().isInterrupted == false) {
                    Thread.sleep(1000)
                    itemsWithThumbnail = database.getCountCatalystWithThumbnail()
                    itemsWithUrlThumbnail = database.getCountCatalystWithUrlThumbnail()
                    itemsFromDatabase = database.getCountCatalyst()
                    runOnUiThread {
                        onProgressUpdate()
                    }
                }
            } catch (e: Exception) {
                //
            } finally {
                threadUpdateProgressOfDownloadThumbnail = null
            }
        }
    }

    inner class RunnableUpdate(fullUpdateInput: Boolean) : Runnable {

        private var fullUpdate: Boolean = fullUpdateInput

        //region methods used in doInBackground
        private fun checkIfRowIsAvailable(row: JSONArray): Boolean {
            return Spreadsheet.getValueStringFromDocsApi(row, Configuration.SPREADSHEET_CATALYST_ID)
                .isEmpty() == false
        }

        private fun onProgressUpdate(progressStep: Float) {
            activityUpdateBinding.progressBar.progress = progressStep.toInt()
            activityUpdateBinding.notification.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.color_main
                )
            )
            activityUpdateBinding.notification.text = Configuration.UPDATE_WAIT
        }

        private fun getCountOfCatalyst(fullUpdate: Boolean): Pair<Int, Int> {
            var countDatabase = 0
            val countSpreadsheet: Int = Spreadsheet.getCountCatalyst()
            if (fullUpdate == false) {
                countDatabase = database.getCountCatalyst()
            }
            return Pair(countDatabase, countSpreadsheet)
        }

        @Suppress("SameParameterValue")
        private fun getBatchJsonArrayOfUpdate(batchSize: Int, countDatabase: Int): JSONArray {
            val batchJsonArray = JSONArray()
            val dataCatalysts: JSONArray = Spreadsheet.getDataCatalyst(countDatabase)
            for (i in 0 until (dataCatalysts.length()) step batchSize) {
                if (batchJsonArray.isNull(i)) batchJsonArray.put(JSONArray())
                for (j in i until i + batchSize) {
                    if (dataCatalysts.isNull(j)) break
                    if (checkIfRowIsAvailable(
                            dataCatalysts.getJSONObject(j).getJSONArray("c")
                        ) == false
                    ) throw Exception()
                    (batchJsonArray[i / batchSize] as JSONArray).put(dataCatalysts[j])
                }
            }
            return batchJsonArray
        }

        @Suppress("SameParameterValue")
        private fun insertCatalysts(batchSize: Int, progressAll: Int, batchJsonArray: JSONArray) {
            var progressStep: Float
            for (i in 0 until (batchJsonArray.length())) {
                database.insertCatalysts(batchJsonArray[i] as JSONArray)
                progressStep = ((i * batchSize).toFloat() / progressAll.toFloat()) * (100).toFloat()
                runOnUiThread {
                    onProgressUpdate(progressStep)
                }
            }
        }
        //endregion

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityUpdateBinding.layout, false)
            stopThreadUpdateProgressOfDownloadThumbnail()
            while (threadUpdateProgressOfDownloadThumbnail != null) {
                Thread.sleep(100)
            }
            activityUpdateBinding.progressBar.progress = 0
            activityUpdateBinding.notification.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.color_main
                )
            )
            activityUpdateBinding.notification.text = Configuration.UPDATE_WAIT
        }

        private fun doInBackground(): ProcessStep {
            try {
                val (countDatabase, countSpreadsheet) = getCountOfCatalyst(fullUpdate)
                if (fullUpdate) {
                    database.resetDatabase()
                } else {
                    if (countDatabase == countSpreadsheet) {
                        return ProcessStep.SUCCESS
                    }
                }
                val batchSize = 100
                val batchJsonArray = getBatchJsonArrayOfUpdate(batchSize, countDatabase)
                val progressAll: Int = (countSpreadsheet - countDatabase)
                insertCatalysts(batchSize, progressAll, batchJsonArray)
                return ProcessStep.SUCCESS
            } catch (e: UnknownHostException) {
                return ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                return ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.NETWORK_FAILED -> {
                    activityUpdateBinding.notification.setTextColor(
                        ContextCompat.getColor(applicationContext, R.color.color_failed)
                    )
                    activityUpdateBinding.notification.text = Configuration.NETWORK_FAILED
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    activityUpdateBinding.notification.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.color_failed
                        )
                    )
                    activityUpdateBinding.notification.text = Configuration.UPDATE_FAILED
                }
                ProcessStep.SUCCESS -> {
                    isAvailableUpdateCatalyst = false
                    activityUpdateBinding.progressBar.progress = 100
                    activityUpdateBinding.notification.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.color_main
                        )
                    )
                    activityUpdateBinding.notification.text = Configuration.UPDATE_SUCCESS
                }
                else -> {
                    //
                }
            }
            UserInterface.changeStatusLayout(activityUpdateBinding.layout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }
    //endregion
}
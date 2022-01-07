package pl.autokat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityUpdateBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {

    //TODO
    private lateinit var activityUpdateBinding: ActivityUpdateBinding
    private lateinit var database: Database

    //TODO atomic?
    private var refreshingDatabase: Boolean = false
    private var refreshingWork: Boolean = false


    //region methods used in override
    private fun init(){
        activityUpdateBinding = ActivityUpdateBinding.inflate(layoutInflater)
        val view = activityUpdateBinding.root
        setContentView(view)
        setSupportActionBar(activityUpdateBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
        database = Database(applicationContext)

    }
    private fun setClickListeners(){
        activityUpdateBinding.buttonUpdateNew.setOnClickListener {
            refreshingDatabase = false
            Thread(RunnableUpdate(false)).start()
        }
        activityUpdateBinding.buttonUpdateFull.setOnClickListener {
            refreshingDatabase = false
            Thread(RunnableUpdate(true)).start()
        }
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setClickListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        refreshingDatabase = false
    }

    override fun onResume() {
        super.onResume()
        val itemsWithThumbnail: Int = database.getCountCatalystWithThumbnail()
        val itemsWithUrlThumbnail: Int = database.getCountCatalystWithUrlOfThumbnail()
        val itemsFromDatabase: Int = database.getCountCatalyst()
        activityUpdateBinding.progressBar.progress =
            ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
        activityUpdateBinding.notification.setTextColor(Configuration.COLOR_SUCCESS)
        if (itemsFromDatabase != 0) {
            if (Dynamic.IS_AVAILABLE_UPDATE) {
                activityUpdateBinding.progressBar.progress = 0
                activityUpdateBinding.notification.text = Configuration.DATABASE_NOT_ACTUAL
            } else {
                if (itemsWithThumbnail / itemsFromDatabase != 1) {
                    val textView =
                        Configuration.BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase + ")"
                    activityUpdateBinding.notification.text = textView

                    refreshingDatabase = true
                    //val thread = Thread(RunnableUpdateProgressOfThumbnail())
                    //thread.start()
                    Thread(RunnableUpdateProgressOfThumbnail()).start()
                } else {
                    activityUpdateBinding.notification.text =
                        Configuration.DATABASE_ACTUAL
                }
            }
        } else {
            activityUpdateBinding.notification.text = Configuration.DATABASE_EMPTY
        }
    }
    //endregion



    //region inner classes
    inner class RunnableUpdateProgressOfThumbnail : Runnable {




        override fun run() {
            //--- onPreExecute
            //--- doInBackground
            try {
                val state: Boolean =
                    refreshingDatabase && refreshingWork == false
                if (state == true) {
                    refreshingWork = true
                    while (refreshingDatabase) {
                        Thread.sleep(1000)
                        val itemsWithThumbnail: Int =
                            database.getCountCatalystWithThumbnail()
                        val itemsWithUrlThumbnail: Int =
                            database.getCountCatalystWithUrlOfThumbnail()
                        val itemsFromDatabase: Int =
                            database.getCountCatalyst()
                        //--- onProgressUpdate
                        runOnUiThread {
                            val textView =
                                Configuration.BITMAP_STATUS + " (" + itemsWithThumbnail.toString() + "/" + itemsWithUrlThumbnail.toString() + "/" + itemsFromDatabase.toString() + ")"
                            activityUpdateBinding.notification.text = textView
                            activityUpdateBinding.progressBar.progress =
                                ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
                        }
                    }
                    refreshingWork = false
                }
            } catch (e: Exception) {
                //
            }
            //--- onPostExecute
        }
    }

    inner class RunnableUpdate(fullUpdateInput: Boolean) : Runnable {
        private var fullUpdate: Boolean = fullUpdateInput

        //region methods used in doInBackground
        private fun checkIfRowIsAvailable(row: JSONArray): Boolean {
            return Spreadsheet.getValueStringFromDocsApi(
                row,
                Configuration.SPREADSHEET_CATALYST_ID
            ).isEmpty() == false
        }
        //endregion

        //region methods of run
        private fun onPreExecute(){

        }
        private fun doInBackground(): ProcessStep {

            return ProcessStep.NONE
        }
        private fun onPostExecute(processStep: ProcessStep) {

        }
        //endregion

        /*override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }*/


        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityUpdateBinding.linearLayout,
                    false
                )
                refreshingDatabase = false
                while (refreshingWork) {
                    Thread.sleep(100)
                }
                activityUpdateBinding.progressBar.progress = 0
                activityUpdateBinding.notification.setTextColor(Configuration.COLOR_SUCCESS)
                activityUpdateBinding.notification.text =
                    Configuration.UPDATE_WAIT
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                var countDatabase = 0
                val countSpreadsheet: Int = Spreadsheet.getCountCatalyst()
                if (fullUpdate) {
                    database.resetDatabase()
                } else {
                    countDatabase = database.getCountCatalyst()
                    if (countDatabase == countSpreadsheet) {
                        processStep = ProcessStep.SUCCESS
                    }
                }
                if (processStep != ProcessStep.SUCCESS) {
                    val progressAll: Int = (countSpreadsheet - countDatabase)
                    var progressStep: Float
                    val dataCatalysts: JSONArray = Spreadsheet.getDataCatalyst(countDatabase)
                    val batchJsonArray = JSONArray()
                    val batchSize = 100
                    for (i in 0 until (dataCatalysts.length()) step batchSize) {
                        if (batchJsonArray.isNull(i)) batchJsonArray.put(JSONArray())
                        for (j in i until i + batchSize) {
                            if (dataCatalysts.isNull(j)) break
                            if (checkIfRowIsAvailable(
                                    dataCatalysts.getJSONObject(
                                        j
                                    ).getJSONArray("c")
                                ) == false
                            ) throw Exception()
                            (batchJsonArray[i / batchSize] as JSONArray).put(dataCatalysts[j])
                        }
                    }
                    for (i in 0 until (batchJsonArray.length())) {
                        database.insertCatalysts(batchJsonArray[i] as JSONArray)
                        progressStep =
                            ((i * batchSize).toFloat() / progressAll.toFloat()) * (100).toFloat()
                        //--- onProgressUpdate
                        runOnUiThread {
                            activityUpdateBinding.progressBar.progress =
                                progressStep.toInt()
                            activityUpdateBinding.notification.setTextColor(
                                Configuration.COLOR_SUCCESS
                            )
                            activityUpdateBinding.notification.text =
                                Configuration.UPDATE_WAIT
                        }
                    }
                    processStep = ProcessStep.SUCCESS
                }
            } catch (e: UnknownHostException) {
                processStep = ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.NETWORK_FAILED -> {
                        activityUpdateBinding.notification.setTextColor(
                            Configuration.COLOR_FAILED
                        )
                        activityUpdateBinding.notification.text =
                            Configuration.NETWORK_FAILED
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        activityUpdateBinding.notification.setTextColor(
                            Configuration.COLOR_FAILED
                        )
                        activityUpdateBinding.notification.text =
                            Configuration.UPDATE_FAILED
                    }
                    ProcessStep.SUCCESS -> {
                        Dynamic.IS_AVAILABLE_UPDATE = false
                        activityUpdateBinding.progressBar.progress = 100
                        activityUpdateBinding.notification.setTextColor(
                            Configuration.COLOR_SUCCESS
                        )
                        activityUpdateBinding.notification.text =
                            Configuration.UPDATE_SUCCESS
                    }
                    else -> {
                        //
                    }
                }
                UserInterface.changeStatusLayout(
                    activityUpdateBinding.linearLayout,
                    true
                )
            }
        }
    }
    //endregion
}

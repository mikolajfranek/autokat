package pl.autokat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityUpdateBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {

    private lateinit var activityUpdateBinding: ActivityUpdateBinding
    private lateinit var database: Database
    private var isAvailableUpdateCatalyst: Boolean = false
    private var threadUpdateProgressOfDownloadThumbnail: Thread? = null

    //TODO atomic?
    private var refreshingDatabase: Boolean = false
    private var refreshingIsWorking: Boolean = false

    //region methods used in override
    private fun init() {
        activityUpdateBinding = ActivityUpdateBinding.inflate(layoutInflater)
        val view = activityUpdateBinding.root
        setContentView(view)
        setSupportActionBar(activityUpdateBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SharedPreference.init(this)
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
        refreshingDatabase = false

        //threadUpdateProgressOfDownloadThumbnail.interrupt()
    }

    override fun onResume() {
        super.onResume()
        Thread(RunnableWorkBackground()).start()
    }
    //endregion

    //region methods of turing threads
    private fun startThreadUpdateProgressOfDownloadThumbnail() {
        if (threadUpdateProgressOfDownloadThumbnail == null) {
            threadUpdateProgressOfDownloadThumbnail =
                Thread(RunnableUpdateProgressOfDownloadThumbnail())
            threadUpdateProgressOfDownloadThumbnail!!.start()
        }
    }
    //TODO

    //endregion

    //region inner classes
    inner class RunnableWorkBackground : Runnable {
        private var itemsWithThumbnail: Int = 0
        private var itemsWithUrlThumbnail: Int = 0
        private var itemsFromDatabase: Int = 0

        private fun setInView() {
            activityUpdateBinding.notification.setTextColor(Configuration.COLOR_SUCCESS)
            if (itemsFromDatabase != 0) {
                if (isAvailableUpdateCatalyst) {
                    activityUpdateBinding.progressBar.progress = 0
                    activityUpdateBinding.notification.text = Configuration.DATABASE_NOT_ACTUAL
                } else {
                    if (itemsWithUrlThumbnail != 0) {
                        activityUpdateBinding.progressBar.progress = ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
                    } else {
                        activityUpdateBinding.progressBar.progress = 0
                    }
                    if (itemsWithThumbnail / itemsFromDatabase != 1) {
                        val textView = Configuration.BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase + ")"
                        activityUpdateBinding.notification.text = textView



                        refreshingDatabase = true
                        //if(threadUpdateProgressOfDownloadThumbnail.state == Thread.State.TERMINATED)
                        Thread(RunnableUpdateProgressOfDownloadThumbnail()).start()


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
            UserInterface.changeStatusLayout(
                activityUpdateBinding.linearLayout,
                false
            )
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
            UserInterface.changeStatusLayout(
                activityUpdateBinding.linearLayout,
                true
            )
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


        override fun run() {
            //--- onPreExecute
            //--- doInBackground
            try {
                val state: Boolean =
                    refreshingDatabase && refreshingIsWorking == false
                if (state == true) {
                    refreshingIsWorking = true
                    while (refreshingDatabase) {

                        //odświeżanie widoku co 1s

                        Thread.sleep(1000)
                        val itemsWithThumbnail: Int =
                            database.getCountCatalystWithThumbnail()
                        val itemsWithUrlThumbnail: Int =
                            database.getCountCatalystWithUrlThumbnail()
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

                }
            } catch (e: Exception) {
                //
            } finally {
                refreshingIsWorking = false
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
        private fun onPreExecute() {

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

                //zakoncz tamten wątek, aby rozpocząć ten...
                //zaczekaj aż damten się nie zakończy
                refreshingDatabase = false
                while (refreshingIsWorking) {
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
                        isAvailableUpdateCatalyst = false
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
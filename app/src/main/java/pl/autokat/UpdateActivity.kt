package pl.autokat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityUpdateBinding
import pl.autokat.enums.ProcessStep
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {

    private lateinit var bindingActivityUpdate: ActivityUpdateBinding
    private lateinit var myDatabase: MyDatabase
    private var refreshingDatabase: Boolean = false
    private var refreshingWork: Boolean = false

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityUpdate = ActivityUpdateBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityUpdate.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityUpdate.toolbar)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        SharedPreferences.init(this)
        //init database object
        this.myDatabase = MyDatabase(this.applicationContext)
        //listeneres
        this.bindingActivityUpdate.updateNewButton.setOnClickListener {
            this.refreshingDatabase = false
            Thread(this.TaskUpdateCatalyst(false)).start()
        }
        this.bindingActivityUpdate.updateFullButton.setOnClickListener {
            this.refreshingDatabase = false
            Thread(this.TaskUpdateCatalyst(true)).start()
        }
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        this.refreshingDatabase = false
    }

    //onresume
    override fun onResume() {
        super.onResume()
        val itemsWithThumbnail: Int = this.myDatabase.getCountCatalystWithThumbnail()
        val itemsWithUrlThumbnail: Int = this.myDatabase.getCountCatalystWithUrlOfThumbnail()
        val itemsFromDatabase: Int = this.myDatabase.getCountCatalyst()
        this.bindingActivityUpdate.progessBar.progress =
            ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
        //set info section
        this.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
        if (itemsFromDatabase != 0) {
            if (MyConfiguration.IS_AVAILABLE_UPDATE) {
                this.bindingActivityUpdate.progessBar.progress = 0
                this.bindingActivityUpdate.textView.text = MyConfiguration.INFO_DATABASE_EXPIRE
            } else {
                if (itemsWithThumbnail / itemsFromDatabase != 1) {
                    val textView =
                        MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase + ")"
                    this.bindingActivityUpdate.textView.text = textView
                    //make async task and execute - refresh state of downloading
                    this.refreshingDatabase = true
                    Thread(this.TaskUpdateProgressOfUpdateThumbnail()).start()
                } else {
                    this.bindingActivityUpdate.textView.text =
                        MyConfiguration.INFO_DOWNLOAD_BITMAP_SUCCESS
                }
            }
        } else {
            this.bindingActivityUpdate.textView.text = MyConfiguration.INFO_EMPTY_DATABASE
        }
    }


    //checking if row from spreadsheet is available
    fun checkIfRowIsAvailable(row: JSONArray): Boolean {
        return MySpreadsheet.getValueStringFromDocsApi(
            row,
            MyConfiguration.MY_SPREADSHEET_CATALYST_ID
        ).isEmpty() == false
    }

    //update progress of update
    inner class TaskUpdateProgressOfUpdateThumbnail : Runnable {
        //fields
        //run
        override fun run() {
            //--- onPreExecute
            //--- doInBackground
            try {
                //very very primitive and not atomic
                val state: Boolean =
                    this@UpdateActivity.refreshingDatabase && this@UpdateActivity.refreshingWork == false
                if (state == true) {
                    this@UpdateActivity.refreshingWork = true
                    while (this@UpdateActivity.refreshingDatabase) {
                        Thread.sleep(1000)
                        val itemsWithThumbnail: Int =
                            this@UpdateActivity.myDatabase.getCountCatalystWithThumbnail()
                        val itemsWithUrlThumbnail: Int =
                            this@UpdateActivity.myDatabase.getCountCatalystWithUrlOfThumbnail()
                        val itemsFromDatabase: Int =
                            this@UpdateActivity.myDatabase.getCountCatalyst()
                        //--- onProgressUpdate
                        this@UpdateActivity.runOnUiThread {
                            val textView =
                                MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + itemsWithThumbnail.toString() + "/" + itemsWithUrlThumbnail.toString() + "/" + itemsFromDatabase.toString() + ")"
                            this@UpdateActivity.bindingActivityUpdate.textView.text = textView
                            this@UpdateActivity.bindingActivityUpdate.progessBar.progress =
                                ((itemsWithThumbnail.toFloat() / itemsWithUrlThumbnail.toFloat()) * 100.toFloat()).toInt()
                        }
                    }
                    this@UpdateActivity.refreshingWork = false
                }
            } catch (e: Exception) {
                //nothing
            }
            //--- onPostExecute
        }
    }

    //update catalyst
    inner class TaskUpdateCatalyst(fullUpdateInput: Boolean) : Runnable {
        //fields
        private var fullUpdate: Boolean = fullUpdateInput

        //run
        override fun run() {
            //--- onPreExecute
            this@UpdateActivity.runOnUiThread {
                //disable user interface on process application
                UserInterface.changeStatusLayout(
                    this@UpdateActivity.bindingActivityUpdate.linearLayout,
                    false
                )
                this@UpdateActivity.refreshingDatabase = false
                while (this@UpdateActivity.refreshingWork) {
                    Thread.sleep(100)
                }
                //set process bar
                this@UpdateActivity.bindingActivityUpdate.progessBar.progress = 0
                //set info section
                this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                this@UpdateActivity.bindingActivityUpdate.textView.text =
                    MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.NONE
            try {
                var countDatabase = 0
                val countSpreadsheet: Int = MySpreadsheet.getCountCatalyst()
                //check if user click sync database
                if (this.fullUpdate) {
                    //truncate tables
                    this@UpdateActivity.myDatabase.resetDatabase()
                } else {
                    //calculate count of catalyst
                    countDatabase = this@UpdateActivity.myDatabase.getCountCatalyst()
                    if (countDatabase == countSpreadsheet) {
                        processStep = ProcessStep.SUCCESS
                    }
                }
                if (processStep != ProcessStep.SUCCESS) {
                    //difference between database local and database in spreadsheet
                    val progressAll: Int = (countSpreadsheet - countDatabase)
                    //profess step equals currenly state of process update
                    var progressStep: Float
                    //get data catalyst from spreadsheet which missed
                    val dataCatalysts: JSONArray = MySpreadsheet.getDataCatalyst(countDatabase)
                    //create batch json array which will contain parts of data
                    val batchJsonArray = JSONArray()
                    val batchSize = 100
                    for (i in 0 until (dataCatalysts.length()) step batchSize) {
                        if (batchJsonArray.isNull(i)) batchJsonArray.put(JSONArray())
                        for (j in i until i + batchSize) {
                            if (dataCatalysts.isNull(j)) break
                            if (this@UpdateActivity.checkIfRowIsAvailable(
                                    dataCatalysts.getJSONObject(
                                        j
                                    ).getJSONArray("c")
                                ) == false
                            ) throw Exception()
                            (batchJsonArray[i / batchSize] as JSONArray).put(dataCatalysts[j])
                        }
                    }
                    //iterate over batch array
                    for (i in 0 until (batchJsonArray.length())) {
                        this@UpdateActivity.myDatabase.insertCatalysts(batchJsonArray[i] as JSONArray)
                        //update and publish state of process update
                        progressStep =
                            ((i * batchSize).toFloat() / progressAll.toFloat()) * (100).toFloat()
                        //--- onProgressUpdate
                        this@UpdateActivity.runOnUiThread {
                            this@UpdateActivity.bindingActivityUpdate.progessBar.progress =
                                progressStep.toInt()
                            //set info section
                            this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(
                                MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS
                            )
                            this@UpdateActivity.bindingActivityUpdate.textView.text =
                                MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
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
            this@UpdateActivity.runOnUiThread {
                //do job depends on situation
                when (processStep) {
                    ProcessStep.NETWORK_FAILED -> {
                        this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(
                            MyConfiguration.INFO_MESSAGE_COLOR_FAILED
                        )
                        this@UpdateActivity.bindingActivityUpdate.textView.text =
                            MyConfiguration.INFO_MESSAGE_NETWORK_FAILED
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(
                            MyConfiguration.INFO_MESSAGE_COLOR_FAILED
                        )
                        this@UpdateActivity.bindingActivityUpdate.textView.text =
                            MyConfiguration.INFO_UPDATE_FAILED
                    }
                    ProcessStep.SUCCESS -> {
                        MyConfiguration.IS_AVAILABLE_UPDATE = false
                        this@UpdateActivity.bindingActivityUpdate.progessBar.progress = 100
                        this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(
                            MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS
                        )
                        this@UpdateActivity.bindingActivityUpdate.textView.text =
                            MyConfiguration.INFO_UPDATE_SUCCESS
                    }
                    else -> {
                        //nothing
                    }
                }
                //enable user interface on process application
                UserInterface.changeStatusLayout(
                    this@UpdateActivity.bindingActivityUpdate.linearLayout,
                    true
                )
            }
        }
    }
}

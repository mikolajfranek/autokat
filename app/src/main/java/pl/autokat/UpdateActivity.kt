package pl.autokat

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONArray
import pl.autokat.components.*
import pl.autokat.databinding.ActivityUpdateBinding
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {

    private lateinit var bindingActivityUpdate: ActivityUpdateBinding
    private lateinit var myDatabase: MyDatabase
    private var refreshingDatabase : Boolean = false
    private var refreshingWork : Boolean = false

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityUpdate = ActivityUpdateBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityUpdate.root
        this.setContentView(view)
        //set toolbar
        this.setSupportActionBar(this.bindingActivityUpdate.toolbar as Toolbar?)
        //navigate up
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //init database object
        this.myDatabase = MyDatabase(this.applicationContext)
    }
    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
    override fun onPause(){
        super.onPause()
        this.refreshingDatabase = false
    }
    //onresume
    override fun onResume() {
        super.onResume()
        val itemsWithThumbnail : Int = this.myDatabase.getCountCatalystWithThumbnail()
        val itemsWithUrlThumbnail : Int = this.myDatabase.getCountCatalystWithUrlOfThumbnail()
        val itemsFromDatabase : Int = this.myDatabase.getCountCatalyst()
        this.bindingActivityUpdate.progessBar.progress = ((itemsWithThumbnail.toFloat()/itemsWithUrlThumbnail.toFloat())*100.toFloat()).toInt()
        //set info section
        this.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
        if(itemsFromDatabase != 0){
            if(MyConfiguration.IS_AVAILABLE_UPDATE){
                this.bindingActivityUpdate.progessBar.progress = 0
                this.bindingActivityUpdate.textView.text = MyConfiguration.INFO_DATABASE_EXPIRE
            }else{
                if(itemsWithThumbnail/itemsFromDatabase != 1){
                    this.bindingActivityUpdate.textView.text = (MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase+ ")")
                    //make async task and execute - refresh state of downloading
                    this.refreshingDatabase = true
                    val task = this.RefreshUpdateCatalyst()
                    task.execute()
                }else{
                    this.bindingActivityUpdate.textView.text = MyConfiguration.INFO_DOWNLOAD_BITMAP_SUCCESS
                }
            }
        }else{
            this.bindingActivityUpdate.textView.text = MyConfiguration.INFO_EMPTY_DATABASE
        }
    }
    //click button only new
    fun activityUpdateOnlyNew(view: View?) {
        this.refreshingDatabase = false
        //make async task and execute
        val task = this.UpdateCatalyst(false)
        task.execute()
    }
    //click button full
    fun activityUpdateFull(view: View?) {
        this.refreshingDatabase = false
        //make async task and execute
        val task = this.UpdateCatalyst(true)
        task.execute()
    }
    //checking if row from spreadsheet is available
    fun checkIfRowIsAvailable(row: JSONArray): Boolean{
        val result : Boolean = MyConfiguration.getValueStringFromDocsApi(row, MyConfiguration.MY_SPREADSHEET_CATALYST_ID).isEmpty() == false
        return result
    }

    @SuppressLint("StaticFieldLeak")
    inner class RefreshUpdateCatalyst() : AsyncTask<Void, Int, MyProcessStep>() {
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //very very primitive and not atomic
                val state : Boolean = this@UpdateActivity.refreshingDatabase && this@UpdateActivity.refreshingWork == false
                if(state == false) return MyProcessStep.SUCCESS
                this@UpdateActivity.refreshingWork = true
                while(this@UpdateActivity.refreshingDatabase){
                    Thread.sleep(1000)
                    val itemsWithThumbnail : Int = this@UpdateActivity.myDatabase.getCountCatalystWithThumbnail()
                    val itemsWithUrlThumbnail : Int = this@UpdateActivity.myDatabase.getCountCatalystWithUrlOfThumbnail()
                    val itemsFromDatabase : Int = this@UpdateActivity.myDatabase.getCountCatalyst()
                    this.publishProgress(itemsWithThumbnail, itemsWithUrlThumbnail, itemsFromDatabase)
                }
                this@UpdateActivity.refreshingWork = false
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
            return MyProcessStep.SUCCESS
        }
        //on progress update
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            this@UpdateActivity.bindingActivityUpdate.textView.text = (MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + values[0]!!.toString() + "/" + values[1]!!.toString() + "/" + values[2]!!.toString()+ ")")
            this@UpdateActivity.bindingActivityUpdate.progessBar.progress = ((values[0]!!.toFloat()/values[1]!!.toFloat())*100.toFloat()).toInt()
        }
    }

    //async class which check if exists update of app and update it
    @SuppressLint("StaticFieldLeak")
    inner class UpdateCatalyst(fullUpdateInput: Boolean) : AsyncTask<Void, Int, MyProcessStep>() {
        //fields
        private var fullUpdate : Boolean = fullUpdateInput
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            //disable user interface on process application
            MyUserInterface.enableActivity(this@UpdateActivity.bindingActivityUpdate.linearLayout, false)
            this@UpdateActivity.refreshingDatabase = false
            while(this@UpdateActivity.refreshingWork){
                Thread.sleep(100)
            }
            //set process bar
            this@UpdateActivity.bindingActivityUpdate.progessBar.progress = 0
            //set info section
            this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            this@UpdateActivity.bindingActivityUpdate.textView.text = MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                var countDatabase = 0
                val countSpreadsheet : Int = MySpreadsheet.getCountCatalyst()
                //check if user click sync database
                if(this.fullUpdate) {
                    //truncate tables
                    if(this@UpdateActivity.myDatabase.resetDatabase() == false) throw Exception()
                }else{
                    //calculate count of catalyst
                    countDatabase = this@UpdateActivity.myDatabase.getCountCatalyst()
                    if(countDatabase == countSpreadsheet) return MyProcessStep.SUCCESS
                }
                //difference between database local and database in spreadsheet
                val progressAll : Int = (countSpreadsheet - countDatabase)
                //profess step equals currenly state of process update
                var progressStep: Float
                //get data catalyst from spreadsheet which missed
                val dataCatalysts:  JSONArray = MySpreadsheet.getDataCatalyst(countDatabase)
                //create batch json array which will contain parts of data
                val batchJsonArray = JSONArray()
                val batchSize = 100
                for(i in 0 until (dataCatalysts.length()) step batchSize){
                    if(batchJsonArray.isNull(i)) batchJsonArray.put(JSONArray())
                    for(j in i until i+batchSize){
                        if(dataCatalysts.isNull(j)) break
                        if(this@UpdateActivity.checkIfRowIsAvailable(dataCatalysts.getJSONObject(j).getJSONArray("c")) == false) throw Exception()
                        (batchJsonArray[i/batchSize] as JSONArray).put(dataCatalysts[j])
                    }
                }
                //iterate over batch array
                for(i in 0 until (batchJsonArray.length())){
                    if(this@UpdateActivity.myDatabase.insertCatalysts(batchJsonArray[i] as JSONArray) == false) throw Exception()
                    //update and publish state of process update
                    progressStep = ((i*batchSize).toFloat()/progressAll.toFloat()) * (100).toFloat()
                    this.publishProgress(progressStep.toInt())
                }
            }
            catch(e: UnknownHostException){
                return MyProcessStep.NETWORK_FAILED
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
            return MyProcessStep.SUCCESS
        }
        //on progress update
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            this@UpdateActivity.bindingActivityUpdate.progessBar.progress = values[0]!!.toInt()
            //set info section
            this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            this@UpdateActivity.bindingActivityUpdate.textView.text = MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //do job depends on situation
            when(result){
                MyProcessStep.NETWORK_FAILED -> {
                    this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@UpdateActivity.bindingActivityUpdate.textView.text = MyConfiguration.INFO_MESSAGE_NETWORK_FAILED
                }
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    this@UpdateActivity.bindingActivityUpdate.textView.text = MyConfiguration.INFO_UPDATE_FAILED
                }
                MyProcessStep.SUCCESS -> {
                    MyConfiguration.IS_AVAILABLE_UPDATE = false
                    this@UpdateActivity.bindingActivityUpdate.progessBar.progress = 100
                    this@UpdateActivity.bindingActivityUpdate.textView.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                    this@UpdateActivity.bindingActivityUpdate.textView.text = MyConfiguration.INFO_UPDATE_SUCCESS
                }
            }
            //enable user interface on process application
            MyUserInterface.enableActivity(this@UpdateActivity.bindingActivityUpdate.linearLayout, true)
        }
    }
}

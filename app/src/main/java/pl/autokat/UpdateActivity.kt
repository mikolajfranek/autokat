package pl.autokat

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_update.*
import org.json.JSONArray
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {
    //fields
    private lateinit var database: MyDatabase
    private var refreshingDatabase : Boolean = false
    private var refreshingWork : Boolean = false

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)
        //init database object
        database = MyDatabase(applicationContext)
    }
    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun onPause(){
        super.onPause()
        this.refreshingDatabase = false
    }
    //onresume
    override fun onResume() {
        super.onResume()
        val itemsWithThumbnail : Int = database.getCountCatalystWithThumbnail()
        val itemsWithUrlThumbnail : Int = database.getCountCatalystWithUrlOfThumbnail()
        val itemsFromDatabase : Int = database.getCountCatalyst()
        activity_update_progessbar.progress = ((itemsWithThumbnail.toFloat()/itemsWithUrlThumbnail.toFloat())*100.toFloat()).toInt()
        //set info section
        activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
        if(itemsFromDatabase != 0){
            if(MyConfiguration.IS_AVAILABLE_UPDATE){
                activity_update_progessbar.progress = 0
                activity_update_textview.text = MyConfiguration.INFO_DATABASE_EXPIRE
            }else{
                if(itemsWithThumbnail/itemsFromDatabase != 1){
                    activity_update_textview.text = (MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + itemsWithThumbnail + "/" + itemsWithUrlThumbnail + "/" + itemsFromDatabase+ ")")
                    //make async task and execute - refresh state of downloading
                    this.refreshingDatabase = true
                    val task = RefreshUpdateCatalyst()
                    task.execute()
                }else{
                    activity_update_textview.text = MyConfiguration.INFO_DOWNLOAD_BITMAP_SUCCESS
                }
            }
        }else{
            activity_update_textview.text = MyConfiguration.INFO_EMPTY_DATABASE
        }
    }
    //click button only new
    fun activityUpdateOnlyNew(view: View?) {
        this.refreshingDatabase = false
        //make async task and execute
        val task = UpdateCatalyst(false)
        task.execute()
    }
    //click button full
    fun activityUpdateFull(view: View?) {
        this.refreshingDatabase = false
        //make async task and execute
        val task = UpdateCatalyst(true)
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
                val state : Boolean = refreshingDatabase && refreshingWork == false
                if(state == false) return MyProcessStep.SUCCESS
                refreshingWork = true
                while(refreshingDatabase){
                    Thread.sleep(1000)
                    val itemsWithThumbnail : Int = database.getCountCatalystWithThumbnail()
                    val itemsWithUrlThumbnail : Int = database.getCountCatalystWithUrlOfThumbnail()
                    val itemsFromDatabase : Int = database.getCountCatalyst()
                    publishProgress(itemsWithThumbnail, itemsWithUrlThumbnail, itemsFromDatabase)
                }
                refreshingWork = false
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
            return MyProcessStep.SUCCESS
        }
        //on progress update
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            activity_update_textview.text = (MyConfiguration.INFO_DOWNLOAD_BITMAP_STATUS + " (" + values[0]!!.toString() + "/" + values[1]!!.toString() + "/" + values[2]!!.toString()+ ")")
            activity_update_progessbar.progress = ((values[0]!!.toFloat()/values[1]!!.toFloat())*100.toFloat()).toInt()
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
            MyUserInterface.enableActivity(this@UpdateActivity.activity_update_linearlayout, false)
            //set process bar
            activity_update_progessbar.progress = 0
            //set info section
            activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            activity_update_textview.text = MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                var countDatabase = 0
                val countSpreadsheet : Int = MySpreadsheet.getCountCatalyst()
                //check if user click sync database
                if(fullUpdate) {
                    //truncate tables
                    if(database.resetDatabase() == false) throw Exception()
                }else{
                    //calculate count of catalyst
                    countDatabase = database.getCountCatalyst()
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
                val batchSize = 500
                for(i in 0 until (dataCatalysts.length()) step batchSize){
                    if(batchJsonArray.isNull(i)) batchJsonArray.put(JSONArray())
                    for(j in i until i+batchSize){
                        if(dataCatalysts.isNull(j)) break
                        if(checkIfRowIsAvailable(dataCatalysts.getJSONObject(j).getJSONArray("c")) == false) throw Exception()
                        (batchJsonArray[i/batchSize] as JSONArray).put(dataCatalysts[j])
                    }
                }
                //iterate over batch array
                for(i in 0 until (batchJsonArray.length())){
                    if(database.insertCatalysts(batchJsonArray[i] as JSONArray) == false) throw Exception()
                    //update and publish state of process update
                    progressStep = ((i*batchSize).toFloat()/progressAll.toFloat()) * (100).toFloat()
                    publishProgress(progressStep.toInt())
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
            activity_update_progessbar.progress = values[0]!!.toInt()
            //set info section
            activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            activity_update_textview.text = MyConfiguration.INFO_MESSAGE_WAIT_UPDATE
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //do job depends on situation
            when(result){
                MyProcessStep.NETWORK_FAILED -> {
                    activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    activity_update_textview.text = MyConfiguration.INFO_MESSAGE_NETWORK_FAILED
                }
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                    activity_update_textview.text = MyConfiguration.INFO_UPDATE_FAILED
                }
                MyProcessStep.SUCCESS -> {
                    MyConfiguration.IS_AVAILABLE_UPDATE = false
                    activity_update_progessbar.progress = 100
                    activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                    activity_update_textview.text = MyConfiguration.INFO_UPDATE_SUCCESS
                }
            }
            //enable user interface on process application
            MyUserInterface.enableActivity(this@UpdateActivity.activity_update_linearlayout, true)
        }
    }
}

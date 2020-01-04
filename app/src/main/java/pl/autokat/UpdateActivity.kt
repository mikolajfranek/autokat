package pl.autokat

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_update.*
import org.json.JSONArray
import java.net.URL
import java.net.UnknownHostException

class UpdateActivity : AppCompatActivity() {
    //fields
    private lateinit var database: MyDatabase
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
    //click button only new
    fun activityUpdateOnlyNew(view: View?) {
        //make async task and execute
        val task = UpdateCatalyst(false)
        task.execute()
    }
    //click button full
    fun activityUpdateFull(view: View?) {
        //make async task and execute
        val task = UpdateCatalyst(true)
        task.execute()
    }
    //async class which check if exists update of app and update it
    @SuppressLint("StaticFieldLeak")
    private inner class UpdateCatalyst(fullUpdateInput: Boolean) : AsyncTask<Void, Int, MyProcessStep>() {
        //fields
        private var fullUpdate : Boolean = fullUpdateInput
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            //disable user interface on process application
            MyUserInterface.enableActivity(this@UpdateActivity.activity_update_linearlayout, false)
            //set process bar
            activity_update_progessbar.progress = 0
            activity_update_progessbar.isVisible = true
            //set info section
            activity_update_textview.isVisible = true
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
                val dataCatalysts: JSONArray = MySpreadsheet.getDataCatalyst(countDatabase)
                //represent variable of one catalyst
                val row = ContentValues()
                //iterate over all elements and add to database
                for(i in 0 until dataCatalysts.length()){
                    //get element
                    val element : JSONArray = dataCatalysts.getJSONArray(i)
                    //prepare thumbnail
                    val urlSharedPicture = element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_URL_PICTURE)
                    val urlThumbnail = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 128, 128)
                    //add values
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_ID_PICTURE))
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_URL_PICTURE))
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_THUMBNAIL, URL(urlThumbnail).readBytes())
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_NAME))
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_BRAND))
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_PLATINUM).toFloat())
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_PALLADIUM).toFloat())
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_RHODIUM).toFloat())
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_TYPE))
                    row.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_WEIGHT).toFloat())
                    //insert element
                    if(database.insertCatalysts(row) == false) throw Exception()
                    //update and publish state of process update
                    progressStep = (i.toFloat()/progressAll.toFloat()) * (100).toFloat()
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

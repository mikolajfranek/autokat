package pl.autokat

import android.content.ContentValues
import android.graphics.BitmapFactory
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
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var database: MyDatabase

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

    //async class which check if exists update of app
    private inner class UpdateCatalyst(fullUpdateInput: Boolean) : AsyncTask<Void, Int, Boolean>() {

        private var fullUpdate : Boolean = fullUpdateInput

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()

            //disable user interface on process application
            MyUserInterface.enableActivity(this@UpdateActivity.activity_update_linearlayout, false)


            activity_update_progessbar.progress = 0
            activity_update_progessbar.isVisible = true

            activity_update_textview.isVisible = true
            activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
            activity_update_textview.text = "Trwa aktualizacja..."
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): Boolean {
            try{
                var countDatabase : Int = 0
                val countSpreadsheet : Int = MySpreadsheet.getCountCatalyst()

                if(fullUpdate) {
                    if(database.resetDatabase() == false) throw Exception()
                }else{
                    countDatabase = database.getCountCatalyst()
                }

                if(countDatabase == countSpreadsheet) return true

                val progressAll : Int = (countSpreadsheet - countDatabase)
                var progressStep : Float = 0.0F
                val dataCatalysts: JSONArray = MySpreadsheet.getDataCatalyst(countDatabase)
                val values = ContentValues()
                for(i in 0 until dataCatalysts.length()){
                    val element : JSONArray = dataCatalysts.getJSONArray(i)

                    val urlSharedPicture = element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_URL_PICTURE)
                    val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 128, 128)

                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PICTURE, URL(urlPicture).readBytes())
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID_PICTURE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_ID_PICTURE))
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_URL_PICTURE))
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_NAME, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_NAME))
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_BRAND, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_BRAND))
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PLATINUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_PLATINUM).replace(',','.').toFloat())
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_PALLADIUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_PALLADIUM).replace(',','.').toFloat())
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_RHODIUM, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_RHODIUM).replace(',','.').toFloat())
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_TYPE, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_TYPE))
                    values.put(MyConfiguration.DATABASE_ELEMENT_CATALYST_WEIGHT, element.getString(MyConfiguration.MY_SPREADSHEET_CATALYST_WEIGHT).replace(',','.').toFloat())

                    if(database.insertCatalysts(values) == false) throw Exception()

                    progressStep = (i.toFloat()/progressAll.toFloat()) * (100).toFloat()
                    publishProgress(progressStep.toInt())
                }
            }catch(e: Exception){
                return false
            }
            return true
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            activity_update_progessbar.progress = values[0]!!.toInt()
        }

        //post execute
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)

            if(result){
                activity_update_progessbar.progress = 100
                activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_SUCCESS)
                activity_update_textview.text = "Baza danych jest aktualna"
            }else{
                activity_update_textview.setTextColor(MyConfiguration.INFO_MESSAGE_COLOR_FAILED)
                activity_update_textview.text = "Wystąpił błąd aktualizacji"
            }

            MyUserInterface.enableActivity(this@UpdateActivity.activity_update_linearlayout, true)
        }
    }

}

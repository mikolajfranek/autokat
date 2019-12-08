package pl.autokat

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class ResultActivity : AppCompatActivity() {
    //fields
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapter: ArrayAdapter<ItemCatalyst>


    class ViewHolder(view: View) {
        val tvTitle: TextView
        init {
            this.tvTitle = view.item_text as TextView
        }
    }



    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init databases
        database = MyDatabase(applicationContext)







        databaseAdapter = object : ArrayAdapter<ItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view : View
                val vh: ViewHolder

                if (convertView == null) {
                    view = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                    vh = ViewHolder(view)
                    view.isEnabled = false
                    view.tag = vh

                } else {
                    view = convertView
                    vh = view.tag as ViewHolder
                }
                val someItem = getItem(position)
                vh.tvTitle.text =  someItem?.CatalystId.toString()

                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)


        var result = database.getDataCatalyst("")
        databaseAdapter.clear()
        databaseAdapter.addAll(result)

    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //open configuration values activity
    fun openConfigurationValuesActivity(){
        val intent = Intent(applicationContext, ConfigurationValuesActivity::class.java)
        startActivity(intent)
        finish()
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        when(id){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
                return true
            }
            R.id.toolbar_list_update -> {
                CheckUpdate().execute()
                return true
            }
            else -> {
                finish()
                return true
            }
        }
    }

    //async class which check if exists update of app
    private inner class CheckUpdate() : AsyncTask<Void, Int, MyProcessStep>() {

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_WAIT_UPDATE, Toast.LENGTH_SHORT).show()
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //retrieve and parse to json data from spreadsheet
                val resultFromUrl = URL(MySpreadsheet.getUrlVersion()).readText()
                val resultJson = MySpreadsheet.parseResultToJson(resultFromUrl)

                //check if result exists
                val rows = resultJson.getJSONObject("table").getJSONArray("rows")
                if(rows.length() != 1) return MyProcessStep.APPLICATION_IS_UP_TO_DATE

                //get url of new application
                val urlNewApp = rows.getJSONObject(0).getJSONArray("c").getJSONObject(1).getString("v")

                //target path where app will be downloaded
                val targetPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + MyConfiguration.FILE_APPLICATION_NEW_VERSION

                //download file and save it
                URL(urlNewApp).openStream().use { input -> FileOutputStream(File(targetPath)).use { output -> input.copyTo(output)}}

                //run downloaded file
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.fromFile(File(targetPath)), "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

                return MyProcessStep.SUCCESS
            }catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
        }

        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)

            when(result){
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(applicationContext, MyConfiguration.EXCEPTION_MESSAGE_UNHANDLED_EXCEPTION, Toast.LENGTH_SHORT).show()
                }
                MyProcessStep.APPLICATION_IS_UP_TO_DATE -> {
                    Toast.makeText(applicationContext, MyConfiguration.EXCEPTION_MESSAGE_APPLICATION_IS_UP_TO_DATE, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

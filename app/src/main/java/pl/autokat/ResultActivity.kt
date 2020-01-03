package pl.autokat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.my_item_catalyst.view.*
import java.util.*


class ResultActivity : AppCompatActivity() {

    //fields
    private lateinit var database: MyDatabase
    private lateinit var databaseAdapter: ArrayAdapter<MyItemCatalyst>
    private var scrollPreLast: Int = 0
    private var scrollLimit : Int = MyConfiguration.DATABASE_PAGINATE_LIMIT
    private var menu : Menu? = null

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //set toolbar
        setSupportActionBar(toolbar as Toolbar?)
        //navigate up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init shared preferences
        MySharedPreferences.init(this)

        //init database object
        database = MyDatabase(applicationContext)

        //init listener on change text
        activity_result_edittext.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@ResultActivity.refreshListView()
            }
        })
        //init database adapter
        databaseAdapter = object : ArrayAdapter<MyItemCatalyst>(applicationContext, R.layout.my_item_catalyst) {
            override fun isEnabled(position: Int): Boolean {
                return false
            }

            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view : View = layoutInflater.inflate(R.layout.my_item_catalyst, parent, false)
                val itemCatalyst = getItem(position)!!
                val visibilityCatalyst : Boolean = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY).toInt() != 0

                view.item_id_picture.text = itemCatalyst.idPicture.toString()

                view.item_picture.setImageBitmap(itemCatalyst.thumbnail)
                view.item_picture.setOnClickListener {
                    val intent = Intent(applicationContext, PictureActivity::class.java)
                    intent.putExtra("urlPicture", itemCatalyst.urlPicture)
                    startActivity(intent)
                }

                view.item_brand.text = itemCatalyst.brand
                view.item_type.text = itemCatalyst.type
                view.item_name.text = if (itemCatalyst.name!!.takeLast(1) == "\n") itemCatalyst.name!!.substring(0, itemCatalyst.name!!.length-1)  else itemCatalyst.name
                view.item_weight.text = (MyConfiguration.formatStringFloat(itemCatalyst.weight.toString(), 3) + " kg")

                view.item_platinum.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.platinum.toString() else "0.0", 3) + " g/kg")
                view.item_palladium.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.palladium.toString() else "0.0", 3) + " g/kg")
                view.item_rhodium.text = (MyConfiguration.formatStringFloat(if (visibilityCatalyst) itemCatalyst.rhodium.toString() else "0.0", 3) + " g/kg")

                val pricePl = itemCatalyst.countPricePln()
                val courseUsdPlnFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
                val courseUsdPln : Float = if(courseUsdPlnFromConfiguration.isEmpty()) 0.0F else courseUsdPlnFromConfiguration.toFloat()

                val priceEur = if(courseUsdPln != 0.0F) (pricePl / courseUsdPln) else 0.0F

                view.item_price_eur.text = (MyConfiguration.formatStringFloat(priceEur.toString(), 2) + " €")
                view.item_price_pl.text = (MyConfiguration.formatStringFloat(pricePl.toString(), 2) + " zł")
                return view
            }
        }
        activity_result_listView.setAdapter(databaseAdapter)


        activity_result_listView.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastItem : Int = firstVisibleItem + visibleItemCount
                if (lastItem == totalItemCount && scrollPreLast != lastItem) {
                    scrollLimit += MyConfiguration.DATABASE_PAGINATE_LIMIT
                    this@ResultActivity.refreshListView(scrollLimit)
                    scrollPreLast = lastItem
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
        })

        this.refreshListView()
    }

    //navigate up
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //onresume
    override fun onResume() {
        super.onResume()

        /* checking time */
        if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) this.openMainActivity()

        //make async task and execute
        val task = CheckUpdate()
        task.execute()
    }

    //toolbar option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list_result, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    //open main activity
    fun openMainActivity(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //open configuration values activity
    fun openConfigurationValuesActivity(){
        val intent = Intent(applicationContext, ConfigurationValuesActivity::class.java)
        startActivity(intent)
    }

    //open update activity
    private fun openUpdateActivity() {
        val intent = Intent(applicationContext, UpdateActivity::class.java)
        startActivity(intent)
    }

    //open about activity
    fun openAboutActivity(){
        val intent = Intent(applicationContext, AboutActivity::class.java)
        startActivity(intent)
    }

    //option menu selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        return when(id){
            R.id.toolbar_list_configuration_values -> {
                this.openConfigurationValuesActivity()
                true
            }
            R.id.toolbar_list_update -> {
                this.openUpdateActivity()
                true
            }
            R.id.toolbar_list_about -> {
                this.openAboutActivity()
                true
            }
            else -> {
                finish()
                true
            }
        }
    }

    //refresh list view
    fun refreshListView(limit: Int = MyConfiguration.DATABASE_PAGINATE_LIMIT){
        if(limit == MyConfiguration.DATABASE_PAGINATE_LIMIT){
            this.scrollPreLast = 0
            this.scrollLimit = MyConfiguration.DATABASE_PAGINATE_LIMIT
        }

        val result = database.getDataCatalyst(activity_result_edittext.text.toString(), limit)
        databaseAdapter.clear()
        databaseAdapter.addAll(result)
    }

    //async class which check if exists update of app
    private inner class CheckUpdate : AsyncTask<Void, Void, MyProcessStep>() {

        private var updateCourses : Boolean = false
        private var updateCatalyst : Boolean = false

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()

            //disable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, false)
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                //update value of courses - if from last update passed 6h
                val lastTimestampUpdateCourse : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_UPDATE_COURSE_TIMESTAMP)
                if(lastTimestampUpdateCourse.isEmpty() || ((Date().time - lastTimestampUpdateCourse.toLong()) > (MyConfiguration.ONE_DAY_IN_MILLISECONDS/4))){
                    MyCatalystValues.getValues()
                }


                //flag update catalyst - if amount in spreadsheet is greater than in local database
                val databaseCatalystCount = database.getCountCatalyst()
                updateCatalyst = databaseCatalystCount == 0 || MySpreadsheet.getCountCatalyst() > databaseCatalystCount


                /* authentication */
                val rows = MySpreadsheet.getDataLogin(MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LOGIN))
                if(rows?.length() != 1) {
                    return MyProcessStep.USER_FAILED_LOGIN
                }
                //get row element
                val element = rows?.getJSONObject(0).getJSONArray("c")


                //save data
                val elementLicenceDate = element.getJSONObject(2).getString("v")
                val discount = element.getJSONObject(3).getString("v")
                val visibility = element.getJSONObject(4).getString("v")


                /* save configuration */
                //save licence date
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_LICENCE_DATE_OF_END, elementLicenceDate)
                //save discount
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT, discount)
                //save visibility
                MySharedPreferences.setKeyToFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_VISIBILITY, visibility)

                /* checking time */
                if(MyConfiguration.checkTimeOnPhone("", MyTimeChecking.CHECKING_LICENCE) == false) return MyProcessStep.USER_ELAPSED_DATE_LICENCE

            }catch(e: Exception){
                //nothing
            }

            return MyProcessStep.SUCCESS
        }

        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)


            //do job depends on situation
            when(result) {
                MyProcessStep.USER_ELAPSED_DATE_LICENCE -> {
                    this@ResultActivity.openMainActivity()
                }
            }


            //set visibility of update catalyst
            if(this@ResultActivity.menu != null){
                if(updateCatalyst){
                    this@ResultActivity.menu!!.getItem(1).setIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_action_update_catalyst_yellow))
                }else{
                    this@ResultActivity.menu!!.getItem(1).setIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_action_update_catalyst))
                }
            }

            //refresh list view
            this@ResultActivity.refreshListView()

            //enable user interface on process application
            MyUserInterface.enableActivity(this@ResultActivity.activity_result_linearlayout, true)
        }
    }
}

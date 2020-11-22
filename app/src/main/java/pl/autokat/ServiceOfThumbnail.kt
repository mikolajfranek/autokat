package pl.autokat

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.core.app.JobIntentService
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class ServiceOfThumbnail : JobIntentService() {

    companion object {
        private const val UNIQUE_JOB_ID : Int = 1000
        private var JOB_IS_EXISTS : Boolean = false
        fun enqueueWork(context: Context) {
            //assuming that it is atomic (change variable) - in this case will be only one job which something do
            if(JOB_IS_EXISTS == false){
                JOB_IS_EXISTS = true
                enqueueWork(context, ServiceOfThumbnail::class.java, UNIQUE_JOB_ID, Intent())
            }
        }
    }

    //download thumbnails
    override fun onHandleWork(intent: Intent) {
        try{
            val database = MyDatabase(applicationContext)
            val items : JSONArray = database.getCatalystWithoutThumbnail()
            for (i in 0 until items.length()){
                val id : Int = (items[i] as JSONObject).getInt(MyConfiguration.DATABASE_ELEMENT_CATALYST_ID)
                val urlSharedPicture : String = (items[i] as JSONObject).getString(MyConfiguration.DATABASE_ELEMENT_CATALYST_URL_PICTURE)
                if(urlSharedPicture.isEmpty()) continue
                val urlPicture = MyConfiguration.getPictureUrlFromGoogle(urlSharedPicture, 128, 128)
                if(database.updateCatalyst(id, URL(urlPicture).readBytes()) == false) throw Exception()
            }
        }catch (e:Exception){
            //nothing
        }finally {
            JOB_IS_EXISTS = false
        }
    }

    //method for debuging
    val mHandler: Handler = Handler()
    fun showToast(text: CharSequence?) {
        mHandler.post {
            Toast.makeText(this@ServiceOfThumbnail, text, Toast.LENGTH_SHORT).show()
        }
    }
}

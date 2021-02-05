package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MyProcessStep
import pl.autokat.components.MyUserInterface
import pl.autokat.databinding.ActivityPictureBinding
import java.net.URL
import java.net.UnknownHostException

class PictureActivity : AppCompatActivity() {

    private lateinit var bindingActivityPicture: ActivityPictureBinding

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bindingActivityPicture = ActivityPictureBinding.inflate(this.layoutInflater)
        val view = this.bindingActivityPicture.root
        this.setContentView(view)
        //get data which sent
        val urlPicture : String = this.intent.getStringExtra("urlPicture")!!.toString()
        //make async task and execute
        Thread(this.TaskDownloadFullPicture(urlPicture)).start()
    }
    //async class which download and set oryginal picture in full size
    inner class TaskDownloadFullPicture(urlPictureInput: String) : Runnable {
        //fields
        private var bitmap : Bitmap? = null
        private var urlPicture : String = urlPictureInput
        //run
        override fun run() {
            //--- onPreExecute
            this@PictureActivity.runOnUiThread {
                //disable user interface on process application
                MyUserInterface.enableActivity(this@PictureActivity.bindingActivityPicture.linearLayout,false)
                Toast.makeText(this@PictureActivity.applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_WAIT, Toast.LENGTH_SHORT).show()
            }
            //--- doInBackground
            var myProcessStep : MyProcessStep = MyProcessStep.SUCCESS
            try{
                val urlThumbnail = MyConfiguration.getPictureUrlFromGoogle(this.urlPicture, 1920, 1080)
                this.bitmap = BitmapFactory.decodeStream(URL(urlThumbnail).openConnection().getInputStream())
            }
            catch(e: UnknownHostException){
                myProcessStep = MyProcessStep.NETWORK_FAILED
            }
            catch(e: Exception){
                myProcessStep = MyProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            this@PictureActivity.runOnUiThread {
                when(myProcessStep){
                    MyProcessStep.NETWORK_FAILED -> {
                        Toast.makeText(this@PictureActivity.applicationContext, MyConfiguration.INFO_MESSAGE_NETWORK_FAILED, Toast.LENGTH_SHORT).show()
                        this@PictureActivity.finish()
                    }
                    MyProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(this@PictureActivity.applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_FAILED, Toast.LENGTH_SHORT).show()
                        this@PictureActivity.finish()
                    }
                    MyProcessStep.SUCCESS -> {
                        this@PictureActivity.bindingActivityPicture.photoView.setImageBitmap(this.bitmap)
                    }
                }
                //enable user interface on process application
                MyUserInterface.enableActivity(this@PictureActivity.bindingActivityPicture.linearLayout,true)
            }
        }
    }
}

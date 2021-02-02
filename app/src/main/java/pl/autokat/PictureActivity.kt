package pl.autokat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.MyConfiguration
import pl.autokat.components.MyProcessStep
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
        val task = this.DownloadOryginalPicture(urlPicture)
        task.execute()
    }

    //async class which download and set oryginal picture in full size
    @SuppressLint("StaticFieldLeak")
    private inner class DownloadOryginalPicture(urlPictureInput: String) : AsyncTask<Void, Void, MyProcessStep>() {
        //fields
        private var bitmap : Bitmap? = null
        private var urlPicture : String = urlPictureInput
        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(this@PictureActivity.applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_WAIT, Toast.LENGTH_SHORT).show()
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                val urlThumbnail = MyConfiguration.getPictureUrlFromGoogle(this.urlPicture, 1920, 1080)
                this.bitmap = BitmapFactory.decodeStream(URL(urlThumbnail).openConnection().getInputStream())
            }
            catch(e: UnknownHostException){
                return MyProcessStep.NETWORK_FAILED
            }
            catch(e: Exception){
                return MyProcessStep.UNHANDLED_EXCEPTION
            }
            return MyProcessStep.SUCCESS
        }
        //post execute
        override fun onPostExecute(result: MyProcessStep) {
            super.onPostExecute(result)
            //do job depends on situation
            when(result){
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
        }
    }
}

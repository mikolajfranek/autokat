package pl.autokat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.databinding.ActivityPictureBinding
import java.net.URL
import java.net.UnknownHostException

class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //get data which sent
        val urlPicture : String = intent.getStringExtra("urlPicture")!!.toString()
        //make async task and execute
        val task = DownloadOryginalPicture(urlPicture)
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
            Toast.makeText(applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_WAIT, Toast.LENGTH_SHORT).show()
        }
        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): MyProcessStep {
            try{
                val urlThumbnail = MyConfiguration.getPictureUrlFromGoogle(urlPicture, 1920, 1080)
                bitmap = BitmapFactory.decodeStream(URL(urlThumbnail).openConnection().getInputStream())
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
                    Toast.makeText(applicationContext, MyConfiguration.INFO_MESSAGE_NETWORK_FAILED, Toast.LENGTH_SHORT).show()
                    finish()
                }
                MyProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_FAILED, Toast.LENGTH_SHORT).show()
                    finish()
                }
                MyProcessStep.SUCCESS -> {
                    binding.activityPicture.setImageBitmap(bitmap)
                }
            }
        }
    }
}

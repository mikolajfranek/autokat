package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.Configuration
import pl.autokat.components.Parser
import pl.autokat.enums.ProcessStep
import pl.autokat.components.UserInterface
import pl.autokat.databinding.ActivityPictureBinding
import java.net.URL
import java.net.UnknownHostException

class PictureActivity : AppCompatActivity() {

    private lateinit var activityPictureBinding: ActivityPictureBinding

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPictureBinding = ActivityPictureBinding.inflate(layoutInflater)
        val view = activityPictureBinding.root
        setContentView(view)
        val urlPicture: String = intent.getStringExtra("urlPicture")!!.toString()
        Thread(TaskDownloadFullPicture(urlPicture)).start()
    }
    //endregion

    inner class TaskDownloadFullPicture(urlPictureInput: String) : Runnable {
        private var bitmap: Bitmap? = null
        private var urlPicture: String = urlPictureInput

        override fun run() {
            //--- onPreExecute
            runOnUiThread {
                UserInterface.changeStatusLayout(
                    activityPictureBinding.linearLayout,
                    false
                )
                Toast.makeText(
                    this@PictureActivity.applicationContext,
                    Configuration.BITMAP_WAIT,
                    Toast.LENGTH_SHORT
                ).show()
            }
            //--- doInBackground
            var processStep: ProcessStep = ProcessStep.SUCCESS
            try {
                val parsedUrlPicture =
                    Parser.parseUrlOfPicture(urlPicture, 1920, 1080)
                bitmap =
                    BitmapFactory.decodeStream(URL(parsedUrlPicture).openConnection().getInputStream())
            } catch (e: UnknownHostException) {
                processStep = ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                processStep = ProcessStep.UNHANDLED_EXCEPTION
            }
            //--- onPostExecute
            runOnUiThread {
                when (processStep) {
                    ProcessStep.NETWORK_FAILED -> {
                        Toast.makeText(
                            this@PictureActivity.applicationContext,
                            Configuration.NETWORK_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    ProcessStep.UNHANDLED_EXCEPTION -> {
                        Toast.makeText(
                            this@PictureActivity.applicationContext,
                            Configuration.BITMAP_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    ProcessStep.SUCCESS -> {
                        activityPictureBinding.photoView.setImageBitmap(bitmap)
                    }
                    else -> {
                        //
                    }
                }
                UserInterface.changeStatusLayout(
                    activityPictureBinding.linearLayout,
                    true
                )
            }
        }
    }
}
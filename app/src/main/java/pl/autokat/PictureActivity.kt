package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.autokat.components.Configuration
import pl.autokat.components.Parser
import pl.autokat.components.UserInterface
import pl.autokat.databinding.ActivityPictureBinding
import pl.autokat.enums.ProcessStep
import java.net.URL
import java.net.UnknownHostException

class PictureActivity : AppCompatActivity() {

    private lateinit var activityPictureBinding: ActivityPictureBinding

    //region methods used in override
    private fun init() {
        activityPictureBinding = ActivityPictureBinding.inflate(layoutInflater)
        val view = activityPictureBinding.root
        setContentView(view)
    }

    private fun receiveExtraUrlAndDownload() {
        val urlPicture: String = intent.getStringExtra("urlPicture")!!.toString()
        Thread(RunnableDownloadFullPicture(urlPicture)).start()
    }
    //endregion

    //region override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        receiveExtraUrlAndDownload()
    }
    //endregion

    //region inner classes
    inner class RunnableDownloadFullPicture(urlPictureInput: String) : Runnable {

        private var bitmap: Bitmap? = null
        private var urlPicture: String = urlPictureInput

        //region methods of run
        private fun onPreExecute() {
            UserInterface.changeStatusLayout(activityPictureBinding.linearLayout, false)
            Toast.makeText(applicationContext, Configuration.BITMAP_WAIT, Toast.LENGTH_SHORT).show()
        }

        private fun doInBackground(): ProcessStep {
            return try {
                val parsedUrlPicture = Parser.parseUrlOfPicture(urlPicture, 3840, 2160)
                bitmap = BitmapFactory.decodeStream(
                    URL(parsedUrlPicture).openConnection().getInputStream()
                )
                ProcessStep.SUCCESS
            } catch (e: UnknownHostException) {
                ProcessStep.NETWORK_FAILED
            } catch (e: Exception) {
                ProcessStep.UNHANDLED_EXCEPTION
            }
        }

        private fun onPostExecute(processStep: ProcessStep) {
            when (processStep) {
                ProcessStep.NETWORK_FAILED -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.NETWORK_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                ProcessStep.UNHANDLED_EXCEPTION -> {
                    Toast.makeText(
                        applicationContext,
                        Configuration.BITMAP_FAILED,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                ProcessStep.SUCCESS -> {
                    activityPictureBinding.photo.setImageBitmap(bitmap)
                }
                else -> {
                    //
                }
            }
            UserInterface.changeStatusLayout(activityPictureBinding.linearLayout, true)
        }
        //endregion

        override fun run() {
            runOnUiThread {
                onPreExecute()
            }
            val processStep: ProcessStep = doInBackground()
            runOnUiThread {
                onPostExecute(processStep)
            }
        }
    }
    //endregion
}
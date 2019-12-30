package pl.autokat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_picture.*


class PictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)

        //get data which sent
        val idPicture : String = intent.getStringExtra("idPicture")!!.toString()

        //make async task and execute
        val task = DownloadOryginalPicture(idPicture)
        task.execute()
    }

    //async class which download and set oryginal picture
    private inner class DownloadOryginalPicture(idPictureInput: String) : AsyncTask<Void, Void, Boolean>() {

        private var bitmap : Bitmap? = null
        private var idPicture : String = idPictureInput

        //pre execute
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_WAIT, Toast.LENGTH_SHORT).show()
        }

        //do in async mode - in here can't modify user interface
        override fun doInBackground(vararg p0: Void?): Boolean {
            return try{
                bitmap = MySpreadsheet.getBitmapOfIdPicture(idPicture)
                if(bitmap == null) return false
                true
            }catch(e: Exception){
                false
            }
        }

        //post execute
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if(result){
                activity_picture.setImageBitmap(bitmap)
            }else{
                Toast.makeText(applicationContext, MyConfiguration.INFO_DOWNLOAD_BITMAP_FAILED, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
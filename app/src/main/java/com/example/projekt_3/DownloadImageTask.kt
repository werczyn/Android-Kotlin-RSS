package com.example.projekt_3

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.URL

//https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android

class DownloadImageTask(bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {

    var bmImage: ImageView = bmImage

    override fun doInBackground(vararg params: String?): Bitmap? {
        val urldisplay = params[0]
        var mIcon11: Bitmap? = null
        try {
            val input: InputStream = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }

}
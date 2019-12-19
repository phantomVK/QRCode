package com.phantomvk.qrcode.zxingdemo

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.WriterException
import com.phantomvk.qrcode.zxing.Encoder
import kotlinx.android.synthetic.main.activity_generator.*
import java.lang.ref.SoftReference

class GeneratorActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
        val size = (resources.displayMetrics.density * 150).toInt()

        text1.text = "https://google.com/"
        CodeTask("https://google.com/", size, resources, image1)

        text2.text = "https://bing.com/"
        CodeTask("https://bing.com/", size, resources, image2)

        text3.text = "https://github.com/"
        CodeTask("https://github.com/", size, resources, image3)

        text4.text = "https://stackoverflow.com/"
        CodeTask("https://stackoverflow.com/", size, resources, image4)

        text5.text = "https://medium.com/"
        CodeTask("https://medium.com/", size, resources, image5)

        text6.text = "https://youtube.com/"
        CodeTask("https://youtube.com/", size, resources, image6)
    }

}

class CodeTask(private val contents: String,
               private val pixels: Int,
               private val resources: Resources,
               imageView: AppCompatImageView) : AsyncTask<Void, Void, Bitmap?>() {

    private val softRef: SoftReference<AppCompatImageView> = SoftReference(imageView)

    init {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

    override fun doInBackground(vararg params: Void?): Bitmap? {
        return try {
            val dst = Encoder.encodeQrCode(contents, pixels, HINTS)
            val src = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            Encoder.drawBitmap(dst, src, PAINT)
        } catch (e: WriterException) {
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) softRef.get()?.setImageBitmap(result)
    }

    private companion object {
        private val PAINT = Encoder.getPaint()
        private val HINTS = Encoder.getHints()
    }
}

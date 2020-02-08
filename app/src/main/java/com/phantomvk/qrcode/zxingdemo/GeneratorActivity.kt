package com.phantomvk.qrcode.zxingdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.WriterException
import com.phantomvk.qrcode.core.util.CoreUtil
import com.phantomvk.qrcode.zxing.Decoder
import com.phantomvk.qrcode.zxing.Encoder
import kotlinx.android.synthetic.main.activity_generator.*
import java.lang.ref.SoftReference


class GeneratorActivity : AppCompatActivity() {

    private val tasks = ArrayList<EncodeTask>(6)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
        val size = CoreUtil.dp(this, 180).toInt()

        text1.text = "https://google.com/"
        tasks += EncodeTask("https://google.com/", size, resources, image1)

        text2.text = "https://bing.com/"
        tasks += EncodeTask("https://bing.com/", size, resources, image2)

        text3.text = "https://github.com/"
        tasks += EncodeTask("https://github.com/", size, resources, image3)

        text4.text = "https://stackoverflow.com/"
        tasks += EncodeTask("https://stackoverflow.com/", size, resources, image4)

        text5.text = "https://medium.com/"
        tasks += EncodeTask("https://medium.com/", size, resources, image5)

        text6.text = "https://youtube.com/"
        tasks += EncodeTask("https://youtube.com/", size, resources, image6)
    }

    override fun onDestroy() {
        super.onDestroy()

        tasks.filter { it.status != AsyncTask.Status.FINISHED }
            .forEach { it.cancel(true) }
    }
}

class EncodeTask(
    private val contents: String,
    private val pixels: Int,
    private val resources: Resources,
    imageView: AppCompatImageView
) : AsyncTask<Void, Void, Bitmap?>() {

    private val softRef = SoftReference(imageView)

    init {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

    override fun doInBackground(vararg params: Void?): Bitmap? {
        if (isCancelled) return null
        return try {
            val dst = Encoder.encodeQrCode(contents, pixels, HINTS)
            val src = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            Encoder.drawBitmap(dst, src, PAINT)
        } catch (e: WriterException) {
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        val v = softRef.get()
        if (result == null || v == null) return

        v.setImageBitmap(result)
        v.setOnLongClickListener {
            val bitmap = (v.drawable as BitmapDrawable).bitmap
            val applicationContext = v.context.applicationContext
            DecodeTask(bitmap, applicationContext)
            return@setOnLongClickListener true
        }
    }

    private companion object {
        private val PAINT = Encoder.getPaint()
        private val HINTS = Encoder.getHints()
    }
}

class DecodeTask(
    private val bitmap: Bitmap,
    private val context: Context
) : AsyncTask<Void, Void, String?>() {

    init {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

    override fun doInBackground(vararg params: Void?): String? {
        return Decoder.decode(bitmap)
    }

    override fun onPostExecute(result: String?) {
        if (result != null) Toast.makeText(context, result, Toast.LENGTH_LONG).show()
    }
}

package com.phantomvk.qrcode.zxingdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GeneratorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
    }

    companion object {
        fun start(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, GeneratorActivity::class.java))
        }
    }
}

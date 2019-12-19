package com.phantomvk.qrcode.zxingdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanQRCode.setOnClickListener {
            startActivity(Intent(this, ScannerActivity::class.java))
        }

        generateQRCode.setOnClickListener {
            startActivity(Intent(this, GeneratorActivity::class.java))
        }
    }
}

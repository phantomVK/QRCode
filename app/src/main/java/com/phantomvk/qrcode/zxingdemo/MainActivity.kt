package com.phantomvk.qrcode.zxingdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanQRCode.setOnClickListener { ScannerActivity.start(this) }
        generateQRCode.setOnClickListener { GeneratorActivity.start(this) }
    }
}

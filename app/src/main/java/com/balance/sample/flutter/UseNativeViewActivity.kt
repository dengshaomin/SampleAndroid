package com.balance.sample.flutter

import android.os.Bundle
import com.balance.sample.R
import io.flutter.embedding.android.FlutterActivity

class UseNativeViewActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_native_view)
    }
}
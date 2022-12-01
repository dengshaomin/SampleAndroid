package com.balance.sample.flutter

import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import io.flutter.embedding.android.FlutterActivity

class BalanceFlutterActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).init()
    }
}
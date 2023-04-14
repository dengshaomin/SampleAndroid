package com.balance.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balance.sample.autoservice.ComponentBuilder
import com.balance.sample.autoservice.ComponentConstants
import com.balance.sample.autoservice.ComponentManager
import com.balance.sample.bottomsheet.BottomCardActivity
import com.balance.sample.flutter.FlutterEnterActivity
import com.balance.sample.h5offline.WebActivity
import com.balance.sample.hook.HookActivity
import com.balance.sample.preweb.PreWebViewActivity
import com.balance.sample.scanwifi.WifiScanActivity
import com.balance.sample.touchevent.TouchActivity
import com.balance.sample.viewpager.ViewPagerActivity
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
//
    fun enterH5(view: View) {
        startActivity(Intent(this, WebActivity::class.java))
    }

    fun enterFlutter(view: View) {
        startActivity(Intent(this, FlutterEnterActivity::class.java))
    }

    fun enterAutoService(view: View) {
        val value = ComponentManager.action(
            ComponentBuilder(this,
                ComponentConstants.ComponentA.COMPONENT_A,
                ComponentConstants.ComponentA.METHOD_0,
                mutableMapOf<String, Any>().apply {
                    put("key", "123")
                })
        )
        Toast.makeText(this, "result from component:${value}", Toast.LENGTH_SHORT).show()
    }

    fun enterTouchEvent(view: View) {
        startActivity(Intent(this, TouchActivity::class.java))
    }

    fun enterViewPager(view: View) {
        startActivity(Intent(this, ViewPagerActivity::class.java))
    }

    fun enterHook(view: View) {
        startActivity(Intent(this, HookActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
//        Toast.makeText(this, ClipboardUtils.getText().toString(), Toast.LENGTH_SHORT).show()
    }

    fun enterWifiScan(view: View) {
        startActivity(Intent(this, WifiScanActivity::class.java))
    }

    fun enterPreloadWeb(view: View) {
        startActivity(Intent(this,PreWebViewActivity::class.java))
    }
}
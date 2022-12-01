package com.balance.sample.flutter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class NativeMethodChannel {
    private lateinit var context: Context

    companion object {
        const val native_method_channel = "native_method_channel"
        val instance: NativeMethodChannel by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NativeMethodChannel()
        }
    }

    fun init(context: Context) {
        this.context = context
    }

    fun registerMethodChannel(flutterEngine: FlutterEngine):MethodChannel {
        return MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            native_method_channel
        ).apply {
            setMethodCallHandler { call, result ->
                when (call.method) {
                    "getBatteryLevelAsync" -> {
                        result.success(getBatteryLevel())
                    }
                }
            }
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(context).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }
        return batteryLevel
    }
}
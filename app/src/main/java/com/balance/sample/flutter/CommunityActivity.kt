package com.balance.sample.flutter

import android.os.Bundle
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.*

class CommunityActivity : FlutterActivity() {
    private var methodChannel: MethodChannel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            Thread.sleep(3000)
            withContext(Dispatchers.Main) {
                methodChannel?.invokeMethod(
                    "getFlutterMethod",
                    null,
                    object : MethodChannel.Result {
                        override fun success(result: Any?) {
                            Toast.makeText(
                                this@CommunityActivity,
                                result?.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun error(
                            errorCode: String?,
                            errorMessage: String?,
                            errorDetails: Any?
                        ) {
                            Toast.makeText(this@CommunityActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun notImplemented() {
                        }

                    })
            }
        }
        runBlocking {
            Thread.sleep(1000)
        }
    }

    override fun onBackPressed() {
//        setResult(1001, Intent().apply {
//            putExtra("data", this@CommunityActivity.localClassName)
//        })
        finish()
    }

    override fun getInitialRoute(): String? {
        return "community"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        methodChannel = NativeMethodChannel.instance.registerMethodChannel(flutterEngine)
    }
}
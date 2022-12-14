package com.balance.sample

import android.app.Application
import android.content.ClipData
import android.os.IBinder
import android.os.IInterface
import android.util.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class App : Application() {
    companion object {
        lateinit var application: Application
    }
    lateinit var flutterEngine: FlutterEngine
    override fun onCreate() {
        super.onCreate()
        application  = this
        // Instantiate a FlutterEngine.
        flutterEngine = FlutterEngine(this)

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
            .getInstance()
            .put("cache_0", flutterEngine)
//        hookClipBoardService()
    }
    private fun hookClipBoardService() {
        val serviceManager = Class.forName("android.os.ServiceManager")
        val getService = serviceManager.getDeclaredMethod("getService", String::class.java)
        val remoteBinder = getService.invoke(null, CLIPBOARD_SERVICE) as IBinder
        val hookBinder = Proxy.newProxyInstance(
            serviceManager.classLoader,
            arrayOf<Class<*>>(
                IBinder::class.java
            ), ClipboardHookRemoteBinderHandler(remoteBinder)
        ) as IBinder
        val cacheField = serviceManager.getDeclaredField("sCache")
        cacheField.isAccessible = true
        val sCache = cacheField.get(null) as MutableMap<String, IBinder>
        sCache[CLIPBOARD_SERVICE] = hookBinder
    }


    class ClipboardHookRemoteBinderHandler(var remoteBinder: IBinder) : InvocationHandler {
        private lateinit var stubClass: Class<*>
        private lateinit var iInterface: Class<*>

        init {
            try {
                stubClass = Class.forName("android.content.IClipboard\$Stub")
                iInterface = Class.forName("android.content.IClipboard")
            } catch (e: ClassNotFoundException) {
                Log.e("balance", e.message.toString())
            }
        }

        @Throws(Throwable::class)
        override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
            return if ("queryLocalInterface" == method.name) {
                Proxy.newProxyInstance(
                    proxy.javaClass.classLoader, arrayOf(
                        IBinder::class.java,
                        IInterface::class.java, iInterface
                    ),
                    ClipboardHookLocalBinderHandler(remoteBinder, stubClass)
                )
            } else method.invoke(remoteBinder, *args)
        }
    }

    class ClipboardHookLocalBinderHandler(remoteBinder: IBinder, stubClass: Class<*>) :
        InvocationHandler {
        // ?????????Service?????? (IInterface)
        private lateinit var iInterface: IInterface

        init {
            kotlin.runCatching {
                val asInterfaceMethod = stubClass.getDeclaredMethod(
                    "asInterface",
                    IBinder::class.java
                )
                iInterface = asInterfaceMethod.invoke(null, remoteBinder) as IInterface
            }
        }

        override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
            // ?????????????????????????????? "you are hooked"
            Log.e("balance","${method.name}:${args.toString()}")
            if ("getPrimaryClip" == method.name) {
                return ClipData.newPlainText(null, "you are hooked")
            }
            // ????????????,???????????????????????????????????????
            return if ("hasPrimaryClip" == method.name) {
                true
            } else method.invoke(iInterface, *args)
        }
    }
}
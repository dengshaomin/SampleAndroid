package com.balance.sample.preweb

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.MutableContextWrapper
import android.os.Bundle
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebView
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class WebFactory {
    private val webPool = Stack<WebView>()
    private val inited = AtomicBoolean(false)
    private val activitys = mutableMapOf<WeakReference<Activity>, WebView>()

    companion object {
        val instance: WebFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            WebFactory()
        }
    }

    fun init(application: Application, count: Int) {
        if (!inited.compareAndSet(false, true)) {
            return
        }
        Looper.myQueue().addIdleHandler {
            for (i in 0 until count) {
                webPool.push(createWebView(application))
            }
            false
        }
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                synchronized(activitys){
                    val iterator = activitys.iterator()
                    while (iterator.hasNext()){
                        val enter  = iterator.next()
                        if(enter.key.get() == activity){
                            val webView = enter.value
                            webView.loadUrl("")
                            (webView.parent as ViewGroup).removeView(webView)
                            webPool.push(webView)
                            iterator.remove()
                        }
                    }
                }
            }
        })
    }

    fun pick(activity: Activity,url:String?):WebView {
        var webView:WebView
        if(webPool.isNotEmpty()){
            webView = webPool.pop()
            activitys[WeakReference(activity)] = webView
        }else{
            webView = createWebView(activity.application)
        }
        val contextWrapper = webView.context as MutableContextWrapper
        contextWrapper.baseContext = activity
        webView.loadUrl(url?:"")
        return webView
    }

    private fun createWebView(application: Application): WebView {
        return WebView(MutableContextWrapper(application)).apply {
            settings.javaScriptEnabled = true
        }
    }
}
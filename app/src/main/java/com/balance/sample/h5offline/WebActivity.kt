package com.balance.sample.h5offline

import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.balance.sample.databinding.ActivityWebBinding
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ZipUtils
import java.io.File

class WebActivity : AppCompatActivity() {
    private var url: String = "https://www.baidu.com/"
    private lateinit var binding: ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.apply {
            this.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                }
            }
            this.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
//                    if (request != null) {
//                        val res =
//                            GlideImgCacheManager.interceptRequest(view, request.getUrl())
//                        if (res != null) {
//                            return res;
//                        }
//                    }
                    return super.shouldInterceptRequest(view, request)
                }
            }
            this.settings.apply {
                javaScriptEnabled = true
                //appCache android 13后将废弃，chrome不再支持
//                allowFileAccess = true
//                setAppCacheEnabled(true)
//                setAppCachePath(this@WebActivity.cacheDir.absolutePath +"webview" + File.separator + "appCache")
//                setAppCacheMaxSize(Long.MAX_VALUE)
            }
            loadUrl(this@WebActivity.url)
        }
        loadLocalH5()
    }

    private fun loadLocalH5() {
        val miniDir = File(filesDir,"mini")
        val assetsDir = "mini"
        val result = ResourceUtils.copyFileFromAssets(assetsDir, miniDir.absolutePath)
        miniDir.listFiles().filter {
            it.name.endsWith(".zip")
        }?.map {
            ZipUtils.unzipFile(it,File(miniDir,it.nameWithoutExtension))
        }
        binding.webView.loadUrl(Uri.fromFile(File(miniDir,"h5_lab/dist/index.html")).toString())
    }
}
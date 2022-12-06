package com.balance.sample.h5offline

import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import com.balance.sample.App
import okhttp3.Cache
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object OkHttpCacheManager {
    private var allCount = 0
    private var cacheCount = 0
    private val mHttpClient: OkHttpClient
    private val TAG = "OkHttpCacheManager"
    private var sOkHttpCacheManager: OkHttpCacheManager? = null

    //只缓存白名单中的资源
    private val CACHE_MIME_TYPE = object : HashSet<String>() {
        init {
            add("html")
            add("htm")
            add("js")
            add("ico")
            add("css")
            add("png")
            add("jpg")
            add("jpeg")
            add("gif")
            add("bmp")
            add("ttf")
            add("woff")
            add("woff2")
            add("otf")
            add("eot")
            add("svg")
            add("xml")
            add("swf")
            add("txt")
            add("text")
            add("conf")
            add("webp")
        }
    }

    init {
        //设置缓存的目录文件
        val httpCacheDirectory =
            File(App.application.externalCacheDir, "x-webview-http-cache")
        //仅作为日志使用
        if (httpCacheDirectory.exists()) {
            val result = httpCacheDirectory.listFiles()
            for (file in result) {
                Log.e(TAG, "file = " + file.absolutePath)
            }
        }
        //缓存的大小，OkHttp会使用DiskLruCache缓存
        val cacheSize = 20 * 1024 * 1024 // 20 MiB
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())
        //设置缓存
        mHttpClient =
            OkHttpClient.Builder().addNetworkInterceptor(MyOkHttpCacheInterceptor()).cache(cache)
                .build()
    }

    /**
     * 针对url级别的缓存，包括主文档，图片，js，css等
     *
     * @param url
     * @param headers
     * @return
     */
    fun interceptRequest(url: String, headers: Map<String?, String?>?): WebResourceResponse? {
        try {
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (TextUtils.isEmpty(extension) || !CACHE_MIME_TYPE.contains(extension.toLowerCase())) {
                //不在支持的缓存范围内
                Log.e(TAG, "+" + url + " 's extension is " + extension + "!!not support...")
                return null
            }
            val startTime = System.currentTimeMillis()
            val reqBuilder: Request.Builder = Request.Builder().url(url)
            if (headers != null) {
                for ((key, value) in headers) {
                    Log.e(TAG, String.format("header:(%s=%s)", key, value))
                    reqBuilder.addHeader(key!!, value!!)
                }
            }
            val request: Request = reqBuilder.get().build()
            val response = mHttpClient.newCall(request).execute()
            if (response.code() != 200) {
                Log.e(TAG, "response code = " + response.code() + ",extension = " + extension)
                return null
            }

            //  String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
            val mimeType: String = MediaType.parse(url)?.type() ?: ""
            Log.e(TAG, "mimeType = $mimeType,extension = $extension,url = $url")
            val okHttpWebResourceResponse =
                WebResourceResponse(mimeType, "", response.body()!!.byteStream())
            val cacheRes = response.cacheResponse()
            val endTime = System.currentTimeMillis()
            val costTime = endTime - startTime
            allCount++
            if (cacheRes != null) {
                cacheCount++
                Log.e(
                    TAG,
                    String.format(
                        "count rate = (%s),costTime = (%s);from cache: %s",
                        1.0f * cacheCount / allCount,
                        costTime,
                        url
                    )
                )
            } else {
                Log.e(TAG, String.format("costTime = (%s);from server: %s", costTime, url))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var message = response.message()
                if (TextUtils.isEmpty(message)) {
                    message = "OK"
                }
                try {
                    okHttpWebResourceResponse.setStatusCodeAndReasonPhrase(response.code(), message)
                } catch (e: Exception) {
                    return null
                }
                val stringListMap: Map<String, List<String>?> = response.headers().toMultimap()
                val header: MutableMap<String, String> = HashMap()
                for ((key, value) in stringListMap) {
                    if (value != null && !value.isEmpty()) {
                        header[key] = value[0]
                    }
                }
                //    Map<String, String> header = MimeTypeMap.multimapToSingle(response.headers().toMultimap());
                okHttpWebResourceResponse.responseHeaders = header
            }
            return okHttpWebResourceResponse
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

}
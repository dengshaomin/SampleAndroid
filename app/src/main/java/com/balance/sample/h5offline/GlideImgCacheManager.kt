package com.balance.sample.h5offline

import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object GlideImgCacheManager {
    private const val TAG = "GlideCache"
    private var sGlideImgCacheManager: GlideImgCacheManager? = null

    //只缓存白名单中的图片资源
    private val CACHE_IMG_TYPE: HashSet<*> = object : HashSet<Any?>() {
        init {
            add("png")
            add("jpg")
            add("jpeg")
            add("bmp")
            add("webp")
        }
    }
    private const val CACHE_CSS_TYPE = "css"

    /**
     * webview本身通过配置可以达到缓存缓存js/css/image等资源
     * 对于一些全局静态/base等js/css/image可以打包放在assets里，然后通过interceptRequest拦截加载本地的
     *
     * */
    /**
     * 拦截资源
     * @param url
     * @return 请求结果
     */
    fun interceptRequest(webView: WebView?, uri: Uri?): WebResourceResponse? {

//        / 步骤1:拦截资源请求URL，并判断URL里的资源的文件名是否包含事先缓存在本地的文件名
        // 假设拦截到的资源请求URL为：https://mms-secr.cdn.bcebos.com/xuanfa1/wenzi1.png
        // 资源文件名为:wenzi1.png
//        if (request.getUrl().toString().contains("wenzi1.png")) {
//            // 步骤2:创建一个输入流
//            InputStream ins = null;
//            try {
//                // 步骤3:获得需要替换的资源(存放在assets文件夹里)
//                // a. 先在app/src/main下创建一个assets文件夹
//                // b. 在assets文件夹里再创建一个images文件夹
//                // c. 在images文件夹放上我们要缓存的资源wenzi1.png
//                ins = getApplicationContext().getAssets().open("images/wenzi1.png");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            // 步骤4:替换资源
//            // 参数1：http请求里该图片的Content-Type,此处图片为image/png
//            // 参数2：编码类型
//            // 参数3：存放着替换资源的输入流
//            return new WebResourceResponse("image/png","utf-8", ins);
//        }
        try {
            val url = uri?.toString()
            val host = uri?.host
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            LogUtils.e("balance", url)
            if (TextUtils.isEmpty(extension) || webView == null || url == null || host == null) {
                //不在支持的缓存范围内
                return null
            }
            if (CACHE_IMG_TYPE.contains(extension.lowercase())) {
                return getImageCache(webView!!, url)
            }
//            when (extension) {
//                CACHE_CSS_TYPE -> {
//                    return getCssCache(webView!!,host, url!!)
//                }
//            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

    private fun getCssCache(webView: WebView, host:String,url: String): WebResourceResponse? {
        val dir = EncryptUtils.encryptSHA1ToString(host)
        val cacheName = EncryptUtils.encryptSHA1ToString(url)
        val cachePath = File(webView.context.cacheDir,"${dir}/${cacheName}")
        if(cachePath.exists()){
//            return WebResourceResponse(webView,"UTF-8",)
        }
        return null
    }

    private fun getImageCache(webView: WebView, url: String): WebResourceResponse? {
        val bitmap =
            Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(url)
                .submit().get()
        getBitmapInputStream(bitmap, Bitmap.CompressFormat.JPEG)?.let {
            return WebResourceResponse("image/jpg", "UTF-8", it)
        }
        return null
    }

    /**
     * 将bitmap进行压缩转换成InputStream
     *
     * @param bitmap
     * @param compressFormat
     * @return
     */
    private fun getBitmapInputStream(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat
    ): InputStream? {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(compressFormat, 80, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()
            return ByteArrayInputStream(data)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }
}
package com.balance.sample.h5offline

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class MyOkHttpCacheInterceptor: Interceptor {
    private var maxAga = 365 //default
    private var timeUnit = TimeUnit.DAYS
    fun setMaxAge(maxAga: Int, timeUnit: TimeUnit) {
        this.maxAga = maxAga
        this.timeUnit = timeUnit
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val cacheControl: CacheControl = CacheControl.Builder()
            .maxAge(maxAga, timeUnit)
            .build()
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}
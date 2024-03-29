package com.balance.sample.asyncloadview/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.util.Pools.SynchronizedPool
import java.util.concurrent.ArrayBlockingQueue

class CustomerAsyncLayoutInflater1(val context: Context) {
    companion object {
        private const val TAG = "com.balance.sample.asyncloadview.CustomerAsyncLayoutInflater"
    }

    var mInflateThread = InflateThread.instance()
    private val mHandlerCallback = Handler.Callback { msg ->
        val request = msg.obj as InflateRequest
        (request.callback as? (InflateBean<View>) -> Unit)?.invoke(
            InflateBean<View>().apply {
                view = request.view
            }
        )
        mInflateThread.releaseRequest(request)
        true
    }
    var mHandler: Handler = Handler(mHandlerCallback)

    @UiThread
    inline fun <reified T> inflate(
        noinline callback: (InflateBean<T>) -> Unit
    ) {
        if (callback == null) {
            throw NullPointerException("callback argument may not be null!")
        }
        val request = mInflateThread.obtainRequest()
        request.inflater = this
        request.cls = T::class.java
        request.callback = callback
        mInflateThread.enqueue(request)
    }



    class InflateRequest internal constructor() {
        var inflater: CustomerAsyncLayoutInflater1? = null
        var cls: Any? = null
        var view: View? = null
        var callback: Any? = null
    }


    class InflateThread : Thread() {
        private val mQueue = ArrayBlockingQueue<InflateRequest>(10)
        private val mRequestPool = SynchronizedPool<InflateRequest>(10)

        // Extracted to its own method to ensure locals have a constrained liveness
        // scope by the GC. This is needed to avoid keeping previous request references
        // alive for an indeterminate amount of time, see b/33158143 for details
        @SuppressLint("LongLogTag")
        private fun runInner() {
            val request: InflateRequest
            request = try {
                mQueue.take()
            } catch (ex: InterruptedException) {
                // Odd, just continue
                Log.w(TAG, ex)
                return
            }
            try {
                request.view = ((request.cls as Class<View>).getConstructor(Context::class.java)).newInstance(request.inflater?.context)
            } catch (ex: RuntimeException) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(
                    TAG,
                    "Failed to inflate resource in the background! Retrying on the UI"
                            + " thread", ex
                )
            }
            Message.obtain(request.inflater?.mHandler, 0, request)
                .sendToTarget()
        }

        override fun run() {
            while (true) {
                runInner()
            }
        }

        fun obtainRequest(): InflateRequest {
            var obj = mRequestPool.acquire()
            if (obj == null) {
                obj = InflateRequest()
            }
            return obj
        }

        fun releaseRequest(obj: InflateRequest) {
            obj.callback = null
            obj.inflater = null
            obj.cls = null
            obj.view = null
            mRequestPool.release(obj)
        }

        fun enqueue(request: InflateRequest) {
            try {
                mQueue.put(request)
            } catch (e: InterruptedException) {
                throw RuntimeException(
                    "Failed to enqueue async inflate request", e
                )
            }
        }

        companion object {
            fun instance(): InflateThread {
                val ins = InflateThread()
                ins.start()
                return ins
            }
        }
    }

}

class InflateBean<T> {
    var view: T? = null
    var parent: ViewGroup? = null

}
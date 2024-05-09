package com.balance.sample.websocket

import android.os.Bundle
import android.text.Html
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balance.sample.databinding.ActivityWebSocketBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit


class WebSocketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebSocketBinding
    private var httpClient = OkHttpClient.Builder().pingInterval(3, TimeUnit.SECONDS).build()
    private var connected = false
    private var webSocket: WebSocket? = null
    private val msgs = mutableListOf<String>()
    private var reConnectionJob: Job? = null
    private val RetryConnectTime = 5000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebSocketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNet()
        binding.connect.setOnClickListener {
            doConnect()
        }
        binding.disconnect.setOnClickListener {
            if (!connected) {
                return@setOnClickListener
            }
            webSocket?.close(1000, "close")
        }
        binding.send.setOnClickListener {
            if (!connected) {
                ToastUtils.showShort("connect first")
                return@setOnClickListener
            }
            webSocket?.send(binding.etContent.text.trim().toString())
            binding.etContent.text = null
        }
        binding.msgList.apply {
            layoutManager = LinearLayoutManager(this@WebSocketActivity)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    return object : RecyclerView.ViewHolder(TextView(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }) {}
                }

                override fun getItemCount(): Int {
                    return msgs.size
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    (holder.itemView as TextView).text = Html.fromHtml(msgs[position])
                }

            }
        }
//        startStomp()
    }

    private fun startStomp() {
        val mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            "ws://pe-dingdong.nio.com/pe/dingdong/msg/websocket"
        )
//        wss://pe-dingdong.nio.com/pe/dingdong/msg/958/y1agrlsb/websocket?ws_app_id=100119
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000)
        val compositeDisposable = CompositeDisposable()
        val dispLifecycle: Disposable = mStompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        val a = 1
                    }

                    LifecycleEvent.Type.ERROR -> {
                        val a = 1
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        val a = 1
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        val a = 1
                    }
                }
            }
        val dispTopic = mStompClient.topic("/room/h5/charge-map")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage: StompMessage ->
                LogUtils.e("nothing",topicMessage?.payload?.toString())
            }
            ) { throwable: Throwable? ->
                val a = 1
            }

        compositeDisposable.add(dispTopic)
        compositeDisposable.add(dispLifecycle)
        mStompClient.connect()

    }

    private val netChangeListener = object : NetworkUtils.OnNetworkStatusChangedListener {
        override fun onDisconnected() {
            addMsg("网络断开了")
            connected = false
            changeButtonState()
        }

        override fun onConnected(networkType: NetworkUtils.NetworkType?) {
            addMsg("网络恢复了")
            tryReConnect()
        }
    }

    private fun initNet() {
//        NetworkUtils.registerNetworkStatusChangedListener(netChangeListener)
    }

    override fun onDestroy() {
        NetworkUtils.unregisterNetworkStatusChangedListener(netChangeListener)
        super.onDestroy()
    }

    private val listener = object : WebSocketListener() {
        /**
         * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
         * messages.
         */
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            connected = true
//            this@WebSocketActivity.webSocket = webSocket
            changeButtonState()
            addMsg("connected success")
        }

        /** Invoked when a text (type `0x1`) message has been received.  */
        override fun onMessage(webSocket: WebSocket?, text: String?) {
            addMsg(text ?: "")
        }

        /** Invoked when a binary (type `0x2`) message has been received.  */
        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            addMsg(bytes?.utf8() ?: "")
        }

        /**
         * Invoked when the remote peer has indicated that no more incoming messages will be
         * transmitted.
         */
        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {

        }

        /**
         * Invoked when both peers have indicated that no more messages will be transmitted and the
         * connection has been successfully released. No further calls to this listener will be made.
         */
        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            connected = false
            changeButtonState()
            addMsg("closed")
        }

        /**
         * Invoked when a web socket has been closed due to an error reading from or writing to the
         * network. Both outgoing and incoming messages may have been lost. No further calls to this
         * listener will be made.
         */
        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            addMsg("fail:${response?.message() ?: t?.message ?: ""}")
//            connected = false
//            changeButtonState()
//            tryReConnect()
            //SocketException 通常意味着底层TCP连接出现了错误，可能是网络断开
            //EOFException 通常表示连接意外关闭，也可能是服务端关闭了连接
//            if (t is SocketException || t is EOFException || t is SSLException) {
            connected = false
            changeButtonState()
            tryReConnect()
//            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun tryReConnect() {
        if (reConnectionJob?.isActive == true) {
            return
        }
        reConnectionJob?.cancel()
        reConnectionJob = null
        reConnectionJob = GlobalScope.launch {
            while (isActive) {
                if (connected) {
                    reConnectionJob?.cancel()
                }
                doConnect()
                delay(RetryConnectTime)
            }
        }
    }

    private fun doConnect() {
        if (connected) {
            return
        }
        if (webSocket != null) {
            webSocket?.close(1000, "")
            webSocket = null
        }
        webSocket = httpClient.newWebSocket(
            Request.Builder().url("ws://pe-dingdong-test.nio.com/pe/dingdong/toc/ws?device_id=1&room_id=2").build(), listener
        )
    }

    private fun addMsg(msg: String) {
        msgs.add(msg)
        runOnUiThread {
            binding.msgList.adapter?.notifyDataSetChanged()
        }
    }

    private fun changeButtonState() {
        runOnUiThread {
            binding.connect.isEnabled = !connected
            binding.disconnect.isEnabled = connected
            binding.connect.setBackgroundColor(getColor(if (!connected) android.R.color.holo_green_light else android.R.color.darker_gray))
            binding.disconnect.setBackgroundColor(getColor(if (connected) android.R.color.holo_green_light else android.R.color.darker_gray))
        }
    }
}
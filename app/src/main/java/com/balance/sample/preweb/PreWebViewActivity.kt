package com.balance.sample.preweb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balance.sample.R
import kotlinx.android.synthetic.main.activity_pre_web_view.*

class PreWebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_web_view)
        container_0.addView(WebFactory.instance.pick(this,"https://www.baidu.com"))
        container_1.addView(WebFactory.instance.pick(this,"https://m.jd.com"))
    }
}
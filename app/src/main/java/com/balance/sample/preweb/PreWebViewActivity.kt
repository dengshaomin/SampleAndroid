package com.balance.sample.preweb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.balance.sample.R
import com.balance.sample.databinding.ActivityPreWebViewBinding

class PreWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.container0.addView(WebFactory.instance.pick(this,"https://www.baidu.com"))
        binding.container1.addView(WebFactory.instance.pick(this,"https://m.jd.com"))
    }
}
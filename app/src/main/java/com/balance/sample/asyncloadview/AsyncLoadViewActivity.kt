package com.balance.sample.asyncloadview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.balance.sample.R
import com.balance.sample.databinding.ActivityAsyncLoadViewBinding

class AsyncLoadViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAsyncLoadViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsyncLoadViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //官方，通过layout id
        AsyncLayoutInflater(this).inflate(
            R.layout.item_banner_item, null
        ) { view, resid, parent ->
            val a = 1
        }

        //异步创建自定义布局
        CustomerAsyncLayoutInflater(this).inflate(
            Button::class.java, null, object :
                CustomerAsyncLayoutInflater.CustomerOnInflateFinishedListener {
                override fun onInflateFinished(view: View?, parent: ViewGroup?) {
                    val a = 1
                }
            }
        )
    }
}

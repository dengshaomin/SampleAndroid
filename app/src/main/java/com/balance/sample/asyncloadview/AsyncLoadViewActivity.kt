package com.balance.sample.asyncloadview

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.setPadding
import com.balance.sample.R
import com.balance.sample.databinding.ActivityAsyncLoadViewBinding
import com.blankj.utilcode.util.SizeUtils

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
        CustomerAsyncLayoutInflater1(this).apply {
            inflate<Button> {
                it.view?.let {
                    it.text = "i am a button"
                    it.isAllCaps = false
                    it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    binding.container.addView(it)
                }

            }
            inflate<TextView> {
                it.view?.let {
                    it.text = "i am a text"
                    it.setPadding(SizeUtils.dp2px(20f))
                    it.gravity = Gravity.CENTER
                    it.setBackgroundResource(R.color.purple_200)
                    it.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = SizeUtils.dp2px(10f)
                    }
                    binding.container.addView(it)
                }
            }
        }
    }
}

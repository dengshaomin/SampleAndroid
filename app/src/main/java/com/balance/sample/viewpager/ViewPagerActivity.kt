package com.balance.sample.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.balance.sample.R
import com.balance.sample.databinding.ActivityViewPagerBinding

class ViewPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPagerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewpage2.apply {
            updateData(mutableListOf<String>().apply {
                add("https://img2.baidu.com/it/u=1395980100,2999837177&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1669741200&t=8eb649ecbed27443f423a05512fcd6b8")
                add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Flmg.jj20.com%2Fup%2Fallimg%2F1114%2F040221103339%2F210402103339-8-1200.jpg&refer=http%3A%2F%2Flmg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1672200478&t=7c7361991a9692b9c345151d7c02fefc")
            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.viewpage2.startLoop()
    }

    override fun onPause() {
        super.onPause()
        binding.viewpage2.stopLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewpage2.onDestory()
    }


}
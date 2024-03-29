package com.balance.sample.touchevent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balance.sample.R
import com.balance.sample.databinding.ActivityTouchBinding
import com.balance.sample.databinding.ActivityWifiScanBinding

class TouchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTouchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nestedList.apply {
            layoutManager = LinearLayoutManager(this@TouchActivity)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    log("onCreateViewHolder")
                    return object : RecyclerView.ViewHolder(TextView(parent.context).apply {
                        layoutParams = RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT.apply {
                                setPadding(50)
                            })
                    }) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    log("onBindViewHolder$position")
                    (holder.itemView as TextView).text = position.toString()
                }

                override fun getItemCount(): Int {
                    return 30
                }
            }
        }
    }
    private fun log(s: String) {
        Log.e("balance", s)
    }

}
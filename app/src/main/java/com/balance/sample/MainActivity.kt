package com.balance.sample

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balance.sample.asyncloadview.AsyncLoadViewActivity
import com.balance.sample.autoservice.ComponentBuilder
import com.balance.sample.autoservice.ComponentConstants
import com.balance.sample.autoservice.ComponentManager
import com.balance.sample.databinding.ActivityMainBinding
import com.balance.sample.flutter.FlutterEnterActivity
import com.balance.sample.fragment.callback.FragmentCallBackActivity
import com.balance.sample.fragment.navigation.FragmentNavigationActivity
import com.balance.sample.h5offline.WebActivity
import com.balance.sample.hook.HookActivity
import com.balance.sample.preweb.PreWebViewActivity
import com.balance.sample.scanwifi.WifiScanActivity
import com.balance.sample.touchevent.TouchActivity
import com.balance.sample.viewpager.ViewPagerActivity
import com.blankj.utilcode.util.SizeUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val items = mutableListOf<ItemAction>().apply {
        add(ItemAction("Asynclayoutinflater 异步创建布局") {
            startActivity(
                Intent(
                    this@MainActivity, AsyncLoadViewActivity::class.java
                )
            )
        })
        add(ItemAction("Fragment 回调跳板") {
            startActivity(
                Intent(
                    this@MainActivity, FragmentCallBackActivity::class.java
                )
            )
        })
        add(ItemAction("Fragment 导航") {
            startActivity(
                Intent(
                    this@MainActivity, FragmentNavigationActivity::class.java
                )
            )
        })
        add(ItemAction("H5 预加载") {
            startActivity(Intent(this@MainActivity, PreWebViewActivity::class.java))
        })
        add(ItemAction("Flutter") {
            startActivity(
                Intent(
                    this@MainActivity, FlutterEnterActivity::class.java
                )
            )
        })
        add(ItemAction("H5 离线包") {
            startActivity(
                Intent(
                    this@MainActivity, WebActivity::class.java
                )
            )
        })
        add(ItemAction("Android AutoService") {
            val value = ComponentManager.action(
                ComponentBuilder(this@MainActivity,
                    ComponentConstants.ComponentA.COMPONENT_A,
                    ComponentConstants.ComponentA.METHOD_0,
                    mutableMapOf<String, Any>().apply {
                        put("key", "123")
                    })
            )
            Toast.makeText(this@MainActivity, "result from component:${value}", Toast.LENGTH_SHORT)
                .show()
        })

        add(ItemAction("Touch") {
            startActivity(Intent(this@MainActivity, TouchActivity::class.java))
        })
        add(ItemAction("ViewPager") {
            startActivity(Intent(this@MainActivity, ViewPagerActivity::class.java))
        })
        add(ItemAction("Hook") {
            startActivity(Intent(this@MainActivity, HookActivity::class.java))
        })
        add(ItemAction("Wifi 扫描") {
            startActivity(Intent(this@MainActivity, WifiScanActivity::class.java))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = SizeUtils.dp2px(10f)
                }
            })
            adapter = ListAdapter(items)
        }


    }


    class ItemAction(val name: String, val action: () -> Unit)
    class ListAdapter(private val items: MutableList<ItemAction>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(Button(parent.context).apply {
                isAllCaps = false
                setBackgroundColor(resources.getColor(R.color.purple_200))
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }) {

            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as? Button)?.let {
                it.text = items[position].name
                it.setOnClickListener {
                    items[position].action.invoke()
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
//        Toast.makeText(this, ClipboardUtils.getText().toString(), Toast.LENGTH_SHORT).show()
    }


}

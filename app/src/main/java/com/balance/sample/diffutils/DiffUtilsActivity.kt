package com.balance.sample.diffutils

import android.graphics.Rect
import android.media.browse.MediaBrowser.ItemCallback
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.balance.sample.databinding.ActivityDiffUtilsBinding
import com.balance.sample.utils.SizeUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import org.checkerframework.common.returnsreceiver.qual.This
import java.text.FieldPosition

class DiffUtilsActivity : AppCompatActivity() {
    val item0 = ItemBean(0)
    val item1 = ItemBean(1)
    val item2 = ItemBean(2)

    val oldData = mutableListOf<ItemBean>().apply {
        add(item0)
        add(item1)
        add(item2)
    }

    val newData = mutableListOf<ItemBean>().apply {
        add(item1)
        add(item2)
    }

    private lateinit var binding: ActivityDiffUtilsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiffUtilsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.apply {
            layoutManager = LinearLayoutManager(this@DiffUtilsActivity)
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = SizeUtils.dp2px(10f)
                }
            })
        }
        binding.testDiff.setOnClickListener {
            testDiffUtils()
        }
        binding.testList.setOnClickListener {
            testDiffList()
        }
        binding.testAsyncList.setOnClickListener {
            testAsyncDiffList()
        }
    }

    /**
     * adapter刷新数据会优先调用notifyItemChanged->onBindViewHolder来刷新数据提高效率
     * 从oldData切到newData时不会调用任何onCreateViewHolder、onBindViewHolder
     * 从newData切换到oldData时只会调用一次notifyItemChanged->onBindViewHolder
     * */
    private fun testDiffList() {
        if (binding.list.adapter !is DiffAdapter<*>) {
            val adapter = object : DiffAdapter<ItemBean>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup, viewType: Int
                ): RecyclerView.ViewHolder {
                    LogUtils.e("onCreateViewHolder", parent, viewType)
                    return object : RecyclerView.ViewHolder(Button(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    LogUtils.e("onBindViewHolder", holder, position)
                    (holder.itemView as? Button)?.text = (data[position] as? ItemBean)?.name
                }

                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
                ) {
                    LogUtils.e("onBindViewHolder-Payloads", holder, position, payloads)
                    (holder.itemView as? Button)?.text = (data[position] as? ItemBean)?.name
                }
            }
            binding.list.adapter = adapter
        }

        (binding.list.adapter as DiffAdapter<ItemBean>).apply {
            updateData(if (this.data.size == oldData.size) newData else oldData)
        }
    }

    /**
     * 如果item数据架构比较大，compare过程可能会block ui线程，使用AsyncListDiffer放在work线程compare，然后通过handler post到ui线程刷新
     * */
    private fun testAsyncDiffList() {
        if (binding.list.adapter !is AsyncDiffAdapter<*>) {
            val adapter = object : AsyncDiffAdapter<ItemBean>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup, viewType: Int
                ): RecyclerView.ViewHolder {
                    LogUtils.e("onCreateViewHolder", parent, viewType)
                    return object : RecyclerView.ViewHolder(Button(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    LogUtils.e("onBindViewHolder", holder, position)
                    (holder.itemView as? Button)?.text = (data(position) as? ItemBean)?.name
                }

                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
                ) {
                    LogUtils.e("onBindViewHolder-Payloads", holder, position, payloads)
                    (holder.itemView as? Button)?.text = (data(position) as? ItemBean)?.name
                }
            }
            binding.list.adapter = adapter
        }

        (binding.list.adapter as AsyncDiffAdapter<ItemBean>).apply {
            updateData(if(this.itemCount ==  newData.size) oldData else newData)
        }
    }


    fun testDiffUtils() {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldData.size
            }

            override fun getNewListSize(): Int {
                return newData.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldData[oldItemPosition] == newData[newItemPosition]

            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItemPosition, newItemPosition)
            }

        })
        //只调用了onMoved增加了效率
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                LogUtils.e("onInserted", position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                LogUtils.e("onRemoved", position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                LogUtils.e("onMoved", fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                LogUtils.e("onChanged", position, count, payload)
            }

        })
    }
}

abstract class DiffAdapter<T : ItemBean> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    private val diff
    val data = mutableListOf<T>()
    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: MutableList<T>? = null) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return data.size
            }

            override fun getNewListSize(): Int {
                return newData?.size ?: 0
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return newData!![newItemPosition] == data[oldItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition].areContentsTheSame(newData!![newItemPosition])
            }
        })
        data.clear()
        data.addAll(newData ?: mutableListOf())
        diffResult.dispatchUpdatesTo(this)
    }
}

abstract class AsyncDiffAdapter<T> : RecyclerView.Adapter<ViewHolder>() {
    private val gson = Gson()

    private val asyncDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            //不需要每个数据类自己去处理compare逻辑
            return gson.toJson(oldItem) == gson.toJson(newItem)
        }
    })

    fun data(position: Int): T {
        return asyncDiffer.currentList[position]
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    fun updateData(newData: MutableList<T>? = null) {
        asyncDiffer.submitList(newData)
    }
}

class ItemBean(val index: Int = 0) : IDiffBean {
    var name: String? = null
        get() {
            return field ?: index?.toString()
        }

    override fun areContentsTheSame(compareBean: IDiffBean): Boolean {
        return (compareBean as? ItemBean)?.name == this.name && (compareBean as? ItemBean)?.index == this.index
    }
}

interface IDiffBean {
    fun areContentsTheSame(compareBean: IDiffBean): Boolean
}

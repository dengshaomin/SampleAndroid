package com.balance.sample.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.balance.sample.R
import com.balance.sample.databinding.ItemBannerItemBinding
import com.balance.sample.databinding.ViewPowerBannerBinding

class PowerBannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var binding: ViewPowerBannerBinding
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
    private lateinit var viewPager2Adapter: ViewPager2Adapter<String>
    private var currentPosition = 0
    private var isLooping = false

    private val loopTime = 3000L

    init {
        binding = ViewPowerBannerBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.view_power_banner, this)
        )
        initView()
    }

    private fun initView() {
        viewPager2Adapter = ViewPager2Adapter()
        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (currentPosition == 0) {
                        binding.viewpage2.setCurrentItem(
                            viewPager2Adapter.internalData.size - 2,
                            false
                        )
                    } else if (currentPosition == viewPager2Adapter.internalData.size - 1) {
                        binding.viewpage2.setCurrentItem(1, false)
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }
        binding.viewpage2.apply {
            adapter = viewPager2Adapter
            registerOnPageChangeCallback(pageChangeListener)
        }
    }

    fun startLoop() {
        post {
            if (isLooping || viewPager2Adapter.internalData.size == 0) {
                return@post
            }
            isLooping = true
            handler?.postDelayed(mLoopRunnable, loopTime + 1000)
        }
    }

    fun stopLoop() {
        post {
            handler?.removeCallbacks(mLoopRunnable)
            isLooping = false
        }
    }

    fun onDestory() {
        stopLoop()
        binding.viewpage2.unregisterOnPageChangeCallback(pageChangeListener)
    }

    fun updateData(data: MutableList<String>) {
        viewPager2Adapter.updateData(data)
        if (!data.isNullOrEmpty()) {
            currentPosition = 1
            binding.viewpage2.currentItem = currentPosition
        }
    }

    private val mLoopRunnable: Runnable = object : Runnable {
        override fun run() {
            currentPosition = binding.viewpage2.currentItem
            currentPosition++
            binding.viewpage2.currentItem = currentPosition
            handler.postDelayed(this, loopTime)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_DOWN -> {
                stopLoop()
            }
            MotionEvent.ACTION_UP -> startLoop()
        }
        return super.dispatchTouchEvent(ev)

    }

    class ViewPager2Adapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val internalData = mutableListOf<T>()
        fun updateData(data: MutableList<T>?) {
            internalData.clear()
            val tempData = mutableListOf<T>()
            if (!data.isNullOrEmpty()) {
                tempData.add(data[data.size - 1])
                tempData.addAll(data)
                tempData.add(data[0])
            }
            internalData.addAll(tempData)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(BannerItemView(parent.context)) {

            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as BannerItemView).setViewData(internalData[position])
        }

        override fun getItemCount(): Int {
            return internalData.size
        }
    }

    class BannerItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
    ) : ConstraintLayout(context, attrs) {
        private var bannerItemBinding: ItemBannerItemBinding

        init {
            bannerItemBinding = ItemBannerItemBinding.bind(
                LayoutInflater.from(context).inflate(R.layout.item_banner_item, this)
            )
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        }

        fun setViewData(url: Any?) {
            Glide.with(context).load(url).centerCrop()
                .into(bannerItemBinding.image)
        }
    }
}
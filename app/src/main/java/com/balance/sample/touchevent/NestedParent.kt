package com.balance.sample.touchevent

import android.content.Context
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.balance.sample.R

class NestedParent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {
    private lateinit var contentView: View
    private lateinit var childRecycler: RecyclerView
    private lateinit var floatView: View
    private var nested_list:View = findViewById(R.id.nested_list)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        nested_list.layoutParams.height = measuredHeight - floatView.measuredHeight
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView = findViewById(R.id.nested_content)
        floatView = findViewById(R.id.nested_floating)
        childRecycler = findViewById(R.id.nested_list)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (scrollY >= contentView.height) {
            return true
        }
        return super.onTouchEvent(ev)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (scrollY < contentView.height) {
            scrollBy(0, dy)
            consumed[1] = dy
        } else {
            consumed[1] = 0
        }
    }

    override fun fling(velocityY: Int) {
        val dy = FlingUtil.getDistanceByVelocity(context, velocityY)
        if (scrollY < contentView.height) {
            if (scrollY + dy <= contentView.height) {
                super.fling(velocityY)
            } else if (scrollY + dy > contentView.height) {
                val scrollViewNeedScrollY = contentView.height - scrollY
                //让NestedScrollView先处理所有的滚动事件
                val scrollViewNeedVelocity =
                    FlingUtil.getVelocityByDistance(context, scrollViewNeedScrollY.toDouble())
                if (velocityY > 0) {
                    super.fling(scrollViewNeedVelocity)
                } else {
                    super.fling(-scrollViewNeedVelocity)
                }
                //把剩余的滚动事件交给RecyclerView处理
                val recyclerViewScrollY = dy - scrollViewNeedScrollY
                val recyclerViewNeedVelocity =
                    FlingUtil.getVelocityByDistance(context, recyclerViewScrollY)
                if (velocityY > 0) {
                    childRecycler.fling(0, recyclerViewNeedVelocity)
                } else {
                    childRecycler.fling(0, -recyclerViewNeedVelocity)
                }
            }
        }
    }


    private fun log(s: String) {
        Log.e("balance", s)
    }


    object FlingUtil {
        private const val INFLEXION = 0.35f
        private val mFlingFriction = ViewConfiguration.getScrollFriction()
        private val DECELERATION_RATE = (Math.log(0.78) / Math.log(0.9)).toFloat()
        private fun getSplineDeceleration(context: Context, velocity: Int): Double {
            return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * getPhysicalCoeff(context)))
        }

        private fun getSplineDecelerationByDistance(context: Context, distance: Double): Double {
            val decelMinusOne = DECELERATION_RATE - 1.0
            return decelMinusOne * Math.log(distance / (mFlingFriction * getPhysicalCoeff(context))) / DECELERATION_RATE
        }

        private fun getPhysicalCoeff(context: Context): Double {
            val ppi = context.resources.displayMetrics.density * 160.0f
            return (SensorManager.GRAVITY_EARTH * 39.37f * ppi * 0.84f).toDouble()
        }

        //通过初始速度获取最终滑动距离
        fun getDistanceByVelocity(context: Context, velocity: Int): Double {
            val l = getSplineDeceleration(context, velocity)
            val decelMinusOne = DECELERATION_RATE - 1.0
            return mFlingFriction * getPhysicalCoeff(context) * Math.exp(DECELERATION_RATE / decelMinusOne * l)
        }

        //通过需要滑动的距离获取初始速度
        fun getVelocityByDistance(context: Context, distance: Double): Int {
            val l = getSplineDecelerationByDistance(context, distance)
            val velocity =
                (Math.exp(l) * mFlingFriction * getPhysicalCoeff(context) / INFLEXION).toInt()
            return Math.abs(velocity)
        }
    }
}
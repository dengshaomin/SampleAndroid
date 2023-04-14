package com.balance.sample.bottomsheet

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.balance.sample.R

class CustomerBottomCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CoordinatorLayout(context, attrs) {
    private lateinit var root_view: CoordinatorLayout
    private lateinit var bottomSheetBehavior: CustomerBottomSheetBehavior<CoordinatorLayout>
    private var needBackground = true
    private var cardState = CardState.MIDDLE
    private var initCardState = CardState.MIDDLE
    private var canScrollHide = false
    var stateCallBack: ((state: CardState) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.card_customer, this)
        initView()
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CustomerBottomCard)?.let {

            canScrollHide =
                it.getBoolean(
                    R.styleable.CustomerBottomCard_customer_behavior_hideAble,
                    false
                )
            bottomSheetBehavior.apply {
                val value =
                    it.peekValue(R.styleable.CustomerBottomCard_customer_behavior_peekHeight)
                if (value != null && value.data == -1) {
                    setPeekHeight(value.data)
                } else {
                    setPeekHeight(
                        it.getDimensionPixelSize(
                            R.styleable.CustomerBottomCard_customer_behavior_peekHeight,
                            -1
                        )
                    )
                }
                isHideable = canScrollHide
                setFitToContents(
                    it.getBoolean(
                        R.styleable.CustomerBottomCard_customer_behavior_fitToContents,
                        true
                    )
                )
                skipCollapsed =
                    it.getBoolean(
                        R.styleable.CustomerBottomCard_customer_behavior_skipCollapse,
                        false
                    )
                middleHeight = it.getDimensionPixelSize(
                    R.styleable.CustomerBottomCard_customer_behavior_middleHeight,
                    CustomerBottomSheetBehavior.MIDDLE_HEIGHT_AUTO
                )
                setFitToContents(
                    it.getBoolean(
                        R.styleable.CustomerBottomCard_customer_behavior_hideAble,
                        false
                    )
                )
            }

            initCardState = CardState.values().find { state ->
                state.value == it.getInt(
                    R.styleable.CustomerBottomCard_customer_behavior_defaultState,
                    CardState.MIDDLE.value
                )
            } ?: CardState.MIDDLE
            needBackground =
                it.getBoolean(R.styleable.CustomerBottomCard_customer_behavior_needBackGround, true)
            it.recycle()
            setCardState(initCardState)
        }
    }

    private fun initView() {
        root_view = findViewById(R.id.root_view)

        bottomSheetBehavior = CustomerBottomSheetBehavior.from(root_view).apply {
            setBottomSheetCallback(object : CustomerBottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(view: View, newState: Int) {
                    CardState.values().find { it.value == newState }?.let {
                        stateCallBack?.invoke(it)
                    }
                }

                override fun onSlide(var1: View, var2: Float) {
                    setBackgroundColor(
                        Color.argb(
                            ((if (var2 > 0.5) var2 else 0f) * 128).toInt(),
                            0,
                            0,
                            0
                        )
                    )
                }
            })
        }
    }

    fun setCardState(cardState: CardState) {
        bottomSheetBehavior.isHideable = cardState == CardState.HIDE
        bottomSheetBehavior.setState(cardState.value)
        if (canScrollHide) {
            bottomSheetBehavior.isHideable = true
        }
        setBackgroundColor(Color.argb(if (cardState == CardState.BIG) 128 else 0, 0, 0, 0))
        this.cardState = cardState
    }

    override fun onLayoutChild(child: View, layoutDirection: Int) {
        if (childCount > 2) {
            throw RuntimeException("only allow one child~")
        }
        if (childCount > 1) {
            val child = getChildAt(1)
            removeView(child)
            root_view.addView(child)
        }
        super.onLayoutChild(child, layoutDirection)
    }

    enum class CardState(val value: Int) {
        BIG(3), MIDDLE(6), SMALL(4), HIDE(5)
    }
}
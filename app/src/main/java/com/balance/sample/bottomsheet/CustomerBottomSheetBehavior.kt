package com.balance.sample.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.view.*
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.customview.view.AbsSavedState
import androidx.customview.widget.ViewDragHelper
import com.balance.sample.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.WeakReference

class CustomerBottomSheetBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    private var fitToContents = true
    private var maximumVelocity = 0f
    private var peekHeight = 0
    private var peekHeightAuto = false
    private var peekHeightMin = 0
    private var lastPeekHeight = 0
    private var fitToContentsOffset = 0
    private var halfExpandedOffset = 0
    private var collapsedOffset = 0
    var isHideable = false
     var skipCollapsed = false
    private var state = STATE_HALF_EXPANDED
    private var viewDragHelper: ViewDragHelper? = null
    private var ignoreEvents = false
    private var lastNestedScrollDy = 0
    private var nestedScrolled = false
    private var parentHeight = 0
    private var viewRef: WeakReference<V>? = null
    private var nestedScrollingChildRef: WeakReference<View?>? = null
    private var callback: BottomSheetCallback? = null
    private var velocityTracker: VelocityTracker? = null
    private var activePointerId = 0
    private var initialY = 0
    private var touchingScrollingChild = false
    private var importantForAccessibilityMap: MutableMap<View, Int>? = null
    private val dragCallback: ViewDragHelper.Callback
    private var mMiddleHeight = 0
    private var mMiddleHeightAuto = false

    constructor() {
        dragCallback = NamelessClass_1()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        dragCallback = NamelessClass_1()
//        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout)
//        val value = a.peekValue(R.styleable.BottomSheetBehavior_Layout_customer_behavior_peekHeight)
//        if (value != null && value.data == -1) {
//            setPeekHeight(value.data)
//        } else {
//            setPeekHeight(
//                a.getDimensionPixelSize(
//                    R.styleable.BottomSheetBehavior_Layout_customer_behavior_peekHeight,
//                    -1
//                )
//            )
//        }
//        isHideable =
//            a.getBoolean(R.styleable.BottomSheetBehavior_Layout_customer_behavior_hideAble, false)
//        setFitToContents(
//            a.getBoolean(
//                R.styleable.BottomSheetBehavior_Layout_customer_behavior_fitToContents,
//                true
//            )
//        )
//        skipCollapsed =
//            a.getBoolean(
//                R.styleable.BottomSheetBehavior_Layout_customer_behavior_skipCollapse,
//                false
//            )
//        middleHeight = a.getDimensionPixelSize(
//            R.styleable.BottomSheetBehavior_Layout_customer_behavior_middleHeight,
//            MIDDLE_HEIGHT_AUTO
//        )
//        a.recycle()
        val configuration = ViewConfiguration.get(context)
        maximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()
    }

    internal inner class NamelessClass_1 : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return if (state == STATE_DRAGGING) {
                false
            } else if (touchingScrollingChild) {
                false
            } else {
                if (state == 3 && activePointerId == pointerId) {
                    val scroll =
                        nestedScrollingChildRef!!.get()
                    if (scroll != null && scroll.canScrollVertically(-1)) {
                        return false
                    }
                }
                viewRef != null && viewRef!!.get() === child
            }
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            dispatchOnSlide(top)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == 1) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top: Int
            val targetState: Byte
            val currentTop: Int
            if (yvel < 0.0f) {
                if (fitToContents) {
                    currentTop = releasedChild.top
                    if (currentTop < collapsedOffset + HIDE_THRESHOLD && currentTop >= halfExpandedOffset) {
                        top = halfExpandedOffset
                        targetState = STATE_HALF_EXPANDED.toByte()
                    } else {
                        top = fitToContentsOffset
                        targetState = STATE_EXPANDED.toByte()
                    }
                } else {
                    currentTop = releasedChild.top
                    if (currentTop > halfExpandedOffset) {
                        top = halfExpandedOffset
                        targetState = STATE_HALF_EXPANDED.toByte()
                    } else {
                        top = 0
                        targetState = STATE_EXPANDED.toByte()
                    }
                }
            } else if (!isHideable || !shouldHide(
                    releasedChild,
                    yvel
                ) || releasedChild.top <= collapsedOffset && Math.abs(xvel) >= Math.abs(
                    yvel
                )
            ) {
                if (yvel != 0.0f && Math.abs(xvel) <= Math.abs(yvel)) {
                    currentTop = releasedChild.top
                    if (currentTop < halfExpandedOffset) {
                        top = halfExpandedOffset
                        targetState = STATE_HALF_EXPANDED.toByte()
                    } else {
                        top = collapsedOffset
                        targetState = STATE_COLLAPSED.toByte()
                    }
                } else {
                    currentTop = releasedChild.top
                    if (fitToContents) {
                        if (Math.abs(currentTop - fitToContentsOffset) < Math.abs(currentTop - collapsedOffset)) {
                            top = fitToContentsOffset
                            targetState = STATE_EXPANDED.toByte()
                        } else {
                            top = collapsedOffset
                            targetState = STATE_COLLAPSED.toByte()
                        }
                    } else if (currentTop < halfExpandedOffset) {
                        if (currentTop < Math.abs(currentTop - collapsedOffset)) {
                            top = 0
                            targetState = STATE_EXPANDED.toByte()
                        } else {
                            top = halfExpandedOffset
                            targetState = STATE_HALF_EXPANDED.toByte()
                        }
                    } else if (Math.abs(currentTop - halfExpandedOffset) < Math.abs(currentTop - collapsedOffset)) {
                        top = halfExpandedOffset
                        targetState = STATE_HALF_EXPANDED.toByte()
                    } else {
                        top = collapsedOffset
                        targetState = STATE_COLLAPSED.toByte()
                    }
                }
            } else {
                top = parentHeight
                targetState = STATE_HIDDEN.toByte()
            }
            if (viewDragHelper!!.settleCapturedViewAt(releasedChild.left, top)) {
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(
                    releasedChild,
                    this@CustomerBottomSheetBehavior.SettleRunnable(
                        releasedChild,
                        targetState.toInt()
                    )
                )
            } else {
                setStateInternal(targetState.toInt())
            }
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return MathUtils.clamp(
                top,
                expandedOffset,
                if (isHideable) parentHeight else collapsedOffset
            )
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return if (isHideable) parentHeight else collapsedOffset
        }
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: V): Parcelable? {
        return SavedState(super.onSaveInstanceState(parent, child),this.state)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: V, state: Parcelable) {
        val ss = state as SavedState
        ss.superState?.let { super.onRestoreInstanceState(parent, child, it) }
        if (ss.state != STATE_DRAGGING && ss.state != STATE_SETTLING) {
            this.state = ss.state;
        } else {
            this.state = STATE_COLLAPSED;
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child!!.fitsSystemWindows = true
        }
        val savedTop = child!!.top
        parent.onLayoutChild(child, layoutDirection)
        parentHeight = parent.height
        if (peekHeightAuto) {
            if (peekHeightMin == 0) {
                peekHeightMin =
                    parent.resources.getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min)
            }
            lastPeekHeight = Math.max(peekHeightMin, parentHeight - parent.width * 9 / 16)
        } else {
            lastPeekHeight = peekHeight
        }
        if (mMiddleHeightAuto) {
            mMiddleHeight = parentHeight
        }
        fitToContentsOffset = Math.max(0, parentHeight - child.height)
        halfExpandedOffset = parentHeight - mMiddleHeight
        calculateCollapsedOffset()
        if (state == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, expandedOffset)
        } else if (state == STATE_HALF_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, halfExpandedOffset)
        } else if (isHideable && state == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, parentHeight)
        } else if (state == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, collapsedOffset)
        } else if (state == STATE_DRAGGING || state == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.top)
        }
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, dragCallback)
        }
        viewRef = WeakReference(child)
        nestedScrollingChildRef = WeakReference(findScrollingChild(child))
        return true
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean {
        return if (!child!!.isShown) {
            ignoreEvents = true
            false
        } else {
            val action = event.actionMasked
            if (action == 0) {
                reset()
            }
            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker!!.addMovement(event)
            when (action) {
                0 -> {
                    val initialX = event.x.toInt()
                    initialY = event.y.toInt()
                    val scroll =
                        if (nestedScrollingChildRef != null) nestedScrollingChildRef!!.get() else null
                    if (scroll != null && parent.isPointInChildBounds(
                            scroll,
                            initialX,
                            initialY
                        )
                    ) {
                        activePointerId = event.getPointerId(event.actionIndex)
                        touchingScrollingChild = true
                    }
                    ignoreEvents = activePointerId == -1 && !parent.isPointInChildBounds(
                        child,
                        initialX,
                        initialY
                    )
                }
                1, 3 -> {
                    touchingScrollingChild = false
                    activePointerId = -1
                    if (ignoreEvents) {
                        ignoreEvents = false
                        return false
                    }
                }
                2 -> {}
            }
            if (!ignoreEvents && viewDragHelper != null && viewDragHelper!!.shouldInterceptTouchEvent(
                    event
                )
            ) {
                true
            } else {
                val scroll =
                    if (nestedScrollingChildRef != null) nestedScrollingChildRef!!.get() else null
                action == 2 && scroll != null && !ignoreEvents && state != 1 && !parent.isPointInChildBounds(
                    scroll,
                    event.x.toInt(),
                    event.y.toInt()
                ) && viewDragHelper != null && Math.abs(initialY.toFloat() - event.y) > viewDragHelper!!.touchSlop.toFloat()
            }
        }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return if (!child!!.isShown) {
            false
        } else {
            val action = event.actionMasked
            if (state == STATE_DRAGGING && action == 0) {
                true
            } else {
                if (viewDragHelper != null) {
                    viewDragHelper!!.processTouchEvent(event)
                }
                if (action == 0) {
                    reset()
                }
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                }
                velocityTracker!!.addMovement(event)
                if (action == 2 && !ignoreEvents && Math.abs(initialY.toFloat() - event.y) > viewDragHelper!!.touchSlop.toFloat()) {
                    viewDragHelper!!.captureChildView(child, event.getPointerId(event.actionIndex))
                }
                !ignoreEvents
            }
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        lastNestedScrollDy = 0
        nestedScrolled = false
        return axes and 2 != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (type != 1) {
            if (target === nestedScrollingChildRef!!.get()) {
                val currentTop = child!!.top
                val newTop = currentTop - dy
                if (dy > 0) {
                    if (newTop < expandedOffset) {
                        consumed[1] = currentTop - expandedOffset
                        ViewCompat.offsetTopAndBottom(child, -consumed[1])
                        setStateInternal(STATE_EXPANDED)
                    } else {
                        consumed[1] = dy
                        ViewCompat.offsetTopAndBottom(child, -dy)
                        setStateInternal(STATE_DRAGGING)
                    }
                } else if (dy < 0 && !target.canScrollVertically(-1)) {
                    if (newTop > collapsedOffset && !isHideable) {
                        consumed[1] = currentTop - collapsedOffset
                        ViewCompat.offsetTopAndBottom(child, -consumed[1])
                        setStateInternal(STATE_COLLAPSED)
                    } else {
                        consumed[1] = dy
                        ViewCompat.offsetTopAndBottom(child, -dy)
                        setStateInternal(STATE_DRAGGING)
                    }
                }
                dispatchOnSlide(child.top)
                lastNestedScrollDy = dy
                nestedScrolled = true
            }
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        type: Int
    ) {
        if (child!!.top == expandedOffset) {
            setStateInternal(STATE_EXPANDED)
        } else if (target === nestedScrollingChildRef!!.get() && nestedScrolled) {
            val top: Int
            val targetState: Byte
            if (lastNestedScrollDy > 0) {
                val currentTop = child.top
                if (currentTop <= collapsedOffset - HIDE_THRESHOLD && currentTop >= halfExpandedOffset) {
                    top = halfExpandedOffset
                    targetState = STATE_HALF_EXPANDED.toByte()
                } else {
                    top = expandedOffset
                    targetState = STATE_EXPANDED.toByte()
                }
            } else if (isHideable && shouldHide(child, yVelocity)) {
                top = parentHeight
                targetState = STATE_HIDDEN.toByte()
            } else if (lastNestedScrollDy == 0) {
                val currentTop = child.top
                if (fitToContents) {
                    if (Math.abs(currentTop - fitToContentsOffset) < Math.abs(currentTop - collapsedOffset)) {
                        top = fitToContentsOffset
                        targetState = STATE_EXPANDED.toByte()
                    } else {
                        top = collapsedOffset
                        targetState = STATE_COLLAPSED.toByte()
                    }
                } else if (currentTop < halfExpandedOffset) {
                    if (currentTop < Math.abs(currentTop - collapsedOffset)) {
                        top = 0
                        targetState = STATE_EXPANDED.toByte()
                    } else {
                        top = halfExpandedOffset
                        targetState = STATE_HALF_EXPANDED.toByte()
                    }
                } else if (Math.abs(currentTop - halfExpandedOffset) < Math.abs(currentTop - collapsedOffset)) {
                    top = halfExpandedOffset
                    targetState = STATE_HALF_EXPANDED.toByte()
                } else {
                    top = collapsedOffset
                    targetState = STATE_COLLAPSED.toByte()
                }
            } else {
                val currentTop = child.top
                if (currentTop <= halfExpandedOffset + HIDE_THRESHOLD && currentTop > HIDE_THRESHOLD) {
                    top = halfExpandedOffset
                    targetState = STATE_HALF_EXPANDED.toByte()
                } else {
                    top = collapsedOffset
                    targetState = STATE_COLLAPSED.toByte()
                }
            }
            if (viewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState.toInt()))
            } else {
                setStateInternal(targetState.toInt())
            }
            nestedScrolled = false
        }
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return target === nestedScrollingChildRef!!.get() && (state != STATE_EXPANDED || super.onNestedPreFling(
            coordinatorLayout,
            child,
            target,
            velocityX,
            velocityY
        ))
    }

    fun isFitToContents(): Boolean {
        return fitToContents
    }

    fun setFitToContents(fitToContents: Boolean) {
        if (this.fitToContents != fitToContents) {
            this.fitToContents = fitToContents
            if (viewRef != null) {
                calculateCollapsedOffset()
            }
            setStateInternal(if (this.fitToContents && state == STATE_HALF_EXPANDED) STATE_HALF_EXPANDED else state)
        }
    }

    fun setPeekHeight(peekHeight: Int) {
        var layout = false
        if (peekHeight == -1) {
            if (!peekHeightAuto) {
                peekHeightAuto = true
                layout = true
            }
        } else if (peekHeightAuto || this.peekHeight != peekHeight) {
            peekHeightAuto = false
            this.peekHeight = Math.max(0, peekHeight)
            collapsedOffset = parentHeight - peekHeight
            layout = true
        }
        if (layout && state == STATE_COLLAPSED && viewRef != null) {
            viewRef!!.get()?.requestLayout()
        }
    }

    fun getPeekHeight(): Int {
        return if (peekHeightAuto) -1 else peekHeight
    }

    var middleHeight: Int
        get() = if (mMiddleHeightAuto) -1 else mMiddleHeight
        set(middleHeight) {
            var layout = false
            if (middleHeight == PEEK_HEIGHT_AUTO) {
                if (!mMiddleHeightAuto) {
                    mMiddleHeightAuto = true
                    layout = true
                }
            } else if (mMiddleHeightAuto || mMiddleHeight != middleHeight) {
                mMiddleHeightAuto = false
                mMiddleHeight = Math.max(0, middleHeight)
                layout = true
            }
            if (layout && state == STATE_COLLAPSED && viewRef != null) {
                val view = viewRef!!.get()
                view?.requestLayout()
            }
        }

    fun setBottomSheetCallback(callback: BottomSheetCallback?) {
        this.callback = callback
    }

    fun setState(state: Int) {
        if (state != this.state) {
            if (viewRef == null) {
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_HALF_EXPANDED || isHideable && state == STATE_HIDDEN) {
                    this.state = state
                }
            } else {
                val child = viewRef!!.get()
                if (child != null) {
                    val parent = child.parent
                    if (parent != null && parent.isLayoutRequested && ViewCompat.isAttachedToWindow(
                            child
                        )
                    ) {
                        child.post(Runnable { startSettlingAnimation(child, state) })
                    } else {
                        startSettlingAnimation(child, state)
                    }
                }
            }
        }
    }

    fun getState(): Int {
        return state
    }

    fun setStateInternal(state: Int) {
        if (this.state != state) {
            this.state = state
            if (state != STATE_HALF_EXPANDED && state != STATE_EXPANDED) {
                if (state == STATE_HIDDEN || state == STATE_COLLAPSED) {
                    updateImportantForAccessibility(false)
                }
            } else {
                updateImportantForAccessibility(true)
            }
            val bottomSheet = viewRef!!.get() as View?
            if (bottomSheet != null && callback != null) {
                callback!!.onStateChanged(bottomSheet, state)
            }
        }
    }

    private fun calculateCollapsedOffset() {
        if (fitToContents) {
            collapsedOffset = Math.max(parentHeight - lastPeekHeight, fitToContentsOffset)
        } else {
            collapsedOffset = parentHeight - lastPeekHeight
        }
    }

    private fun reset() {
        activePointerId = -1
        if (velocityTracker != null) {
            velocityTracker!!.recycle()
            velocityTracker = null
        }
    }

    fun shouldHide(child: View, yvel: Float): Boolean {
        return if (skipCollapsed) {
            true
        } else if (child.top < collapsedOffset) {
            false
        } else {
            val newTop =
                child.top.toFloat() + yvel * HIDE_FRICTION
            Math.abs(newTop - collapsedOffset.toFloat()) / peekHeight.toFloat() > HIDE_THRESHOLD
        }
    }

    @VisibleForTesting
    fun findScrollingChild(view: View?): View? {
        return if (ViewCompat.isNestedScrollingEnabled(view!!)) {
            view
        } else {
            if (view is ViewGroup) {
                val group = view
                var i = 0
                val count = group.childCount
                while (i < count) {
                    val scrollingChild = findScrollingChild(group.getChildAt(i))
                    if (scrollingChild != null) {
                        return scrollingChild
                    }
                    ++i
                }
            }
            null
        }
    }

    private val yVelocity: Float
        private get() = if (velocityTracker == null) {
            0.0f
        } else {
            velocityTracker!!.computeCurrentVelocity(1000, maximumVelocity)
            velocityTracker!!.getYVelocity(activePointerId)
        }
    private val expandedOffset: Int
        private get() = if (fitToContents) fitToContentsOffset else 0

    fun startSettlingAnimation(child: View, state: Int) {
        val top: Int
        top =
            if (state == STATE_COLLAPSED) {
                collapsedOffset
            } else if (state == STATE_HALF_EXPANDED) {
                halfExpandedOffset
            } else if (state == STATE_EXPANDED) {
                expandedOffset
            } else {
                if (!isHideable || state != STATE_HIDDEN) {
//                if(BuildConfig.DEBUG) {
//                    throw new IllegalArgumentException("Illegal state argument: " + state);
//                }else{
                    return
                    //                }
                }
                parentHeight
            }
        if (viewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, state))
        } else {
            setStateInternal(state)
        }
    }

    fun dispatchOnSlide(top: Int) {
        val bottomSheet = viewRef!!.get() as View?
        if (bottomSheet != null && callback != null) {
            if (top > collapsedOffset) {
                callback!!.onSlide(
                    bottomSheet,
                    (collapsedOffset - top).toFloat() / (parentHeight - collapsedOffset).toFloat()
                )
            } else {
                callback!!.onSlide(
                    bottomSheet,
                    (collapsedOffset - top).toFloat() / (collapsedOffset - expandedOffset).toFloat()
                )
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun updateImportantForAccessibility(expanded: Boolean) {
        if (viewRef != null) {
            val viewParent = (viewRef!!.get() as View?)!!.parent
            if (viewParent is CoordinatorLayout) {
                val parent = viewParent
                val childCount = parent.childCount
                if (VERSION.SDK_INT >= 16 && expanded) {
                    if (importantForAccessibilityMap != null) {
                        return
                    }
                    importantForAccessibilityMap = HashMap(childCount)
                }
                for (i in 0 until childCount) {
                    val child = parent.getChildAt(i)
                    if (child !== viewRef!!.get()) {
                        if (!expanded) {
                            if (importantForAccessibilityMap != null && importantForAccessibilityMap!!.containsKey(
                                    child
                                )
                            ) {
                                ViewCompat.setImportantForAccessibility(
                                    child,
                                    importantForAccessibilityMap!![child]!!
                                )
                            }
                        } else {
                            if (VERSION.SDK_INT >= 16) {
                                importantForAccessibilityMap!![child] =
                                    child.importantForAccessibility
                            }
                            ViewCompat.setImportantForAccessibility(child, 4)
                        }
                    }
                }
                if (!expanded) {
                    importantForAccessibilityMap = null
                }
            }
        }
    }

    protected class SavedState : AbsSavedState {
        val state: Int

        @JvmOverloads
        constructor(source: Parcel, loader: ClassLoader? = null as ClassLoader?) : super(
            source,
            loader
        ) {
            state = source.readInt()
        }

        constructor(superState: Parcelable?, state: Int) : super(superState!!) {
            this.state = state
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : ClassLoaderCreator<SavedState> {
                override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(`in`, loader)
                }

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`, null as ClassLoader?)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    private inner class SettleRunnable internal constructor(
        private val view: View,
        private val targetState: Int
    ) : Runnable {
        override fun run() {
            if (viewDragHelper != null && viewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(view, this)
            } else {
                setStateInternal(targetState)
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    annotation class State
    abstract class BottomSheetCallback {
        abstract fun onStateChanged(var1: View, var2: Int)
        abstract fun onSlide(var1: View, var2: Float)
    }

    companion object {
        const val STATE_DRAGGING = 1 //过渡状态此时用户正在向上或者向下拖动
        const val STATE_SETTLING = 2// 视图从脱离手指自由滑动到最终停下的这一小段时间
        const val STATE_EXPANDED = 3//大
        const val STATE_COLLAPSED = 4//小
        const val STATE_HIDDEN = 5//隐藏
        const val STATE_HALF_EXPANDED = 6//中

        const val PEEK_HEIGHT_AUTO = -1
        private const val HIDE_THRESHOLD = 0.5f
        private const val HIDE_FRICTION = 0.1f
         const val MIDDLE_HEIGHT_AUTO = -1
        fun <V : View> from(view: V): CustomerBottomSheetBehavior<V> {
            val params = view.layoutParams
            return if (params !is CoordinatorLayout.LayoutParams) {
                throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
            } else {
                val behavior = params.behavior
                if (behavior !is CustomerBottomSheetBehavior) {
                    throw IllegalArgumentException("The view is not associated with BottomSheetBehavior")
                } else {
                    behavior as CustomerBottomSheetBehavior<V>
                }
            }
        }
    }
}
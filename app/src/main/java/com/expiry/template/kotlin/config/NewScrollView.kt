package com.expiry.template.kotlin.config

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView

class NewScrollView : ScrollView, ViewTreeObserver.OnGlobalLayoutListener {

    /** 커스텀 뷰의 생성자이다. OVER_SCROLL_NEVER 설정을 안하면 역 스크롤시, 이상한 애니메이션이 출력된다. */
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        overScrollMode = OVER_SCROLL_NEVER
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    var header: View? = null
        set(value) {
            field = value
            field?.let {
                it.translationZ = 1f // 천장에 붙은 뷰가 다른 스크롤 뷰의 레이아웃 뒤에 가려지게 되는걸 방지하는 코드
                it.setOnClickListener { _ ->
                    //클릭 시, 헤더뷰가 최상단으로 오게 스크롤 이동
                    this.smoothScrollTo(scrollX, it.top)
                    callStickListener()
                }
            }
        }

    var stickListener: (View) -> Unit = {}
    var freeListener: (View) -> Unit = {}

    /** 천장에 붙어있는지 아닌지를 체크하는 옵저버 변수 */
    private var mIsHeaderSticky = false
    /** 천장에 붙어있는지 아닌지를 체크하는 옵저버 변수 -> 초기화 */
    private var mHeaderInitPosition = 0f

    /** 해당 함수는 레이아웃에 변경이 생길 때 일어날 것을 추가해주면 된다. onAttachToWindow 에 넣지 않을 것 */
    override fun onGlobalLayout() {
        mHeaderInitPosition = header?.top?.toFloat() ?: 0f
    }

    /** 중요, 해당 함수는 어느 시점에 헤더를 천장에 붙일지, 다시 땔지 등을 구현한다. */
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t

        if (scrolly > mHeaderInitPosition) {
            stickHeader(scrolly - mHeaderInitPosition)
        } else {
            freeHeader()
        }
    }

    /** 헤더가 천장을 넘어섰을 때 stickHeader함수를 통해 천장에 붙이고, 그렇지 않을 경우 freeHeader를 통해 천장에서 다시 땐다. */
    private fun stickHeader(position: Float) {
        header?.translationY = position
        callStickListener()
    }

    /** 붙어있지 않으면 -> 리스너 콜 -> flag를 true로 변환 */
    private fun callStickListener() {
        if (!mIsHeaderSticky) {
            stickListener(header ?: return)
            mIsHeaderSticky = true
        }
    }

    /** 헤더의 translationY를 0으로 해서 복원해줌 */
    private fun freeHeader() {
        header?.translationY = 0f
        callFreeListener()
    }

    private fun callFreeListener() {
        if (mIsHeaderSticky) {
            freeListener(header ?: return)
            mIsHeaderSticky = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}
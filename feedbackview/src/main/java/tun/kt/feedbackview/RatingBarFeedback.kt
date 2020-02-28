package tun.kt.feedbackview

import android.content.Context
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.AbsSeekBar
import android.widget.RatingBar
import android.widget.SeekBar
import java.util.jar.Attributes

class RatingBarFeedback : androidx.appcompat.widget.AppCompatSeekBar {
    private var numStar: Int = 5
    private var widthItem: Int = WindowManager.LayoutParams.WRAP_CONTENT
    constructor(context: Context) : super(context) {
    }
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        val typeArray = context.obtainStyledAttributes(attributes, R.styleable.RatingBarFeedback, 0, 0)
        typeArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }
    private fun init() {

    }
    interface OnRatingBarChange {
        fun OnRatingBarChangeListener(num: Int)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun loadFromAttributes() {

    }

}
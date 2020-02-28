package tun.kt.paintview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.abs


class PaintView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    private val defaultColor = resources.getColor(R.color.defaultBrushColor)
    private val defaultBrushSize = 2 * resources.displayMetrics.scaledDensity
    private var mPaint: Paint = Paint()
    private lateinit var mPath: Path
    private var currentStrokeWidth: Float = 1f
    private var currentBrushColor: Int = defaultColor
    private var mX: Float = 0f
    private var mY: Float = 0f
    private var mCanvas: Canvas
    private var mBitmap: Bitmap
    private var mEmboss: MaskFilter
    private var mBlur: MaskFilter
    private var emboss: Boolean = false
    private var blur: Boolean = false
    private var paths: ArrayList<FingerPath> = ArrayList()
    private var mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var _isDrawing: Boolean = false
    private var mBackgroundColor = DEFAULT_BG_COLOR

    init {
        init(context, attributeSet)
        mEmboss = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), 0.4f, 6f, 3.5f)
        mBlur = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        mBitmap = Bitmap.createBitmap(
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )
        mCanvas = Canvas(mBitmap)
    }

    private fun init(context: Context, attributes: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attributes, R.styleable.PaintView, 0, 0)
        currentStrokeWidth =
            typeArray.getDimension(R.styleable.PaintView_strokeWidth, defaultBrushSize)
        mPaint.isAntiAlias = typeArray.getBoolean(R.styleable.PaintView_isAntiAlias, true)
        mPaint.isDither = typeArray.getBoolean(R.styleable.PaintView_isDither, true)
        mPaint.color = typeArray.getColor(R.styleable.PaintView_brushColor, defaultColor)
        mPaint.style = PAINT_STYLE_STROKE[typeArray.getInt(R.styleable.PaintView_paintStyle, 1)]
        mPaint.strokeJoin =
            PAINT_STROKE_JOINT[typeArray.getInt(R.styleable.PaintView_paintStrokeJoint, 1)]
        mPaint.strokeCap = PAINT_STROKE_CAP[typeArray.getInt(R.styleable.PaintView_strokeCap, 1)]
        emboss = typeArray.getBoolean(R.styleable.PaintView_emboss, false)
        blur = typeArray.getBoolean(R.styleable.PaintView_blur, false)
        mPaint.alpha = 0xff
        mPaint.xfermode = null
        currentBrushColor = typeArray.getColor(R.styleable.PaintView_brushColor, defaultColor)
        typeArray.recycle()
    }

    /**
     * @BrushColor @see [currentBrushColor]#Color of each brush
     * */
    var brushColor: Int
        @ColorInt
        get() = currentBrushColor
        set(value) {
            currentBrushColor = value
        }
    /**
     * @BrushColor @see [currentBrushColor]#Color of draw board
     * */
    var backgroundBoardColor: Int
        @ColorInt
        get() = mBackgroundColor
        set(value) {
            mBackgroundColor = value
            setBackgroundColor(value)
        }
    /**
     * @Unit = dp ([brushSize]*scaledDensity)
     * */
    var brushSize: Float
        get() = currentStrokeWidth / resources.displayMetrics.scaledDensity
        set(value) {
            currentStrokeWidth = value * resources.displayMetrics.scaledDensity
        }
    val isDrawing: Boolean
        get() = _isDrawing
    var paintStyle: Paint.Style
        get() = mPaint.style
        set(value) {
            mPaint.style = value
        }
    var paintStrokeJoint: Paint.Join
        get() = mPaint.strokeJoin
        set(value) {
            mPaint.strokeJoin = value
        }
    var paintStrokeCap: Paint.Cap
        get() = mPaint.strokeCap
        set(value) {
            mPaint.strokeCap = value
        }

    fun normal() {
        emboss = false
        blur = false
    }

    fun emboss() {
        emboss = true
        blur = false
    }

    fun blur() {
        emboss = false
        blur = true
    }

    fun clear() {
        _isDrawing = false
        paths.clear()
        normal()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.save()
        canvas.drawColor(mBackgroundColor)

        for (fingerPath in paths) {
            mPaint.color = fingerPath.color
            mPaint.strokeWidth = fingerPath.strokeWidth
            mPaint.maskFilter = null
            if (fingerPath.emboss) {
                mPaint.maskFilter = mEmboss
            } else if (fingerPath.blur) {
                mPaint.maskFilter = mBlur
            }
            canvas.drawPath(fingerPath.path, mPaint)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fingerPath =
            FingerPath(
                currentBrushColor,
                emboss,
                blur,
                currentStrokeWidth,
                mPath
            )
        paths.add(fingerPath)
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
        _isDrawing = true
    }

    private fun touchMove(x: Float, y: Float) {
        _isDrawing = true
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
        }
        return true
    }

    companion object {
        private const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 2f
        private val PAINT_STYLE_STROKE =
            arrayOf(Paint.Style.FILL, Paint.Style.STROKE, Paint.Style.FILL_AND_STROKE)
        private val PAINT_STROKE_JOINT =
            arrayOf(Paint.Join.MITER, Paint.Join.ROUND, Paint.Join.BEVEL)
        private val PAINT_STROKE_CAP = arrayOf(Paint.Cap.BUTT, Paint.Cap.ROUND, Paint.Cap.SQUARE)
    }

}
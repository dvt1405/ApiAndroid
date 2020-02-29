package tun.kt.paintview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.abs


class PaintView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet),
    Cloneable {
    private val defaultColor = ContextCompat.getColor(context, R.color.defaultBrushColor)
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
    private var _isDrawing: Boolean = false
    private var mBackgroundColor = DEFAULT_BG_COLOR
    private var paths: ArrayList<FingerPath> = ArrayList()
    private var redoPaths = arrayListOf<FingerPath>()
    private var mBitmapPaint = Paint(Paint.DITHER_FLAG)

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
        currentStrokeWidth = typeArray.getDimension(R.styleable.PaintView_strokeWidth, defaultBrushSize)
        mPaint.isAntiAlias = typeArray.getBoolean(R.styleable.PaintView_isAntiAlias, true)
        mPaint.isDither = typeArray.getBoolean(R.styleable.PaintView_isDither, true)
        mPaint.color = typeArray.getColor(R.styleable.PaintView_brushColor, defaultColor)
        mPaint.style = PAINT_STYLE_STROKE[typeArray.getInt(R.styleable.PaintView_paintStyle, 1)]
        mPaint.strokeJoin = PAINT_STROKE_JOINT[typeArray.getInt(R.styleable.PaintView_paintStrokeJoint, 1)]
        mPaint.strokeCap = PAINT_STROKE_CAP[typeArray.getInt(R.styleable.PaintView_strokeCap, 1)]
        emboss = typeArray.getBoolean(R.styleable.PaintView_emboss, false)
        blur = typeArray.getBoolean(R.styleable.PaintView_blur, false)
        mPaint.alpha = 0xff
        mPaint.xfermode = null
        currentBrushColor = typeArray.getColor(R.styleable.PaintView_brushColor, defaultColor)
        typeArray.recycle()
    }


    var bitmapPaint: Paint
        get() = mBitmapPaint
        set(value) {
            mBitmapPaint = value
        }
    var drawPath
        get() = paths
        set(value) {
            paths = value
        }

    /**
     * @BrushColor @see [currentBrushColor]#Color of each brush
     * */
    var brushColor: Int
        @ColorInt
        get() = currentBrushColor
        set(@ColorInt value) {
            currentBrushColor = value
        }
    /**
     * @BrushColor @see [currentBrushColor]#Color of draw board
     * */
    var backgroundBoardColor: Int
        @ColorInt
        get() = mBackgroundColor
        set(@ColorInt value) {
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
    /**
     * Check paint view is empty or not
     * */
    val isEmptyBrush: Boolean
        get() = _isDrawing

    /**
     * set style of brush
     * */
    var paintStyle: Paint.Style
        get() = mPaint.style
        set(value) {
            mPaint.style = value
        }

    /**
     * set style of paint join
     * */
    var paintStrokeJoint: Paint.Join
        get() = mPaint.strokeJoin
        set(value) {
            mPaint.strokeJoin = value
        }

    /**
     * set style of paint stroke
     * */
    var paintStrokeCap: Paint.Cap
        get() = mPaint.strokeCap
        set(value) {
            mPaint.strokeCap = value
        }

    /**
     * get capture of view
     * */
    val picture: ByteArray? get() = getPicture(Type.BYTE_ARRAY) as ByteArray

    /**
     * get capture bitmap of paint view
     * */
    val pictureBitmap: Bitmap? get() = getPicture(Type.BITMAP) as Bitmap

    /**
     * get capture base 64 of paint view
     * */
    val pictureBase64: String? get() = getPicture(Type.BASE64) as String

    val isRedoable = redoPaths.isNotEmpty()

    /**
     * set mask filter normal
     * */
    fun normal() {
        emboss = false
        blur = false
    }

    /**
     * set mask filter emboss
     * */
    fun emboss() {
        emboss = true
        blur = false
    }

    /**
     * set mask filter blur
     * */
    fun blur() {
        emboss = false
        blur = true
    }

    /**
     * @Clear
     * clear paint view
     * */
    fun clear() {
        _isDrawing = false
        paths.clear()
        normal()
        invalidate()
    }

    fun clearRedo() = redoPaths.clear()

    /**
     * @Back
     * Undo an action
     * */
    fun undo() {
        if (paths.isNotEmpty()) {
            redoPaths.add(paths[paths.lastIndex])
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    /**
     * @Return
     * redo an action
     * */
    fun redo() {
        if (redoPaths.isNotEmpty()) {
            val index = redoPaths.lastIndex
            paths.add(redoPaths[index])
            redoPaths.removeAt(index)
            invalidate()
        }
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

    /**
     * Example: Convert to ColorInt first
     * "#ffffff": Color.parseColor("#ffffff")
     * Color.
     * */
    @SuppressLint("WrongThread")
    private fun getPicture(
        type: Type,
        _paintView: PaintView,
        isWithBackgroundColor: Boolean,
        @ColorInt otherBackgroundColor: Int?
    ): Any? {
        Log.e("Background", _paintView.mBackgroundColor.toString())
        val paintView: PaintView = _paintView.clone() as PaintView
        if (!isWithBackgroundColor) {
            if (otherBackgroundColor != null) {
                paintView.mBackgroundColor = otherBackgroundColor
            }
        }
        paintView.isDrawingCacheEnabled = true
        paintView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(paintView.drawingCache)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return when (type) {
            Type.BITMAP -> return bitmap
            Type.BYTE_ARRAY -> {
                return byteArrayOutputStream.toByteArray()
            }
            Type.BASE64 -> Base64.encodeToString(
                byteArrayOutputStream.toByteArray(),
                Base64.DEFAULT
            )
        }
    }

    /**
     * Get real capture of view
     * */
    private fun getPicture(type: Type): Any? = getPicture(type, this, true, null)

    fun getPicture(type: Type, @ColorInt otherBackgroundColor: Int?) =
        getPicture(type, this, false, otherBackgroundColor)

    /**
     * Get capture of view without color of background
     * [mBackgroundColor] = Color.WHITE
     * */
    fun getPicture(type: Type, isWithBackgroundColor: Boolean) =
        getPicture(type, this, isWithBackgroundColor, null)

    /**
     * Get capture of view with other color of background
     * */
    fun getPicture(@ColorInt colorBackground: Int?): ByteArray? =
        getPicture(Type.BYTE_ARRAY, colorBackground) as ByteArray

    /**
     * Get capture of view with other color of background
     * */
    fun getPictureBitmap(@ColorInt otherBackgroundColor: Int?): Bitmap? =
        getPicture(Type.BITMAP, otherBackgroundColor) as Bitmap

    /**
     * Get capture of view with other color of background
     * */
    fun getPictureBase64(@ColorInt colorBackground: Int?): String =
        getPicture(Type.BASE64, colorBackground) as String

    override fun clone(): Any {
        return super.clone()
    }


    enum class Type {
        BYTE_ARRAY, BITMAP, BASE64
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
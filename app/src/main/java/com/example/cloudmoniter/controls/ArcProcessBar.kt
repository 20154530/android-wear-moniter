package com.example.cloudmoniter.controls

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.cloudmoniter.R
import java.text.DecimalFormat

/**
 * 环形进度条
 */
class ArcProcessBar : View {

    private var _scalex: Float = 1f;
    private var _scaley: Float = 1f;
    private val _format1 = DecimalFormat("###")

    private var _percentage = 0f;

    private var _textPainter: TextPaint? = null;
    private var _pathPainter: Paint? = null;
    private var _pathPainterThick: Paint? = null;

    private var _textwidth: Float = 0f;
    private var _textHeight: Float = 0f;

    private var _redraw = false;

    //<editor-fold desc="Porps">
    /**
     * 百分比
     */
    var percentage: Float
        get() = _percentage
        set(value) {
            _percentage = value;
            _redraw = true;
            invalidateTextPaintAndMeasurements()
            invalidate()
        }

    private var _lineColorBg = Color.WHITE;
    var lineColorBg: Int
        get() = _lineColorBg
        set(value) {
            _lineColorBg = value;
        }

    private var _lineColorFg = Color.WHITE;
    var lineColorFg: Int
        get() = _lineColorFg
        set(value) {
            _lineColorFg = value;
        }

    private var _lineThickness = 4f;
    var lineThickness: Float
        get() = _lineThickness
        set(value) {
            _lineThickness = value;
        }

    private var _processThickness = 12f;
    var processThickness: Float
        get() = _processThickness
        set(value) {
            _processThickness = value;
        }

    private var _padding = 0f;
    var padding: Float
        get() = _padding;
        set(value) {
            _padding = value
        }

    private var _staticText: String? = "";
    var staticText: String?
        get() = _staticText;
        set(value) {
            _staticText = value
        }
    //</editor-fold>

    //<editor-fold desc="consturctor">
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
    //</editor-fold>

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val metrics = resources.displayMetrics
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ArcProcessBar, defStyle, 0
        )

        _percentage = a.getFloat(R.styleable.ArcProcessBar_percentage, 0f)
        _lineColorBg = a.getColor(R.styleable.ArcProcessBar_lineColorBg, Color.WHITE)
        _lineColorFg = a.getColor(R.styleable.ArcProcessBar_lineColorFg, Color.WHITE)
        _lineThickness = a.getFloat(R.styleable.ArcProcessBar_lineThickness, 16f)
        _processThickness = a.getFloat(R.styleable.ArcProcessBar_processThickness, 16f)
        _padding = a.getFloat(R.styleable.ArcProcessBar_padding, 4f)
        _staticText = a.getString(R.styleable.ArcProcessBar_staticText)

        _scalex = (metrics.xdpi / 240f)
        _scaley = (metrics.ydpi / 240f)

        _pathPainter = Paint().apply {
            color = (_lineColorBg.and(0x00FFFFFF)).or(0x60000000.toInt())
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = _lineThickness * _scalex
        }

        _pathPainterThick = Paint().apply {
            color = _lineColorFg
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = _processThickness * _scalex
        }

    }

    @SuppressLint("WrongConstant")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val q2 = Math.sqrt(2.0);
        val fontsize = (this.width - 4 * _padding - 2 * _processThickness - 4) / q2.toFloat();
        val font = resources.getFont(R.font.myfont)
        _textPainter = TextPaint().apply {
            color = _lineColorFg
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = Typeface.SERIF
            textAlign = Paint.Align.CENTER
            typeface = font
            textAlignment = 4
            textSize = fontsize
        }

        invalidateTextPaintAndMeasurements();
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val center = Point(this.width / 2, this.height / 2)

        val arcBg = generateArc(1f);
        val arcFg = generateArc(_percentage);

        canvas?.drawPath(arcBg, _pathPainter!!)
        canvas?.drawPath(arcFg, _pathPainterThick!!)

        var percentval = _format1.format(_percentage * 100f)
        if (staticText != null) {
            percentval = staticText;
        }
        canvas?.drawText(percentval, center.x.toFloat(), center.y + _textHeight / 2, _textPainter!!);
    }

    private fun invalidateTextPaintAndMeasurements() {
        _textPainter?.let {
            var percentval = _format1.format(_percentage * 100f)
            if (staticText != null) {
                percentval = staticText;
            }
            val bounds = Rect()
            it.getTextBounds(percentval, 0, percentval.length, bounds)
            _textHeight = bounds.height().toFloat()
            _textwidth = bounds.width().toFloat()
        }
    }

    private fun generateArc(percentage: Float): Path {
        val minedge = Math.min(this.width, this.height)
        val _radius = minedge - (2 * padding + _processThickness) * _scalex;
        val actradius = _radius / 2;
        val center = Point(this.width / 2, this.height / 2)
        val x1 = center.x - actradius;
        val x2 = actradius * 2 + x1;
        val y1 = center.y - actradius;
        val y2 = actradius * 2 + y1;
        val arcBounds = RectF(x1, y1, x2, y2)
        val top = center.y - actradius;

        val path = Path();
        path.rMoveTo(center.x.toFloat(), top);
        path.arcTo(arcBounds, -90f, percentage * 359.9999f, true)

        return path;
    }

}
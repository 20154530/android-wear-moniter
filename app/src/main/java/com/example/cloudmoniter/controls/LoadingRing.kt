package com.example.cloudmoniter.controls

import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator


class LoadingRing : View, ValueAnimator.AnimatorUpdateListener {

    private var _isLoading: Boolean = false;
    var isLoading: Boolean
        get() = _isLoading;
        set(value) {
            _isLoading = value;
        }

    private var _color: Int = Color.WHITE
    var color: Int
        get() = _color;
        set(value) {
            _color = value;
        }

    private val _framerate: Long = 1000 / 80;
    private val _animeDuration: Long = 2160;
    private val _animeActualDuration: Long = _animeDuration * 5 / 4;
    private var _size: Int = 0;
    private var _x: Long = 0
    private var _r: Int = 0;
    private val _paint: Paint = Paint();
    private val _arcpaint: Paint = Paint();
    private lateinit var _rotateAnime: ValueAnimator;
    private lateinit var _opacityAnime: ValueAnimator;
    private lateinit var _timeline: ValueAnimator;
    private lateinit var _offset: ArrayList<Int>;
    private lateinit var _delay: ArrayList<Int>;
    private lateinit var _bitmap: Bitmap;

    //<editor-fold desc="consturctor">
    constructor(context: Context) : super(context) {
        initAnimation();
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAnimation();
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initAnimation();
    }
    //</editor-fold>

    @SuppressLint("Recycle")
    private fun initAnimation() {
        _paint.let {
            it.color = _color;
            it.style = Paint.Style.FILL
        }

        _arcpaint.let {
            it.color = _color;
            it.style = Paint.Style.STROKE;
            it.strokeWidth = 4f
            it.isAntiAlias = true
        }

        val frame1 = Keyframe.ofFloat(0f, 0f);
        frame1.interpolator = PathInterpolator(.13f, .21f, .1f, .7f);
        val frame2 = Keyframe.ofFloat(.1247f, 120f);
        frame2.interpolator = PathInterpolator(.02f, .33f, .38f, .77f);
        val frame3 = Keyframe.ofFloat(.3451f, 203f);
        frame3.interpolator = LinearInterpolator();
        val frame4 = Keyframe.ofFloat(.4649f, 315f);
        frame4.interpolator = PathInterpolator(.57f, .17f, .95f, .75f);
        val frame5 = Keyframe.ofFloat(.5798f, 467f);
        frame5.interpolator = PathInterpolator(0f, .19f, .07f, .72f);
        val frame6 = Keyframe.ofFloat(.8f, 549f);
        frame6.interpolator = LinearInterpolator();
        val frame7 = Keyframe.ofFloat(.925f, 695f);
        frame7.interpolator = PathInterpolator(0f, 0f, .95f, .37f);
        val frame8 = Keyframe.ofFloat(1f, 720f);
        frame8.interpolator = null;
        val rotateHolder =
            PropertyValuesHolder.ofKeyframe("", frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8);
        _rotateAnime = ValueAnimator.ofPropertyValuesHolder(rotateHolder);
        _rotateAnime.duration = _animeDuration;
        _rotateAnime.interpolator = null;

        val oframe0 = Keyframe.ofFloat(0f, 0f);
        oframe0.interpolator = null;
        val oframe1 = Keyframe.ofFloat(.00001f, 1f);
        oframe1.interpolator = null;
        val oframe2 = Keyframe.ofFloat(.922f, 1f);
        oframe2.interpolator = LinearInterpolator();
        val oframe3 = Keyframe.ofFloat(.925f, 0f);
        oframe3.interpolator = LinearInterpolator();
        val oframe4 = Keyframe.ofFloat(1f, 0f);
        oframe4.interpolator = LinearInterpolator();
        val opacityHolder = PropertyValuesHolder.ofKeyframe("", oframe0, oframe1, oframe2, oframe3, oframe4);
        _opacityAnime = ValueAnimator.ofPropertyValuesHolder(opacityHolder);
        _opacityAnime.duration = _animeDuration;
        _opacityAnime.interpolator = null;

        _delay = ArrayList<Int>()
        for (i in 0 until 6) {
            _delay.add(i * 140)
        }

        _offset = ArrayList<Int>()
        for (i in 0 until 6) {
            _offset.add(-110 + i * -6)
        }

        _timeline = ValueAnimator.ofInt(0,_animeDuration.toInt());
        _timeline.repeatCount = ValueAnimator.INFINITE;
        _timeline.interpolator = null;
        _timeline.duration = _animeDuration;
        _timeline.addUpdateListener(this)
        _timeline.start();
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.INVISIBLE) {
            _x = 0;
            _timeline.pause();
            _timeline.start();
        }
        super.setVisibility(visibility)
    }

    @SuppressLint("DrawAllocation")
    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        _size = Math.min(height, width)
        _r = height / 14;

        _bitmap = Bitmap.createBitmap(_size, _size, Bitmap.Config.ARGB_8888);
        _bitmap.eraseColor(Color.TRANSPARENT);
    }

    override fun onDraw(canvas: Canvas?) {
        drawLoadingRing(_x, canvas!!);
    }

    private fun drawLoading(x: Long, canvas: Canvas, index: Int) {
        val timeline = x - _delay[index];
        _rotateAnime.currentPlayTime = timeline;
        val rotate = _rotateAnime.animatedValue as Float;

        _opacityAnime.currentPlayTime = timeline;
        val opacity = _opacityAnime.animatedValue as Float;

        _paint.let { it.alpha = (opacity * 255).toInt() }

        val center = _size / 2;
        val padding = _size / 2 - _r;
        val angle = (rotate.toDouble() + _offset[index]);
        val x = center + padding * Math.sin(Math.toRadians(angle))
        val y = center - padding * Math.cos(Math.toRadians(angle))
        canvas.drawCircle(x.toFloat(), y.toFloat(), _r.toFloat(), _paint);
    }

    private fun drawLoadingRing(x: Long, canvas: Canvas) {
        val time = x.toDouble() / 2f;
        val sinx = Math.sin(Math.toRadians(time - 90));
        val c = Canvas(_bitmap);
        c.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        val actdegree = 120 + 120 * (sinx + 1)

        val hdegree = actdegree / 2;
        val padding = _r.toFloat() * 2;
        val outpadding = 1f;
        val arcoutbounds = RectF(outpadding, outpadding, c.width - outpadding, c.height - outpadding);
        val arcbounds = RectF(padding, padding, c.width - padding, c.height - padding);
        _arcpaint.let { it.strokeWidth = 1f };
        c.drawArc(arcoutbounds, 0f, 360f, false, _arcpaint)
        _arcpaint.let { it.strokeWidth = 8f };
        c.drawArc(arcbounds, -90f + time.toFloat() - hdegree.toFloat(), actdegree.toFloat(), false, _arcpaint)

        canvas.drawBitmap(_bitmap, 0f, 0f, Paint());
    }

    override fun onAnimationUpdate(p0: ValueAnimator?) {
        _x = (p0?.animatedValue!! as Int).toLong();
        postInvalidateOnAnimation();
    }


}
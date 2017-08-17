package com.czp.arcprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;


/**
 * Created by caizepeng on 16/9/6.
 */
public class ArcProgress extends ProgressBar {
    public static final int STYLE_TICK = 1;
    public static final int STYLE_ARC = 0;
    private final int DEFAULT_LINEHEIGHT = dp2px(15);
    private final int DEFAULT_mTickWidth = dp2px(2);
    private final int DEFAULT_mRadius = dp2px(72);
    private final int DEFAULT_mInnerDis = dp2px(4);
    private final int DEFAULT_mUnmProgressColor = 0xffeaeaea;
    private final int DEFAULT_mProgressColor = Color.YELLOW;
    // TODO: 2017/8/17
    private final int DEFAULT_mProgressLineColor = Color.BLACK;
    private final int DEFAULT_mProgressInnerColor = Color.GREEN;
    private final int DEFAULT_OFFSETDEGREE = 60;
    private final int DEFAULT_DENSITY = 4;
    private final int MIN_DENSITY = 2;
    private final int MAX_DENSITY = 8;
    private int mStylePogress = STYLE_TICK;
    private boolean mBgShow;
    private float mRadius;
    private int mArcbgColor;
    private int mBoardWidth;
    private int mDegree = DEFAULT_OFFSETDEGREE;
    private RectF mArcRectf;
    private RectF mArcRectfInner;

    // TODO: 2017/8/17
    private Paint mInnerPaint;
    private Paint mLinePaint;
    private Paint mArcPaint;
    private int mUnmProgressColor;
    private int mProgressColor;
    private int mProgressOverColor;
    private int mProgressInnerColor;
    private int mProgressLineColor;
    private int mTickWidth;
    private int mTickDensity;
    private Bitmap mCenterBitmap;
    private Canvas mCenterCanvas;
    private OnCenterDraw mOnCenter;
    private int mDis;

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, com.czp.arcprogress.R.styleable.ArcProgress);
        mBoardWidth = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_borderWidth, DEFAULT_LINEHEIGHT);
        mUnmProgressColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_unprogresColor, DEFAULT_mUnmProgressColor);
        mProgressColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_progressColor, DEFAULT_mProgressColor);
        mProgressOverColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_progressOverColor, DEFAULT_mProgressColor);
        // TODO
        mProgressLineColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_progressLineColor, DEFAULT_mProgressLineColor);
        mProgressInnerColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_progressInnerColor, DEFAULT_mProgressInnerColor);
        mTickWidth = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_tickWidth, DEFAULT_mTickWidth);
        mTickDensity = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_tickDensity, DEFAULT_DENSITY);
        mRadius = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_radius, DEFAULT_mRadius);
        mArcbgColor = attributes.getColor(com.czp.arcprogress.R.styleable.ArcProgress_arcbgColor, DEFAULT_mUnmProgressColor);
        mTickDensity = Math.max(Math.min(mTickDensity, MAX_DENSITY), MIN_DENSITY);
        mBgShow = attributes.getBoolean(com.czp.arcprogress.R.styleable.ArcProgress_bgShow, false);
        mDegree = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_degree, DEFAULT_OFFSETDEGREE);
        mStylePogress = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_progressStyle, STYLE_TICK);
        mDis = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_innerDis, DEFAULT_mInnerDis);
        boolean capRount = attributes.getBoolean(com.czp.arcprogress.R.styleable.ArcProgress_arcCapRound, false);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcbgColor);
        if (capRount)
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeWidth(mBoardWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mTickWidth);

        // TODO: 2017/8/17
        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setColor(mArcbgColor);
        if (capRount)
            mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStrokeWidth(dp2px(14));
        mInnerPaint.setStyle(Paint.Style.STROKE);
    }

    public void setOnCenterDraw(OnCenterDraw mOnCenter) {
        this.mOnCenter = mOnCenter;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            int widthSize = (int) (mRadius * 2 + mBoardWidth * 2);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int heightSize = (int) (mRadius * 2 + mBoardWidth * 2);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        float roate = getProgress() * 1.0f / getMax();
        float x = mArcRectf.right / 2 + mBoardWidth / 2;
        float y = mArcRectf.right / 2 + mBoardWidth / 2;
        if (mOnCenter != null) {
            if (mCenterCanvas == null) {
                mCenterBitmap = Bitmap.createBitmap((int) mRadius * 2, (int) mRadius * 2, Bitmap.Config.ARGB_8888);
                mCenterCanvas = new Canvas(mCenterBitmap);
            }
            mCenterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mOnCenter.draw(mCenterCanvas, mArcRectf, x, y, mBoardWidth, getProgress());
            canvas.drawBitmap(mCenterBitmap, 0, 0, null);
        }
        int angle = mDegree / 2;
        int count = (360 - mDegree) / mTickDensity;
        int target = (int) (roate * count);
        if (mStylePogress == STYLE_ARC) {
            float targetmDegree = (360 - mDegree) * roate;

            // TODO: 2017/8/17
            mInnerPaint.setColor(mProgressInnerColor);
            canvas.drawArc(mArcRectfInner, 90 + angle, 360 - mDegree, false, mInnerPaint);
            mArcPaint.setColor(mProgressColor);
            canvas.drawArc(mArcRectf, 90 + angle, 360 - mDegree - 90 - 5, false, mArcPaint);
            mArcPaint.setColor(mProgressOverColor);
            canvas.drawArc(mArcRectf, 360 - angle, 90, false, mArcPaint);

            canvas.rotate(180 + angle + targetmDegree, x, y);
            mLinePaint.setColor(mProgressLineColor);
//            canvas.drawLine(x, mBoardWidth + mBoardWidth / 2, x, mBoardWidth - mBoardWidth / 2, mLinePaint);

            canvas.drawLine(x, mBoardWidth - mBoardWidth / 2, x, mBoardWidth + 120, mLinePaint);

        } else {
            if (mBgShow) {
                canvas.drawArc(mArcRectf, 90 + angle, 360 - mDegree, false, mArcPaint);
            }
            canvas.rotate(180 + angle, x, y);
            for (int i = 0; i < count; i++) {
                if (i < target) {
                    mLinePaint.setColor(mProgressColor);
                } else {
                    mLinePaint.setColor(mUnmProgressColor);
                }
                canvas.drawLine(x, mBoardWidth + mBoardWidth / 2, x, mBoardWidth - mBoardWidth / 2, mLinePaint);
                canvas.rotate(mTickDensity, x, y);
            }
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcRectf = new RectF(mBoardWidth,
                mBoardWidth,
                mRadius * 2 - mBoardWidth,
                mRadius * 2 - mBoardWidth);
        // TODO
        mArcRectfInner = new RectF(mBoardWidth + mDis,
                mBoardWidth + mDis,
                mRadius * 2 - mBoardWidth - mDis,
                mRadius * 2 - mBoardWidth - mDis);
        Log.e("DEMO", "right == " + mArcRectf.right + "   mRadius == " + mRadius * 2);
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public interface OnCenterDraw {
        /**
         * @param canvas
         * @param rectF       圆弧的Rect
         * @param x           圆弧的中心x
         * @param y           圆弧的中心y
         * @param storkeWidth 圆弧的边框宽度
         * @param progress    当前进度
         */
        public void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCenterBitmap != null) {
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }

    }
}

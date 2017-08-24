package com.czp.arcprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.text.DecimalFormat;

public class ArcProgress extends ProgressBar {
    private String TAG = "ArcProgress";
    // TODO: 2017/8/18
    private final int DEFAULT_LINEHEIGHT = dp2px(8);//外圈宽度
    private final int DEFAULT_INNER_LINEHEIGHT = dp2px(24);//内圈宽度
    private final int DEFAULT_mRadius = dp2px(100);//外圈半径
    private final int DEFAULT_mInnerDis = dp2px(2);//内外圈间距
    private final int DEFAULT_OFFSETDEGREE = 90;//默认扇形缺失角度
    private final float DEFAULT_MAX_SPEED = 18.0f;//默认maxSpeed
    private final int DEFAULT_LINELENGTH = dp2px(7);//默认刻度线长度
    private final int DEFAULT_TICK_MARK = dp2px(16);//刻度线距外圈距离
    private final int DEFAULT_INNER_START_LENGTH = dp2px(7);//inner start 露出长度
    private final int DEFAULT_OVER_START_LENGTH  =dp2px(10);//over start 露出三角形高度
    private final int DEFAULT_LIGHTLINE = dp2px(4);

    private Context context;
    private float mRadius;//外圈半径
    private int mBoardWidth;//外圈宽度
    private int mLightLine;
    private int mInnerBoardWidth;//内圈宽度
    private int mDegree = DEFAULT_OFFSETDEGREE;//默认缺口角度
    private int lineLength;//刻度线长度
    private RectF mArcRectf;
    private RectF mArcRectfInner;
    private RectF mILRectf;
    private RectF mOLRectf;

    private int overProgress = 20;
    private Paint mInnerPaint;

    // TODO: 2017/8/21
    private Paint mLinePaint;
    private Paint mOuterLinePaint;

    private Paint mArcPaint;
    private Paint mOverSpeedPaint;
    private Bitmap mCenterBitmap;
    private Canvas mCenterCanvas;
    private OnCenterDraw mOnCenter;
    private int mDis;

    private float maxSpeed = 0f;

    public float getmShowSpeed() {
        return Float.parseFloat(df.format(mShowSpeed));
    }

    public void setmShowSpeed(float mShowSpeed) {
        this.mShowSpeed = mShowSpeed;
    }

    private float mShowSpeed = 0.0f;
    private float mSpeedValue;
    private float mOverSpeedValue;
    private float preSpeedValue = 0f;
    private float preOverSpeedValue = maxSpeed;

    private DecimalFormat df;

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, com.czp.arcprogress.R.styleable.ArcProgress);
        //外圈宽度
        mBoardWidth = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_borderWidth, DEFAULT_LINEHEIGHT);
        //内圈宽度
        mInnerBoardWidth = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_innerBorderWidth, DEFAULT_INNER_LINEHEIGHT);
        //外圈半径
        mRadius = attributes.getDimensionPixelOffset(com.czp.arcprogress.R.styleable.ArcProgress_radius, DEFAULT_mRadius);
        //默认扇形缺失角度
        mDegree = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_degree, DEFAULT_OFFSETDEGREE);
        //内外圈距离
        mDis = attributes.getInt(com.czp.arcprogress.R.styleable.ArcProgress_innerDis, DEFAULT_mInnerDis);
        //刻度线长度
        lineLength = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_lineLength, DEFAULT_LINELENGTH);
        //初始化progressBar的最大速度
        maxSpeed = attributes.getFloat(R.styleable.ArcProgress_maxSpeed, DEFAULT_MAX_SPEED);
        //展示数字保留一位小数
        df = new DecimalFormat("0.0");

        mLightLine = DEFAULT_LIGHTLINE;

        // speed
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStrokeWidth(mBoardWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);

        //overSpeed
        mOverSpeedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOverSpeedPaint.setStrokeWidth(mBoardWidth);
        mOverSpeedPaint.setStyle(Paint.Style.STROKE);

        //inner
        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStrokeWidth(mInnerBoardWidth);
        mInnerPaint.setStyle(Paint.Style.STROKE);

        // TODO: 2017/8/21 刻度线
        mOuterLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterLinePaint.setStrokeWidth(dp2px(9));
        mOuterLinePaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(dp2px(1));
        mLinePaint.setStyle(Paint.Style.FILL);
    }

    public void setOnCenterDraw(OnCenterDraw mOnCenter) {
        this.mOnCenter = mOnCenter;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            int widthSize = (int) (mRadius * 2 + mBoardWidth * 2 + mLightLine * 2);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int heightSize = (int) (mRadius * 2 + mBoardWidth * 2 + mLightLine * 2);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        float roate = getProgress() * 1.0f / getMax();
        float roate2 = (100 - getOverProgress()) * 1.0f / getMax();
        float x = (ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2;
//        float x = mArcRectf.right / 2 + mBoardWidth + mDis;
        float y = (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2;
//        float y = mArcRectf.right / 2 + mBoardWidth + mDis;
        if (mOnCenter != null) {
            if (mCenterCanvas == null) {
                mCenterBitmap = Bitmap.createBitmap(ArcProgress.this.getRight() - ArcProgress.this.getLeft(), ArcProgress.this.getBottom() - ArcProgress.this.getTop(), Bitmap.Config.ARGB_8888);
                mCenterCanvas = new Canvas(mCenterBitmap);
            }
            mCenterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mOnCenter.draw(mCenterCanvas, mArcRectf, x, y, getmShowSpeed(), getMaxSpeed());
            canvas.drawBitmap(mCenterBitmap, 0, 0, null);
        }
        int angle = mDegree / 2;

        float targetmDegree = (360 - mDegree) * roate;
        float overmDegree = (360 - mDegree) * roate2;

        // TODO: 2017/8/18 仪表盘内部画笔
        float shaderFlag = Float.valueOf(df.format((targetmDegree) / 360.0f)) > 0.01f ? Float.valueOf(df.format((targetmDegree) / 360.0f)) : 0.03f;
        Shader innerShader = new SweepGradient(x, y, new int[]{Color.argb(0, 255, 255, 255), Color.argb(255, 51, 102, 255), Color.argb(255, 51, 102, 255)},
                new float[]{0f, shaderFlag, 1f});
        Matrix innerMatrix = new Matrix();
        innerMatrix.postRotate(90 + angle - 5, x, y);
        innerShader.setLocalMatrix(innerMatrix);
        mInnerPaint.setShader(innerShader);
        canvas.drawArc(mArcRectfInner, 90 + angle, targetmDegree, false, mInnerPaint);

        // TODO: 2017/8/18 仪表盘常规速度部分1
        Shader speedShader = new SweepGradient(x, y, new int[]{Color.argb(255, 1, 180, 255), Color.argb(255, 51, 102, 255), Color.argb(255, 51, 102, 255)},
                new float[]{0f, shaderFlag, 1f});
        Matrix speedMatrix = new Matrix();
        speedMatrix.postRotate(90 + angle - 5, x, y);
        speedShader.setLocalMatrix(speedMatrix);
        mArcPaint.setShader(speedShader);
        canvas.drawArc(mArcRectf, 90 + angle, targetmDegree, false, mArcPaint);

        // TODO: 2017/8/18 仪表盘常规速度部分2
        shaderFlag = Float.valueOf(df.format((360 - mDegree - overmDegree - 5 - targetmDegree) / 360.0f)) > 0.01f ? Float.valueOf(df.format((360 - mDegree - overmDegree - 5 - targetmDegree) / 360.0f)) : 0.03f;
        Shader speedShader2 = new SweepGradient(x, y, new int[]{Color.argb(255, 166, 166, 166), Color.argb(255, 28, 28, 28), Color.argb(255, 28, 28, 28)},
                new float[]{0f, shaderFlag, 1f});
        Matrix speedMatrix2 = new Matrix();
        speedMatrix2.postRotate(90 + targetmDegree + angle - 5, x, y);
        speedShader2.setLocalMatrix(speedMatrix2);
        mArcPaint.setShader(speedShader2);
        canvas.drawArc(mArcRectf, 90 + angle + targetmDegree, 360 - mDegree - overmDegree - targetmDegree, false, mArcPaint);

        // TODO: 2017/8/18 仪表盘超速部分
        shaderFlag = Float.valueOf(df.format((overmDegree) / 360.0f)) > 0.01f ? Float.valueOf(df.format((overmDegree) / 360.0f)) : 0.03f;
        Shader overSpeedShader = new SweepGradient(x, y, new int[]{Color.argb(255, 246, 108, 28), Color.argb(255, 238, 153, 29), Color.argb(255, 238, 153, 29)},
                new float[]{0f, shaderFlag, 1f});
        Matrix overSpeedMatrix = new Matrix();
        overSpeedMatrix.postRotate(90 + angle + 360 - mDegree - overmDegree - 5, x, y);
        overSpeedShader.setLocalMatrix(overSpeedMatrix);
        mOverSpeedPaint.setShader(overSpeedShader);
        canvas.drawArc(mArcRectf, 90 + angle + 360 - mDegree - overmDegree + 2, overmDegree, false, mOverSpeedPaint);

        // TODO: 2017/8/21 刻度线
        mOuterLinePaint.setShader(speedShader);
        mOuterLinePaint.setStrokeWidth(lineLength);
        int lineDegree = 90 + angle + 5;
        while (lineDegree < 90 + angle + targetmDegree) {
            canvas.drawArc(mOLRectf, lineDegree, 0.5f, false, mOuterLinePaint);
            lineDegree += 10;
        }
        mOuterLinePaint.setShader(null);
        mOuterLinePaint.setColor(Color.argb(255, 50, 50, 50));
        while (lineDegree < 450 - angle) {
            canvas.drawArc(mOLRectf, lineDegree, 0.5f, false, mOuterLinePaint);
            lineDegree += 10;
        }

        // TODO: 2017/8/18 速度指针前端直线
        //起始位置
        mLinePaint.setColor(Color.argb(255, 166, 166, 166));
        canvas.rotate(180 + angle, x, y);
        canvas.drawLine(x, y - mRadius - mBoardWidth / 2 - DEFAULT_TICK_MARK - lineLength / 2,//2刻度线的一半
                x, y - mRadius + mBoardWidth / 2, mLinePaint);

        //inner位置
        mLinePaint.setColor(Color.argb(255, 51, 102, 255));
        canvas.rotate(targetmDegree, x, y);
        canvas.drawLine(x, y - mRadius - mBoardWidth / 2,
                x, y - mRadius + mDis + mInnerBoardWidth + mBoardWidth / 2 + DEFAULT_INNER_START_LENGTH, mLinePaint);// TODO: 2017/8/24 innner 露出 宽度 dp2px(7)

        //overSpeed位置 三角形
        mLinePaint.setColor(Color.argb(255, 246, 108, 28));
        canvas.rotate(360 - mDegree - overmDegree - targetmDegree + 2, x, y);
        if (overmDegree < 2) {
            canvas.drawLine(x, y - mRadius - mBoardWidth / 2,//2刻度线的一半
                    x, y - mRadius + mBoardWidth / 2 + DEFAULT_OVER_START_LENGTH, mLinePaint);// TODO: 2017/8/24 overspeed处 露出 宽度 dp2px(10)
        } else {
            Path path = new Path();
            path.moveTo(x, y - mRadius - mBoardWidth / 2);// 此点为多边形的起点
            path.lineTo(x + 8, y - mRadius - mBoardWidth / 4);
            path.lineTo(x, y - mRadius + mBoardWidth + dp2px(6));//长4dp
            path.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path, mLinePaint);
        }

        //终止位置
        mLinePaint.setColor(Color.argb(255, 238, 153, 29));
        canvas.rotate(overmDegree, x, y);
        canvas.drawLine(x, y - mRadius - mBoardWidth / 2 - DEFAULT_TICK_MARK - lineLength / 2,//2刻度线的一半
                x, y - mRadius + mBoardWidth / 2, mLinePaint);

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcRectf = new RectF((ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 - mRadius,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 - mRadius,
                (ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 + mRadius,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 + mRadius);
        mArcRectfInner = new RectF((ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 - mRadius + mDis + mBoardWidth / 2 + mInnerBoardWidth / 2,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 - mRadius + mDis + mBoardWidth / 2 + mInnerBoardWidth / 2,
                (ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 + mRadius - mDis - mBoardWidth / 2 - mInnerBoardWidth / 2,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 + mRadius - mDis - mBoardWidth / 2 - mInnerBoardWidth / 2);
        // TODO: 2017/8/21
        mILRectf = new RectF((ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 - mRadius,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 - mRadius,
                (ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 + mRadius,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 + mRadius);
        mOLRectf = new RectF((ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 - mRadius - mBoardWidth / 2 - DEFAULT_TICK_MARK,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 - mRadius - mBoardWidth / 2 - DEFAULT_TICK_MARK,
                (ArcProgress.this.getRight() - ArcProgress.this.getLeft()) / 2 + mRadius + mBoardWidth / 2 + DEFAULT_TICK_MARK,
                (ArcProgress.this.getBottom() - ArcProgress.this.getTop()) / 2 + mRadius + mBoardWidth / 2 + DEFAULT_TICK_MARK);
    }

    public int getOverProgress() {
        return overProgress;
    }

    // overSpeed progress
    public synchronized void setOverProgress(int overProgress) {
        //over progress 起始位置
        this.overProgress = overProgress;
        ArcProgress.this.invalidate();
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public interface OnCenterDraw {
        /**
         * @param canvas
         * @param rectF      圆弧的Rect
         * @param x          圆弧的中心x
         * @param y          圆弧的中心y
         * @param speedValue 当前进度
         * @param maxSpeed
         */
        public void draw(Canvas canvas, RectF rectF, float x, float y, float speedValue, float maxSpeed);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCenterBitmap != null) {
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }

    }

    public int speed2Degree(float speed) {
        return (int) ((360 - mDegree) * speed * 1.0f / getMaxSpeed());
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.preOverSpeedValue = maxSpeed;
        this.maxSpeed = maxSpeed;
    }

    public float getmSpeedValue() {
        return mSpeedValue;
    }

    private SpeedGradient speedGradient;
    private SpeedGradient overSpeedGradient;

    public void setmSpeedValue(float mSpeedValue) {
        if (mSpeedValue > this.mOverSpeedValue) return;
        this.mSpeedValue = Float.parseFloat(df.format(mSpeedValue));
        if (preSpeedValue != mSpeedValue) {
            if (speedGradient != null) speedGradient.interrupt();
            // TODO: 2017/8/24  standard usage
            speedGradient = new SpeedGradient(this, preSpeedValue, mSpeedValue, maxSpeed, SpeedGradient.FLAG_SPEED);
            speedGradient.start();

            // TODO: 2017/8/24 debug
//            this.setProgress((int) (100 * mSpeedValue / maxSpeed));
//            setmShowSpeed(mSpeedValue);

            preSpeedValue = mSpeedValue;
        }
    }

    public float getmOverSpeedValue() {
        return mOverSpeedValue;
    }

    public void setmOverSpeedValue(float mOverSpeedValue) {
        if (mOverSpeedValue < this.mSpeedValue) return;
        this.mOverSpeedValue = Float.parseFloat(df.format(mOverSpeedValue));
        if (preOverSpeedValue != mOverSpeedValue) {
            if (overSpeedGradient != null) overSpeedGradient.interrupt();
            // TODO: 2017/8/24  standard usage
            overSpeedGradient = new SpeedGradient(this, preOverSpeedValue, mOverSpeedValue, maxSpeed, SpeedGradient.FLAG_OVERSPEED);
            overSpeedGradient.start();

            // TODO: 2017/8/24 debug
//            this.setOverProgress((int) (100 * mOverSpeedValue / maxSpeed));

            preOverSpeedValue = mOverSpeedValue;
        }
    }


}

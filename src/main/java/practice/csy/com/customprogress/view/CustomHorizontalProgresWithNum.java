package practice.csy.com.customprogress.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import practice.csy.com.customprogress.R;

/**
 * Created by user on 2017-8-8.
 * <p>
 * 直接继承ProgressBar可以实现进度额保存
 * <p>
 * 带数字进度
 */

public class CustomHorizontalProgresWithNum extends ProgressBar {

    //默认值
    private static final int DEAFUALT_PROGRESS_UNREACH_HEIGHH = 10;//dp
    protected static final int DEAFUALT_PROGRESS_UNREACH_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_REACH_HEIGHH = 10;//dp
    protected static final int DEAFUALT_PROGRESS_REACH_CORLOR = 0xFFFC00D1;
    protected static final int DEAFUALT_PROGRESS_TEXT_SIZE = 10;//sp
    protected static final int DEAFUALT_PROGRESS_TEXT_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_TEXT_OFFSET = 10;//dp
    protected static final int DEAFUALT_PROGRESS_VIEW_WIDTH = 200;//进度条默认宽度

    protected int HorizontalProgresUnReachColor;//不能用static修饰,不然多个View会共用此属性
    private int HorizontalProgresTextColorComplete;
    protected int HorizontalProgresReachColor;
    private int HorizontalProgresReachHeight;
    protected int HorizontalProgresTextColor;
    protected int HorizontalProgresTextSize;
    protected int HorizontalProgresTextOffset;
    private PorterDuffXfermode mPorterDuffXfermode;
    private boolean HorizontalHaveText = false;

    protected Paint mPaint = new Paint();

    public CustomHorizontalProgresWithNum(Context context) {
        this(context, null);
    }

    public CustomHorizontalProgresWithNum(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHorizontalProgresWithNum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getStyleabletAttr(attrs);
    }

    private void init() {
        mPaint.setTextSize(HorizontalProgresTextSize);//设置画笔文字大小,便于后面测量文字宽高
        mPaint.setColor(HorizontalProgresTextColor);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }


    /**
     * 获取自定义属性
     */
    protected void getStyleabletAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomHorizontalProgressStyle);
        HorizontalProgresUnReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressUnReachColor, DEAFUALT_PROGRESS_UNREACH_CORLOR);
        HorizontalProgresReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressReachColor, DEAFUALT_PROGRESS_REACH_CORLOR);
        //将sp、dp统一转换为sp
        HorizontalProgresReachHeight = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressReachHeight, dp2px(getContext(), DEAFUALT_PROGRESS_REACH_HEIGHH));
        HorizontalHaveText = typedArray.getBoolean(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressHaveText, false);
        if (HorizontalHaveText) {
            HorizontalProgresTextColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextColor, DEAFUALT_PROGRESS_TEXT_CORLOR);
            HorizontalProgresTextSize = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextSize, sp2px(getContext(), DEAFUALT_PROGRESS_TEXT_SIZE));
            HorizontalProgresTextOffset = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextOffset, DEAFUALT_PROGRESS_TEXT_OFFSET);
            HorizontalProgresTextColorComplete = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextColorComplete, HorizontalProgresTextColor);
        } else {
            HorizontalProgresTextOffset = 0;
            HorizontalProgresTextSize = 0;
        }
        typedArray.recycle();//记得加这句
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);//计算宽高
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽高
    }

    private String progress = "";
    private float textBaseY;
    private float progressX;
    private float textWidth;
    private float realWidth;
    private float radio;
    private float textStartX;
    private RectF mRectF;
    private double mRectR;


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //save、restore 图层的保存和回滚相关的方法 详见 http://blog.csdn.net/tianjian4592/article/details/45234419
        canvas.save();
        //移动图层到垂直居中位置
        canvas.translate(0, getHeight() / 2 - HorizontalProgresReachHeight / 2);
        progress = getProgress() + "%";
        textWidth = mPaint.measureText(progress);
        //progressBar实际宽度
        realWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        radio = getProgress() * 1.0f / getMax();
        progressX = radio * realWidth;

        RectF mRectB = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + realWidth, HorizontalProgresReachHeight);
        mPaint.setColor(HorizontalProgresUnReachColor);
        canvas.drawRoundRect(mRectB, HorizontalProgresReachHeight / 2, HorizontalProgresReachHeight / 2, mPaint);

        //矩形（高度比宽度高，圆角的时候会有问题，需要特殊处理）
        //绘制走完的进度线
        mPaint.setColor(HorizontalProgresReachColor);
        if (progressX <= HorizontalProgresReachHeight / 2) {
            //绘制左半圆
            //内切圆半径
            mRectR = Math.sqrt(progressX * HorizontalProgresReachHeight - Math.pow(progressX, 2));
            //内切圆矩形
            mRectF = new RectF(getPaddingLeft(), (float) (getPaddingTop() + HorizontalProgresReachHeight / 2 - mRectR), getPaddingLeft() + 2 * progressX, (float) (getPaddingTop() + HorizontalProgresReachHeight / 2 + mRectR));
            //左半圆
            canvas.drawArc(mRectF, -90f, -180f, true, mPaint);
        } else if (progressX <= HorizontalProgresReachHeight) {
            //左侧为半圆右侧为椭圆
            //内切圆矩形
            mRectF = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + HorizontalProgresReachHeight, getPaddingTop() + HorizontalProgresReachHeight);
            //左半圆
            canvas.drawArc(mRectF, -90f, -180f, true, mPaint);
            RectF oval2 = new RectF(getPaddingLeft() + HorizontalProgresReachHeight - progressX, getPaddingTop(), getPaddingLeft() + progressX, HorizontalProgresReachHeight);
            // 设置个新的长方形，扫描测量
            canvas.drawArc(oval2, -90f, 180f, true, mPaint);
        } else {
            //圆角矩形
            mRectF = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + progressX, HorizontalProgresReachHeight);
            canvas.drawRoundRect(mRectF, HorizontalProgresReachHeight / 2, HorizontalProgresReachHeight / 2, mPaint);
        }

        //绘制进度
        mPaint.setColor(HorizontalProgresTextColor);
        mPaint.setTextSize(HorizontalProgresTextSize);
        if (textBaseY == 0) {
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            textBaseY = (mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
        }
        textStartX = getPaddingLeft() + progressX + HorizontalProgresTextOffset / 2;
        if (textStartX >= getPaddingLeft() + realWidth - HorizontalProgresTextOffset / 2 - textWidth) {
            textStartX = getPaddingLeft() + realWidth - textWidth - HorizontalProgresTextOffset / 2;
            Bitmap srcBitmap = Bitmap.createBitmap(getPaddingLeft() + (int) realWidth, HorizontalProgresReachHeight, Bitmap.Config.ARGB_8888);
            Canvas srcCanvas = new Canvas(srcBitmap);
            srcCanvas.drawText(progress, textStartX, textBaseY, mPaint);
            // 设置混合模式
            mPaint.setXfermode(mPorterDuffXfermode);
            mPaint.setColor(HorizontalProgresTextColorComplete);
            RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + progressX, HorizontalProgresReachHeight);
            //mWidth是不断变化的
            // 绘制源图形
            srcCanvas.drawRect(rectF, mPaint);
            // 绘制目标图
            canvas.drawBitmap(srcBitmap, 0, 0, null);
            // 清除混合模式
            mPaint.setXfermode(null);
            mPaint.setColor(HorizontalProgresTextColor);
        } else {
            canvas.drawText(progress, textStartX, textBaseY, mPaint);
        }
        canvas.restore();
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }


    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    protected int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = dp2px(getContext(), DEAFUALT_PROGRESS_VIEW_WIDTH);//
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    protected int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            //此处高度为走完的进度高度和未走完的机大以及文字的高度的最大值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());//得到字的高度有二种方式,第一种是React,第二种这个
            result = Math.max(textHeight, Math.max(HorizontalProgresReachHeight, HorizontalProgresReachHeight)) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


}

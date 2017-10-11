package practice.csy.com.customprogress.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import practice.csy.com.customprogress.R;


/**
 * 直接继承ProgressBar可以实现进度额保存
 * <p>
 * 百分比显示位置分内部和外部，如果是在外部，建议HorizontalProgressBackgroundColor颜色为view父布局的颜色（无色差）且文本颜色一定要与父布局的颜色不一样，否则显示不出来（颜色一样看不出来）
 */

public class CustomHorizontalProgresWithNumber extends ProgressBar {

    private Context mContext;
    protected static final int DEAFUALT_PROGRESS_UNREACH_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_REACH_HEIGHH = 10;//dp
    protected static final int DEAFUALT_PROGRESS_REACH_CORLOR = 0xFFFC00D1;
    protected static final int DEAFUALT_PROGRESS_TEXT_SIZE = 10;//sp
    protected static final int DEAFUALT_PROGRESS_TEXT_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_TEXT_OFFSET = 10;//dp
    protected static final int DEAFUALT_PROGRESS_VIEW_WIDTH = 200;//进度条默认宽度

    //不能用static修饰,不然多个View会共用此属性
    protected int HorizontalProgressReachColor;
    protected int HorizontalProgressUnReachColor;
    private int HorizontalProgressReachHeight;
    protected int HorizontalProgressTextColor;
    protected int HorizontalProgressTextSize;
    protected int HorizontalProgressTextOffset;
    private boolean HorizontalProgressTextInnerOrOutSide = true;//默认进度文本在内部
    private float progressRealWidth;//进度条真实宽度
    private int HorizontalProgressBackgroundColor;//进度条背景色
    private float textOccupyOffsetMaxWidth;//文本占有最大宽度（包含左右）

    //绘制时变量
    private float radio;//比例
    private int textWidth;//文本实时宽度
    private float progressX;//进度条实时宽度
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private int progressWidth, progressHeight;//进度条（整个view）宽高
    private float textStart;

    protected Paint mPaint = new Paint();

    public CustomHorizontalProgresWithNumber(Context context) {
        this(context, null);
    }

    public CustomHorizontalProgresWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHorizontalProgresWithNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        getStyleabletAttr(attrs);
    }

    private void init() {
        mPaint.setTextSize(HorizontalProgressTextSize);//设置画笔文字大小,便于后面测量文字宽高
        mPaint.setColor(HorizontalProgressTextColor);
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
    }


    /**
     * 获取自定义属性
     */
    protected void getStyleabletAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomHorizontalProgressStyle);
        HorizontalProgressUnReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressUnReachColor, DEAFUALT_PROGRESS_UNREACH_CORLOR);
        HorizontalProgressReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressReachColor, DEAFUALT_PROGRESS_REACH_CORLOR);
        //将sp、dp统一转换为sp
        HorizontalProgressReachHeight = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressReachHeight, dp2px(getContext(), DEAFUALT_PROGRESS_REACH_HEIGHH));
        HorizontalProgressTextColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextColor, DEAFUALT_PROGRESS_TEXT_CORLOR);
        HorizontalProgressTextSize = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextSize, sp2px(getContext(), DEAFUALT_PROGRESS_TEXT_SIZE));
        HorizontalProgressTextOffset = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextOffset, DEAFUALT_PROGRESS_TEXT_OFFSET);
        HorizontalProgressTextInnerOrOutSide = typedArray.getBoolean(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressTextInnerOrOutSide, true);
        HorizontalProgressBackgroundColor = typedArray.getColor(R.styleable.CustomHorizontalProgressStyle_HorizontalProgressBackgroundColor, ContextCompat.getColor(mContext, R.color.CFFFFFF));
        typedArray.recycle();//记得加这句
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);//计算宽高
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽高
    }


    private double mRectR;
    private RectF mRectF;
    private float textBaseY;


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();//save、restore 图层的保存和回滚相关的方法 详见 http://blog.csdn.net/tianjian4592/article/details/45234419
        canvas.translate(0, getHeight() / 2 - HorizontalProgressReachHeight / 2);//移动图层到垂直居中位置
        radio = getProgress() * 1.0f / getMax();
        textOccupyOffsetMaxWidth = mPaint.measureText("100%") + HorizontalProgressTextOffset;
        textWidth = (int) mPaint.measureText(getProgress() + "%");
        //progress实际宽度
        float realWidth;
        if (progressRealWidth == 0) {
            progressRealWidth = progressWidth - paddingLeft - paddingRight;
        }
        if (HorizontalProgressTextInnerOrOutSide) {
            realWidth = progressRealWidth;
        } else {
            //如果文本在进度条外部，则进度条的长度去掉文本最大宽度作为进度条宽度
            realWidth = progressRealWidth - textOccupyOffsetMaxWidth;
        }
        progressX = radio * realWidth;
        //绘制背景
        mPaint.setColor(HorizontalProgressBackgroundColor);
        mPaint.setStrokeWidth(HorizontalProgressReachHeight);
        //圆角 int left, int top, int right, int bottom
        RectF mRectB = new RectF(paddingLeft, paddingTop, paddingLeft + realWidth, paddingTop + HorizontalProgressReachHeight);
        canvas.drawRoundRect(mRectB, HorizontalProgressReachHeight / 2, HorizontalProgressReachHeight / 2, mPaint);//圆角矩形
        //绘制走完的进度线
        mPaint.setColor(HorizontalProgressReachColor);
        if (progressX <= HorizontalProgressReachHeight / 2) {
            //绘制左半圆
            //内切圆半径
            mRectR = Math.sqrt(progressX * HorizontalProgressReachHeight - Math.pow(progressX, 2));
            //内切圆矩形
            mRectF = new RectF(paddingLeft, (float) (paddingTop + HorizontalProgressReachHeight / 2 - mRectR), paddingLeft + 2 * progressX, (float) (paddingTop + HorizontalProgressReachHeight / 2 + mRectR));
            //左半圆
            canvas.drawArc(mRectF, -90f, -180f, true, mPaint);
        } else if (progressX <= HorizontalProgressReachHeight) {
            //左侧为半圆右侧为椭圆
            //内切圆矩形
            mRectF = new RectF(paddingLeft, paddingTop, paddingLeft + HorizontalProgressReachHeight, paddingTop + HorizontalProgressReachHeight);
            //左半圆
            canvas.drawArc(mRectF, -90f, -180f, true, mPaint);
            RectF oval2 = new RectF(paddingLeft + HorizontalProgressReachHeight - progressX, paddingTop, paddingLeft + progressX, HorizontalProgressReachHeight);
            // 设置个新的长方形，扫描测量
            canvas.drawArc(oval2, -90f, 180f, true, mPaint);
        } else {
            //圆角矩形
            mRectF = new RectF(paddingLeft, paddingTop, paddingLeft + progressX, HorizontalProgressReachHeight);
            canvas.drawRoundRect(mRectF, HorizontalProgressReachHeight / 2, HorizontalProgressReachHeight / 2, mPaint);
        }
        //绘制进度
        mPaint.setColor(HorizontalProgressTextColor);
        mPaint.setTextSize(HorizontalProgressTextSize);
        if (textBaseY == 0) {
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            textBaseY = (mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
        }
        if (HorizontalProgressTextInnerOrOutSide) {
            if (progressX < textWidth + HorizontalProgressTextColor) {
                textStart = paddingLeft + (progressX - textWidth) / 2;
            } else if (progressX >= textWidth + HorizontalProgressTextOffset) {
                textStart = paddingLeft + progressX - textWidth - HorizontalProgressTextOffset / 2;
            }
        } else {
            textStart = paddingLeft + progressX + HorizontalProgressTextOffset / 2;
        }
        if (HorizontalProgressTextInnerOrOutSide) {
            if (progressX > textWidth)
                canvas.drawText(getProgress() + "%", textStart, textBaseY, mPaint);
        } else {
            canvas.drawText(getProgress() + "%", textStart, textBaseY, mPaint);
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
        progressWidth = result;
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
            result = Math.max(textHeight, HorizontalProgressReachHeight) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        progressHeight = result;
        return result;
    }


}

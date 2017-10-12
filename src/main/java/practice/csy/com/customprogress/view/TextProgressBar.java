package practice.csy.com.customprogress.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import practice.csy.com.customprogress.R;
import practice.csy.com.customprogress.constant.Constant;

/**
 * Created by Jingjing.hu on 2016/11/18.
 */

public class TextProgressBar extends ProgressBar {
    private int TEXT_SIZE_12 = (int) getResources().getDimension(R.dimen.text_12);
    private String mPercentText = "0%";
    private String mDefaultText = getResources().getString(R.string.download);
    private String mFinishText = getResources().getString(R.string.finish);
    private String mFailedText = getResources().getString(R.string.failed);
    private Rect mPauseRect;
    private Rect mPercentRect;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private float mWidth;
    private int mStateType = Constant.Default;

    public TextProgressBar(Context context) {
        super(context);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public synchronized void setStateType(int stateType) {
        mStateType = stateType;
        invalidate();
    }

    public synchronized int getStateType() {
        return mStateType;
    }

    public void init() {
        mPauseRect = new Rect();
        mPercentRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mPaint.setColor(Color.parseColor("#00B38A"));
        mPaint.setTextSize(TEXT_SIZE_12);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setXfermode(null);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPercentText = getProgress() + "%";
        if (getProgress() == getMax()) mStateType = Constant.Finish;
        mWidth = getWidth() * getProgress() / 100;//进度作一下处理，解决不同分辨率的适配问题
        mPaint.getTextBounds(mPercentText, 0, mPercentText.length(), mPercentRect);//为了获取文字的宽高以及坐标位置，get之后，rect.centerX才有值
        mPaint.getTextBounds(mFinishText, 0, mFinishText.length(), mPauseRect);
        int textX = (getWidth() / 2) - mPauseRect.centerX();//获取“暂停”文字的中心横坐标
        int textY = (getHeight() / 2) - mPauseRect.centerY();
        int percentX = (getWidth() / 2) - mPercentRect.centerX();//获取百分比文字的中心横坐标
        int percentY = (getHeight() / 2) - mPercentRect.centerY();
        Bitmap srcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(srcBitmap);
        switch (mStateType) {
            case Constant.Default://默认初始化状态
                drawTextUI(canvas, textX, textY, mDefaultText, srcBitmap, srcCanvas);
                break;
            case Constant.Pause:
            case Constant.Downloading:
                drawTextUI(canvas, percentX, percentY, mPercentText, srcBitmap, srcCanvas);
                break;
            case Constant.Finish:
                mPaint.setColor(Color.WHITE);
                canvas.drawText(mFinishText, textX, textY, mPaint);
                break;
            case Constant.Failed:
                mPaint.setColor(Color.RED);
                canvas.drawText(mFailedText, textX, textY, mPaint);
                break;
        }
    }

    private void drawTextUI(Canvas canvas, int x, int y, String textContent, Bitmap srcBitmap, Canvas srcCanvas) {
        mPaint.setColor(Color.parseColor("#00b38a"));
        canvas.drawText(textContent, x, y, mPaint);
        srcCanvas.drawText(textContent, x, y, mPaint);

        // 设置混合模式
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(Color.WHITE);
        RectF rectF = new RectF(0, 0, mWidth, getHeight());//mWidth是不断变化的
        // 绘制源图形
        srcCanvas.drawRect(rectF, mPaint);
        // 绘制目标图
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        // 清除混合模式
        mPaint.setXfermode(null);
        // 恢复画笔颜色
        mPaint.setColor(Color.parseColor("#00b38a"));
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

}

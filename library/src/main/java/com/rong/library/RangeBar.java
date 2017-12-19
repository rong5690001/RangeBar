package com.rong.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chen.huarong on 2017/12/15.
 * 范围选择进度条
 * "textSize" format="dimension"/>
 * "textColor" format="color"/>
 * "min" format="integer"/> 最小值
 * "max" format="integer"/>  最大值
 * "proBackground" format="reference"/> 进度条背景
 * "proColor" format="color"/> 进度条颜色
 * "thumbDrawable" format="reference"/> 圆形按钮drawable
 * "preValue" format="string"/> 顶部文字前缀
 * "lastValue" format="string"/> 顶部文字后缀
 */

public class RangeBar extends View {
    private static final int TOUCH_NULL = 0X1;
    private static final int TOUCH_LEFT = 0X2;
    private static final int TOUCH_RIGHT = 0X3;
    private static final int DEFAULT_TEXTSIZE = 14;
    private static final int DEFAULT_TEXTCOLOR = Color.BLACK;
    private int touchType = TOUCH_NULL;
    //左边圆形按钮
    private Paint leftThumbPaint;
    private int leftThumbX;
    private Rect leftThumbRect = new Rect();
    //右边圆形按钮
    private Paint rightThumbPaint;
    private int rightThumbX;
    private Rect rightThumbRect = new Rect();
    private Drawable thumbDrawable;
    private int thumbRadius;
    private Paint centerDrawablePaint;
    private int centerY;
    private Drawable mBackground;
    private int lineHeight;//线的高度
    private Paint topTextPaint;
    private int topTextSize = DEFAULT_TEXTSIZE;
    private int topTextColor = DEFAULT_TEXTCOLOR;
    private int topTxtHeight;

//    private Paint bottomTextPaint;
//    private int bottomTextSize = DEFAULT_TEXTSIZE;
//    private int bottomTextColor = DEFAULT_TEXTCOLOR;
//    private int bottomTxtHeight;

    private int min;
    private int max;
    private String preValue;
    private String lastValue;
    //    private String minTxt = "￥0";
//    private String maxTxt = "￥2000";
    private int progressWidth;//进度条的宽度
    private int offset;//进度条的偏移量
//    private int rightOffset;//进度条的右偏移量

    public RangeBar(Context context) {
        this(context, null);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeBar);

        topTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeBar_textSize
                , DensityUtils.sp2px(getContext(), 14));
        topTextColor = typedArray.getColor(R.styleable.RangeBar_textColor, DEFAULT_TEXTCOLOR);
        mBackground = typedArray.getDrawable(R.styleable.RangeBar_proBackground);
        thumbDrawable = typedArray.getDrawable(R.styleable.RangeBar_thumbDrawable);
        preValue = typedArray.getString(R.styleable.RangeBar_preValue);
        preValue = TextUtils.isEmpty(preValue) ? "" : preValue;
        lastValue = typedArray.getString(R.styleable.RangeBar_lastValue);
        lastValue = TextUtils.isEmpty(lastValue) ? "" : lastValue;
        min = typedArray.getInteger(R.styleable.RangeBar_min, 0);
        max = typedArray.getInteger(R.styleable.RangeBar_max, 2000);
        int proColor = typedArray.getColor(R.styleable.RangeBar_proColor, Color.BLUE);
        typedArray.recycle();

        leftThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint.setColor(proColor);

        topTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        topTextPaint.setTextSize(topTextSize);
        topTextPaint.setColor(topTextColor);
        topTextPaint.setTextAlign(Paint.Align.CENTER);

//        bottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bottomTextPaint.setTextSize(bottomTextSize);
//        bottomTextPaint.setColor(bottomTextColor);
        if (mBackground == null) {
            mBackground = new ColorDrawable(Color.GRAY);
        }
        if (thumbDrawable == null) {
            thumbDrawable = new ColorDrawable(Color.YELLOW);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Paint.FontMetrics fontMetrics = topTextPaint.getFontMetrics();
        topTxtHeight = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
//        fontMetrics = bottomTextPaint.getFontMetrics();
//        bottomTxtHeight = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineHeight = h / 7;
        thumbRadius = lineHeight * 2;
        centerDrawablePaint.setStrokeWidth(lineHeight);
        centerY = h * 2 / 3;
        //默认位置
        leftThumbX = w / 4;
        rightThumbX = w * 3 / 4;

        int maxValueTxtWidth = (int) topTextPaint.measureText(preValue + max + lastValue);
        offset = Math.max(thumbRadius, maxValueTxtWidth / 2);

//        int minValueTxtWidth = (int) topTextPaint.measureText(preValue + min + lastValue);
//        offset = Math.max(thumbRadius, minValueTxtWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBackground != null) {//背景
            mBackground.setBounds(getProgressLeft()
                    , centerY - lineHeight / 2
                    , getProgressRight()
                    , centerY + lineHeight / 2);
            mBackground.draw(canvas);
        }

        //进度线
        canvas.drawLine(leftThumbX, centerY, rightThumbX, centerY, centerDrawablePaint);
        if (thumbDrawable != null) {
            //左边按钮
            leftThumbRect.set(leftThumbX - thumbRadius
                    , centerY - thumbRadius
                    , leftThumbX + thumbRadius
                    , centerY + thumbRadius);
            rightThumbRect.set(rightThumbX - thumbRadius
                    , centerY - thumbRadius
                    , rightThumbX + thumbRadius
                    , centerY + thumbRadius);
            thumbDrawable.setBounds(leftThumbRect);
            thumbDrawable.draw(canvas);
            //右边按钮
            thumbDrawable.setBounds(rightThumbRect);
            thumbDrawable.draw(canvas);
        }

        //左边文字
        canvas.drawText(getValue(leftThumbX)
                , leftThumbX
                , centerY - thumbRadius - topTxtHeight / 2
                , topTextPaint);

        //右边文字
        canvas.drawText(getValue(rightThumbX)
                , rightThumbX
                , centerY - thumbRadius - topTxtHeight / 2
                , topTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchValid(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchType == TOUCH_NULL) return super.onTouchEvent(event);
                if (touchType == TOUCH_LEFT) {
                    leftThumbX = (int) event.getX();
                    if (isOutside(event)) {//不在进度条范围内
                        leftThumbX = getProgressLeft();
                    } else if (isCollision(leftThumbX)) {//发生碰撞
                        leftThumbX = rightThumbX - thumbRadius * 2;
                    }
                } else {
                    rightThumbX = (int) event.getX();
                    if (isOutside(event)) {//不在进度条范围内
                        rightThumbX = getProgressRight();
                    } else if (isCollision(rightThumbX)) {//发生碰撞
                        rightThumbX = leftThumbX + thumbRadius * 2;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchType = TOUCH_NULL;
                invalidate();
                break;
        }

        return true;
    }

    private String getValue(int thumbX) {
        return preValue
                + (min + (max - min) * (thumbX - getProgressLeft()) / ((getProgressRight()) - getProgressLeft()))
                + lastValue;
    }

    private int getProgressLeft() {
        return offset;
    }

    private int getProgressRight() {
        return getWidth() - offset;
    }

    /**
     * 是否在进度范围内
     *
     * @return
     */
    private boolean isOutside(MotionEvent event) {
        return event.getX() >= getProgressRight()
                || event.getX() <= getProgressLeft();
    }

    private void touchValid(MotionEvent event) {
        if (event.getX() >= leftThumbRect.left
                && event.getX() <= leftThumbRect.right
                && event.getY() >= leftThumbRect.top
                && event.getY() <= leftThumbRect.bottom) {//触摸左边按钮
            touchType = TOUCH_LEFT;
        } else if (event.getX() >= rightThumbRect.left
                && event.getX() <= rightThumbRect.right
                && event.getY() >= rightThumbRect.top
                && event.getY() <= rightThumbRect.bottom) {
            touchType = TOUCH_RIGHT;
        } else {
            touchType = TOUCH_NULL;
        }
    }

    /**
     * 是否碰撞
     *
     * @return
     */
    private boolean isCollision(int thumbX) {
        switch (touchType) {
            case TOUCH_NULL:
                return true;
            case TOUCH_LEFT:
                return thumbX + thumbRadius >= rightThumbX - thumbRadius;
            case TOUCH_RIGHT:
                return thumbX - thumbRadius <= leftThumbX + thumbRadius;
            default:
                return true;
        }
    }

}

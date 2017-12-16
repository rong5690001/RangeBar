package com.rong.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chen.huarong on 2017/12/15.
 */

public class RangeBar extends View {
    private static final int TOUCH_NULL = 0X1;
    private static final int TOUCH_LEFT = 0X2;
    private static final int TOUCH_RIGHT = 0X3;
    private static final int DEFAULT_TEXTSIZE = 14;
    private static final int DEFAULT_TEXTCOLOR = Color.BLACK;
    private int touchType = TOUCH_NULL;

    private Paint leftThumbPaint;
    private int leftThumbX;
    private Rect leftThumbRect = new Rect();
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

    private int min = 0;
    private int max = 100;
    private int leftValue;
    private int rightValue;
//    private String minTxt = "￥0";
//    private String maxTxt = "￥2000";

    public RangeBar(Context context) {
        this(context, null);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        leftThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint.setColor(Color.BLUE);

        topTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        topTextPaint.setTextSize(DensityUtils.sp2px(getContext(), topTextSize));
        topTextPaint.setColor(topTextColor);
        topTextPaint.setTextAlign(Paint.Align.CENTER);

//        bottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bottomTextPaint.setTextSize(bottomTextSize);
//        bottomTextPaint.setColor(bottomTextColor);

        mBackground = ContextCompat.getDrawable(context, R.drawable.range_bar_bg);
        thumbDrawable = ContextCompat.getDrawable(context, R.drawable.thumb_white);


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
        //TODO
        leftThumbX = w / 4;
        rightThumbX = w * 3 / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBackground != null) {//背景
            mBackground.setBounds(thumbRadius
                    , centerY - lineHeight / 2
                    , getWidth() - thumbRadius
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
        canvas.drawText(String.valueOf(getValue(leftThumbX))
                , leftThumbX
                , centerY - thumbRadius - topTxtHeight / 2
                , topTextPaint);

        //右边文字
        canvas.drawText(String.valueOf(getValue(rightThumbX))
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
                        leftThumbX = thumbRadius;
                    } else if (isCollision(leftThumbX)) {//发生碰撞
                        leftThumbX = rightThumbX - thumbRadius;
                    }
                } else {
                    rightThumbX = (int) event.getX();
                    if (isOutside(event)) {//不在进度条范围内
                        rightThumbX = getWidth() - thumbRadius;
                    } else if (isCollision(rightThumbX)) {//发生碰撞
                        rightThumbX = leftThumbX + thumbRadius;
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

    private int getValue(int thumbX) {
        return min + (max - min) * (thumbX - thumbRadius) / (getWidth() - thumbRadius * 2);
    }

    /**
     * 是否在进度范围内
     *
     * @return
     */
    private boolean isOutside(MotionEvent event) {
        return event.getX() >= getWidth() - thumbRadius
                || event.getX() <= 0 + thumbRadius;
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

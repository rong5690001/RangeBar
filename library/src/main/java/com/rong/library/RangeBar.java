package com.rong.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chen.huarong on 2017/12/15.
 */

public class RangeBar extends View {

    private Paint leftDrawablePaint;
    private Paint rightDrawablePaint;
    private Paint centerDrawablePaint;

    public RangeBar(Context context) {
        this(context, null);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        leftDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDrawablePaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int startX = getLeft();
        int endX = getRight();

        canvas.drawLine(startX, 0, endX, 0, centerDrawablePaint);
    }
}

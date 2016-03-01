package com.baiyyyhjl.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.baiyyyhjl.customview.R;

/**
 * Created by huangjinlong on 2016/2/29.
 */
public class CustomProgressBar extends View{
    /**
     * 第一圈的颜色
     */
    private int mFirstColor;
    /**
     * 第二圈的颜色
     */
    private int mSecondColor;

    /**
     * 圆弧的宽度
     */
    private int mCircleWidth;

    /**
     * 进度条的速度
     */
    private int mSpeed;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 当前的进度
     */
    private int mProgress = 0;
    /**
     * 是否应该开始下一个
     */
    private boolean isNext;
    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0 ; i < n; i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.CustomProgressBar_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomProgressBar_secondColor:
                    mSecondColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.CustomProgressBar_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomProgressBar_speed:
                    mSpeed = a.getInt(attr, 20);
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
        // 绘图线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    mProgress ++;
                    if (mProgress == 360){
                        mProgress = 0;
                        isNext = !isNext;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 圆心
        int centre = getWidth() / 2;
        // 半径(半径是指圆心到圆边宽度的二分之一)
        int radius = centre - mCircleWidth / 2;
        // 设置圆边宽度
        mPaint.setStrokeWidth(mCircleWidth);
        // 设置锯齿
        mPaint.setAntiAlias(true);
        // 设置空心
        mPaint.setStyle(Paint.Style.STROKE);
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
        if (!isNext){
            // 如果是第一圈，跑完画第二圈
            mPaint.setColor(mFirstColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            // 绘制圆弧
            mPaint.setColor(mSecondColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);
        } else {
            // 如果是第二圈，跑完画第二圈
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            // 绘制圆弧
            mPaint.setColor(mFirstColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);
        }
    }
}

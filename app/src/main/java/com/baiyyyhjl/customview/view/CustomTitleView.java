package com.baiyyyhjl.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.baiyyyhjl.customview.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by huangjinlong on 2016/2/26.
 */
public class CustomTitleView extends View {
    /**
     * 文本
     */
    private String mTitleText;
    /**
     * 文本颜色
     */
    private int mTitleTextColor;
    /**
     * 文本大小
     */
    private int mTitleTextSize;

    /**
     * 获得bitmap图片
     */
    private Bitmap mImage;
    /**
     * 图片缩放类型
     */
    private int mImageScaleType;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 绘制的矩形区域
     */
    private Rect rect;

    /**
     * 控件的宽度和高度
     */
    private int width;
    private int height;

    private int SCALE_TYPE_FILLXY = 0;
    private int SCALE_TYPE_CENTER = 1;

    public CustomTitleView(Context context) {
        this(context, null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获得自定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 获得自定义的样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTitleView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTitleView_titleTextView:
                    mTitleText = a.getString(attr);
                    break;
                case R.styleable.CustomTitleView_titleTextColorView:
                    // 默认颜色为黑色
                    mTitleTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTitleView_titleTextSizeView:
                    // 默认字体大小为16sp,TypeValue也可以把sp转换为px
                    mTitleTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomTitleView_image:
                    mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomTitleView_imageScaleType:
                    mImageScaleType = a.getInt(attr, 0);
                    break;
            }
        }
        /**
         * 遍历整个自定义属性赋值之后将TypedArray回收
         */
        a.recycle();
        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setColor(mTitleTextColor);
        rect = new Rect();
        mBound = new Rect();
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);

        /**
         * 在构造中添加点击事件
         */
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleText = randomText();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            // 由图片决定的宽度
            int desireImage = getPaddingRight() + getPaddingRight() + mImage.getWidth();
            // 由文字决定的宽度
            int desireText = getPaddingLeft() + +getPaddingRight() + mBound.width();
            if (widthMode == MeasureSpec.AT_MOST) //wrap_content
            {
                int desired = Math.max(desireImage, desireText); // 图片跟文字宽度中大的值
                width = Math.min(desired, widthSize);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            // 图片加文字的高度
            int desired = getPaddingTop() + getPaddingBottom() + mImage.getHeight() + mBound.height();
            if (heightMode == MeasureSpec.AT_MOST) {

                height = Math.min(desired, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制边框
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        // 矩形区域的绘制点
        rect.left = getPaddingLeft();
        rect.top = getPaddingTop();
        rect.right = width - getPaddingRight();
        rect.bottom = height - getPaddingBottom();
        mPaint.setColor(mTitleTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        /**
         * 如果当前设置的宽度小于字体的宽度，将字体改为XXX
         */
        if (width < mBound.width()){
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitleText, paint, (float)(width - getPaddingLeft() - getPaddingRight()), TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), height - getPaddingBottom(), mPaint);
        } else
        {
            // 正常情况将字体居中
            canvas.drawText(mTitleText, width / 2 - mBound.width() * 1.0f / 2, height - getPaddingBottom(), mPaint);
        }
        // 取消使用掉的块
        rect.bottom -= mBound.height();
        // 如果图片是充满区块
        if (mImageScaleType == SCALE_TYPE_FILLXY){
            canvas.drawBitmap(mImage, null, rect, mPaint);
        } else {
            // 如果图片是自适应
            rect.left = width / 2 - mImage.getWidth() / 2;
            rect.top = (height - mBound.height()) / 2 - mImage.getHeight() / 2;
            rect.right = width / 2 + mImage.getWidth() / 2;
            rect.bottom = (height - mBound.height()) / 2 + mImage.getHeight() / 2;
            canvas.drawBitmap(mImage, null, rect, mPaint);
        }

//        // 绘制背景
//        mPaint.setColor(Color.YELLOW);
//        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
//        // 绘制字体大小
//        mPaint.setColor(mTitleTextColor);
//        mPaint.setTextSize(mTitleTextSize);
//        // 将字绘制在屏幕中央
//        canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }

    private String randomText() {
        Random random = new Random();
        Set<Integer> set = new HashSet<>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set) {
            sb.append("" + i);
        }
        return sb.toString();
    }
}

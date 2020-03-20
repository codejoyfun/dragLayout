package com.lxtx.mydraglayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @author 宁锟
 * @since 2020/3/20
 */
public class RefreshView extends View {

    public RefreshView(Context context) {
        super(context);
        init();
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public RefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int MAX_RADIUS = (int) ScreenUtil.dp(getContext(), 10);
    private int MIN_RADIUS = MAX_RADIUS / 2;
    private int radius = 0;
    private int offsetY = 0;
    private Paint paint = new Paint();
    private float alphaThreshold = 0.7f;//消隐的阈值(透明度开始变化的阈值)
    private float pointVisibleThreshold = 0.9f;//圆点出现的阈值
    private float pointGoneThreshold = 0.6f;//圆点消失的阈值

    //进度 1到0.9 逐渐变大   从0.7开始变小，直到0.6变无
    private void init() {
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(android.R.color.background_dark));

        setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, offsetY, radius, paint);
    }

    public void setProgress(float progress) {
        // 进度 1到0.9 逐渐变大
        if (progress >= pointVisibleThreshold) {
            radius = (int) (MAX_RADIUS * (1f - (progress - pointVisibleThreshold) / (1f - pointVisibleThreshold)));
            if (progress == pointVisibleThreshold) {//浮点数转整型有精度缺失，临界值需要保证恢复原值
                radius = MAX_RADIUS;
            }
        }
        //从0.7开始变小，直到0.6变无
        if (progress >= pointGoneThreshold && progress <= alphaThreshold) {
            radius = MIN_RADIUS + (int) ((MAX_RADIUS - MIN_RADIUS) * (progress - pointGoneThreshold) / (alphaThreshold - pointGoneThreshold));
            if (progress == alphaThreshold) {
                radius = MAX_RADIUS;
            }
        }

        offsetY = (int) ((float) getHeight() * progress + (float) getHeight() * (1f - progress) / 3f);
        postInvalidate();
        if (progress > alphaThreshold) {
            setAlpha(1);
            return;
        }
        if (progress == 0f) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            setAlpha(progress / alphaThreshold);
        }


    }
}

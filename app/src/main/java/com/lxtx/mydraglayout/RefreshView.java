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

    private static final float THRESHOLD_ALPHA = 0.7f;//消隐的阈值(透明度开始变化的阈值)
    private static final float THRESHOLD_POINT_VISIBLE = 0.95f;//圆点出现的阈值
    private static final float THRESHOLD_POINT_GONE = 0.6f;//圆点消失的阈值
    private final float MAX_SMALL_POINT_OFFSET = ScreenUtil.dp(getContext(), 30);//两边圆点的最大偏移值
    private final int MAX_RADIUS = (int) ScreenUtil.dp(getContext(), 5);
    private final int COMMON_RADIUS = (int) ScreenUtil.dp(getContext(), 3);
    private int centerPointRadius = 0;
    private int smallPointRadius = 0;
    private int offsetY = 0;
    private int smallPointOffsetX = 0;//左边圆点的横坐标偏移
    private Paint paint = new Paint();

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
        canvas.drawCircle(getWidth() / 2, offsetY, centerPointRadius, paint);
        canvas.drawCircle(getWidth() / 2 - smallPointOffsetX, offsetY, smallPointRadius, paint);
        canvas.drawCircle(getWidth() / 2 + smallPointOffsetX, offsetY, smallPointRadius, paint);
    }

    public void setProgress(float progress) {
        // 进度 1到0.9 逐渐变大
        if (progress >= THRESHOLD_POINT_VISIBLE) {
            centerPointRadius = (int) (MAX_RADIUS * (1f - (progress - THRESHOLD_POINT_VISIBLE) / (1f - THRESHOLD_POINT_VISIBLE)));
            if (progress == THRESHOLD_POINT_VISIBLE) {//浮点数转整型有精度缺失，临界值需要保证恢复原值
                centerPointRadius = MAX_RADIUS;
            }
        }
        //从0.7开始变小，直到0.6变无
        if (progress >= THRESHOLD_POINT_GONE && progress < THRESHOLD_POINT_VISIBLE) {
            centerPointRadius = COMMON_RADIUS + (int) ((MAX_RADIUS - COMMON_RADIUS) * (progress - THRESHOLD_POINT_GONE) / (THRESHOLD_POINT_VISIBLE - THRESHOLD_POINT_GONE));
        }

        if (progress > THRESHOLD_POINT_VISIBLE) {
            smallPointOffsetX = 0;
            smallPointRadius = 0;
        } else if (progress >= THRESHOLD_POINT_GONE) {
            //从0.9到0.6这个过程，两边圆点逐渐展开
            smallPointOffsetX = (int) (MAX_SMALL_POINT_OFFSET * (1f - (progress - THRESHOLD_POINT_GONE) / (THRESHOLD_POINT_VISIBLE - THRESHOLD_POINT_GONE)));
            smallPointRadius = COMMON_RADIUS;
        }

        offsetY = (int) ((float) getHeight() * progress + (float) getHeight() * (1f - progress) / 3f);
        postInvalidate();
        if (progress > THRESHOLD_ALPHA) {
            setAlpha(1);
            return;
        }
        if (progress == 0f) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            setAlpha(progress / THRESHOLD_ALPHA);
        }


    }
}

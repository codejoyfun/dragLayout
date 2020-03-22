package com.lxtx.mydraglayout.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.lxtx.mydraglayout.MyApp;
import com.lxtx.mydraglayout.progress.CenterPointStrategy;
import com.lxtx.mydraglayout.progress.RefreshViewAlphaStrategy;
import com.lxtx.mydraglayout.progress.SmallPointStrategy;
import com.lxtx.mydraglayout.util.ScreenUtil;

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

    public static final float THRESHOLD_ALPHA = 0.7f;//消隐的阈值(透明度开始变化的阈值)
    public static final float THRESHOLD_POINT_VISIBLE = 0.95f;//圆点出现的阈值
    public static final float THRESHOLD_POINT_GONE = 0.6f;//圆点消失的阈值
    public static final float MAX_SMALL_POINT_OFFSET = ScreenUtil.dp(MyApp.appContext, 30);//两边圆点的最大偏移值
    public static final int MAX_RADIUS = (int) ScreenUtil.dp(MyApp.appContext, 5);
    public static final int COMMON_RADIUS = (int) ScreenUtil.dp(MyApp.appContext, 3);
    private int offsetY = 0;
    private Paint pointPaint = new Paint();
    private SmallPointStrategy smallPointStrategy = new SmallPointStrategy();
    private CenterPointStrategy centerPointStrategy = new CenterPointStrategy();
    private RefreshViewAlphaStrategy alphaStrategy = new RefreshViewAlphaStrategy();

    //进度 1到0.9 逐渐变大   从0.7开始变小，直到0.6变无
    private void init() {
        initPointPaint();
        setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    private void initPointPaint(){
        pointPaint.setStyle(Style.FILL);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(getResources().getColor(android.R.color.background_dark));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, offsetY, centerPointStrategy.getRadius(), pointPaint);
        canvas.drawCircle(getWidth() / 2 - smallPointStrategy.getOffsetX(), offsetY, smallPointStrategy.getRadius(), pointPaint);
        canvas.drawCircle(getWidth() / 2 + smallPointStrategy.getOffsetX(), offsetY, smallPointStrategy.getRadius(), pointPaint);
    }

    public void setProgress(float progress) {
        centerPointStrategy.onProgressChange(progress);
        smallPointStrategy.onProgressChange(progress);
        alphaStrategy.onProgressChange(progress);

        setAlpha(alphaStrategy.getAlpha());
        setVisibility(alphaStrategy.shouldVisible() ? View.VISIBLE : View.GONE);

        offsetY = (int) ((float) getHeight() * progress + (float) getHeight() * (1f - progress) / 3f);
        postInvalidate();
    }
}

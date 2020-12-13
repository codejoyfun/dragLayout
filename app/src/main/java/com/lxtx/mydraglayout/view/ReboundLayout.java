package com.lxtx.mydraglayout.view;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.lxtx.mydraglayout.OnReboundListener;
import com.lxtx.mydraglayout.util.ScreenUtil;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.ACTION_UP;

/**
 * @author 宁锟
 * @since 2020/4/7
 */
public class ReboundLayout extends NestedScrollingViewGroup {

    public ReboundLayout(Context context) {
        super(context);
    }

    public ReboundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReboundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public ReboundLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final int REBOUND_SLOP = (int) ScreenUtil.dp(200);//边界超出该距离，执行回弹监听器的逻辑
    private static final int CONSUME_FACTOR = 2;//overScroll消耗的距离比例
    private Scroller scroller = new Scroller(getContext());//负责view的滑动
    private OnReboundListener onReboundListener;

    public void setOnReboundListener(OnReboundListener listener){
        onReboundListener = listener;
    }

    public RecyclerView getContentView(){
        return getChildCount() == 0? null : (RecyclerView) getChildAt(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == ACTION_UP && getScrollY() > 0) {
            if(onReboundListener != null && getScrollY() > REBOUND_SLOP) onReboundListener.onRebound();
            scroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY(), 500);
            invalidate();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (getScrollY() > 0) {
            scrollBy(0, dy / CONSUME_FACTOR);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed > 0 && type == ViewCompat.TYPE_TOUCH) {
            scrollBy(0, dyUnconsumed);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        //边界检测
        if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }
        View childView = getChildAt(0);
        childView.layout(l, t, r, b);
    }
}

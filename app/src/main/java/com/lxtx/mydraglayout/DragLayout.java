package com.lxtx.mydraglayout;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;

/**
 * todo 1 加入多点触摸 2 加入recyclerView，处理触摸拦截冲突事件
 *
 * @author 宁锟
 * @since 2020/3/15
 */
public class DragLayout extends ViewGroup implements NestedScrollingParent3 {


    public DragLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    int lastTouchY = 0;
    int maxScrollY = 0;
    int topViewCenterScrollY = 0; //topview的中心Y坐标
    Scroller scroller = new Scroller(getContext());
    GestureDetectorCompat detector;
    private VelocityTracker velocityTracker; // 速度辅助工具
    private ViewConfiguration viewConfiguration;
    private int topViewHeight;//顶部view的高度

    private void init(Context context) {
        detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

        });
        velocityTracker = VelocityTracker.obtain();
        viewConfiguration = ViewConfiguration.get(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addRecyclerViewItemTouchListener((RecyclerView) getChildAt(1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastTouchY = (int) event.getY();
            }
            return true;
            case MotionEvent.ACTION_MOVE: {
                int dy = (int) (event.getY() - lastTouchY);
                int tempScrollY = getScrollY() - dy;
                if (tempScrollY >= 0 && tempScrollY <= maxScrollY) {
                    scrollBy(0, -dy);
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                velocityTracker.computeCurrentVelocity(1000);
                if (isFlingDown(velocityTracker.getYVelocity())) {
                    scrollToBottom();
                } else if (isFlingUp(velocityTracker.getYVelocity())) {
                    scrollToTop();
                } else if (getScrollY() > topViewCenterScrollY) {
                    scrollToBottom();
                } else {
                    scrollToTop();
                }
                break;
        }
        lastTouchY = (int) event.getY();
        return super.onTouchEvent(event);
    }

    public void openTop() {

    }

    public void openBottom() {

    }

    private boolean canChildrenScrollVertically(MotionEvent event, int direction) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (inTouchRange(event, childView)) {
                if (childView instanceof RecyclerView) {
                    if (canRecyclerViewScrollVertically((RecyclerView) childView, direction)) {
                        return true;
                    }
                } else if (childView.canScrollVertically(direction)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canRecyclerViewScrollVertically(RecyclerView recyclerView, int direction) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager());
        int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return (direction == -1 && lastCompletelyVisibleItemPosition != layoutManager.getItemCount() - 1) || (direction == 1 && firstCompletelyVisibleItemPosition != 0);
    }

    private boolean inTouchRange(MotionEvent event, View view) {
        return event.getX() >= view.getLeft() && event.getX() <= view.getRight() && event.getY() >= (view.getTop() - getScrollY()) && event.getY() <= (view.getBottom() - getScrollY());
    }

    private boolean isFlingDown(float velocity) {//view向下滑动
        return velocity < 0 && Math.abs(velocity) > viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private boolean isFlingUp(float velocity) {//view向上滑动
        return velocity > 0 && Math.abs(velocity) > viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private void scrollToTop() {
        scroller.startScroll(0, getScrollY(), 0, 0 - getScrollY());
        invalidate();
    }

    private void scrollToBottom() {
        scroller.startScroll(0, getScrollY(), 0, maxScrollY - getScrollY());
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //第一个view按照自己定义的宽高来，第二个view和父view的大小一致
        View firstChild = getChildAt(0);
        measureChild(firstChild, widthMeasureSpec, heightMeasureSpec);
        View secondChild = getChildAt(1);
        measureChild(secondChild, widthMeasureSpec, heightMeasureSpec);

        int realWidth = MeasureSpec.getSize(widthMeasureSpec);
        topViewHeight = firstChild.getMeasuredHeight();
        int totalHeight = topViewHeight + secondChild.getMeasuredHeight();
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        int realHeight = Math.max(totalHeight, specHeight);
        setMeasuredDimension(realWidth, realHeight);
        maxScrollY = realHeight - specHeight;
        topViewCenterScrollY = firstChild.getMeasuredHeight() / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int usedHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(l, t + usedHeight, l + child.getMeasuredWidth(), t + usedHeight + child.getMeasuredHeight());
            usedHeight = child.getMeasuredHeight();
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {

    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if(type == ViewCompat.TYPE_TOUCH && scroller.isFinished()){
            if (getScrollY() > topViewCenterScrollY) {
                scrollToBottom();
            } else {
                scrollToTop();
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {}

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean hiddenTop = dy > 0 && getScrollY() < topViewHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);
        if (hiddenTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        if (getScrollY() >= topViewHeight) {
            return false;
        }
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
        }

        if (isFlingDown(velocityY)) {
            scrollToTop();
            return true;
        } else if (isFlingUp(velocityY)) {
            scrollToBottom();
            return true;
        }
        return true;
    }

    @Override
    public void scrollTo(int x, int y) {
        //边界检测
        if (y < 0) {
            y = 0;
        }
        if (y > topViewHeight) {
            y = topViewHeight;
        }
        super.scrollTo(x, y);
    }

    private void addRecyclerViewItemTouchListener(RecyclerView rv) {
        rv.addOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (getScrollY() > topViewCenterScrollY) {
                        scrollToBottom();
                    } else {
                        scrollToTop();
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}

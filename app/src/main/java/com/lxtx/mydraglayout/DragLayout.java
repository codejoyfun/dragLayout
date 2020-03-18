package com.lxtx.mydraglayout;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
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

import static android.view.MotionEvent.ACTION_UP;

/**
 * todo 1 加入多点触摸 2 加入recyclerView，处理触摸拦截冲突事件
 *
 * @author 宁锟
 * @since 2020/3/15
 */
public class DragLayout extends ViewGroup implements NestedScrollingParent3 {

    private final int PULL_UP_DIRECTION = 1;//上拉的方向值
    private final int PULL_DOWN_DIRECTION = -1;//下拉的方向值

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
    int triggerDistance = 0; //ScrollY小于该值，触发topView显示
    Scroller scroller = new Scroller(getContext());
    GestureDetectorCompat detector;
    private VelocityTracker velocityTracker; // 速度辅助工具
    private ViewConfiguration viewConfiguration;
    private int topViewHeight;//顶部view的高度

    private RecyclerView topRv;
    private RecyclerView bottomRv;

    private void init(Context context) {
        detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

        });
        velocityTracker = VelocityTracker.obtain();
        viewConfiguration = ViewConfiguration.get(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topRv = (RecyclerView) getChildAt(0);
        bottomRv = (RecyclerView) getChildAt(1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        velocityTracker.addMovement(ev);
        if (ev.getAction() == ACTION_UP) {
            scroller.abortAnimation();
            velocityTracker.computeCurrentVelocity(1000);
            if (isFlingDown(velocityTracker.getYVelocity()) && inTouchRange(ev, topRv) && !canRecyclerViewScrollVertically(topRv, -1)) {
                scrollToBottom();
            } else if (isFlingUp(velocityTracker.getYVelocity()) && inTouchRange(ev, bottomRv) && !canRecyclerViewScrollVertically(bottomRv, 1)) {
                scrollToTop();
            } else if (getScrollY() > triggerDistance) {
                scrollToBottom();
            } else {
                scrollToTop();
            }
        }
        return super.dispatchTouchEvent(ev);
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
        if (getScrollY() == 0) {
            return;
        }
        scroller.startScroll(0, getScrollY(), 0, 0 - getScrollY());
        invalidate();
    }

    private void scrollToBottom() {
        if (getScrollY() == maxScrollY) {
            return;
        }
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
        triggerDistance = firstChild.getMeasuredHeight() * 3 / 4;
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
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (!scroller.isFinished()) {
            return;
        }
        if (target == bottomRv) {
            boolean hiddenTop = dy > 0 && topViewIsInScreen();//topView还在屏幕内显示而且手指向上滑
            //手指向下滑，contentView不能再下拉了(已经滑到顶部了)
            boolean showTop = dy < 0 /*&& getScrollY() > 0*/ && !canDropDown(target);
            if (hiddenTop || showTop) {
                scrollBy(0, dy);
                consumed[1] = dy;
                log("Scroll", "onNestedPreScroll if " + hiddenTop + " " + showTop);
            }
        } else {
            boolean hiddenBottom = dy < 0 && contentViewIsInScreen();//手指向下滑，而且contentView还在屏幕内显示
            boolean showBottom = dy > 0 && !canPullUp(target);//手指向上滑，而且topView已经不能再上拉了(已滑到底部)
            if (hiddenBottom || showBottom) {
                scrollBy(0, dy);
                consumed[1] = dy;
                log("Scroll", "onNestedPreScroll else " + hiddenBottom + " " + showBottom);
            }
        }
    }

    /**
     * topView是否在屏幕内显示
     *
     * @return
     */
    private boolean topViewIsInScreen() {
        return getScrollY() < topViewHeight;
    }

    /**
     * contentView是否在屏幕内显示
     *
     * @return
     */
    private boolean contentViewIsInScreen() {
        return getScrollY() > 0;
    }

    /**
     * view是否能下拉
     *
     * @param view
     * @return
     */
    private boolean canDropDown(View view) {
        return ViewCompat.canScrollVertically(view, PULL_DOWN_DIRECTION);
    }

    private boolean canPullUp(View view) {
        return ViewCompat.canScrollVertically(view, PULL_UP_DIRECTION);
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

    @Override
    protected void onDetachedFromWindow() {
        velocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    private void log(String tag, String msg) {
        Log.i(tag, msg);
    }
}

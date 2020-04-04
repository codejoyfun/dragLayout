package com.lxtx.mydraglayout.view;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

import com.lxtx.mydraglayout.ScrollRatioListener;
import com.lxtx.mydraglayout.UserAction;
import com.lxtx.mydraglayout.util.ScreenUtil;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * todo 1 加入多点触摸 2 加入recyclerView，处理触摸拦截冲突事件 3 添加一个滑动监听器 4 给topView加一个蒙版，仿照下拉刷新的效果
 * todo 5 写attr文件，能在xml配置属性
 *
 * @author codejoyfun
 * @since 2020/3/15
 */
public class DragLayout extends NestedScrollingViewGroup implements UserAction {

    private static final int PULL_UP_DIRECTION = 1;//上拉的方向值
    private static final int PULL_DOWN_DIRECTION = -1;//下拉的方向值

    int maxScrollY = 0;//最大的滑动偏移
    int triggerDistance = 0; //ScrollY小于该值，触发topView显示
    float triggerRatio = 0.8f; //滑动多少比例才能拉下topView
    private int topViewHeight;//顶部view的高度
    private int dampingFactor = 50;//阻尼系数
    private float topLeaveSpace = ScreenUtil.dp(80);//底部留出部分空间
    private boolean attached = false; // 是否添加到窗口系统
    private LinkedList<Runnable> afterLayoutRunnableList;//布局完成后要执行的任务
    private ScrollRatioListener scrollRatioListener;

    Scroller scroller = new Scroller(getContext());//负责view的滑动
    private VelocityTracker velocityTracker; // 速度辅助工具
    private ViewConfiguration viewConfiguration;//view常见参数常量类

    private RecyclerView topRv;//头部RecycleView
    private RecyclerView bottomRv;//底部RecycleView
    private RefreshView refreshView;//下拉刷新view

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

    private void init(Context context) {
        velocityTracker = VelocityTracker.obtain();
        viewConfiguration = ViewConfiguration.get(context);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (null != afterLayoutRunnableList && !afterLayoutRunnableList.isEmpty()) {
                    while (afterLayoutRunnableList.size() > 0) {
                        afterLayoutRunnableList.removeFirst().run();
                    }
                }
            }
        });
    }
////////////////////////////////////对外提供的一些接口////////////////////////////////////////////////////////////////
    /**
     * 打开topView
     */
    @Override
    public void openTop(final boolean smooth) {
        postAfterLayout(new Runnable() {
            @Override
            public void run() {
                if (smooth) {
                    scrollToTop();
                } else {
                    scrollTo(0, 0);
                }
            }
        });
    }

    /**
     * 打开bottomView
     */
    @Override
    public void openBottom(final boolean smooth) {
        postAfterLayout(new Runnable() {
            @Override
            public void run() {
                if (smooth) {
                    scrollToBottom();
                } else {
                    scrollTo(0, topViewHeight);
                }
            }
        });
    }

    /**
     * 设置滑动比例监听器
     *
     * @param listener
     */
    @Override
    public void setScrollRatioListener(ScrollRatioListener listener) {
        scrollRatioListener = listener;
    }
//////////////////////////////////////////触摸事件////////////////////////////////////////////////////////////////////
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        velocityTracker.addMovement(ev);
        switch (ev.getAction()) {
            case ACTION_DOWN:
                scroller.abortAnimation();
                needReset = false;
                break;
            case ACTION_MOVE:
                break;
            case ACTION_UP: {
                scroller.abortAnimation();
                velocityTracker.computeCurrentVelocity(1000);
                //手指向上fling && 触摸点在topRv里 && topRv已经滑到底部不能再上拉
                if (isFlingUp(velocityTracker.getYVelocity()) && inTouchRange(ev, topRv) && !canPullUp(topRv)) {
                    scrollToBottom();
                } else if (getScrollY() == 0 && ev.getY() > topViewHeight) {//点击留白处,跳到bottomView(topView处于完全显示状态,但手指抬起点在topView之外)
                    scrollToBottom();
                    return true;
                } else if (curLayer == LAYER_TOP && getScrollY() > topViewHeight - triggerDistance) {//当前显层是topView,上拉到一定距离,跳到bottomView
                    scrollToBottom();
                } else if (getScrollY() < triggerDistance) {//当前scrollY不足triggerDistance,跳到top,否则跳到bottom
                    scrollToTop();
                } else {
                    scrollToBottom();
                }
            }
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    //////////////////////////////////////////滑动逻辑//////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (!scroller.isFinished()) {
            return;
        }
        if (target == bottomRv) {
            boolean hideTop = dy > 0 && topViewIsInScreen();//topView还在屏幕内显示而且手指向上滑
            boolean showTop = dy < 0 && !canDropDown(target);//手指向下滑，bottomRv不能再下拉了(已经滑到顶部了)
            if (hideTop || showTop) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        } else {
            boolean hideBottom = dy < 0 && contentViewIsInScreen();//手指向下滑，而且contentView还在屏幕内显示
            boolean showBottom = dy > 0 && !canPullUp(target);//手指向上滑，而且topView已经不能再上拉了(已滑到底部)
            if (hideBottom || showBottom) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        }
    }

    // FIXME: 想不到一个更好的命名
    private boolean needReset = false;//需要复位

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed < 0 && type == ViewCompat.TYPE_NON_TOUCH) {//手指在bottomRv下滑，bottomRv未消费完的距离，留给父view
            scrollBy(0, dyUnconsumed);
            //后续需要判断scroller是否已经完成滚动了
            //完成滚动后，根据topRv显示在屏幕的比例去决定最终停留的页面
            needReset = true;
        }
    }


    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        if (target == bottomRv && topViewIsInScreen()) {//触摸的目标view是bottomRv && topRv有显示在屏幕上，DragLayout接管fling事件
            return true;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    ///////////////////////////////////////测量逻辑///////////////////////////////////////////////////////////////
    int topChildIndex = 0;//topRv对应的下标
    int refreshChildIndex = 1;//刷新蒙版对应的下标
    int bottomChildIndex = 2;//bottomRv对应的下标

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(getChildAt(bottomChildIndex), widthMeasureSpec, heightMeasureSpec);//bottomRv填满draglayout在屏幕内的显示空间
        //新建除高度要少topLeaveSpace之外，其他一样的measureSpec
        int measureSpecLess = MeasureSpec.makeMeasureSpec((int) (getChildAt(bottomChildIndex).getMeasuredHeight() - topLeaveSpace), MeasureSpec.EXACTLY);
        measureChild(getChildAt(refreshChildIndex), widthMeasureSpec, measureSpecLess);
        measureChild(getChildAt(topChildIndex), widthMeasureSpec, measureSpecLess);
        //计算DragLayout的总高度
        int totalHeight = getChildAt(topChildIndex).getMeasuredHeight() + getChildAt(bottomChildIndex).getMeasuredHeight();
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        int realHeight = Math.max(totalHeight, specHeight);
        //设置DragLayout的最终大小
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), realHeight);
        //记录topView的高度
        topViewHeight = getChildAt(topChildIndex).getMeasuredHeight();
        //记录垂直方向最大的滑动偏移
        maxScrollY = realHeight - specHeight;
        //记录触发跳转到topRv滑动偏移阈值
        triggerDistance = (int) (getChildAt(topChildIndex).getMeasuredHeight() * triggerRatio);
    }
////////////////////////////////////////布局逻辑///////////////////////////////////////////////////////////////
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //topRv和刷新蒙版放在同样的位置，而且蒙版盖在topRv的上面，而bottomRv放在topRv的下方
        int usedHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (refreshChildIndex == i) {
                child.layout(l, t, l + child.getMeasuredWidth(), t + child.getMeasuredHeight());
            } else {
                child.layout(l, t + usedHeight, l + child.getMeasuredWidth(), t + usedHeight + child.getMeasuredHeight());
                usedHeight += child.getMeasuredHeight();
            }
        }
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //这里实现的逻辑是 1.计算出滑动比例，传递给蒙版进行刷新 2.根据滑动比例，缩放topRv 3.通知监听滑动比例的回调
        super.onScrollChanged(l, t, oldl, oldt);
        float ratio = ((float) t) / maxScrollY;//注意这个radio值是随着用户下拉越来越小的(从1到0的变化过程)
        refreshView.setProgress(ratio);

        topRv.setPivotX(topRv.getMeasuredWidth() / 2f);
        topRv.setPivotY(0);
        float minScale = 0.5f;
        float scale = minScale + (1f - ratio) * (1f - minScale);
        topRv.setScaleX(scale);
        topRv.setScaleY(scale);
        topRv.setTranslationY(t);
        if (scrollRatioListener != null) {
            scrollRatioListener.onScroll(ratio);
        }
    }

    /**
     * 触摸点是否在指定view的范围内
     * @param event
     * @param view
     * @return
     */
    private boolean inTouchRange(MotionEvent event, View view) {
        return event.getX() >= view.getLeft() && event.getX() <= view.getRight() && event.getY() >= (view.getTop() - getScrollY()) && event.getY() <= (view.getBottom() - getScrollY());
    }

    /**
     * 根据速度大小判断手指是否向上fling
     * @param velocity
     * @return
     */
    private boolean isFlingUp(float velocity) {
        return velocity < 0 && Math.abs(velocity) > viewConfiguration.getScaledMinimumFlingVelocity() * dampingFactor;
    }
    /**
     * 根据速度大小判断手指是否向下fling
     * @param velocity
     * @return
     */
    private boolean isFlingDown(float velocity) {
        return velocity > 0 && Math.abs(velocity) > viewConfiguration.getScaledMinimumFlingVelocity() * dampingFactor;
    }

    //记录当前显示的层次
    int curLayer = 1;
    private static final int LAYER_TOP = 0;
    private static final int LAYER_BOTTOM = 1;

    private void scrollToTop() {
        curLayer = LAYER_TOP;
        if (getScrollY() == 0) {
            return;
        }
        scroller.startScroll(0, getScrollY(), 0, 0 - getScrollY());
        invalidate();
    }

    private void scrollToBottom() {
        curLayer = LAYER_BOTTOM;
        topRv.scrollToPosition(0);
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
        } else if (needReset) {
            needReset = false;
            if (getScrollY() < triggerDistance) {
                scrollToTop();
            } else {
                scrollToBottom();
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
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
     * @param view
     * @return
     */
    private boolean canDropDown(View view) {
        return ViewCompat.canScrollVertically(view, PULL_DOWN_DIRECTION);
    }
    /**
     * view是否能上拉
     * @param view
     * @return
     */
    private boolean canPullUp(View view) {
        return ViewCompat.canScrollVertically(view, PULL_UP_DIRECTION);
    }

    /**
     * 在布局完成之后执行的Runnable
     * @param runnable
     */
    private void postAfterLayout(Runnable runnable) {
        if (attached && !isLayoutRequested()) {
            runnable.run();
        } else {
            if (null == afterLayoutRunnableList) {
                afterLayoutRunnableList = new LinkedList<>();
            }
            afterLayoutRunnableList.addLast(runnable);
        }
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topRv = (RecyclerView) getChildAt(topChildIndex);
        refreshView = (RefreshView) getChildAt(refreshChildIndex);
        bottomRv = (RecyclerView) getChildAt(bottomChildIndex);
    }

    @Override
    protected void onDetachedFromWindow() {
        velocityTracker.recycle();
        attached = false;
        super.onDetachedFromWindow();
    }

    private void log(String tag, String msg) {
        Log.i(tag, msg);
    }
}

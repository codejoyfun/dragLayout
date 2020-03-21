package com.lxtx.mydraglayout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;

/**
 * 更好的做法，通过动态代理的方式去改写方法的实现
 *
 * @author 宁锟
 * @since 2020/3/21
 */
public class SpeedLayoutManager extends LinearLayoutManager {
    private double speedRatio;//改变竖直滑动的速度比例

    public SpeedLayoutManager(Context context) {
        super(context);
    }

    public SpeedLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SpeedLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        //屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
        int originValue = super.scrollVerticallyBy((int) (speedRatio * dy), recycler, state);
        if (originValue == (int) (speedRatio * dy)) {
            return dy;
        }
        return originValue;
    }

    public SpeedLayoutManager setSpeedRatio(double speedRatio) {
        this.speedRatio = speedRatio;
        return this;
    }
}

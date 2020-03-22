package com.lxtx.mydraglayout.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 宁锟
 * @since 2020/3/21
 */
public class FlingRecyclerView extends RecyclerView {
    private double scale = 10;//抛掷速度的缩放因子

    public FlingRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FlingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setflingScale(double scale) {
        this.scale = scale;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if(velocityY < 0) velocityY *= scale;//手指向下fling，加速
        return super.fling(velocityX, velocityY);
    }
}

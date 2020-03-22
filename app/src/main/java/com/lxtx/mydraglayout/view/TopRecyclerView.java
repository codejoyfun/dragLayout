package com.lxtx.mydraglayout.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 宁锟
 * @since 2020/3/22
 */
public class TopRecyclerView extends RecyclerView {
    public TopRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TopRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float progress = 1f;
    Camera camera = new Camera();
    float maxDistance = 10f;
    boolean oneTime = false;
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
//            camera.translate(0,0,maxDistance);
//            camera.applyToCanvas(c);

    }
}

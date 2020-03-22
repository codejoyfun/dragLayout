package com.lxtx.mydraglayout.progress;

import static com.lxtx.mydraglayout.view.RefreshView.COMMON_RADIUS;
import static com.lxtx.mydraglayout.view.RefreshView.MAX_SMALL_POINT_OFFSET;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_GONE;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_VISIBLE;

/**
 * @author 宁锟
 * @since 2020/3/22
 */
public class SmallPointStrategy implements PointProgressStrategy {
    private int offsetX = 0;//左边圆点的横坐标偏移
    private int radius = 0;

    @Override
    public void onProgressChange(float progress) {
        //大于0.95 消失不见
        if (progress > THRESHOLD_POINT_VISIBLE) {
            offsetX = 0;
            radius = 0;
        } else if (progress >= THRESHOLD_POINT_GONE) {
            //从0.95到0.6这个过程，两边圆点逐渐展开
            offsetX = (int) (MAX_SMALL_POINT_OFFSET * (1f - (progress - THRESHOLD_POINT_GONE) / (THRESHOLD_POINT_VISIBLE - THRESHOLD_POINT_GONE)));
            radius = COMMON_RADIUS;
        }
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
    public int getRadius() {
        return radius;
    }
}

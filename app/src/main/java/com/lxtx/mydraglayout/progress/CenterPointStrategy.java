package com.lxtx.mydraglayout.progress;

import static com.lxtx.mydraglayout.view.RefreshView.COMMON_RADIUS;
import static com.lxtx.mydraglayout.view.RefreshView.MAX_RADIUS;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_BECOME_SMALLER;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_VISIBLE;

/**
 * @author codejoyfun
 * @since 2020/3/22
 */
public class CenterPointStrategy implements PointProgressStrategy {
    private int radius = 0;
    @Override
    public void onProgressChange(float progress) {
        // 进度 1到0.95 逐渐变大
        if (progress >= THRESHOLD_POINT_VISIBLE) {
            radius = (int) (MAX_RADIUS * (1f - (progress - THRESHOLD_POINT_VISIBLE) / (1f - THRESHOLD_POINT_VISIBLE)));
        }else if (progress >= THRESHOLD_POINT_BECOME_SMALLER) {//从0.95开始变小，直到0.6变无
            radius = COMMON_RADIUS + (int) ((MAX_RADIUS - COMMON_RADIUS) * (progress - THRESHOLD_POINT_BECOME_SMALLER) / (THRESHOLD_POINT_VISIBLE - THRESHOLD_POINT_BECOME_SMALLER));
        }
    }

    @Override
    public int getOffsetX() {//不需要
        return 0;
    }

    @Override
    public int getRadius() {
        return radius;
    }
}

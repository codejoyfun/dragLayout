package com.lxtx.draglayout.progress;

import static com.lxtx.draglayout.view.RefreshView.THRESHOLD_POINT_BECOME_SMALLER;
import static com.lxtx.draglayout.view.RefreshView.THRESHOLD_POINT_FADE;

/**
 * @author codejoyfun
 * @since 2020/4/6
 */
public class PointAlphaStrategy implements AlphaStrategy {
    private float alpha = 0f;

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void onProgressChange(float progress) {
        if (progress > THRESHOLD_POINT_BECOME_SMALLER) {
            alpha = 1f;
        } else if (progress >= THRESHOLD_POINT_FADE) {//当达到最大偏移的时候，就要开始小圆点的淡出
            alpha = (progress - THRESHOLD_POINT_FADE) / (THRESHOLD_POINT_BECOME_SMALLER - THRESHOLD_POINT_FADE);
        } else {
            alpha = 0f;
        }
    }
}

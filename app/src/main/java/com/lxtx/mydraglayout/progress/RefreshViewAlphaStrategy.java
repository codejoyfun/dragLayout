package com.lxtx.mydraglayout.progress;

import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_ALPHA;

/**
 * @author codejoyfun
 * @since 2020/3/22
 */
public class RefreshViewAlphaStrategy implements AlphaStrategy {
    private float alpha = 0f;

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void onProgressChange(float progress) {
        if (progress > THRESHOLD_ALPHA) {
            alpha = 1;
        } else {
            alpha = progress / THRESHOLD_ALPHA;
        }
    }
}

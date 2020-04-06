package com.lxtx.mydraglayout.progress;

import static com.lxtx.mydraglayout.view.RefreshView.COMMON_RADIUS;
import static com.lxtx.mydraglayout.view.RefreshView.MAX_SMALL_POINT_OFFSET;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_BECOME_SMALLER;
import static com.lxtx.mydraglayout.view.RefreshView.THRESHOLD_POINT_VISIBLE;

/**
 * @author codejoyfun
 * @since 2020/3/22
 */
public class SmallPointStrategy implements PointProgressStrategy{
    private int offsetX = 0;//左边圆点的横坐标偏移
    private int radius = 0;

    @Override
    public void onProgressChange(float progress) {
        //大于0.95 消失不见
        if (progress > THRESHOLD_POINT_VISIBLE) {
            offsetX = 0;
            radius = 0;
        } else{
            //从0.95到0.6这个过程，两边圆点逐渐展开
            offsetX = (int) (MAX_SMALL_POINT_OFFSET * (1f - (progress - THRESHOLD_POINT_BECOME_SMALLER) / (THRESHOLD_POINT_VISIBLE - THRESHOLD_POINT_BECOME_SMALLER)));
            //做边界判断
            if(offsetX <= 0){
                offsetX = 0;
            }
            if(offsetX > MAX_SMALL_POINT_OFFSET){
                offsetX = MAX_SMALL_POINT_OFFSET;
            }
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

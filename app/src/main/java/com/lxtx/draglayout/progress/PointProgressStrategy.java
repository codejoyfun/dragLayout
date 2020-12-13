package com.lxtx.draglayout.progress;

/**
 * @author codejoyfun
 * @since 2020/3/22
 */
public interface PointProgressStrategy extends ProgressStrategy {
    int getOffsetX();//获取当前横坐标偏移
    int getRadius();//获取当前半径
}

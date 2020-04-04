package com.lxtx.mydraglayout;

/**
 * @author codejoyfun
 * @since 2020/3/20
 * 提供给用户的通用操作
 */
public interface UserAction {
    void openTop(boolean smooth);
    void openBottom(boolean smooth);
    void setScrollRatioListener(ScrollRatioListener listener);
}

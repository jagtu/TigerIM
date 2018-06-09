package cn.ittiger.im.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/04
 *      desc    :
 * </pre>
 */

public class PreViewPager extends ViewPager {
    public PreViewPager(Context context) {
        super(context);
    }

    public PreViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}

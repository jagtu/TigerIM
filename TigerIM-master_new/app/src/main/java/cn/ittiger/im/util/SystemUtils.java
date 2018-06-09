package cn.ittiger.im.util;

import android.content.Context;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/23
 *      desc    :
 * </pre>
 */

public class SystemUtils {

    public static int dipTopx(Context context,float dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp*density + 0.5);
    }
}

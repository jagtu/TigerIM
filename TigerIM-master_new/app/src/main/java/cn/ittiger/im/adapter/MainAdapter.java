package cn.ittiger.im.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.ittiger.im.fragment.ContactFragment;
import cn.ittiger.im.fragment.MessageFragment;
import cn.ittiger.im.fragment.MyFragment;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/04
 *      desc    :
 * </pre>
 */

public class MainAdapter extends FragmentPagerAdapter {

    private static final int MAX_SIZE = 3;

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MessageFragment();
            case 1:
                return new ContactFragment();
            case 2:
                return MyFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return MAX_SIZE;
    }
}

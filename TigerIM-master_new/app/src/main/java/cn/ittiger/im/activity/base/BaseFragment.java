package cn.ittiger.im.activity.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.ittiger.im.R;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2018/4/17 10:06.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
public class BaseFragment extends Fragment {

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, cn.ittiger.R.anim.activity_still);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(cn.ittiger.R.anim.activity_in, cn.ittiger.R.anim.activity_still);
    }
}

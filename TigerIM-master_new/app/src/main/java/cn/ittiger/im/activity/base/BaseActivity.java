package cn.ittiger.im.activity.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import cn.ittiger.im.R;

/**
 * Created by Administrator on 2017/6/21.
 */

public class BaseActivity extends AppCompatActivity{

    protected AppCompatActivity mActivity;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("加载中...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("BaseActivity", mActivity.getClass().getSimpleName());
    }

    protected void show(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开一个带动画的fragment，并将其添加到回退栈
     *
     * @param fragment
     * @param layoutId
     */
//    protected void addFragmentAndAnim(Fragment fragment, int layoutId) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.right_enter_anim, R.anim.right_exit_anim, R.anim.right_enter_anim, R.anim.right_exit_anim);
//        transaction.add(layoutId, fragment);
//        transaction.addToBackStack("task");
//        transaction.commit();
//    }

//    /**
//     * 获取String类型的返回结果
//     *
//     * @param data
//     * @param callbak
//     */
//    public void postString(String data, String url, final HttpUtils.OnOkHttpCallback callbak) {
//
//        HttpUtils.getInstance().POST(mActivity, data, url, new HttpUtils.OnOkHttpCallback() {
//            @Override
//            public void onSuccess(String body) {
//                if (callbak != null) {
//                    callbak.onSuccess(body);
//                }
//            }
//
//            @Override
//            public void onError(Request error, Exception e) {
//                if (callbak != null) {
//                    callbak.onError(error, e);
//                }
//            }
//        });
//
//    }

    /**
     * 根据泛型获取返回结果
     *
     * @param data
     * @param callName
     * @param httpResult
     * @param <T>
     */
//    public <T> void getHttpContent(String data, String callName, final HttpResult<T> httpResult) {
//
//    }

    /**
     * 初始化刷新控件
     *
     * @param trl
     */
    protected void initProgress(TwinklingRefreshLayout trl) {
        ProgressLayout progressLayout = new ProgressLayout(mActivity);
        progressLayout.setColorSchemeResources(R.color.colorPrimary);
        trl.setHeaderView(progressLayout);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_still, cn.ittiger.R.anim.activity_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, cn.ittiger.R.anim.activity_still);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(cn.ittiger.R.anim.activity_in, cn.ittiger.R.anim.activity_still);
    }
}

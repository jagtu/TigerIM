package cn.ittiger.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.adapter.MainAdapter;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.IntentHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主页面
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class MainActivity extends IMBaseActivity {

    @BindView(R.id.first_iv)
     ImageView mFirstIv;
    @BindView(R.id.second_iv)
     ImageView mSecondIv;
    @BindView(R.id.third_iv)
     ImageView mThirdIv;
    @BindView(R.id.first_rb)
     TextView mFirstRb;
    @BindView(R.id.second_rb)
     TextView mSecondRb;
    @BindView(R.id.third_rb)
     TextView mThirdRb;
    @BindView(R.id.main_vp)
     ViewPager mPagerVp;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.add_iv)
    ImageView mAddIv;
    private PopupWindow mAddPopup;
    private MainAdapter mAdapter;

    private ProgressDialog mProgressDialog;


    public static void goActivity(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    public static void goActivity(Context context,String action){
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("key",action);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        //普通消息接收监听
        Intent intent = getIntent();
        if(intent!=null){
            String key = intent.getStringExtra("key");
            if("exit".equals(key)){
                LoginActivity.goActivity(this);
                this.finish();
            }
//            return;
        }
        SmackListenerManager.addGlobalListener();
        SmackMultiChatManager.bindJoinMultiChat();

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("");
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);//延时1s
                    mProgressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initView() {
        mPagerVp.setOffscreenPageLimit(3);
        mAdapter = new MainAdapter(getSupportFragmentManager());
        mPagerVp.setAdapter(mAdapter);
        mFirstIv.setSelected(true);
        mFirstRb.setSelected(true);
    }

    public void chooseView(View view){
        switch (view.getId()){
            case R.id.first_ll:
                mSecondIv.setSelected(false);
                mSecondRb.setSelected(false);
                mThirdIv.setSelected(false);
                mThirdRb.setSelected(false);
                mFirstIv.setSelected(true);
                mFirstRb.setSelected(true);
                mTitleTv.setText(R.string.text_message);
                mAddIv.setVisibility(View.VISIBLE);
                mPagerVp.setCurrentItem(0,false);
                break;
            case R.id.second_ll:
                mThirdIv.setSelected(false);
                mThirdRb.setSelected(false);
                mFirstIv.setSelected(false);
                mFirstRb.setSelected(false);
                mSecondIv.setSelected(true);
                mSecondRb.setSelected(true);
                mTitleTv.setText(R.string.text_contact);
               // mAddIv.setVisibility(View.GONE);
                mPagerVp.setCurrentItem(1,false);

                //by jagtu 点击通讯录时刷新群组
                try {
//                    SmackManager.getInstance().queryRoom();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("====", "====失败" + e.toString());
                }

                break;
            case R.id.third_ll:
                mFirstIv.setSelected(false);
                mFirstRb.setSelected(false);
                mSecondIv.setSelected(false);
                mSecondRb.setSelected(false);
                mThirdIv.setSelected(true);
                mThirdRb.setSelected(true);
                mTitleTv.setText(R.string.text_my_title);
                mAddIv.setVisibility(View.GONE);
                mPagerVp.setCurrentItem(2,false);
                break;
        }
    }

    public void addClick(View view) {
        if(mPagerVp.getCurrentItem() == 0) {
            //群聊
            if(mAddPopup == null){
              View contentView =   LayoutInflater.from(this).inflate(R.layout.popupwindow_main_add,null);
                mAddPopup = new PopupWindow(contentView, ViewPager.LayoutParams.WRAP_CONTENT,ViewPager.LayoutParams.WRAP_CONTENT);
                mAddPopup.setOutsideTouchable(false);
                mAddPopup.setFocusable(true);
                mAddPopup.setBackgroundDrawable(new ColorDrawable());
                contentView.findViewById(R.id.multichat_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MultiListActivity.goActivity(MainActivity.this);
                        mAddPopup.dismiss();
                    }
                });
            }
            if(!mAddPopup.isShowing()){
                mAddPopup.showAsDropDown(mAddIv,0,0, Gravity.TOP);
            }else {
                mAddPopup.dismiss();
            }
        }else if(mPagerVp.getCurrentItem() == 1){
            AddFriendActivity.goActivity(this);
        }
    }

    @Override
    protected void onDestroy() {

//        SmackManager.getInstane().logout();
//        SmackManager.getInstance().disconnect();
//        SmackListenerManager.getInstance().destroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //登出操作
    }

    @Override
    protected void onStart() {

        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public boolean doubleExitAppEnable() {

        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTabFragmentEvent(Integer index) {

        if(index == IntentHelper.CONTACT_TAB_INDEX || index == IntentHelper.MESSAGE_TAB_INDEX) {
            //收到消息了。。。。
        }
    }
}

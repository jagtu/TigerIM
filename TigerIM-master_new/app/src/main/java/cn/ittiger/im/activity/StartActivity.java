package cn.ittiger.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import org.jivesoftware.smack.SmackConfiguration;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.Api;
import cn.ittiger.im.Constant;
import cn.ittiger.im.R;
import cn.ittiger.im.RetrofitManager;
import cn.ittiger.im.WrapData;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.LoginResult;
import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.bean.UserInfo;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.util.ActivityUtil;
import cn.ittiger.util.UIUtil;
import cn.ittiger.util.ValueUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // 默认情况下软件盘不自动弹出
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (System.currentTimeMillis() > 1540374400000L) {
            finish();
            return;
        }
        init();
    }

    private void init() {
        final  User user = LoginHelper.getUser();
        if(!user.isValidate()){
            Log.i("===","====走了这里");
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.goActivity(StartActivity.this);
                    StartActivity.this.finish();
                }
            },1000*2);
        }else{
            final  SmackManager smackManager = SmackManager.getInstance();
            if(smackManager.getConnection() == null){
                smackManager.connect();
            }
//            Observable.just(smackManager)
//                    .delay(1500, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.io())//指定下面的flatMap线程
//                    .flatMap(new Func1<SmackManager, Observable<LoginResult>>() {
//                        @Override
//                        public Observable<LoginResult> call(SmackManager smackManager) {
//                            return Observable.just(SmackManager.getInstance().login(user.getUsername(),user.getPassword()));
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
//                    .subscribe(new Action1<LoginResult>() {
//                        @Override
//                        public void call(LoginResult loginResult) {
//                            if (loginResult.isSuccess()) {
//                                mainStart();
//                            } else {
//                                loginStart();
//                            }
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            Toast.makeText(StartActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                        }
//                    });

            final MemberBean mMemberBean = LoginHelper.getMemberBean();
            App.getInstance().mMemberBean = mMemberBean;
            Api api;
            String usernameWithoutPrefix = user.getUsername();
            String key = Constant.KEY;
            if (mMemberBean != null) {
                api = RetrofitManager.getInstance(mMemberBean.getUrl() + "/customer/service/").create(Api.class);
                if (usernameWithoutPrefix.startsWith(mMemberBean.getPrefix())) {
                    usernameWithoutPrefix = usernameWithoutPrefix.substring(mMemberBean.getPrefix().length());
                }
                key = mMemberBean.getPkey();
            } else {

                Toast.makeText(StartActivity.this, "登录已过期", Toast.LENGTH_SHORT).show();
                loginStart();
                return;
            }

            api.getConsumeList(key, usernameWithoutPrefix, user.getPassword())
                    .flatMap(new Func1<WrapData<UserInfo>, Observable<LoginResult>>() {
                        @Override
                        public Observable<LoginResult> call(final WrapData<UserInfo> wrapData) {
                            if (wrapData != null) {
                                App.getInstance().mUserInfo = wrapData.getResult();
                            }
                            if (wrapData != null && !wrapData.isSuccessed()) {
                                return Observable.just(new LoginResult(false, wrapData.getMsg()));
                            } else {
                                LoginResult loginResult = SmackManager.getInstance().login(user.getUsername(), "123456");
                                return Observable.just(loginResult);
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
                    .subscribe(new Action1<LoginResult>() {
                        @Override
                        public void call(LoginResult loginResult) {
                            if (loginResult.isSuccess()) {
                                mainStart();
                            } else {
                                loginStart();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            loginStart();
                            Toast.makeText(StartActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void mainStart() {
        MainActivity.goActivity(StartActivity.this);
        StartActivity.this.finish();
    }

    private void loginStart() {
        LoginActivity.goActivity(StartActivity.this);
        StartActivity.this.finish();
    }
}

package cn.ittiger.im.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ittiger.im.Api;
import cn.ittiger.im.Constant;
import cn.ittiger.im.R;
import cn.ittiger.im.RetrofitManager;
import cn.ittiger.im.WrapData;
import cn.ittiger.im.activity.base.BaseActivity;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.LoginResult;
import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.bean.UserInfo;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.ListViewDialog;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.StringUtils;
import cn.ittiger.util.ActivityUtil;
import cn.ittiger.util.PreferenceHelper;
import cn.ittiger.util.UIUtil;
import cn.ittiger.util.ValueUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 登陆openfire服务器
 *
 * @auther: hyl
 * @time: 2015-10-23下午1:36:59
 */
public class LoginActivity extends BaseActivity {
    /**
     * 登陆用户
     */
    @BindView(R.id.et_login_username)
    EditText mEditTextUser;
    /**
     * 登陆密码
     */
    @BindView(R.id.et_login_password)
    EditText mEditTextPwd;
    /**
     * 登陆按钮
     */
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @BindView(R.id.tv_change_member)
    TextView mTvChangeMember;

    /**
     * 记住密码
     */
    @BindView(R.id.cb_remember_password)
    AppCompatCheckBox mCbRememberPassword;
    private ProgressDialog dialog;
    private ListViewDialog mListViewDialog;
    private MemberBean mMemberBean;
//    private int choice;
    private Api api;


    public static void goActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        ButterKnife.bind(this);

        mMemberBean = LoginHelper.getMemberBean();
        App.getInstance().mMemberBean = mMemberBean;

        initViews();
        initUserInfo();
    }


    private void initViews() {
        SmackManager.getInstance();
        mListViewDialog = new ListViewDialog(mActivity);

        if (mMemberBean!=null && !StringUtils.isNullOrEmpty(mMemberBean.getName())) {
            mTvChangeMember.setText(mMemberBean.getName());
        }else {
            mTvChangeMember.setText("请选择会员系统");
        }

        dialog = new ProgressDialog(mActivity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("登录中...");

        mEditTextUser.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        mEditTextUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextUser.setCursorVisible(true);
            }
        });
        mEditTextUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mEditTextPwd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUserInfo() {

        boolean isRemember = LoginHelper.isRememberPassword();
        if (isRemember) {
            User user = LoginHelper.getUser();
            String username = user.getUsername();
            if (mMemberBean != null) {
                if (username.startsWith(mMemberBean.getPrefix())) {
                    username = username.substring(mMemberBean.getPrefix().length());
                }
            }
            mEditTextUser.setText(username);
            mEditTextPwd.setText(user.getPassword());
        }
        mCbRememberPassword.setChecked(isRemember);
    }

    /**
     * 登陆响应
     *
     * @param v
     */
    @OnClick(R.id.btn_login)
    public void onLoginClick(View v) {
        final String username = mEditTextUser.getText().toString();
        final String password = mEditTextPwd.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            UIUtil.showToast(this, getString(R.string.login_error_user));
            return;
        }
        if (ValueUtil.isEmpty(password)) {
            UIUtil.showToast(this, getString(R.string.login_error_password));
            return;
        }
//        mBtnLogin.setEnabled(false);

        final SmackManager smackManager = SmackManager.getInstance();
        if (smackManager.getConnection() == null) {
            smackManager.connect();
        }

        String key = Constant.KEY;
        if (mMemberBean != null) {
            api = RetrofitManager.getInstance(mMemberBean.getUrl() + "/customer/service/").create(Api.class);
            key = mMemberBean.getPkey();
        } else {
//            api = RetrofitManager.getInstance(Constant.BASE_API).create(Api.class);

            UIUtil.showToast(this, getString(R.string.login_error_system));
            return;
        }

        dialog.setMessage("登录中");
        dialog.show();

        api.getConsumeList(key, username, password)
                .flatMap(new Func1<WrapData<UserInfo>, Observable<LoginResult>>() {
                    @Override
                    public Observable<LoginResult> call(final WrapData<UserInfo> wrapData) {
                        if (wrapData != null) {
                            App.getInstance().mUserInfo = wrapData.getResult();
                        }
                        if (wrapData != null && !wrapData.isSuccessed()) {
                            return Observable.just(new LoginResult(false, wrapData.getMsg()));
                        } else {
                            String usersName;
                            if (mMemberBean == null) {
                                usersName = username;
                            } else {
                                usersName = mMemberBean.getPrefix() + username;
                            }
                            LoginResult loginResult = SmackManager.getInstance().login(usersName, "123456");
                            return Observable.just(loginResult);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
                .doOnNext(new Action1<LoginResult>() {
                    @Override
                    public void call(LoginResult loginResult) {
                        LoginHelper.rememberRassword(true);
                    }
                }).subscribe(new Action1<LoginResult>() {
            @Override
            public void call(LoginResult loginResult) {
                if (loginResult != null && loginResult.isSuccess()) {
                    loginResult.getUser().setPassword(password);
                    LoginHelper.saveUser(loginResult.getUser());
                    if (loginResult.isSuccess()) {
                        MainActivity.goActivity(LoginActivity.this);
                        LoginActivity.this.finish();
                    } else {
                        Toast.makeText(App.getContext(), "登录失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        mBtnLogin.setEnabled(true);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(App.getContext(), loginResult.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    mBtnLogin.setEnabled(true);
                    dialog.dismiss();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mBtnLogin.setEnabled(true);
                Log.i("======", "=======失败==" + throwable.toString());
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        dialog = null;
    }

    /**
     * 用户注册
     *
     * @param v
     */
    @OnClick(R.id.tv_login_register)
    public void onRegisterClick(View v) {
        ActivityUtil.startActivity(this, RegisterActivity.class);
    }

    @OnClick(R.id.tv_change_member)
    public void changeMember(View v) {
        dialog.setMessage("");
        api = RetrofitManager.getInstance(Constant.BASE_MEMBER).create(Api.class);
        api.getMembers().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        dialog.show();
                    }
                }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        }).subscribe(new Action1<List<MemberBean>>() {
            @Override
            public void call(final List<MemberBean> memberBeans) {
                if (memberBeans != null) {

                    String[] members = new String[memberBeans.size()];
                    for (int i = 0; i < memberBeans.size(); i++) {
                        members[i] = memberBeans.get(i).getName();
                    }

                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("会员系统")
                            .setItems(members, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMemberBean = memberBeans.get(which);
                                    App.getInstance().mMemberBean = mMemberBean;


                                    if (mMemberBean!=null && !StringUtils.isNullOrEmpty(mMemberBean.getName())) {
                                        mTvChangeMember.setText(mMemberBean.getName());
                                    }else {
                                        mTvChangeMember.setText("请选择会员系统");
                                    }

                                    PreferenceHelper.putString("member", mMemberBean.getPrefix());
                                    LoginHelper.saveMemberBean(mMemberBean);
                                    dialog.dismiss();
                                }
                            }).show();

//                    //创建对话框
//                    AlertDialog dialog = builder.create();
//
//                    //设置确定按钮
//                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mMemberBean = memberBeans.get(choice);
//                            PreferenceHelper.putString("member", mMemberBean.getPrefix());
//                            LoginHelper.saveMemberBean(mMemberBean);
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();//显示对话框

                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
    }
}

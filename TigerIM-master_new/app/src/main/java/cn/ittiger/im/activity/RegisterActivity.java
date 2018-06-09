package cn.ittiger.im.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.util.ActivityUtil;
import cn.ittiger.util.UIUtil;
import cn.ittiger.util.ValueUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 注册
 *
 * @auther: hyl
 * @time: 2015-10-28上午10:52:49
 */
public class RegisterActivity extends IMBaseActivity {
    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.acet_username)
    EditText acetUsername;
    @BindView(R.id.acet_nickname)
    EditText acetNickname;
    @BindView(R.id.acet_password)
    EditText acetPassword;
    @BindView(R.id.acet_repassword)
    EditText acetRepassword;
    @BindView(R.id.btn_register_cancel)
    Button btnRegisterCancel;
    @BindView(R.id.btn_register_ok)
    Button btnRegisterOk;
    private ProgressDialog dialog;
//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;
//    @BindView(R.id.toolbarTitle)
//    TextView mToolbarTitle;

//    //用户名
//    @BindView(R.id.til_username)
//    TextInputLayout mUserTextInput;
//    @BindView(R.id.acet_username)
//    AppCompatEditText mUserEditText;
//
//    //昵称
//    @BindView(R.id.til_nickname)
//    TextInputLayout mNicknameTextInput;
//    @BindView(R.id.acet_nickname)
//    AppCompatEditText mNicknameEditText;
//
//    //密码
//    @BindView(R.id.til_password)
//    TextInputLayout mPasswordTextInput;
//    @BindView(R.id.acet_password)
//    AppCompatEditText mPasswordEditText;
//
//    //重复密码
//    @BindView(R.id.til_repassword)
//    TextInputLayout mRePasswordTextInput;
//    @BindView(R.id.acet_repassword)
//    AppCompatEditText mRePasswordEditText;
//
//    /**
//     * 注册
//     */
//    @BindView(R.id.btn_register_ok)
//    Button mBtnRegisterOk;
//    /**
//     * 注册取消
//     */
//    @BindView(R.id.btn_register_cancel)
//    Button mBtnRegisterCancel;
//    @BindView(R.id.im_title_back)
//    ImageView imTitleBack;
//    @BindView(R.id.tv_title_content)
//    TextView tvTitleContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);
        ButterKnife.bind(this);
        acetUsername.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        acetUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acetUsername.setCursorVisible(true);
            }
        });
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("注册中...");
        tvTitleContent.setText(getString(R.string.title_register));
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }//恶化你

        });
    }

    @OnClick(R.id.btn_register_ok)
    public void onRegisterOk(View v) {

        final String username = acetUsername.getText().toString();
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");//a-z,A-Z,0-9,_,3～16位
        if (!pattern.matcher(username).matches()) {
            Toast.makeText(mActivity,R.string.error_register_input_username_invalid,Toast.LENGTH_LONG).show();
//            mUserTextInput.setError(getString(R.string.error_register_input_username_invalid));
            return;
        }
        final String nickname = acetNickname.getText().toString();
        if (ValueUtil.isEmpty(nickname)) {
            Toast.makeText(mActivity,R.string.error_register_input_nickname,Toast.LENGTH_LONG).show();
//            mNicknameTextInput.setError(getString(R.string.error_register_input_nickname));
            return;
        }
        String password = acetPassword.getText().toString();
        pattern = Pattern.compile("^[a-zA-Z0-9]{6,18}$");//a-z,A-Z,0-9,_,3～16位
        if (!pattern.matcher(password).matches()) {
            Toast.makeText(mActivity,R.string.error_register_input_password_invalid,Toast.LENGTH_LONG).show();
//            mPasswordTextInput.setError(getString(R.string.error_register_input_password_invalid));
            acetPassword.setText("");
            return;
        }
        final String repassword = acetRepassword.getText().toString();
        if (!pattern.matcher(password).matches()) {
            Toast.makeText(mActivity,R.string.error_register_input_password_invalid,Toast.LENGTH_LONG).show();
//            mRePasswordTextInput.setError(getString(R.string.error_register_input_password_invalid));
            acetRepassword.setText("");
            return;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(mActivity,R.string.error_register_input_password_not_equal,Toast.LENGTH_LONG).show();
//            mRePasswordTextInput.setError(getString(R.string.error_register_input_password_not_equal));
            acetRepassword.setText("");
            return;
        }
        dialog.show();
        register(username, nickname, repassword);
    }

    public void register(final String username, String nickname, final String password) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("name", nickname);

        Observable.just(attributes)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Map<String, String>, Boolean>() {
                    @Override
                    public Boolean call(Map<String, String> attribute) {

                        return SmackManager.getInstance().registerUser(username, password, attribute);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        dialog.dismiss();
                        if (aBoolean) {
                            UIUtil.showToast(RegisterActivity.this, R.string.hint_register_success);
                            ActivityUtil.finishActivity(RegisterActivity.this);
                        } else {
                            UIUtil.showToast(RegisterActivity.this, R.string.hint_register_failure);
                        }
                    }
                });
    }

    @OnClick(R.id.btn_register_cancel)
    public void onRegisterCancel(View v) {

        ActivityUtil.finishActivity(this);
    }
}

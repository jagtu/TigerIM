package cn.ittiger.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.bean.ContactEntity;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.util.ActivityUtil;
import cn.ittiger.util.UIUtil;
import cn.ittiger.util.ValueUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 添加好友
 *
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class AddFriendActivity extends IMBaseActivity {

    @BindView(R.id.acet_friend_user)
    EditText acetFriendUser;
    @BindView(R.id.acet_friend_nickname)
    EditText acetFriendNickname;
    @BindView(R.id.btn_add_friend)

    Button mBtnAddFriend;
    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;

    public static void goActivity(Context context) {
        Intent intent = new Intent(context, AddFriendActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this);
        tvTitleContent.setText(getString(R.string.title_add_friend));
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AddFriendActivity.this.finish();
            }
        });
    }

    @OnClick(R.id.btn_add_friend)
    public void onAddFriendClick(View v) {

        final String username = acetFriendUser.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            Toast.makeText(mActivity,R.string.error_input_friend_username,Toast.LENGTH_LONG).show();
            return;
        }
        final String nickname = acetFriendNickname.getText().toString();
        if (ValueUtil.isEmpty(nickname)) {
            Toast.makeText(mActivity,R.string.error_input_friend_nickname,Toast.LENGTH_LONG).show();
            return;
        }
        Observable.create(new Observable.OnSubscribe<RosterEntry>() {
            @Override
            public void call(Subscriber<? super RosterEntry> subscriber) {

                boolean flag = SmackManager.getInstance().addFriend(username, nickname, null);
                if (flag) {
                    RosterEntry entry = SmackManager.getInstance().getFriend(username);
                    subscriber.onNext(entry);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new IllegalArgumentException());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RosterEntry>() {
                    @Override
                    public void onCompleted() {

                        UIUtil.showToast(mActivity, R.string.hint_add_friend_success);
                        ActivityUtil.finishActivity(mActivity);
                    }

                    @Override
                    public void onError(Throwable e) {

                        UIUtil.showToast(mActivity, R.string.hint_add_friend_failure);
                    }

                    @Override
                    public void onNext(RosterEntry rosterEntry) {

                        EventBus.getDefault().post(new ContactEntity(rosterEntry));
                    }
                });
    }
}

package cn.ittiger.im.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;
import cn.ittiger.im.adapter.ChatAdapter;
import cn.ittiger.im.adapter.MultiContactAdapter;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.DBQueryHelper;
import cn.ittiger.im.util.LoginHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 群聊列表
 */
public class MultiListActivity extends BaseActivity {

    @BindView(R.id.multi_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_title_content)
    TextView mTitle;
    @BindView(R.id.im_title_back)
    ImageView mBack;
    @BindView(R.id.add_iv)
    ImageView mAdd;
    private MultiContactAdapter mMultiContactAdapter;

    public static final int REQUEST_ADD_MSG = 1001;

    public static void goActivity(Context context) {
        Intent intent = new Intent(context, MultiListActivity.class);
        context.startActivity(intent);
    }

    public static void goActivity(Context context, RoomBean bean) {
        Intent intent = new Intent(context, MultiListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("remove", bean);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_list);

        ButterKnife.bind(this);
        mAdd.setVisibility(View.VISIBLE);
        mTitle.setText("群聊");//群聊列表

        mMultiContactAdapter = new MultiContactAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mMultiContactAdapter);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultiListActivity.this, CreateMultiChatActivity.class);
                MultiListActivity.this.startActivityForResult(intent, REQUEST_ADD_MSG);
            }
        });
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            RoomBean roomBean = intent.getParcelableExtra("remove");
            if (roomBean != null) {
                mMultiContactAdapter.removeData(roomBean);
                SmackMultiChatManager.deleteMultiChat(roomBean);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        Log.i("===","====onRestart");
        if (intent != null) {
            RoomBean roomBean = intent.getParcelableExtra("remove");
            if (roomBean != null) {
                mMultiContactAdapter.removeData(roomBean);
                SmackMultiChatManager.deleteMultiChat(roomBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomBean(RoomBean roomBean) {
        //向其他人发起聊天时接收到的事件
        if (mMultiContactAdapter == null) {
            return;
        }
        mMultiContactAdapter.addData(roomBean);
    }


    private void init() {
        //查询群数据库中查询
        Observable.create(new Observable.OnSubscribe<List<RoomBean>>() {
            @Override
            public void call(Subscriber<? super List<RoomBean>> subscriber) {

                List<RoomBean> roomBeans = DBQueryHelper.queryRoom();
                subscriber.onNext(roomBeans);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RoomBean>>() {
                    @Override
                    public void call(List<RoomBean> roomBeans) {
                        mMultiContactAdapter.addData(roomBeans);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_MSG:
                if (resultCode == RESULT_OK) {
                    //增加数据
                    mMultiContactAdapter.addData((RoomBean) data.getParcelableExtra("return"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package cn.ittiger.im.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.bean.UserInfo;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.adapter.CheckableContactAdapter;
import cn.ittiger.im.decoration.ContactItemDecoration;
import cn.ittiger.im.bean.CheckableContactEntity;
import cn.ittiger.im.provider.CreateProvider;
import cn.ittiger.im.provider.join.JoinRoom;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.DBQueryHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 创建群聊界面
 *
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class CreateMultiChatActivity extends IMBaseActivity {

    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;
    @BindView(R.id.acet_friend_user)
    EditText mRoomEt;
    @BindView(R.id.btn_add_friend)
    Button mCreateBtn;
    @BindView(R.id.ok_tv)
    TextView mOkTv;

    @BindView(R.id.tv_title_content)
    TextView mTitle;
    @BindView(R.id.im_title_back)
    ImageView mBack;
    @BindView(R.id.multi_crate)
    View mCreateLl;
    private ProgressDialog dialog;

    private CheckableContactAdapter mAdapter;
    private List<CheckableContactEntity> mCheckList;
    private CreateRoom mCreateRoom;
    private Map<String, String> roomUser;

    public static void goActivity(Context context) {
        Intent intent = new Intent(context, CreateMultiChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_multi_chat);
        ButterKnife.bind(this);
        init();
        mIndexStickyView.addItemDecoration(new ContactItemDecoration());
        getData();
    }

    private void init() {
        mCheckList = new ArrayList<>();
        mTitle.setText(R.string.text_create_multi_chat);
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("群聊创建中...");
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mRoomEt.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    return;
                }
                dialog.show();
                createMultiChat(name);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        mOkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始加入
                if (mCheckList == null || mCheckList.isEmpty()) {
                    Toast.makeText(CreateMultiChatActivity.this, "请选择群成员", Toast.LENGTH_SHORT).show();
                } else {
                    //开始加入
                    dialog.setMessage("群聊创建中...");
                    dialog.show();
                    joinRoom();
                }
            }
        });
    }

    public void getData() {

        Observable.create(new Observable.OnSubscribe<List<CheckableContactEntity>>() {
            @Override
            public void call(Subscriber<? super List<CheckableContactEntity>> subscriber) {

                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<CheckableContactEntity> list = new ArrayList<>();
                if(friends!=null){
                    for (RosterEntry friend : friends) {
                        if (friend.getName() != null) {
                            //排除客服
                            if (friend.getName().startsWith("#") || friend.getName().equals(LoginHelper.getUser().getUsername())) {
                                continue;
                            }
                            UserInfo userInfo = App.getInstance().mUserInfo;
                            if (userInfo != null && userInfo.getSuperior() != null) {
                                if (friend.getName().equals(userInfo.getSuperior().getUserName())) {
                                    continue;
                                }
                            }
                        }
                        list.add(new CheckableContactEntity(friend));
                    }
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())//指定上面的Subscriber线程
                .observeOn(AndroidSchedulers.mainThread())//指定下面的回调线程
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        Logger.e(throwable, "create multi chat query contact list failure");
                    }
                })
                .subscribe(new Action1<List<CheckableContactEntity>>() {
                    @Override
                    public void call(List<CheckableContactEntity> contacts) {
                        Log.i("====", "===走了这里");
                        if (mAdapter == null) {
                            mAdapter = new CheckableContactAdapter(mActivity, contacts);
                            mAdapter.setOnItemClickListener(mContactItemClickListener);
                            mIndexStickyView.setAdapter(mAdapter);
                        } else {
                            mAdapter.reset(contacts);
                        }
                    }
                });
    }

    OnItemClickListener<CheckableContactEntity> mContactItemClickListener = new OnItemClickListener<CheckableContactEntity>() {

        @Override
        public void onItemClick(View childView, int position, CheckableContactEntity item) {

            item.setChecked(!item.isChecked());
            mAdapter.notifyItemChanged(position);
            if (item.isChecked()) {
                mCheckList.add(item);
            } else {
                mCheckList.remove(item);
            }
        }
    };

    /**
     * 创建多人聊天
     */
    private void createMultiChat(final String roomName) {
        mCreateBtn.setEnabled(false);
        try {
            SmackManager.getInstance().createChatRoom(roomName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CreateMultiChatActivity.this, "创建失败,请稍后重试...", Toast.LENGTH_SHORT).show();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            mCreateBtn.setEnabled(true);
        }
    }

    /**
     * 加入群聊
     */
    public void joinRoom() {
        roomUser = new ArrayMap<>();
        String userName = LoginHelper.getUser().getUsername();
        roomUser.put(SmackManager.getInstance().getChatJid(userName), userName);
        for (CheckableContactEntity entity : mCheckList) {
            RosterEntry rosterEntry = entity.getRosterEntry();
            String jid = SmackManager.getInstance().getFullJid(rosterEntry.getUser());
            roomUser.put(jid, rosterEntry.getName());//邀请入群
        }
        try {
            SmackManager.getInstance().joinRoom(roomUser, mCreateRoom.getGroupid(), userName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加入失败,请稍后重试", Toast.LENGTH_SHORT).show();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    @Override
    public boolean isLceActivity() {

        return true;
    }

    //创建房间成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveRoom(CreateRoom room) {
        //创建房间成功
        mCreateRoom = room;
        if (mCreateRoom == null) {
            mCreateBtn.setEnabled(true);
            Toast.makeText(this, "群聊创建失败,请稍后重试...", Toast.LENGTH_SHORT);
        } else {

            //by jagtu 先把群主加入群中
            joinRoom();

            mTitle.setText("选择群成员");
            mBack.setVisibility(View.GONE);
            mCreateLl.setVisibility(View.GONE);
            mOkTv.setVisibility(View.VISIBLE);
            mIndexStickyView.setVisibility(View.VISIBLE);

//            mAdapter.notifyDataSetChanged();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //加入
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJoin(final RoomBean room) {
        //加入成功
        if(!room.getRoomJid().equals(mCreateRoom.getGroupid())){
            //不是当前房间
            return ;
        }
        if (roomUser == null || roomUser.size() == 0) {
            Toast.makeText(this, "加入失败,请稍后重试", Toast.LENGTH_SHORT).show();
        } else if(roomUser.size() == 1){

            //by jagtu:只有群主一人
            //什么都不做

        } else {
            String userName = LoginHelper.getUser().getUsername();
            RoomBean roomBean = new RoomBean(mCreateRoom.getGroupid(),mCreateRoom.getGroupname(),SmackManager.getInstance().getChatJid(userName));
            SmackMultiChatManager.saveMultiChat(roomBean);
            //返回并且赋值
            Intent intent = new Intent();
            intent.putExtra("return",roomBean);
            setResult(RESULT_OK,intent);
            this.finish();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SmackListenerManager.getInstance().addCreteRoomListener();
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

package cn.ittiger.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.adapter.CheckableContactAdapter;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.CheckableContactEntity;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.bean.UserInfo;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.ui.recyclerview.CommonRecyclerView;
import cn.ittiger.im.ui.recyclerview.HeaderAndFooterAdapter;
import cn.ittiger.im.util.DBQueryHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.util.PreferenceHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2018/4/16 10:23.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
public class RemoveMemberActivity extends IMBaseActivity {

    private IndexStickyView mView;
    private CheckableContactAdapter mAdapter;

    private RoomBean mRoomBean;

    ArrayList<CheckableContactEntity> mCheckList = new ArrayList<>();

    public static void goActivity(Activity context, Parcelable room) {
        Intent intent = new Intent(context, RemoveMemberActivity.class);
        intent.putExtra("members", room);
        context.startActivityForResult(intent, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_member);
        mView = (IndexStickyView) findViewById(R.id.indexStickyView);
        mRoomBean = getIntent().getParcelableExtra("members");
        TextView tvTitleContent = (TextView) findViewById(R.id.tv_title_content);
        tvTitleContent.setText("移除");

        findViewById(R.id.im_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getData();
    }

    public void getData() {

        Observable.create(new Observable.OnSubscribe<List<CheckableContactEntity>>() {
            @Override
            public void call(Subscriber<? super List<CheckableContactEntity>> subscriber) {

                List<RoomUser> roomUsers = DBQueryHelper.queryRoomUser(mRoomBean);
                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<CheckableContactEntity> list = new ArrayList<>();
                if (friends != null) {
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
                            if(roomUsers!=null){
                                boolean isContain = false;
                                for(RoomUser roomUser:roomUsers){
                                    String jid = roomUser.getJid();
                                    if(jid.contains("#")){
                                        jid = jid.substring(jid.indexOf("#")+1,jid.length());
                                    }
                                    Log.i("===","======com==="+friend.getName()+"==="+jid);
                                    if(friend.getName().equals(jid)){
                                        isContain = true;
                                        break;
                                    }
                                }
                                if(isContain){
                                    list.add(new CheckableContactEntity(friend));
                                }
                            }
                        }

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
                        if (mAdapter == null) {
                            mAdapter = new CheckableContactAdapter(mActivity, contacts);
                            mAdapter.setOnItemClickListener(new OnItemClickListener<CheckableContactEntity>() {
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
                            });
                            mView.setAdapter(mAdapter);
                        } else {
                            mAdapter.reset(contacts);
                        }
                    }
                });
    }

    public void click(View view) {
        remove();
    }

    //移除
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemove(final RoomBean room) {
        //加入成功
        Log.i("kkkk", "=======删除成功");
//        if(!room.getRoomJid().equals(mRoomBean.getRoomJid())){
//           //不是当前房间
//            return ;J
//        }
//        String userName = LoginHelper.getUser().getUsername();
//        RoomBean roomBean = new RoomBean(mRoomBean.getRoomJid()+"",mRoomBean.getName(),SmackManager.getInstance().getChatJid(userName));
//        SmackMultiChatManager.saveMultiChat(roomBean);
//        //返回并且赋值
//        Intent intent = new Intent();
//        intent.putExtra("return",roomBean);
//        setResult(RESULT_OK,intent);
        this.finish();
    }


    public void remove() {
        Map<String, String> roomUser = new ArrayMap<>();
        String userName = LoginHelper.getUser().getUsername();
//        roomUser.put(SmackManager.getInstance().getFullJid(userName), userName);
        if (mCheckList.isEmpty()) {
            return;
        }
        String prefix = PreferenceHelper.getString("member");

        for (CheckableContactEntity entity : mCheckList) {
            RosterEntry rosterEntry = entity.getRosterEntry();
            String name = prefix+rosterEntry.getName();
//            String jid = SmackManager.getInstance().getFullJid(name);
            roomUser.put(name, name);
        }
        Log.i("=====","==jid======删除=="+roomUser+"===="+mRoomBean.getRoomJid()+"==="+userName);
        try {
//            if(userName.contains("#")){
//                userName = userName.substring(userName.indexOf("#")+1,userName.length());
//            }
//            userName = prefix + userName;
            Log.i("=====","====delete"+userName);
            SmackManager.getInstance().exitRoom(roomUser, mRoomBean.getRoomJid(), userName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "失败,请稍后重试", Toast.LENGTH_SHORT).show();
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

    static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        AdapterView.OnItemClickListener listener;
        ArrayList<UserEntity> members;

        public Adapter(ArrayList<UserEntity> members) {
            this.members = members;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(20);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(params);
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final TextView view = (TextView) holder.itemView;
            UserEntity roomUser = members.get(position);
            String name = roomUser.user.getJid();
            if(!TextUtils.isEmpty(name) && name.contains("#")){
               name = name.substring(name.indexOf("#")+1);
            }
            view.setText(name);
            if (roomUser.check) {
                view.setBackgroundColor(Color.BLUE);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }

            if (listener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(null, view, holder.getAdapterPosition(), 0);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
            this.listener = listener;
        }
    }

    static class UserEntity {
        boolean check = false;
        RoomUser user;

        public UserEntity(RoomUser user) {
            this.user = user;
        }

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public RoomUser getUser() {
            return user;
        }

        public void setUser(RoomUser user) {
            this.user = user;
        }
    }
}

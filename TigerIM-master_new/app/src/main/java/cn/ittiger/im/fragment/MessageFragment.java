package cn.ittiger.im.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseFragment;
import cn.ittiger.im.Api;
import cn.ittiger.im.Constant;
import cn.ittiger.im.R;
import cn.ittiger.im.RetrofitManager;
import cn.ittiger.im.activity.MultiChatActivity;
import cn.ittiger.im.activity.SystemMassageActivity;
import cn.ittiger.im.adapter.ChatRecordAdapter;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.bean.ChatRecord;
import cn.ittiger.im.bean.PageBean;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.decoration.CommonItemDecoration;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.recyclerview.CommonRecyclerView;
import cn.ittiger.im.ui.recyclerview.HeaderAndFooterAdapter;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.DBQueryHelper;
import cn.ittiger.im.util.IMUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static cn.ittiger.im.constant.MessageType.MESSAGE_TYPE_IMAGE;
import static cn.ittiger.im.constant.MessageType.MESSAGE_TYPE_TEXT;
import static cn.ittiger.im.constant.MessageType.MESSAGE_TYPE_VOICE;

/**
 * 聊天消息列表
 *
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class MessageFragment extends BaseFragment implements CommonRecyclerView.OnItemClickListener {
    @BindView(R.id.recycler_message_record)
    CommonRecyclerView mRecyclerView;
    @BindView(R.id.chat_friend_avatar)
    ImageView chatFriendAvatar;
    @BindView(R.id.chat_message_count)
    TextView chatMessageCount;
    @BindView(R.id.chat_system)
    TextView chatSystem;
    @BindView(R.id.chat_system_message)
    TextView chatSystemMessage;
    @BindView(R.id.chat_system_time)
    TextView chatSystemTime;
    @BindView(R.id.ll_system)
    LinearLayout llSystem;

    private Api api;
    private ArrayList<PageBean> mPageBeans;
    private LinearLayoutManager mLayoutManager;
    private ChatRecordAdapter mAdapter;
    private HashMap<String, Integer> mMap = new HashMap<>();//聊天用户的用户名与用户聊天记录Position的映射关系

    private SharedPreferences sp;

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState) {

        sp = getActivity().getSharedPreferences("MessageSp", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CommonItemDecoration());
//        mRecyclerView.setOnItemClickListener(this);
        llSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageCount.setVisibility(View.GONE);

                if (mPageBeans != null && mPageBeans.size() > 0) {
                    SystemMassageActivity.goActivity(MessageFragment.this.getContext(), mPageBeans);
                    sp.edit().putLong("clicktime", System.currentTimeMillis()).apply();
                    Observable.just(mPageBeans)
                            .flatMap(new Func1<ArrayList<PageBean>, Observable<PageBean>>() {
                                @Override
                                public Observable<PageBean> call(ArrayList<PageBean> pageBeans) {
                                    return Observable.from(pageBeans);
                                }
                            })
                            .flatMap(new Func1<PageBean, Observable<PageBean>>() {
                                @Override
                                public Observable<PageBean> call(PageBean o) {
                                    try {
                                        DBHelper.getInstance().getSQLiteDB().save(o);
                                    } catch (Exception e) {

                                    }
                                    return Observable.just(o);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                }
            }
        });
        return view;
    }

    @Override
    public void refreshData() {

        Observable.create(new Observable.OnSubscribe<List<ChatRecord>>() {
            @Override
            public void call(Subscriber<? super List<ChatRecord>> subscriber) {

//                List<ChatRecord> list = DBHelper.getInstance().getSQLiteDB().queryAll(ChatRecord.class);
//                Log.e("query", "call: "+list.size());
                List<ChatRecord> list = DBQueryHelper.queryChatRecord();
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        refreshFailed();
                        Logger.e(throwable, "get chat record failure");
                    }
                })
                .subscribe(new Action1<List<ChatRecord>>() {
                    @Override
                    public void call(List<ChatRecord> chatRecords) {
                        List<ChatRecord> mChatRecords = new ArrayList<>();
                        for (ChatRecord chatRecord : chatRecords) {
                            if (mChatRecords.size() == 0) {
                                mChatRecords.add(chatRecord);
                            } else {
                                for (int i = 0; i < mChatRecords.size(); i++) {
                                    ChatRecord record = mChatRecords.get(i);
                                    if (chatRecord.getUnReadMessageCount() >= record.getUnReadMessageCount()) {
                                        mChatRecords.add(i, chatRecord);
                                        break;
                                    }
                                }
                            }
                        }
                        mAdapter = new ChatRecordAdapter(mContext, mChatRecords);
                        mAdapter.setOnItemClick(new ChatRecordAdapter.OnItemClick() {
                            @Override
                            public void onClick(ChatRecord chatRecord) {
                                //点击处理，更新
                                if (isRemoving() || mAdapter == null) {
                                    return;
                                }
                                chatRecord.initMessageCount();
                                DBHelper.getInstance().getSQLiteDB().update(chatRecord);//更新数据库中的记录
                                if (chatRecord.isMulti()) {
                                    MultiChatActivity.goActivity(mContext, new RoomBean(chatRecord.getChatJid(), chatRecord.getFriendNickname(), chatRecord.getOwn()));
                                } else {
                                    IMUtil.startChatActivity(mContext, chatRecord);
                                }
                            }

                            @Override
                            public void onRemove(int position) {
                                if(mAdapter!=null){
                                    String delUserName = mAdapter.update(position);
                                    if (delUserName!=null){
                                        mMap.remove(delUserName);
                                    }
                                }
                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                        refreshSuccess();
                        Log.i("===", "=======refreshSuccess");
                    }
                });
        if (api == null) {
            api = RetrofitManager.getInstance(Constant.BASE_PAGE).create(Api.class);
        }

        api.getPage(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
                .subscribe(new Action1<ArrayList<PageBean>>() {
                    @Override
                    public void call(ArrayList<PageBean> pageBeans) {
                        if (pageBeans != null && pageBeans.size() > 0) {
                            long lastclicktime = sp.getLong("clicktime", 0);
                            int unreadcount = pageBeans.size();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault());
                            List<PageBean> all = null;
                            for (PageBean bean : pageBeans) {
                                try {
                                    long time = format.parse(bean.getCreatetime()).getTime();
                                    if (time < lastclicktime) {
                                        unreadcount--;
                                    }
                                } catch (Exception e) {
                                    try {
                                        if (all == null) {
                                            all = DBHelper.getInstance().getSQLiteDB().queryAll(PageBean.class);
                                        }
                                        for (PageBean pageBean : all) {
                                            if (bean.getId().equals(pageBean.getId())) {
                                                unreadcount--;
                                                break;
                                            }
                                        }
                                    } catch (Exception ex) {

                                    }
                                }
                            }

                            mPageBeans = pageBeans;
                            final PageBean pageBean = pageBeans.get(0);
                            chatSystemTime.setText(pageBean.getCreatetime());
                            chatSystemMessage.setText(pageBean.getTitle());
                            if (unreadcount > 0) {
                                chatMessageCount.setVisibility(View.VISIBLE);
                                String pages = unreadcount > 99 ? "..." : String.valueOf(unreadcount);
                                chatMessageCount.setText(pages);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("===", "=请求==" + throwable.toString());
                        mPageBeans = null;
                    }
                });

    }

    @Override
    public void onItemClick(HeaderAndFooterAdapter adapter, int position, View itemView) {


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mAdapter = null;
        mMap.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRecordEvent(ChatRecord event) {
        //向其他人发起聊天时接收到的事件
        if (isRemoving() || mAdapter == null) {
            return;
        }
        if (mAdapter.getData().indexOf(event) > -1) {
            return;//已经存在此人的聊天窗口记录
        }
        addChatRecord(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomBean(RoomBean roomBean) {
        //向其他人发起聊天时接收到的事件
        if (isRemoving() || mAdapter == null) {
            return;
        }
        mAdapter.nameUpdate(roomBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {
        //收到发送的消息时接收到的事件(包括别人发送的和自己发送的消息)
        if (isRemoving() || mAdapter == null) {
            return;
        }
        ChatRecord chatRecord = getChatRecord(message);
        if (chatRecord == null) {//还没有创建此朋友的聊天记录

//            if (!message.isMeSend() && message.isMulti()){
//                try {
//                    SmackManager.getInstance().queryRoom();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            chatRecord = new ChatRecord(message);
            chatRecord.setMulti(message.isMulti());
            addChatRecord(chatRecord);
//            chatRecord.setUuid(message.getRoomJid());
            if (message.getRoomJid() != null) {
                //判断是否需要刷新群组信息 by jagtu
                String nickName = DBQueryHelper.queryRoomName(message.getRoomJid());
                if (nickName == null || nickName.length() == 0 || nickName.equals(message.getRoomJid())){
                    try {
                        SmackManager.getInstance().queryRoom();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chatRecord.setFriendNickname(nickName);
            } else {
                chatRecord.setFriendNickname(message.getFriendNickname());
            }

            if (message.getOwner() != null) {
                chatRecord.setOwn(message.getOwner());
            } else {
                try {
                    chatRecord.setOwn(DBHelper.getInstance().getSQLiteDB().queryOne(RoomBean.class, "roomJid=?", new String[]{message.getRoomJid()}).getOwner());
                } catch (Exception e) {

                }
            }


            try {
                DBQueryHelper.queryChatUser(chatRecord.getFriendUsername(), chatRecord.getFriendNickname());
            } catch (Exception e){

            }

            DBHelper.getInstance().getSQLiteDB().save(chatRecord);
        } else {
//            chatRecord.setUuid(message.getUuid());
            chatRecord.setChatTime(message.getDatetime());

            if (message.getMessageType() == MESSAGE_TYPE_TEXT.value()) {
                chatRecord.setLastMessage(message.getContent());
            }else if (message.getMessageType() == MESSAGE_TYPE_IMAGE.value()) {
                chatRecord.setLastMessage("[图片]");
            }else if (message.getMessageType() == MESSAGE_TYPE_VOICE.value()) {
                chatRecord.setLastMessage("[语音]");
            }else{
                chatRecord.setLastMessage(message.getContent());
            }

            if (message.isMeSend()) {
                chatRecord.initMessageCount();
            } else {
                chatRecord.updateUnReadMessageCount();
            }
            mAdapter.update(chatRecord);
            DBHelper.getInstance().getSQLiteDB().update(chatRecord);//更新数据库中的记录
        }
    }

    private void addChatRecord(ChatRecord chatRecord) {
        mAdapter.add(chatRecord, 0);
        mLayoutManager.scrollToPosition(0);
        for (String key : mMap.keySet()) {//创建新的聊天记录之后，需要将之前的映射关系进行更新
            mMap.put(key, mMap.get(key) + 1);
        }
    }

    /**
     * 根据消息获取聊天记录窗口对象
     *
     * @param message
     * @return
     */
    private ChatRecord getChatRecord(ChatMessage message) {
        ChatRecord chatRecord = null;
        if (message.isMulti()) {
            if (mMap.containsKey(message.getRoomJid())) {
                chatRecord = mAdapter.getData().get(mMap.get(message.getRoomJid()));
                chatRecord.setMulti(true);
            } else {
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    chatRecord = mAdapter.getData().get(i);
                    if (chatRecord.getMeUsername().equals(message.getMeUsername()) &&
                            chatRecord.getChatJid().equals(message.getRoomJid())) {
                        mMap.put(message.getRoomJid(), i);
                        break;
                    } else {
                        chatRecord = null;
                    }
                }

            }
            return chatRecord;
        }

        if (mMap.containsKey(message.getFriendUsername())) {
            chatRecord = mAdapter.getData().get(mMap.get(message.getFriendUsername()));
        } else {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                chatRecord = mAdapter.getData().get(i);
                if (IMUtil.cutPrefix(chatRecord.getMeUsername()).equals(IMUtil.cutPrefix(message.getMeUsername())) &&
                        IMUtil.cutPrefix(chatRecord.getFriendUsername()).equals(IMUtil.cutPrefix(message.getFriendUsername()))) {
                    mMap.put(message.getFriendUsername(), i);
                    break;
                } else {
                    chatRecord = null;
                }
            }
        }
//        if (chatRecord == null) {
//            chatRecord = DBQueryHelper.queryChatRecord(message.getUuid());
//        }
        if (chatRecord == null) {
            chatRecord = DBQueryHelper.queryChatRecord(SmackManager.getInstance().getChatJid(message.getFriendUsername()));
        }
        return chatRecord;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getTitle() {

        return getString(R.string.text_message);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        unbinder = ButterKnife.bind(this, rootView);
//        return rootView;
//    }


    //刷新群组成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveQueryRoom(RoomBean room) {

        Log.d("onReceiveQueryRoom","刷新群组成功");
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

}

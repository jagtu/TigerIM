package cn.ittiger.im.fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseFragment;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.MultiListActivity;
import cn.ittiger.im.adapter.ContactAdapter;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.bean.MultiAddMessage;
import cn.ittiger.im.decoration.ContactItemDecoration;
import cn.ittiger.im.adapter.viewholder.ContactViewHolder;
import cn.ittiger.im.bean.ContactEntity;
import cn.ittiger.im.bean.ContactMenuEntity;
import cn.ittiger.im.provider.join.JoinRoom;
import cn.ittiger.im.ui.ChatPromptDialog;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.IMUtil;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
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
import org.jivesoftware.smack.roster.RosterEntry;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 联系人列表
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class ContactFragment extends BaseFragment {
//    @BindView(R.id.contactRefreshLayout)
//    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;

    ContactAdapter mAdapter;

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        mIndexStickyView.addItemDecoration(new ContactItemDecoration());
    }

    @Override
    public void refreshData() {
        startRefresh();
        Observable.create(new Observable.OnSubscribe<List<ContactEntity>>() {
            @Override
            public void call(Subscriber<? super List<ContactEntity>> subscriber) {

                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<ContactEntity> list = new ArrayList<>();
                if(friends!=null){
                    for (RosterEntry friend : friends) {
                        ContactEntity contactEntity = new ContactEntity(friend);
                        String userName = contactEntity.getRosterEntry().getName();
                        while (userName.contains("@")){
                            userName = userName.substring(0,userName.indexOf("@"));
                        }
                        MemberBean memberBean = App.getInstance().mMemberBean;
                        if (memberBean != null) {
                            if (userName.startsWith(memberBean.getPrefix())) {
                                userName = userName.substring(memberBean.getPrefix().length());
                            }
                        }
                        try {
                            contactEntity.getRosterEntry().setName(userName);
                            list.add(contactEntity);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
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

                refreshFailed();
            }
        })
        .subscribe(new Action1<List<ContactEntity>>() {
            @Override
            public void call(List<ContactEntity> contacts) {

                if(mAdapter == null) {
                    mAdapter = new ContactAdapter(mContext, contacts);
                    mAdapter.setOnItemClickListener(mContactItemClickListener);
                    mIndexStickyView.setAdapter(mAdapter);
                    mIndexStickyView.addIndexHeaderAdapter(getHeaderMenuAdapter());
                } else {
                    mAdapter.reset(contacts);
                }
                refreshSuccess();
            }
        });


    }

    private IndexHeaderFooterAdapter<ContactMenuEntity> getHeaderMenuAdapter() {

        String indexValue = getString(R.string.contact_index_menu);
        List<ContactMenuEntity> list = new ArrayList<>();
        list.add(new ContactMenuEntity(getString(R.string.contact_multi_chat), ContactMenuEntity.MenuType.MULTI_CHAT));
        //list.add(new ContactMenuEntity(getString(R.string.contact_grouping), ContactMenuEntity.MenuType.GROUP));

        IndexHeaderFooterAdapter<ContactMenuEntity> adapter = new IndexHeaderFooterAdapter<ContactMenuEntity>(indexValue, null, list) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_view, parent, false);
                return new ContactViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, ContactMenuEntity itemData) {

                ContactViewHolder viewHolder = (ContactViewHolder) holder;
                viewHolder.getImageView().setImageResource(itemData.getAvatar());
                viewHolder.getTextView().setText(itemData.getMenuName());
            }
        };
        adapter.setOnItemClickListener(mContactMenuItemClickListener);
        return adapter;
    }

    OnItemClickListener<ContactEntity> mContactItemClickListener = new OnItemClickListener<ContactEntity>() {

        @Override
        public void onItemClick(View childView, int position, ContactEntity item) {

            IMUtil.startChatActivity(mContext, item.getRosterEntry());
        }
    };

    OnItemClickListener<ContactMenuEntity> mContactMenuItemClickListener = new OnItemClickListener<ContactMenuEntity>() {

        @Override
        public void onItemClick(View childView, int position, ContactMenuEntity item) {

            switch (item.getMenuType()) {
                case MULTI_CHAT://群聊
                    MultiListActivity.goActivity(getContext());
                    break;
//                case GROUP:
//                    UIUtil.showToast(mContext, R.string.text_function_none);
//                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("ContactFragment","ContactFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("ContactFragment","ContactFragment onResume");
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mAdapter = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddContactEntityEvent(ContactEntity event) {

        if(!isRemoving() && mAdapter != null) {
            mAdapter.add(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addMultiEvent(MultiAddMessage event) {

//        if(!isRemoving() && mAdapter != null) {
//            mAdapter.add(event);
//        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveJoin(List<JoinRoom> joinRooms) {
        //房间成员//建群成功
        if(isRemoving() || mAdapter == null) {
            return;
        }

    }

    @Override
    public String getTitle() {

        return getString(R.string.text_contact);
    }

    @Override
    public void refreshFailed() {

        super.refreshFailed();
     //   mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshSuccess() {

        super.refreshSuccess();
     //   mRefreshLayout.setRefreshing(false);
    }
}

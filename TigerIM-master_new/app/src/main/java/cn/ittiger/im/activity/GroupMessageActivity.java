package cn.ittiger.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;
import cn.ittiger.im.bean.CheckableContactEntity;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.DBQueryHelper;
import cn.ittiger.im.util.ImageLoaderHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.StringUtils;
import cn.ittiger.util.PreferenceHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 群组信息
 */
public class GroupMessageActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_NAME = 1001;
    private static final int MSG_NOTICE = 1002;

    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.gw_group)
    GridView gwGroup;
    @BindView(R.id.tv_personal_nickname)
    TextView tvPersonalNickname;
    @BindView(R.id.ll_nickname)
    LinearLayout llNickname;
    @BindView(R.id.tv_setmsg_title)
    TextView tvSetmsgTitle;
    @BindView(R.id.tv_setting_clear)
    TextView tvSettingClear;
    @BindView(R.id.bt_setting_exit)
    Button btSettingExit;
    @BindView(R.id.rl_group_gg)
    RelativeLayout rlGroupGg;
    @BindView(R.id.text_notice)
    TextView mNotice;
    @BindView(R.id.name_tt)
    ImageView mNameTt;
    @BindView(R.id.noce_tt)
    ImageView mNoceTt;
    private List<RoomUser> mList = new ArrayList<>();
    private BaseAdapter mAdapter;
    private RoomBean mRoomBean;
    private ProgressDialog dialog;

    ArrayList<RoomUser> messages = new ArrayList<>();


    public static void goActivity(Context context, RoomBean bean) {
        Intent intent = new Intent(context, GroupMessageActivity.class);
        intent.putExtra("key03", bean);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        ButterKnife.bind(this);
        tvTitleContent.setText("群组信息");
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mRoomBean = intent.getParcelableExtra("key03");
        tvPersonalNickname.setText(mRoomBean.getName());
        if (!StringUtils.isNullOrEmpty(mRoomBean.getNotice())){
            mNotice.setText(mRoomBean.getNotice());
        }
        messages.clear();
        Observable.create(new Observable.OnSubscribe<List<RoomUser>>() {
            @Override
            public void call(Subscriber<? super List<RoomUser>> subscriber) {

                List<RoomUser> roomUsers = DBQueryHelper.queryRoomUser(mRoomBean);
                if (roomUsers != null) {
                    messages.addAll(roomUsers);
                }

                subscriber.onNext(messages);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RoomUser>>() {
                    @Override
                    public void call(List<RoomUser> roomUsers) {
                        if (roomUsers.size() > 0) {
                            mList.clear();
                            mList.addAll(roomUsers);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

        btSettingExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //by jagtu

                dialog.show();

                if (!mRoomBean.getOwner().equals(LoginHelper.getUser().getUsername())) {
                    //非群主 退出群组

                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MultiListActivity.goActivity(GroupMessageActivity.this, mRoomBean);
                            GroupMessageActivity.this.finish();
                            dialog.dismiss();
                        }
                    }, 1500);

                    try {
                        Map<String, String> roomUser = new ArrayMap<>();
                        String userName = LoginHelper.getUser().getUsername();
                        roomUser.put(userName, userName);
                        SmackManager.getInstance().exitRoom(roomUser, mRoomBean.getRoomJid(), userName);
                        DBHelper.getInstance().getSQLiteDB().delete(mRoomBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    //群主
                    //删除并退出群
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MultiListActivity.goActivity(GroupMessageActivity.this, mRoomBean);
                            GroupMessageActivity.this.finish();
                            dialog.dismiss();
                        }
                    }, 1500);
                    Map<String, String> map = new HashMap<>();
                    map.put("groupname", mRoomBean.getRoomJid());
                    try {
                        SmackManager.getInstance().deleteRoom(map);
                        DBHelper.getInstance().getSQLiteDB().delete(mRoomBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        final String username = LoginHelper.getUser().getUsername();
        if(mRoomBean.getOwner().contains("@")){
            mRoomBean.setOwner(mRoomBean.getOwner().substring(0,mRoomBean.getOwner().indexOf("@")));
        }
        if (!mRoomBean.getOwner().equals(username)) {
            //非群主 by jagtu
//            btSettingExit.setVisibility(View.GONE);
            btSettingExit.setText("退出群组");
            rlGroupGg.setClickable(false);
            llNickname.setClickable(false);
            mNameTt.setVisibility(View.GONE);
            mNoceTt.setVisibility(View.GONE);
        } else {
            //群主
//            btSettingExit.setVisibility(View.VISIBLE);
            btSettingExit.setText("删除并退出");
            rlGroupGg.setClickable(true);
            llNickname.setClickable(true);
            mNameTt.setVisibility(View.VISIBLE);
            mNoceTt.setVisibility(View.VISIBLE);
        }
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("加载中...");

        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mRoomBean != null && (mRoomBean.getOwner() + "").equals(LoginHelper.getUser().getUsername())) {
                    return mList.size() + 2;
                } else {
                    return mList.size();
                }
            }

            @Override
            public Object getItem(int i) {
                return i;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, final ViewGroup viewGroup) {

                LayoutInflater inflater = LayoutInflater.from(mActivity);
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.layout_gridview_item, null);
                    holder = new ViewHolder();
                    convertView.setTag(holder);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.item_im);
                    holder.mOwner = (TextView) convertView.findViewById(R.id.item_owner);
                    holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
//                if (position == 0) {
//                    holder.mOwner.setVisibility(View.VISIBLE);
//                } else {
//                    holder.mOwner.setVisibility(View.GONE);
//                }

                if (position == mList.size()) {
                    holder.imageView.setImageResource(R.mipmap.icon_addpersion);
                    holder.item_name.setText("");
                    holder.imageView.setTag("+");
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddGroupMemberActivity.goActivity(GroupMessageActivity.this, mRoomBean);
                        }
                    });
                } else if (position >= mList.size() + 1) {
                    holder.item_name.setText("-");
                    holder.imageView.setImageResource(R.mipmap.icon_deletpersion);
                    holder.imageView.setTag("");
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RemoveMemberActivity.goActivity(GroupMessageActivity.this, mRoomBean);
                        }
                    });
                } else {

                    final RoomUser roomUser = mList.get(position);

                    holder.imageView.setTag(roomUser.getJid());
                    Log.i("====","===JIDJID=="+roomUser.getJid());
                    holder.imageView.setImageResource(R.mipmap.icon_my_head);
                    ImageLoaderHelper.loadImg(holder.imageView, roomUser.getJid());

                    String prefix = PreferenceHelper.getString("member");
                    String username = roomUser.getJid();
                    if(username.contains("#")){
                        username = roomUser.getJid().substring(prefix.length(), roomUser.getJid().length());
                    }
                    holder.item_name.setText(username);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击时间
                            Log.i("imageView","onClick");
                        }
                    });
                }
                return convertView;
            }
        };
        gwGroup.setAdapter(mAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomBean(RoomBean roomBean) {
        //向其他人发起聊天时接收到的事件
        if (mRoomBean.getRoomJid().equals(roomBean.getRoomJid())) {
            setTitle(mRoomBean.getName());
        }
    }


    private void initView() {
        llNickname.setOnClickListener(this);
        rlGroupGg.setOnClickListener(this);

//        gwGroup.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://停止滚动
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nickname://群名称
                startActivity(new Intent(mActivity, ChangeFlockNameActivity.class));
                break;
            case R.id.rl_group_gg://群公告
                Intent intent = new Intent(this, ChangeFlockNoticeActivity.class);
                intent.putExtra("key03", mRoomBean);
                startActivityForResult(intent, 1000);
                break;
        }

    }

    static class ViewHolder {
        ImageView imageView;
        TextView mOwner;
        TextView item_name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            String notice = data.getStringExtra("key");
            mRoomBean.setNotice(notice);
            mNotice.setText(String.valueOf(notice));
        }
        initData();
//        try {
//            SmackManager.getInstance().queryGroupUser(mRoomBean.getRoomJid(), LoginHelper.getUser().getUsername());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package cn.ittiger.im.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.AboutNewActivity;
import cn.ittiger.im.activity.PersonalDetailsActivity;
import cn.ittiger.im.activity.SettingActivity;
import cn.ittiger.im.activity.ShowImageActivity;
import cn.ittiger.im.activity.base.BaseFragment;
import cn.ittiger.im.adapter.ChatAdapter;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.CircleImageView;
import cn.ittiger.im.ui.MyImageDialog;
import cn.ittiger.im.util.ImageLoaderHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.LruUtils;
import cn.ittiger.im.util.SystemUtils;
import cn.ittiger.util.PreferenceHelper;


public class MyFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.im_my_head)
    CircleImageView imMyHead;
    @BindView(R.id.tv_my_name)
    TextView tvMyName;
    @BindView(R.id.rl_my)
    RelativeLayout rlMy;
    @BindView(R.id.im_my_setting)
    ImageView imMySetting;
    @BindView(R.id.rl_seitting)
    RelativeLayout rlSeitting;
    Unbinder unbinder;
    @BindView(R.id.im_personal)
    ImageView imPersonal;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.account)
    TextView mAccount;

    private String userName;

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;

    }

    private void initView() {
        rlMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("lbb", "点击了个人");
                Intent intent = new Intent(getActivity(), PersonalDetailsActivity.class);
                startActivity(intent);
            }
        });

        imMyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("lbb", "点击了头像");
                ShowImageActivity.startByUserName(getActivity(), LoginHelper.getUser().getUsername());
            }
        });

        rlSeitting.setOnClickListener(this);
        rlAbout.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        final User user = LoginHelper.getUser();
        if (user != null) {
            String name = user.getUsername();
            String prefix = PreferenceHelper.getString("member");
            String username = name.substring(prefix.length(),name.length());
            String nickName = user.getNickname();
            if (TextUtils.isEmpty(nickName)) {
                nickName = user.getUsername();
                tvMyName.setText(String.valueOf(username));
            }else {
                tvMyName.setText(String.valueOf(nickName));
            }

            mAccount.setText("账号:" + username);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final User user = LoginHelper.getUser();
        if (user != null) {
            String name = user.getUsername();
            String prefix = PreferenceHelper.getString("member");
            String username = name.substring(prefix.length(),name.length());
            String nickName = user.getNickname();
            if (TextUtils.isEmpty(nickName)) {
                nickName = user.getUsername();
            }
//            if(nickName.contains("#")){
//                nickName = nickName.substring(nickName.indexOf("#")+1,nickName.length());
//            }
            Log.i("=====","======更改头像"+nickName);
            ImageLoaderHelper.loadImg(imMyHead, nickName);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my://tiaozh到个人信息
                Intent intent1 = new Intent(getActivity(), PersonalDetailsActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_seitting://tiaozhuan 设置页面
                Intent intent2 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_about:
                Intent intent3 = new Intent(getActivity(), AboutNewActivity.class);
                startActivity(intent3);
                break;


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

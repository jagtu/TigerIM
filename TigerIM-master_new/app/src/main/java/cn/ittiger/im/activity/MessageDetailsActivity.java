package cn.ittiger.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.BaseActivity;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.bean.PageBean;

/**
 * 消息详情
 */
public class MessageDetailsActivity extends BaseActivity {

    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.activity_message_details)
    LinearLayout activityMessageDetails;
    @BindView(R.id.message_title)
    TextView mMessageTitle;
    @BindView(R.id.message_time)
    TextView mMessageTime;
    @BindView(R.id.create_name)
    TextView mCreateName;
    @BindView(R.id.message_conten)
    TextView mMessageContent;

    public PageBean mPageBean;

    public static void goActivity(Context context, PageBean pageBean){
        Intent intent = new Intent(context,MessageDetailsActivity.class);
        intent.putExtra("key",pageBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mPageBean = getIntent().getParcelableExtra("key");
        if(mPageBean == null){
            this.finish();
        }
        tvTitleContent.setText("消息详情");
        mMessageTitle.setText(String.valueOf(mPageBean.getTitle()));
        mMessageTime.setText(String.valueOf(mPageBean.getCreatetime()));
        mCreateName.setText(String.valueOf(mPageBean.getCreaterName()));
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(TextUtils.isEmpty(mPageBean.getContent())){
            return;
        }

        mMessageContent.setText(Html.fromHtml(mPageBean.getContent()));
    }
}

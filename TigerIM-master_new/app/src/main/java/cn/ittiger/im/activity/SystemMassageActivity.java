package cn.ittiger.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.BaseActivity;
import cn.ittiger.im.activity.base.IMBaseActivity;
import cn.ittiger.im.activity.base.UniversalAdapter;
import cn.ittiger.im.activity.base.UniversalViewHolder;
import cn.ittiger.im.bean.PageBean;

/**
 * 系统消息
 */
public class SystemMassageActivity extends BaseActivity {

    @BindView(R.id.gather_listView)
    ListView gatherListView;
    @BindView(R.id.gather_trl)
    TwinklingRefreshLayout gatherTrl;
    @BindView(R.id.activity_system_massage)
    RelativeLayout activitySystemMassage;
    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    private UniversalAdapter<PageBean> mAdapter;

    public List<PageBean> mPageBeans;

    public static void goActivity(Context context,ArrayList<PageBean> list){
        Intent intent = new Intent(context,SystemMassageActivity.class);
        intent.putParcelableArrayListExtra("key",list);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_massage);
        ButterKnife.bind(this);
        tvTitleContent.setText("系统消息");
        initData();
        initListener();
    }
    private void initListener() {
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageDetailsActivity.goActivity(SystemMassageActivity.this,mPageBeans.get(i));

            }
        });
        gatherTrl.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                gatherTrl.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                gatherTrl.finishLoadmore();
            }
        });
    }

    private void initData() {initProgress(gatherTrl);
       mPageBeans = getIntent().getParcelableArrayListExtra("key");
       if(mPageBeans== null){
           this.finish();
       }
        mAdapter = new UniversalAdapter<PageBean>(mActivity, mPageBeans, R.layout.layout_list_sys_msg) {
            @Override
            public void convert(UniversalViewHolder holder, int position, PageBean pages) {
                TextView name = (TextView) holder.getConvertView().findViewById(R.id.tv_name);
                TextView title = (TextView) holder.getConvertView().findViewById(R.id.tv_title);
                name.setText(String.valueOf(pages.getCreaterName()));
                title.setText(String.valueOf(pages.getTitle()));

            }
        };
        gatherListView.setAdapter(mAdapter);
    }

}

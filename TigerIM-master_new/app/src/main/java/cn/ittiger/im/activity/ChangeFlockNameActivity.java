package cn.ittiger.im.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.BaseActivity;
import cn.ittiger.im.activity.base.IMBaseActivity;

/**
 * 修改群名片
 */
public class ChangeFlockNameActivity extends cn.ittiger.base.BaseActivity {

    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.activity_change_flock_name)
    LinearLayout activityChangeFlockName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_flock_name);
        ButterKnife.bind(this);
        tvTitleContent.setText("修改群名片");
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }
}

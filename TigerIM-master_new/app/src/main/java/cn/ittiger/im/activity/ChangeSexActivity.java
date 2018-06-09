package cn.ittiger.im.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;

public class ChangeSexActivity extends BaseActivity {

    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.et_change_sex)
    EditText etChangeSex;
    @BindView(R.id.bt_flock_sex)
    Button btFlockSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sex);
        ButterKnife.bind(this);
        tvTitleContent.setText("性别");
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

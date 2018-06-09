package cn.ittiger.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.BaseActivity;

/**
 * 昵称修改
 */
public class ChangeNicknameActivity extends cn.ittiger.base.BaseActivity {

    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.activity_change_nickname)
    LinearLayout activityChangeNickname;
    @BindView(R.id.et_nick_name)
    EditText etNickName;
    @BindView(R.id.bt_nick_save)
    Button btNickSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        ButterKnife.bind(this);
        tvTitleContent.setText("修改昵称");
       String name=getIntent().getStringExtra("name");
        if(!name.equals("")&&name!=null){
            etNickName.setText(name);
        }
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent();
                // 设置结果，并进行传送
                mActivity.setResult(2, mIntent);
                mActivity.finish();
            }
        });
        btNickSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etNickName.getText().toString())){
                    Toast.makeText(ChangeNicknameActivity.this,"请输入昵称",Toast.LENGTH_SHORT).show();
                    return;
                }
                    Intent mIntent = new Intent();
                    mIntent.putExtra("nickname", etNickName.getText().toString());
                    // 设置结果，并进行传送
                    Log.e("lbb",etNickName.getText().toString());
                    mActivity.setResult(0, mIntent);
                    mActivity.finish();
            }
        });

        
    }
}

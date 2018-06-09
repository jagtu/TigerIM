package cn.ittiger.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.LoginHelper;

/**
 * 修改群公告
 */
public class ChangeFlockNoticeActivity extends BaseActivity {


    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.activity_change_flock_notice)
    LinearLayout activityChangeFlockNotice;
    @BindView(R.id.et_notice_name)
    EditText etNoticeName;
    @BindView(R.id.bt_notice_save)
    Button btNoticeSave;
    private RoomBean mRoomBean;
    private ProgressDialog mProgressDialog;

    public static void goActivity(Context context, RoomBean bean){
        Intent intent = new Intent(context,ChangeFlockNoticeActivity.class);
        intent.putExtra("key03",bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_flock_notice);
        ButterKnife.bind(this);
        tvTitleContent.setText("修改群公告");
        mRoomBean = getIntent().getParcelableExtra("key03");
        if(mRoomBean == null){
            this.finish();
            return;
        }
        etNoticeName.setText(mRoomBean.getNotice());
        imTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("修改中...");
        btNoticeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String notice = etNoticeName.getText().toString();
                if(TextUtils.isEmpty(notice)){
                    Toast.makeText(ChangeFlockNoticeActivity.this,"请输入群公告",Toast.LENGTH_SHORT).show();
                }else {
                    mProgressDialog.show();
                    Map<String,String> map = new HashMap<>();
                    map.put("notice",notice);
                    String userName = LoginHelper.getUser().getUsername();
                    try {
                        SmackManager.getInstance().changeNotice(map, SmackManager.getInstance().getChatJid(userName),mRoomBean.getRoomJid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("key",notice);
                            setResult(RESULT_OK,intent);
                            ChangeFlockNoticeActivity.this.finish();
                            mProgressDialog.dismiss();
                        }
                    },1500);
                }
            }
        });


    }
}

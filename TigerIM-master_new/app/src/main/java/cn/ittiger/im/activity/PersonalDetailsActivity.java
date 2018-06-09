package cn.ittiger.im.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.constant.MessageType;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.CircleImageView;
import cn.ittiger.im.ui.SelectPicPopupWindow;
import cn.ittiger.im.util.AppFileHelper;
import cn.ittiger.im.util.ImageLoaderHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.SystemUtils;
import cn.ittiger.util.BitmapUtil;
import cn.ittiger.util.DateUtil;
import cn.ittiger.util.FileUtil;
import cn.ittiger.util.PreferenceHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 个人信息
 */
public class PersonalDetailsActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 选择图片
     */
    private static final int REQUEST_CODE_GET_IMAGE = 1001;
    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 1002;
    @BindView(R.id.im_title_back)
    ImageView imTitleBack;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.activity_personal_details)
    LinearLayout activityPersonalDetails;
    @BindView(R.id.ll_nickname)
    LinearLayout llNickname;
    @BindView(R.id.im_personal_head)
    CircleImageView imPersonalHead;
    @BindView(R.id.tv_personal_nickname)
    TextView tvPersonalNickname;
    @BindView(R.id.tv_system_name)
    TextView tvSystemName;
    @BindView(R.id.ll_personal_changehead)
    LinearLayout llPersonalChangehead;
    @BindView(R.id.ll_delails_sex)
    LinearLayout llDelailsSex;
//    @BindView(R.id.ll_delails_email)
//    LinearLayout llDelailsEmail;
//    @BindView(R.id.ll_delails_signature)
//    LinearLayout llDelailsSignature;
    @BindView(R.id.ll_delails_exit)
    LinearLayout llDelailsExit;
    @BindView(R.id.user_name)
    TextView mUserName;
    private int requestCode;
    private ProgressDialog mProgressDialog;
    private SelectPicPopupWindow mSelectPicPopupWindow;
    private String mPicPath = "";
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tv_pop_tickphoto:
                    takePhoto();
                    if (mSelectPicPopupWindow != null) {
                        mSelectPicPopupWindow.dismiss();
                    }
                    break;
                case R.id.tv_pop_album:
                    selectImage();
                    if (mSelectPicPopupWindow != null) {
                        mSelectPicPopupWindow.dismiss();
                    }
                    break;
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                Toast.makeText(PersonalDetailsActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public static Bitmap getUserImage(XMPPConnection connection) {
        ByteArrayInputStream bais = null;
        try {
            VCard vcard = new VCard();
            // 加入这句代码，解决No VCard for
            ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
            vcard.load(connection, LoginHelper.getUser().getUsername() + "@" + connection.getServiceName());
            if (vcard.getAvatar() == null)
                return null;
            bais = new ByteArrayInputStream(vcard.getAvatar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bais == null)
            return null;
        Bitmap bitmap=  BitmapFactory.decodeStream(bais);

        bitmap = SystemUtils.createCircleImage(bitmap);

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        ButterKnife.bind(this);
        tvTitleContent.setText("个人信息");
        final User user = LoginHelper.getUser();
        if (user != null) {
            String nickName = user.getNickname();
            String name = user.getUsername();
            String prefix = PreferenceHelper.getString("member");
            String username = name.substring(prefix.length(), name.length());
            if (TextUtils.isEmpty(nickName)) {
                nickName = username;
                tvPersonalNickname.setText(username);
            } else {
                tvPersonalNickname.setText(nickName);
            }
            mUserName.setText(username);
           String avatName = user.getNickname();
           if(TextUtils.isEmpty(avatName)){
               avatName = user.getUsername();
           }
            ImageLoaderHelper.loadImg(imPersonalHead, avatName);
        }

        if (App.getInstance().mMemberBean != null) {
            tvSystemName.setText(App.getInstance().mMemberBean.getName());
        }else {
            tvSystemName.setText("**");
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在退出...");
        imTitleBack.setOnClickListener(this);
        llNickname.setOnClickListener(this);
        llDelailsExit.setOnClickListener(this);
        llPersonalChangehead.setOnClickListener(this);
//        llDelailsEmail.setOnClickListener(this);
        llDelailsSex.setOnClickListener(this);
//        llDelailsSignature.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_title_back://返回
                onBackPressed();
                break;
            case R.id.ll_nickname://昵称
                requestCode = 1;
                startActivityForResult(new Intent(mActivity, ChangeNicknameActivity.class).putExtra("name",
                        tvPersonalNickname.getText().toString()), requestCode);
                break;
            case R.id.ll_delails_exit://退出
                //退出登录
                mProgressDialog.show();
                LoginHelper.saveUser(null);
                SmackManager.getInstance().logout();
                SmackManager.getInstance().disconnect();
                SmackManager.getInstance().destroy();
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.goActivity(PersonalDetailsActivity.this, "exit");
                        mProgressDialog.dismiss();
                        PersonalDetailsActivity.this.finish();
                    }
                }, 2000);
                break;
            case R.id.ll_personal_changehead://头像
                showPopFormBottom(view);
                break;
            case R.id.ll_delails_sex://性别
                startActivity(new Intent(mActivity, ChangeSexActivity.class));
                break;
        }

    }

    public void showPopFormBottom(View view) {
        mSelectPicPopupWindow = new SelectPicPopupWindow(this, onClickListener);
        mSelectPicPopupWindow.showAtLocation(findViewById(R.id.ll_personal_changehead), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f; //0.0-1.0
        getWindow().setAttributes(lp);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        mSelectPicPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f; //0.0-1.0
                getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 从图库选择图片
     */
    public void selectImage() {

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        }
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        String dir = AppFileHelper.getAppChatMessageDir(MessageType.MESSAGE_TYPE_IMAGE.value()).getAbsolutePath();
        mPicPath = dir + "/" + DateUtil.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".png";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPicPath)));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 0) {
            Log.e("lbb接受", data.getExtras().getString("nickname"));
            String nickName = data.getStringExtra("nickname");
            tvPersonalNickname.setText(nickName);
            User user = LoginHelper.getUser();
            user.setNickname(nickName);
            LoginHelper.saveUser(user);
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//拍照成功
                takePhotoSuccess();
            } else if (requestCode == REQUEST_CODE_GET_IMAGE) {//图片选择成功
                if (data != null) {
                    Uri dataUri = data.getData();
                    if (dataUri != null) {
                        File file = FileUtil.uri2File(this, dataUri);
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imPersonalHead.setImageBitmap(bitmap);
                        try {
                            setUserImage(SmackManager.getInstance().getConnection(), fileToBytes(file));
                        } catch (XMPPException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 照片拍摄成功
     */
    public void takePhotoSuccess() {

        Bitmap bitmap = BitmapUtil.createBitmapWithFile(mPicPath, 640);
        BitmapUtil.createPictureWithBitmap(mPicPath, bitmap, 80);
        imPersonalHead.setImageBitmap(bitmap);
        try {
            setUserImage(SmackManager.getInstance().getConnection(), fileToBytes(new File(mPicPath)));
        } catch (XMPPException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传头像
     */
    private void setUserImage(final XMPPConnection connection, final byte[] image) throws XMPPException {


        final VCard card = new VCard();
        try {
            card.load(connection);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        if (!connection.isConnected()) {
            try {
                ((XMPPTCPConnection) connection).connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rx.Observable.just(connection)
                .flatMap(new Func1<XMPPConnection, rx.Observable<String>>() {
                    @Override
                    public rx.Observable<String> call(XMPPConnection xmppConnection) {
                        StanzaFilter filter = new AndFilter(new PacketIDFilter(card.getPacketID()), new PacketTypeFilter(IQ.class));
                        PacketCollector collector = connection.createPacketCollector(filter);

                        String encodeImage = new String(Base64.encode(image));
                        card.setAvatar(image, encodeImage);
                        card.setEncodedImage(encodeImage);
                        card.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodeImage + "</BINVAL>", true);
                        Log.i("other", "上传头像的方法！");
                        try {
                            try {
                                VCardManager.getInstanceFor(connection).saveVCard(card);
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            }
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        return rx.Observable.just("上传成功");
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static byte[] fileToBytes(File file) throws IOException {
        byte[] buffer = null;

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } finally {
                if (null != fis) {
                    fis.close();
                }
            }
        }

        return buffer;
    }


}

package cn.ittiger.im.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cn.ittiger.im.bean.ImageBean;
import cn.ittiger.im.bean.VoiceBean;

public class UploadUtils {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    private static String ACCESS_KEY = "73YvwwinYzt30Afta-IVnKQrJ1Afn3aUCkae1jWu";
    private static String SECRET_KEY = "QYTr8e6HCBiVFju3e_Zpo27X7nYFvFZZ4Ac7P3rY";
    //要上传的空间
    private static String bucketnameVoice = "voicefile";
    private static String bucketnameImage = "img-nature";

    private static String domainVoice = "http://p7dsfgicj.bkt.clouddn.com";
    private static String domainImage = "http://p84whdsjt.bkt.clouddn.com";
    private Context mContext;
    private ProgressDialog mProgressDialog;

    //密钥配置
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    private SuccessListener mSuccessListener;
    private Configuration config = new Configuration.Builder()
            .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
            .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
            .connectTimeout(10)           // 链接超时。默认10秒
            .useHttps(true)               // 是否使用https上传域名
            .responseTimeout(60)          // 服务器响应超时。默认60秒
            .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
            .build();
    //创建上传对象
    private UploadManager uploadManager = new UploadManager(config);

    public UploadUtils(Context context) {
        mContext = context;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("上传中...");
    }

    public void setSuccessListener(SuccessListener mSuccessListener) {
        this.mSuccessListener = mSuccessListener;
    }

    public void uploadVoice(String key, File file, final double duration) throws IOException {
        try {
            //调用put方法上传
            mProgressDialog.show();
            uploadManager.put(file, key, getVoiceUpToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {

                    mProgressDialog.dismiss();
                    VoiceBean voiceBean = new Gson().fromJson(response.toString(), VoiceBean.class);
                    if (voiceBean == null) {
                        Toast.makeText(mContext, "发送语音失败", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mSuccessListener != null) {
                            mSuccessListener.upLoadSuccess("voice", domainVoice + "/" + voiceBean.getKey(), duration);
                        }
                    }
                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }

    private String getVoiceUpToken() {
        return auth.uploadToken(bucketnameVoice);
    }

    public void uploadImage(String key, File file) throws IOException {
        try {
            mProgressDialog.show();

            //调用put方法上传
            uploadManager.put(file, key, getImageUpToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {

                    mProgressDialog.dismiss();
                    ImageBean imageBean = new Gson().fromJson(response.toString(), ImageBean.class);
                    if (imageBean == null) {
                        Toast.makeText(mContext, "发送图片失败", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mSuccessListener != null) {
                            mSuccessListener.upLoadSuccess("image", domainImage + "/" + imageBean.getKey(), 0);
                        }
                    }
                }
            }, null);

        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private String getImageUpToken() {
        return auth.uploadToken(bucketnameImage);
    }

    public interface SuccessListener {
        void upLoadSuccess(String type, String url, double duration);
    }
}
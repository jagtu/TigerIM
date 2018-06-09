package cn.ittiger.im.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.base.BaseChatActivity;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.constant.FileLoadState;
import cn.ittiger.im.constant.KeyBoardMoreFunType;
import cn.ittiger.im.constant.MessageType;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.RecordingVoiceView;
import cn.ittiger.im.util.AppFileHelper;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.IMUtil;
import cn.ittiger.im.util.UploadUtils;
import cn.ittiger.util.BitmapUtil;
import cn.ittiger.util.DateUtil;
import cn.ittiger.util.FileUtil;
import cn.ittiger.util.ValueUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 单聊窗口
 *
 * @author: laohu on 2017/1/12
 * @site: http://ittiger.cn
 */
public class ChatActivity extends BaseChatActivity {

    /**
     * 选择图片
     */
    private static final int REQUEST_CODE_GET_IMAGE = 1;
    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    /**
     * 聊天窗口对象
     */
    private Chat mChat;
    private String mPicPath = "";

    private UploadUtils mUploadUtils;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_layout);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        mUploadUtils = new UploadUtils(this);
        setReceiveListener();
        String jid = mChatUser.getChatJid();
        if (jid.contains("@")) {
            jid = jid.substring(0, jid.indexOf("@"));
        }
        mChat = SmackManager.getInstance().createChat(jid + "@" + SmackManager.SERVER_NAME);
        addReceiveFileListener();
    }

    private void setReceiveListener() {
        mUploadUtils.setSuccessListener(new UploadUtils.SuccessListener() {
            @Override
            public void upLoadSuccess(final String type, String url, final double duration) {

                Observable.just(url)
                        .observeOn(Schedulers.io())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String message) {
                                try {
                                    ChatMessage msg = new ChatMessage(MessageType.MESSAGE_TYPE_VOICE.value(), true);
                                    JSONObject json = new JSONObject();
                                    if ("image".equals(type)) {
                                        json.put("type", "image");
                                        json.put("data", "[图片]");
                                        json.put("imageUrl", message);
                                        msg.setMessageType(MessageType.MESSAGE_TYPE_IMAGE.value());
                                    } else if ("voice".equals(type)) {
                                        json.put("type", "audio");
                                        json.put("data", "[语音]");
                                        json.put("duration", duration / 1000 + "s");
                                        json.put("audioUrl", message);
                                    }

                                    mChat.sendMessage(json.toString());
                                    msg.setFriendNickname(mChatUser.getFriendNickname());
                                    msg.setFriendUsername(mChatUser.getFriendUsername());
                                    msg.setMeUsername(mChatUser.getMeUsername());
                                    msg.setMeNickname(mChatUser.getMeNickname());
                                    msg.setFilePath(message);
                                    msg.setContent(duration / 1000 + "s");
                                    DBHelper.getInstance().getSQLiteDB().save(msg);
                                    EventBus.getDefault().post(msg);
                                } catch (Exception e) {
                                    Logger.e(e, "send failure");
                                }
                            }
                        });
            }
        });
    }

    /**
     * 接收文件
     */
    public void addReceiveFileListener() {

        FileTransferManager fileTransferManager = FileTransferManager.getInstanceFor(SmackManager.getInstance().getConnection());
        fileTransferManager.addFileTransferListener(new FileTransferListener() {
            @Override
            public void fileTransferRequest(FileTransferRequest fileTransferRequest) {
                IncomingFileTransfer incomingFileTransfer = fileTransferRequest.accept();
                int messageType = Integer.parseInt(fileTransferRequest.getDescription());
                File dir = AppFileHelper.getAppChatMessageDir(messageType);
                File file = new File(dir, fileTransferRequest.getFileName());
                try {
                    incomingFileTransfer.recieveFile(file);
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                checkTransferStatus(incomingFileTransfer, file, messageType, false);
            }
        });
    }


    /**
     * 检查发送文件、接收文件的状态
     *
     * @param transfer
     * @param file        发送或接收的文件
     * @param messageType 文件类型，语音或图片
     * @param isMeSend    是否为发送
     */
    private void checkTransferStatus(final FileTransfer transfer, final File file, final int messageType, final boolean isMeSend) {

        final ChatMessage msg = new ChatMessage(messageType, isMeSend);
        msg.setFriendNickname(mChatUser.getFriendNickname());
        msg.setFriendUsername(mChatUser.getFriendUsername());
        msg.setMeUsername(mChatUser.getMeUsername());
        msg.setMeNickname(mChatUser.getMeNickname());
        msg.setFilePath(file.getAbsolutePath());
        DBHelper.getInstance().getSQLiteDB().save(msg);

        Observable.create(new Observable.OnSubscribe<ChatMessage>() {
            @Override
            public void call(Subscriber<? super ChatMessage> subscriber) {
                addChatMessageView(msg);
                subscriber.onNext(msg);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<ChatMessage, ChatMessage>() {
                    @Override
                    public ChatMessage call(ChatMessage chatMessage) {

                        while (!transfer.isDone()) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return chatMessage;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ChatMessage>() {
                    @Override
                    public void call(ChatMessage chatMessage) {
                        if (FileTransfer.Status.complete.toString().equals(transfer.getStatus())) {//传输完成
                            chatMessage.setFileLoadState(FileLoadState.STATE_LOAD_SUCCESS.value());
                            mAdapter.update(chatMessage);
                        } else {
                            chatMessage.setFileLoadState(FileLoadState.STATE_LOAD_ERROR.value());
                            mAdapter.update(chatMessage);
                        }
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {
        if (IMUtil.cutPrefix(mChatUser.getMeUsername()).equals(IMUtil.cutPrefix(message.getMeUsername())) && !message.isMulti()) {
            addChatMessageView(message);
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    @Override
    public void send(final String message) {

        if (ValueUtil.isEmpty(message)) {
            return;
        }
        Observable.just(message)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String message) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("type", "text");
                            json.put("data", message);
                            mChat.sendMessage(json.toString());
                            ChatMessage msg = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), true);
                            msg.setFriendNickname(mChatUser.getFriendNickname());
                            msg.setFriendUsername(mChatUser.getFriendUsername());
                            msg.setMeUsername(IMUtil.cutPrefix(mChatUser.getMeUsername()));
                            msg.setMeNickname(mChatUser.getMeNickname());
                            msg.setContent(message);
                            DBHelper.getInstance().getSQLiteDB().save(msg);
                            EventBus.getDefault().post(msg);
                        } catch (Exception e) {
                            Logger.e(e, "send message failure");
                        }
                    }
                });
    }

    /**
     * 发送语音消息
     *
     * @param audioFile
     */
    @Override
    public void sendVoice(File audioFile, double duration) {
        try {
            mUploadUtils.uploadVoice(audioFile.getName(), audioFile, duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void functionClick(KeyBoardMoreFunType funType) {

        switch (funType) {
            case FUN_TYPE_IMAGE://选择图片
                selectImage();
                break;
            case FUN_TYPE_TAKE_PHOTO://拍照
                takePhoto();
                break;
        }
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

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//拍照成功
                takePhotoSuccess();
            } else if (requestCode == REQUEST_CODE_GET_IMAGE) {//图片选择成功
                Uri dataUri = data.getData();
                if (dataUri != null) {
                    imageFile = FileUtil.uri2File(this, dataUri);
                    try {
                        mUploadUtils.uploadImage(imageFile.getName(), imageFile);
                    } catch (Exception e) {
                        e.printStackTrace();
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
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        try {
            imageFile = new File(mPicPath);
            mUploadUtils.uploadImage(imageFile.getName(), imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

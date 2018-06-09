package cn.ittiger.im.bean;

import cn.ittiger.database.annotation.Column;
import cn.ittiger.database.annotation.PrimaryKey;
import cn.ittiger.database.annotation.Table;
import cn.ittiger.im.constant.FileLoadState;
import cn.ittiger.im.constant.MessageType;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :
 * </pre>
 */
@Table(name = "RoomMessage")
public class RoomMessage {

    public static final String KEY_FROM_NICKNAME = "fromNickName";
    public static final String KEY_MESSAGE_CONTENT = "messageContent";
    public static final String KEY_MULTI_CHAT_SEND_USER = "multiChatSendUser";

    @PrimaryKey(isAutoGenerate = true)
    private long mId;
    /**
     * 消息内容
     */
    @Column(columnName = "message")
    private String mContent;
    /**
     * 消息类型  {@link MessageType}
     */
    @Column(columnName = "messageType")
    private int mMessageType;
    /**
     * 聊天好友的用户名,群聊时为群聊的jid,格式为：老胡创建的群@conference.121.42.13.79
     */
    @Column(columnName = "roomJid")
    private String roomJid;
    /**
     * 聊天好友的昵称
     */
    @Column(columnName = "sendNickname")
    private String sendNickname;
    /**
     * 自己的用户名
     */
    @Column(columnName = "sendUsername")
    private String mName;
    /**
     * 消息发送接收的时间
     */
    @Column(columnName = "dateTime")
    private String mDatetime;
    /**
     * 当前消息是否是自己发出的
     */
    @Column(columnName = "isMeSend")
    private boolean mIsMeSend;
    /**
     * 接收的图片或语音路径
     */
    @Column(columnName = "filePath")
    private String mFilePath;
    /**
     * 文件加载状态 {@link FileLoadState}
     */
    @Column(columnName = "fileLoadState")
    private int mFileLoadState = FileLoadState.STATE_LOAD_START.value();


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void setMessageType(int messageType) {
        mMessageType = messageType;
    }

    public String getRoomJid() {
        return roomJid;
    }

    public void setRoomJid(String roomId) {
        this.roomJid = roomId;
    }

    public String getSendNickname() {
        return sendNickname;
    }

    public void setSendNickname(String sendNickname) {
        this.sendNickname = sendNickname;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDatetime() {
        return mDatetime;
    }

    public void setDatetime(String datetime) {
        mDatetime = datetime;
    }

    public boolean isMeSend() {
        return mIsMeSend;
    }

    public void setMeSend(boolean meSend) {
        mIsMeSend = meSend;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getFileLoadState() {
        return mFileLoadState;
    }

    public void setFileLoadState(int fileLoadState) {
        mFileLoadState = fileLoadState;
    }
}

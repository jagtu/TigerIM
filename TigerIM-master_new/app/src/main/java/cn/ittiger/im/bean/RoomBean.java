package cn.ittiger.im.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

import cn.ittiger.database.annotation.Column;
import cn.ittiger.database.annotation.PrimaryKey;
import cn.ittiger.database.annotation.Table;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :房间id...
 * </pre>
 */
@Table(name = "RoomBean")
public class RoomBean implements Parcelable{

    @PrimaryKey(isAutoGenerate = true)
    private long mId;
    @Column(columnName = "roomJid")
    private String mRoomJid;
    @Column(columnName = "name")
    private String mName;
    @Column(columnName = "owner")
    private String owner;
    @Column(columnName = "notice")
    private String notice;//notice="群公告"

    @Column(columnName = "roomimg")
    private String roomimg;//roomimg="群头像"

    public RoomBean(String roomJid, String name,String owner) {
        mRoomJid = roomJid;
        mName = name;
        this.owner = owner;
        this.notice = "";
        this.roomimg = "";
    }

    public RoomBean(String roomJid, String name,String owner, String notice,String roomimg) {
        mRoomJid = roomJid;
        mName = name;
        this.owner = owner;
        this.notice = notice;
        this.roomimg = roomimg;
    }

    protected RoomBean(Parcel in) {
        mId = in.readLong();
        mRoomJid = in.readString();
        mName = in.readString();
        owner = in.readString();
        notice = in.readString();
        roomimg = in.readString();
    }

    public static final Creator<RoomBean> CREATOR = new Creator<RoomBean>() {
        @Override
        public RoomBean createFromParcel(Parcel in) {
            return new RoomBean(in);
        }

        @Override
        public RoomBean[] newArray(int size) {
            return new RoomBean[size];
        }
    };

    public RoomBean() {

    }

    public boolean isValidate() {
        if(TextUtils.isEmpty(mRoomJid) || TextUtils.isEmpty(mName)){
            return false;
        }
        return true;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getRoomJid() {
        return mRoomJid;
    }

    public void setRoomJid(String roomJid) {
        mRoomJid = roomJid;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getRoomimg() {
        return roomimg;
    }

    public void setRoomimg(String roomimg) {
        this.roomimg = roomimg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mRoomJid);
        parcel.writeString(mName);
        parcel.writeString(owner);
        parcel.writeString(notice);
        parcel.writeString(roomimg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RoomBean){
            RoomBean roomBean = (RoomBean) obj;
            if(getRoomJid().equals(roomBean.getRoomJid())){
                return true;
            }
        }
        return super.equals(obj);
    }
}

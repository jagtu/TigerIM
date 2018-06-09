package cn.ittiger.im.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import cn.ittiger.database.annotation.Column;
import cn.ittiger.database.annotation.PrimaryKey;
import cn.ittiger.database.annotation.Table;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :保存本地
 * </pre>
 */

@Table(name = "RoomUser")
public class RoomUser implements Parcelable {

    @PrimaryKey(isAutoGenerate = true)
    private long mId;
    @Column(columnName = "roomJid")
    private String roomJid;
    @Column(columnName = "jid")
    private String jid;
    @Column(columnName = "name")
    private String name;
    @Column(columnName = "icon")
    private String icon;

    public RoomUser(String roomJid,String jid, String name) {
        this.roomJid = roomJid;
        this.jid = jid;
        this.name = name;
        this.icon = "";
    }

    public RoomUser() {

    }

    public void setIcon(String icon ) {
        this.icon = icon;
        if(this.icon == null){
            this.icon = null;
        }
    }

    protected RoomUser(Parcel in) {
        mId = in.readLong();
        roomJid = in.readString();
        jid = in.readString();
        name = in.readString();
        icon = in.readString();
    }

    public static final Creator<RoomUser> CREATOR = new Creator<RoomUser>() {
        @Override
        public RoomUser createFromParcel(Parcel in) {
            return new RoomUser(in);
        }

        @Override
        public RoomUser[] newArray(int size) {
            return new RoomUser[size];
        }
    };

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getRoomJid() {
        return roomJid;
    }

    public void setRoomJid(String roomJid) {
        this.roomJid = roomJid;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(roomJid);
        parcel.writeString(jid);
        parcel.writeString(name);
        parcel.writeString(icon);
    }
}

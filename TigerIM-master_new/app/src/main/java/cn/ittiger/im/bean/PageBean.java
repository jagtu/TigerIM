package cn.ittiger.im.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.ittiger.database.annotation.PrimaryKey;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/createrName29
 *      desc    :
 * </pre>
 */

public class PageBean implements Parcelable{

    private String createtime;
    private int creater;
    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private String createrName;

    public PageBean() {
    }

    protected PageBean(Parcel in) {
        createtime = in.readString();
        creater = in.readInt();
        id = in.readString();
        title = in.readString();
        content = in.readString();
        createrName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createtime);
        dest.writeInt(creater);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(createrName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PageBean> CREATOR = new Creator<PageBean>() {
        @Override
        public PageBean createFromParcel(Parcel in) {
            return new PageBean(in);
        }

        @Override
        public PageBean[] newArray(int size) {
            return new PageBean[size];
        }
    };

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getCreater() {
        return creater;
    }

    public void setCreater(int creater) {
        this.creater = creater;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }
}

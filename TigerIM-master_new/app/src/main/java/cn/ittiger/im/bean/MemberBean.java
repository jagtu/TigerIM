package cn.ittiger.im.bean;

import java.io.Serializable;

/**
 * Created by yangsong on 2018/5/4.
 */

public class MemberBean implements Serializable {


    /**
     * id : 1
     * name : 系统一
     * note :
     * pkey : a620fedbc44649b9aa9ae614ac874213
     * prefix : t#
     * url : http://d1.myc178.com
     */

    private int id;
    private String name;
    private String note;
    private String pkey;
    private String prefix;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

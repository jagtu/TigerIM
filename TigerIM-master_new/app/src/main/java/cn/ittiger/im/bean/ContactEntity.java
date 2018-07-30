package cn.ittiger.im.bean;

import android.text.TextUtils;

import cn.ittiger.indexlist.entity.BaseEntity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 联系人实体
 * @author: laohu on 2016/12/26
 * @site: http://ittiger.cn
 */
public class ContactEntity implements BaseEntity {
    private RosterEntry mRosterEntry;

    public ContactEntity(RosterEntry rosterEntry) {

        mRosterEntry = rosterEntry;
        if(TextUtils.isEmpty(mRosterEntry.getName())){
            try {
                mRosterEntry.setName(mRosterEntry.getUser());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getIndexField() {

        return mRosterEntry.getName();
    }



    public RosterEntry getRosterEntry() {

        return mRosterEntry;
    }
}

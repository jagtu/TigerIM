package cn.ittiger.im.bean;

import java.util.List;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :
 * </pre>
 */

public class MultiAddMessage {

    private List<RoomUser> mRoomUsers;
    private RoomBean mRoomBean;

    public MultiAddMessage(List<RoomUser> roomUsers, RoomBean roomBean) {
        mRoomUsers = roomUsers;
        mRoomBean = roomBean;
    }

    public List<RoomUser> getRoomUsers() {
        return mRoomUsers;
    }

    public void setRoomUsers(List<RoomUser> roomUsers) {
        mRoomUsers = roomUsers;
    }

    public RoomBean getRoomBean() {
        return mRoomBean;
    }

    public void setRoomBean(RoomBean roomBean) {
        mRoomBean = roomBean;
    }
}

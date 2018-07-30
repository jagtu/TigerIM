package cn.ittiger.im.util;

import android.util.Log;

import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.bean.ChatRecord;
import cn.ittiger.im.bean.ChatUser;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomMessage;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.constant.Constant;
import cn.ittiger.im.smack.SmackMultiChatManager;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;

/**
 * 数据库查询帮助类
 *
 * @author: laohu on 2017/1/20
 * @site: http://ittiger.cn
 */
public class DBQueryHelper {

    /**
     * 根据好友信息查询对应的ChatUser，如果数据库中不存在则创建新的
     *
     * @param friendRoster  好友信息
     * @return
     */
     public static ChatUser queryChatUser(RosterEntry friendRoster) {

         return queryChatUser(friendRoster.getUser(), friendRoster.getName());
     }


    /**
     * 根据多人聊天信息查询对应的ChatUser，如果数据库中不存在则创建新的
     *
     * @param multiUserChat
     * @return
     */
    public static ChatUser queryChatUser(MultiUserChat multiUserChat) {

        String friendUserName = multiUserChat.getRoom();
        int idx = friendUserName.indexOf(Constant.MULTI_CHAT_ADDRESS_SPLIT);
        String friendNickName = friendUserName.substring(0, idx);
        String whereClause = "meUserName=? and friendUserName=? and isMulti=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername(), friendUserName, "true"};
        ChatUser chatUser = DBHelper.getInstance().getSQLiteDB().queryOne(ChatUser.class, whereClause, whereArgs);
        if(chatUser == null) {
            chatUser = new ChatUser(friendUserName, friendNickName, true);
            DBHelper.getInstance().getSQLiteDB().save(chatUser);
        }
        return chatUser;
    }

    /**
     * 根据好友信息查询对应的ChatUser，如果数据库中不存在则创建新的
     *
     * @param friendUserName
     * @param friendNickName
     * @return
     */
    public static ChatUser queryChatUser(String friendUserName, String friendNickName) {

        String whereClause = "meUserName=? and friendUserName=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername(), friendUserName};
        ChatUser chatUser = DBHelper.getInstance().getSQLiteDB().queryOne(ChatUser.class, whereClause, whereArgs);
        if(chatUser == null) {
            chatUser = new ChatUser(friendUserName, friendNickName);
            DBHelper.getInstance().getSQLiteDB().save(chatUser);
        }
        return chatUser;
    }

    public static ChatRecord queryChatRecordBjid(String chatjid) {

        String whereClause = "chatJid=?";
        String[] whereArgs = {chatjid};
        ChatRecord chatUser = DBHelper.getInstance().getSQLiteDB().queryOne(ChatRecord.class, whereClause, whereArgs);
        return chatUser;
    }

    /**
     * 查询登陆用户的所有聊天用户记录
     *
     * @return
     */
    public static List<ChatRecord> queryChatRecord() {

        String whereClause = "meUserName=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername()};
        return DBHelper.getInstance().getSQLiteDB().query(ChatRecord.class, whereClause, whereArgs);
    }

    /**
     * 根据主键查询ChatRecord
     *
     * @param uuid
     * @return
     */
    public static ChatRecord queryChatRecord(String uuid) {

        return DBHelper.getInstance().getSQLiteDB().query(ChatRecord.class, uuid);
    }

    public static ChatRecord queryFirstChatRecord(String meUserName, String friendUserName) {

        String whereClause = "meUserName=? and friendUserName=?";


        String[] whereArgs = {IMUtil.cutPrefix(meUserName), friendUserName};
        List<ChatRecord> list = DBHelper.getInstance().getSQLiteDB().query(ChatRecord.class, whereClause, whereArgs);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<ChatMessage> queryChatMessage(ChatUser chatUser) {

        String whereClause = "meUserName=? and friendUserName=?";
        String meUsername = chatUser.getMeUsername();
        try {
            if (App.getInstance().mMemberBean != null) {
                if (meUsername.startsWith(App.getInstance().mMemberBean.getPrefix())) {
                    meUsername = meUsername.substring(App.getInstance().mMemberBean.getPrefix().length());
                }
            }
        } catch (Exception e){

        }


        String[] whereArgs = {meUsername, chatUser.getFriendUsername()};
        return DBHelper.getInstance().getSQLiteDB().query(ChatMessage.class, whereClause, whereArgs);
    }


    public static List<ChatMessage> queryMulti(RoomBean roomBean) {

        String whereClause = "roomId=? and isMulti=?";
        String[] whereArgs = {roomBean.getRoomJid(),"true"};
        return DBHelper.getInstance().getSQLiteDB().query(ChatMessage.class, whereClause, whereArgs);
    }


    public static List<RoomUser> queryRoomUser(RoomBean roomBean) {

        String whereClause = "roomJid=?";
        String[] whereArgs = {roomBean.getRoomJid()};
        return DBHelper.getInstance().getSQLiteDB().query(RoomUser.class, whereClause, whereArgs);
    }

    public static void deleteRoomUser(String roomJid,String jid) {

        String whereClause = "roomJid =? and jid=?";
        String[] whereArgs = {roomJid,jid};
         DBHelper.getInstance().getSQLiteDB().delete(RoomUser.class, whereClause, whereArgs);
    }

    public static List<RoomBean> queryRoom(){
        return DBHelper.getInstance().getSQLiteDB().queryAll(RoomBean.class);
    }

    public static String queryRoomName(String roomJid) {

        String whereClause = "roomJid=? ";
        String[] whereArgs = {roomJid};
        RoomBean roomBean = DBHelper.getInstance().getSQLiteDB().queryOne(RoomBean.class, whereClause, whereArgs);
        if(roomBean == null){
           // SmackMultiChatManager.bindJoinMultiChat();
            return roomJid;
        }
        return roomBean.getName();
    }

    //by jagtu
    public static void deleteChatMessage(String chatJid) {

        String whereClause = "meUserName=? and friendUserName=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername() ,chatJid};
        DBHelper.getInstance().getSQLiteDB().delete(ChatMessage.class, whereClause, whereArgs);

        ChatMessage chatMessage = DBHelper.getInstance().getSQLiteDB().queryOne(ChatMessage.class, whereClause, whereArgs);
        if (chatMessage!=null){
            Log.e("error","delete chatMessage fail!");
        }
    }


    public static void deleteChatUser(String friendUserName) {

        String whereClause = "meUserName=? and friendUserName=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername(), friendUserName};
        DBHelper.getInstance().getSQLiteDB().delete(ChatUser.class, whereClause, whereArgs);


        ChatUser chatUser = DBHelper.getInstance().getSQLiteDB().queryOne(ChatUser.class, whereClause, whereArgs);
        if (chatUser!=null){
            Log.e("error","deleteChatUser fail!");
        }
    }


    public static void deleteChatRecord(String chatJid) {

        String whereClause = "meUserName=? and friendUserName=?";
        String[] whereArgs = {LoginHelper.getUser().getUsername(), chatJid};
        DBHelper.getInstance().getSQLiteDB().delete(ChatRecord.class, whereClause, whereArgs);

        ChatRecord chatRecord = DBHelper.getInstance().getSQLiteDB().queryOne(ChatRecord.class, whereClause, whereArgs);
        if (chatRecord!=null){
            Log.e("error","delete ChatRecord fail!");
        }
    }

}

package cn.ittiger.im.smack;

import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.BindIQProvider;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.ittiger.im.activity.MultiChatActivity;
import cn.ittiger.im.message.MessagePacketListener;
import cn.ittiger.im.provider.CreatePacketListener;
import cn.ittiger.im.provider.CreateProvider;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.provider.join.ExitRoom;
import cn.ittiger.im.provider.join.JoinPacketListener;
import cn.ittiger.im.provider.join.JoinRoom;
import cn.ittiger.im.provider.query.QueryPacketListener;
import cn.ittiger.im.provider.query.QueryProvider;
import cn.ittiger.im.provider.query.QueryRoom;
import cn.ittiger.im.provider.roomuser.RoomUser;
import cn.ittiger.im.provider.roomuser.RoomUserProvider;

/**
 * Smack全局监听器管理
 * @author: laohu on 2017/1/18
 * @site: http://ittiger.cn
 */
public class SmackListenerManager {
    private static volatile SmackListenerManager sSmackListenerManager;
    /**
     * 单聊消息监听管理器
     */
    private SmackChatManagerListener mChatManagerListener;

    private  QueryPacketListener mQueryPacketListener;


    /**
     * 创建房间监听
     */
    private CreatePacketListener mCreatePacketListener;


    private JoinPacketListener mJoinPacketListener;


    private MultiChatMessageListener mMultiChatMessageListener;
//    /**
//     * 群聊消息监听
//     */
//    private MultiChatMessageListener mMultiChatMessageListener;
//    /**
//     * 群聊信息
//     */
//    private HashMap<String, MultiUserChat> mMultiUserChatHashMap = new HashMap<>();


    private SmackListenerManager() {

        mChatManagerListener = new SmackChatManagerListener();
        mCreatePacketListener = new CreatePacketListener();
        mJoinPacketListener = new JoinPacketListener();
        mMultiChatMessageListener = new MultiChatMessageListener();
        mQueryPacketListener = new QueryPacketListener();
//        mMultiChatMessageListener = new MultiChatMessageListener();
    }

    public static SmackListenerManager getInstance() {

        if(sSmackListenerManager == null) {
            synchronized (SmackListenerManager.class) {
                if(sSmackListenerManager == null) {
                    sSmackListenerManager = new SmackListenerManager();
                }
            }
        }
        return sSmackListenerManager;
    }

    public static void addGlobalListener() {

        addMessageListener();
//        addInvitationListener();
//        addAllMultiChatMessageListener();
        addMultiChatMessageListener();
        SmackManager.getInstance().getConnection().addPacketInterceptor(getInstance().mJoinPacketListener,new PacketTypeFilter(IQ.class));

    }


    public  void addQueryPacketListener() {
        ProviderManager.addIQProvider(QueryRoom.ELEMENT, QueryRoom.NAMESPACE, new QueryProvider());
        ProviderManager.addIQProvider(RoomUser.ELEMENT, RoomUser.NAMESPACE, new RoomUserProvider());
        if(getInstance().mQueryPacketListener == null){
            getInstance().mQueryPacketListener = new QueryPacketListener();
        }
        SmackManager.getInstance().getConnection().addPacketListener(getInstance().mQueryPacketListener,new PacketTypeFilter(QueryRoom.class));
    }

    public  void removeQueryPacketListener() {
        if(getInstance().mQueryPacketListener!=null){
            SmackManager.getInstance().getConnection().removePacketListener(getInstance().mQueryPacketListener);
            getInstance().mQueryPacketListener = null;
        }
    }

   public  void addCreteRoomListener() {
       ProviderManager.addIQProvider(CreateRoom.ELEMENT, CreateRoom.NAMESPACE, new CreateProvider());
       if(getInstance().mCreatePacketListener == null){
           getInstance().mCreatePacketListener = new CreatePacketListener();
       }
        SmackManager.getInstance().getConnection().addPacketListener(getInstance().mCreatePacketListener,new PacketTypeFilter(CreateRoom.class));
   }

   public  void removeRoomListener() {
       if(getInstance().mCreatePacketListener !=null){
           SmackManager.getInstance().getConnection().removePacketListener(getInstance().mCreatePacketListener);
           getInstance().mCreatePacketListener = null;
       }
    }
    /**
     * 添加单聊消息全局监听
     */
    static void addMessageListener() {

        SmackManager.getInstance().getChatManager().addChatListener(getInstance().mChatManagerListener);
    }

    /**
     * 为所有已存在的群添加消息监听
     */
    static void addAllMultiChatMessageListener() {

        //因Smack+openfire群聊在用户退出登陆后，群聊无法保存已加入的用户信息，所以手动添加该群中的用户
        //SmackMultiChatManager.bindJoinMultiChat();
    }

    /**
     * 为指定群聊添加消息监听
     *
     */
    public static void addMultiChatMessageListener() {

        SmackManager.getInstance().getConnection().addPacketListener(getInstance().mMultiChatMessageListener,new PacketTypeFilter(Message.class));
//        getInstance().mMultiUserChatHashMap.put(multiUserChat.getRoom(), multiUserChat);
//
//        multiUserChat.addMessageListener(getInstance().mMultiChatMessageListener);
    }

//    /**
//     * 添加群聊邀请监听
//     */
//    static void addInvitationListener() {
//
//        SmackManager.getInstance().getMultiUserChatManager().addInvitationListener(getInstance().mInvitationListener);
//    }

    public void destroy() {
        SmackManager.getInstance().getConnection().removePacketListener(getInstance().mJoinPacketListener);
        SmackManager.getInstance().getConnection().removePacketListener(mMultiChatMessageListener);
        SmackManager.getInstance().getChatManager().removeChatListener(mChatManagerListener);
        ProviderManager.removeIQProvider(QueryRoom.ELEMENT, QueryRoom.NAMESPACE);
//        SmackManager.getInstance().getMultiUserChatManager().removeInvitationListener(mInvitationListener);
        removeRoomListener();
//        for(MultiUserChat multiUserChat : mMultiUserChatHashMap.values()) {
//            multiUserChat.removeMessageListener(mMultiChatMessageListener);
//        }
        ProviderManager.removeIQProvider(QueryRoom.ELEMENT,QueryRoom.NAMESPACE);
        ProviderManager.removeIQProvider(CreateRoom.ELEMENT, CreateRoom.NAMESPACE);

        removeQueryPacketListener();
        mJoinPacketListener = null;
        mMultiChatMessageListener = null;
        mCreatePacketListener = null;
        mChatManagerListener = null;
//        mInvitationListener = null;
//        mMultiChatMessageListener = null;
//        mMultiUserChatHashMap.clear();
    }
}

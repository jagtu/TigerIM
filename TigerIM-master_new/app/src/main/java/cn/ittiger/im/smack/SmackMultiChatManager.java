package cn.ittiger.im.smack;

import android.util.Log;

import org.jivesoftware.smack.provider.ProviderManager;

import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.provider.query.QueryProvider;
import cn.ittiger.im.provider.query.QueryRoom;
import cn.ittiger.im.util.DBHelper;

/**
 * 房间管理
 *
 * @author: laohu on 2017/2/3
 * @site: http://ittiger.cn
 */
public class SmackMultiChatManager {


    //房间
    public static void deleteAllRoom() {
        DBHelper.getInstance().getSQLiteDB().deleteAll(RoomBean.class);
    }

    //房间
    public static void saveMultiChat(RoomBean bean) {
        Log.i("======","=====notice======"+bean.getNotice());
        if (bean.isValidate()) {
            String whereClause = "roomJid=?";
            String[] whereArgs = {bean.getRoomJid()};
            if (!DBHelper.getInstance().getSQLiteDB().queryIfExist(RoomBean.class, whereClause, whereArgs)) {
                DBHelper.getInstance().getSQLiteDB().save(bean);
            }
        }
    }

    //房间
    public static void deleteMultiChat(RoomBean bean) {
        if (bean.isValidate()) {
            String whereClause = "roomJid=?";
            String[] whereArgs = {bean.getRoomJid()};
            DBHelper.getInstance().getSQLiteDB().delete(RoomBean.class, whereClause, whereArgs);
        }
    }

    public static void saveRoomUser(RoomUser bean) {
        String whereClause = "jid=? and roomJid=?";
        String[] whereArgs = {bean.getJid(), bean.getRoomJid()};
        if (!DBHelper.getInstance().getSQLiteDB().queryIfExist(RoomUser.class, whereClause, whereArgs)) {
            Log.i("====","====保存==="+bean.getJid());
            DBHelper.getInstance().getSQLiteDB().save(bean);
        }
    }

    public static void deleteAllRoomUser() {
        DBHelper.getInstance().getSQLiteDB().deleteAll(RoomUser.class);
    }

    public static void deleteRoom(String roomJid) {
        String whereClause = "roomJid=?";
        String[] whereArgs = {roomJid};
        DBHelper.getInstance().getSQLiteDB().delete(RoomUser.class, whereClause, whereArgs);
    }


    public static void deleteRoomUser(RoomUser bean) {
        String whereClause = "jid=? and roomJid=?";
        String[] whereArgs = {bean.getJid(), bean.getRoomJid()};
        DBHelper.getInstance().getSQLiteDB().delete(RoomUser.class, whereClause, whereArgs);
    }

    public static void bindJoinMultiChat() {
        SmackListenerManager.getInstance().addQueryPacketListener();
        try {
            SmackManager.getInstance().queryRoom();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("====", "====失败" + e.toString());
        }
//        Observable.create(new Observable.OnSubscribe<List<RoomBean>>() {
//            @Override
//            public void call(Subscriber<? super List<RoomBean>> subscriber) {
//                List<RoomBean> roomBeans = DBQueryHelper.queryRoom();
//                if (roomBeans == null) {
//                    return;
//                }
//
//                for (RoomBean roomBean : roomBeans) {
//                    MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(SmackManager.getInstance().getConnection()).getMultiUserChat(roomBean.getRoomJid() + "@" + SmackManager.SERVER_NAME);
//                    SmackListenerManager.addMultiChatMessageListener(multiUserChat);
//                }
//                subscriber.onNext(roomBeans);
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<RoomBean>>() {
//                               @Override
//                               public void call(List<RoomBean> roomBeans) {
//
//                               }
//                           }
//                );
        //获取所有的群列表，并开启消息检测
//        try {
//            SmackManager.getInstance().queryRoom();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //查询之后+检测
//        MultiUserChat multiUserChat = SmackManager.getInstance().getMultiChat(room.getRoomJid());
//        multiUserChat.join(LoginHelper.getUser().getNickname());
//        SmackListenerManager.addMultiChatMessageListener(multiUserChat);
    }
}

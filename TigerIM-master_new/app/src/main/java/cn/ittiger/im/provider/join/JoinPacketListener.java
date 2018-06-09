package cn.ittiger.im.provider.join;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.util.XmlPullUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ittiger.im.bean.MultiAddMessage;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.LoginHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :加入房间的信息
 * </pre>
 */

public class JoinPacketListener implements PacketListener {

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if (packet instanceof JoinRoom) {
            //分发下去加入群聊成功
            final RoomBean room = new RoomBean();
            try {
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = xmlPullParserFactory.newPullParser();
                parser.setInput(new StringReader(packet.toXML().toString()));
                boolean done = false;
                int eventType = parser.getEventType();
                while (!done) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("iq")) {
                            String jid = parser.getAttributeValue("", "to");//id
                            jid = jid.substring(0, jid.lastIndexOf("@"));
                            Log.i("====", "==jid==" + jid);
                            room.setRoomJid(jid);
                            //解析来处理发送
                        }
                        if (parser.getName().equals("item")) {
                            String jid = parser.getAttributeValue("", "jid");//id
                            String name = parser.getAttributeValue("", "name");//name
                            //解析来处理发送
                            if(jid.contains("@")){
                                jid = jid.substring(0, jid.indexOf("@"));
                            }
                            RoomUser roomUser = new RoomUser(room.getRoomJid(), jid, name);
                            SmackMultiChatManager.saveRoomUser(roomUser);
                        }
                    } else if (eventType == XmlPullParser.END_DOCUMENT) {
                        done = true;
                    }
                    eventType = parser.next();
                }
                EventBus.getDefault().post(room);  //room
//                Observable.create(new Observable.OnSubscribe<List<RoomBean>>() {
//                    @Override
//                    public void call(Subscriber<? super List<RoomBean>> subscriber) {
//                        MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(SmackManager.getInstance().getConnection()).getMultiUserChat(room.getRoomJid() + "@" + SmackManager.SERVER_NAME);
//                        SmackListenerManager.addMultiChatMessageListener(multiUserChat);
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    }
//                })
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<List<RoomBean>>() {
//                                       @Override
//                                       public void call(List<RoomBean> roomBeans) {
//
//                                       }
//                                   }
//                        );
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (packet instanceof ExitRoom) {
            //分发下去加入群聊成功
            final RoomBean room = new RoomBean();
            try {
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = xmlPullParserFactory.newPullParser();
                parser.setInput(new StringReader(packet.toXML().toString()));
                boolean done = false;
                int eventType = parser.getEventType();
                while (!done) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("iq")) {
                            String jid = parser.getAttributeValue("", "to");//id
                            jid = jid.substring(0, jid.lastIndexOf("@"));
                            Log.i("====", "==jid==" + jid);
                            room.setRoomJid(jid);
                            //解析来处理发送
                        }
                        if (parser.getName().equals("item")) {
                            String jid = parser.getAttributeValue("", "jid");//id
                            String name = parser.getAttributeValue("", "name");//name
                            //解析来处理发送
                            if(jid.contains("@")){
                                jid = jid.substring(0, jid.indexOf("@"));
                            }
                            SmackMultiChatManager.deleteRoomUser(new RoomUser(room.getRoomJid(), jid, name));
                        }
                    } else if (eventType == XmlPullParser.END_DOCUMENT) {
                        done = true;
                    }
                    eventType = parser.next();
                }
                EventBus.getDefault().post(room);  //room
//                Observable.create(new Observable.OnSubscribe<List<RoomBean>>() {
//                    @Override
//                    public void call(Subscriber<? super List<RoomBean>> subscriber) {
//                        MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(SmackManager.getInstance().getConnection()).getMultiUserChat(room.getRoomJid() + "@" + SmackManager.SERVER_NAME);
//                        SmackListenerManager.addMultiChatMessageListener(multiUserChat);
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    }
//                })
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<List<RoomBean>>() {
//                                       @Override
//                                       public void call(List<RoomBean> roomBeans) {
//
//                                       }
//                                   }
//                        );
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

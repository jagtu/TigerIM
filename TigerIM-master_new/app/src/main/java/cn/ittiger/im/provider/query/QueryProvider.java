package cn.ittiger.im.provider.query;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.smack.SmackListenerManager;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.DBHelper;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class QueryProvider extends IQProvider<QueryRoom> {
    @Override
    public QueryRoom parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        RoomBean roomBean = null;
        SmackMultiChatManager.deleteAllRoom();
        SmackMultiChatManager.deleteAllRoomUser();
        try {
            boolean done = false;
            int eventType = parser.getEventType();
            while (!done) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("group")) {
                        String id = parser.getAttributeValue("", "jid");//id
                        String name = parser.getAttributeValue("", "name");//name
                        String owner = parser.getAttributeValue("", "owner");
                        String notice = parser.getAttributeValue("", "notice");
                        String roomimg = parser.getAttributeValue("", "roomimg");
                        //解析来处理发送
                        roomBean = new RoomBean(id, name,owner,notice,roomimg);
                        SmackMultiChatManager.saveMultiChat(roomBean);
                        EventBus.getDefault().post(roomBean);  //room
                    }
                    if (parser.getName().equals("item")) {
                        String jid = parser.getAttributeValue("", "jid");//id
                        String realName = parser.getAttributeValue("", "realName");//name
                        String icon = parser.getAttributeValue("", "icon");
                        RoomUser roomUser = new RoomUser(roomBean.getRoomJid(), jid, realName);
                        roomUser.setIcon(icon);
                       SmackMultiChatManager.saveRoomUser(roomUser);
                    }
                }
                Log.i("====","==="+parser.getName()+"===="+eventType);
                if(eventType == XmlPullParser.END_TAG && parser.getName().equals("iq")){
                    break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.i("====","===错误="+e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("====","===错误111="+e.toString());

        }
        return new QueryRoom();
    }
}

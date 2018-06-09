package cn.ittiger.im.provider.roomuser;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.provider.query.QueryRoom;
import cn.ittiger.im.smack.SmackMultiChatManager;


/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class RoomUserProvider extends IQProvider<RoomUser> {
    @Override
    public RoomUser parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        try {
            boolean done = false;
            int eventType = parser.getEventType();

            while (!done) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("item")) {
                        String jid = parser.getAttributeValue("", "jid");//id
                        String name = parser.getAttributeValue("", "realName");//name
                        String icon = parser.getAttributeValue("", "icon");//name
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
        return new RoomUser();
    }
}

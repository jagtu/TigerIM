package cn.ittiger.im.provider;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.ittiger.im.smack.SmackListenerManager;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class CreateProvider extends IQProvider<CreateRoom> {
    @Override
    public CreateRoom parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        CreateRoom room = new CreateRoom();
        boolean done = false;
        Log.i("====","====创建走了这里");
        int eventType = parser.next();
        while (!done){
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    String id = parser.getAttributeValue("", "groupid");//id
                    String name = parser.getAttributeValue("","groupname");//name
                    room.setGroupname(name);
                    room.setGroupid(id);
                    EventBus.getDefault().post(room);
                }
            }else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
            parser.next();
        }
        return room;
    }
}

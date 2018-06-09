package cn.ittiger.im.provider.query;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.bean.RoomUser;
import cn.ittiger.im.provider.CreateRoom;
import cn.ittiger.im.smack.SmackMultiChatManager;
import cn.ittiger.im.util.DBHelper;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class QueryPacketListener implements PacketListener {
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if(packet instanceof QueryRoom){
            Log.i("=====","===query==="+packet.toXML());
        }
    }
}

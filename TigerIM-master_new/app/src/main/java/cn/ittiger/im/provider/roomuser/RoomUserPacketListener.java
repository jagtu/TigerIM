package cn.ittiger.im.provider.roomuser;

import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

import cn.ittiger.im.provider.query.QueryRoom;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class RoomUserPacketListener implements PacketListener {
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if(packet instanceof RoomUser){
            Log.i("=====","===query==="+packet.toXML());
        }
    }
}

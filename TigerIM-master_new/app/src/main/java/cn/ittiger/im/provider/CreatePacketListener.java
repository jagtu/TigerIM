package cn.ittiger.im.provider;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class CreatePacketListener implements PacketListener {
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

        if(packet instanceof CreateRoom){
            Log.i("====","==createRoom==走了这里");
        }
    }
}

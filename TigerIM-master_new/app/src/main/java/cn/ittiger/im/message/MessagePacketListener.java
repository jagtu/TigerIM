package cn.ittiger.im.message;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import cn.ittiger.im.provider.CreateRoom;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class MessagePacketListener implements PacketListener {
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        Log.i("=====","======发送消息过来了==");
        if(packet instanceof Message){

        }
    }
}

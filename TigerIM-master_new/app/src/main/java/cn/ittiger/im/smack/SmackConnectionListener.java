package cn.ittiger.im.smack;

import android.util.Log;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * 服务器连接监听
 * @author: laohu on 2017/1/18
 * @site: http://ittiger.cn
 */
public class SmackConnectionListener implements ConnectionListener {

    @Override
    public void connected(XMPPConnection connection) {

        Log.i("=====","==========connection connected");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

        Log.i("=====","==========authenticated");

    }

    @Override
    public void connectionClosed() {

        Log.i("=====","==========connection close");
    }

    @Override
    public void connectionClosedOnError(Exception e) {

        Log.i("=====","==========connection closeOnErr="+e.toString());
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.i("=====","==========connection reconnectingIn="+seconds+"==");
    }

    @Override
    public void reconnectionFailed(Exception e) {

        Log.i("=====","==========reconnectionFailed"+"==");
    }

    @Override
    public void reconnectionSuccessful() {

        Log.i("=====","==========reconnectionSuccessful"+"==");

    }
}

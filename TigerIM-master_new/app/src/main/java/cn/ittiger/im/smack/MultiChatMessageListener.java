package cn.ittiger.im.smack;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.bean.User;
import cn.ittiger.im.constant.MessageType;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.LoginHelper;

/**
 * 多人聊天消息监听
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class MultiChatMessageListener implements PacketListener {

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if(packet instanceof Message){
            Message message = (Message) packet;
            if(message.getType() == Message.Type.groupchat){
                ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), true);
                String roomJid = message.getFrom().substring(0,message.getFrom().lastIndexOf("@"));
                String fromUserNam = message.getFrom().substring(message.getFrom().lastIndexOf("/")+1);
                chatMessage.setRoomJid(roomJid);
                chatMessage.setMulti(true);
                chatMessage.setMeSend(false);
                chatMessage.setFriendNickname(fromUserNam);
                chatMessage.setFriendUsername(fromUserNam);
                User user = LoginHelper.getUser();
                chatMessage.setMeUsername(user.getUsername());
                chatMessage.setMeNickname(user.getNickname());
                chatMessage.setRoomJid(roomJid);
                String body = message.getBody();
                try {
                    JSONObject json = new JSONObject(body);
//                    String type = jsonObject.optString("type");
                    String msg = json.optString("data");
                    chatMessage.setMessageType(MessageType.MESSAGE_TYPE_TEXT.value());
                    chatMessage.setContent(msg);
                    try {
                        String type = json.getString("type");
                        if ("audio".equals(type) || "voice".equals(type)) {
                            chatMessage.setMessageType(MessageType.MESSAGE_TYPE_VOICE.value());
                            chatMessage.setFilePath(json.optString("audioUrl"));
                            chatMessage.setContent(json.getString("duration"));
                        } else if ("image".equals(type)) {
                            chatMessage.setMessageType(MessageType.MESSAGE_TYPE_IMAGE.value());
                            chatMessage.setFilePath(json.getString("imageUrl"));
                        }
                    } catch (Exception e) {

                    }

                    DBHelper.getInstance().getSQLiteDB().save(chatMessage);
                    EventBus.getDefault().post(chatMessage);
                    //查询新消息
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

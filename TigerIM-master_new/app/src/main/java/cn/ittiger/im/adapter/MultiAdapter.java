package cn.ittiger.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.GroupMessageActivity;
import cn.ittiger.im.activity.ShowImageActivity;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.bean.ContactEntity;
import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.constant.EmotionType;
import cn.ittiger.im.constant.FileLoadState;
import cn.ittiger.im.constant.MessageType;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.ui.recyclerview.HeaderAndFooterAdapter;
import cn.ittiger.im.ui.recyclerview.ViewHolder;
import cn.ittiger.im.util.ChatTimeUtil;
import cn.ittiger.im.util.EmotionUtil;
import cn.ittiger.im.util.IMUtil;
import cn.ittiger.im.util.LruUtils;
import cn.ittiger.util.PreferenceHelper;

import static cn.ittiger.im.smack.SmackManager.SERVER_IP;

/**
 * 消息列表数据适配器
 *
 * @author: laohu on 2017/1/17
 * @site: http://ittiger.cn
 */
public class MultiAdapter extends HeaderAndFooterAdapter<ChatMessage> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_RECEIVER = 0;
    private Context mContext;
    /**
     * 音频播放器
     */
    private MediaPlayer mediaPlayer;

    public MultiAdapter(Context context, List<ChatMessage> list) {

        super(list);
        mContext = context;
    }


    @Override
    public int getItemViewTypeForData(int position) {

        return getItem(position).isMeSend() ? VIEW_TYPE_ME : VIEW_TYPE_RECEIVER;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == VIEW_TYPE_ME) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_messgae_item_right_layout, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_messgae_item_left_layout, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final ViewHolder holder, int position, final ChatMessage message) {
        final ChatViewHolder viewHolder = (ChatViewHolder) holder;
        viewHolder.chatNickname.setVisibility(View.VISIBLE);

        String prefix = PreferenceHelper.getString("member");

        if (message.isMeSend()) {
            String chatNickname = message.getMeNickname();
            if (chatNickname!=null && chatNickname.startsWith(prefix)){
                chatNickname = chatNickname.substring(prefix.length());
            }
            viewHolder.chatNickname.setText(chatNickname);
            //by jagtu
            viewHolder.chatUserAvatar.setTag(message.getMeNickname());
            Bitmap bitmap = LruUtils.getInstance().getMemoryCache().get(message.getMeUsername());
            if (bitmap == null) {
                viewHolder.chatUserAvatar.setImageResource(R.mipmap.icon_my_head);
            } else {
                viewHolder.chatUserAvatar.setImageBitmap(bitmap);
            }
        } else {
            String chatNickname = message.getFriendNickname();
            if (chatNickname!=null && chatNickname.startsWith(prefix)){
                chatNickname = chatNickname.substring(prefix.length());
            }
            viewHolder.chatNickname.setText(chatNickname);
            //by jagtu
            viewHolder.chatUserAvatar.setTag(message.getFriendNickname());
            Bitmap bitmap = LruUtils.getInstance().getMemoryCache().get(message.getFriendUsername());
            if (bitmap == null) {
                viewHolder.chatUserAvatar.setImageResource(R.mipmap.icon_my_head);
            } else {
                viewHolder.chatUserAvatar.setImageBitmap(bitmap);
            }
        }
        viewHolder.chatContentTime.setText(ChatTimeUtil.getFriendlyTimeSpanByNow(message.getDatetime()));
        setMessageViewVisible(message.getMessageType(), viewHolder);

        if (message.getMessageType() == MessageType.MESSAGE_TYPE_TEXT.value()) {//文本消息
            //处理表情
            SpannableString content = EmotionUtil.getEmotionContent(mContext, EmotionType.EMOTION_TYPE_CLASSIC, message.getContent());
            viewHolder.chatContentText.setText(content);
        } else if (message.getMessageType() == MessageType.MESSAGE_TYPE_IMAGE.value()) {//图片消息
            Glide.with(((ChatViewHolder) holder).itemView.getContext())
                    .load(message.getFilePath())
                    .into(viewHolder.chatContentImage);
            viewHolder.chatContentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowImageActivity.start(((ChatViewHolder) holder).itemView.getContext(), message.getFilePath());
                }
            });
//            String url = "file://" + message.getFilePath();
//            ImageLoaderHelper.displayImage(viewHolder.chatContentImage, url);
//            showLoading(viewHolder, message);
        } else if (message.getMessageType() == MessageType.MESSAGE_TYPE_VOICE.value()) {//语音消息
            viewHolder.chatContentVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVoice(viewHolder.chatContentVoice, message);
                }
            });
            viewHolder.chatContentText.setText(message.getContent());
//            showLoading(viewHolder, message);
        }

        //by jagtu 设置群聊天-头像点击事件
        /*
        viewHolder.chatUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                Log.e("lbb", "点击了头像"+tag);
                if (tag!=null && tag.length() > 0){
                    Boolean isHave = false;
                    Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                    if(friends!=null){
                        for (RosterEntry friend : friends) {
                            String userName = friend.getName();
                            if (tag.contains(userName)){
                                isHave = true;
                                break;
                            }
                        }
                    }
                    if (isHave) {
                        String userJid = tag.indexOf("@") == -1 ? (tag + "@" + SERVER_IP) : tag;
                        IMUtil.startChatActivity(mContext, userJid, tag);
                    }else {
                        Toast.makeText(mContext,"非好友无法聊天",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        */

    }

    /**
     * 根据消息类型显示对应的消息展示控件
     *
     * @param messageType
     * @param viewHolder
     */
    private void setMessageViewVisible(int messageType, ChatViewHolder viewHolder) {

        if (messageType == MessageType.MESSAGE_TYPE_TEXT.value()) {//文本消息
            viewHolder.chatContentText.setVisibility(View.VISIBLE);
            viewHolder.chatContentImage.setVisibility(View.GONE);
            viewHolder.chatContentVoice.setVisibility(View.GONE);
        } else if (messageType == MessageType.MESSAGE_TYPE_IMAGE.value()) {//图片消息
            viewHolder.chatContentText.setVisibility(View.GONE);
            viewHolder.chatContentImage.setVisibility(View.VISIBLE);
            viewHolder.chatContentVoice.setVisibility(View.GONE);
        } else if (messageType == MessageType.MESSAGE_TYPE_VOICE.value()) {//语音消息
            viewHolder.chatContentText.setVisibility(View.VISIBLE);
            viewHolder.chatContentImage.setVisibility(View.GONE);
            viewHolder.chatContentVoice.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(ChatViewHolder viewHolder, ChatMessage message) {

        if (message.getFileLoadState() == FileLoadState.STATE_LOAD_START.value()) {//加载开始
            viewHolder.chatContentLoading.setBackgroundResource(R.drawable.chat_file_content_loading_anim);
            final AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.chatContentLoading.getBackground();
            viewHolder.chatContentLoading.post(new Runnable() {
                @Override
                public void run() {

                    animationDrawable.start();
                }
            });
            viewHolder.chatContentLoading.setVisibility(View.VISIBLE);
        } else if (message.getFileLoadState() == FileLoadState.STATE_LOAD_SUCCESS.value()) {//加载完成
            viewHolder.chatContentLoading.setVisibility(View.GONE);
        } else if (message.getFileLoadState() == FileLoadState.STATE_LOAD_ERROR.value()) {
            viewHolder.chatContentLoading.setBackgroundResource(R.drawable.file_load_fail);
        }
    }

    /**
     * 播放语音信息
     *
     * @param iv
     * @param message
     */
    private void playVoice(final ImageView iv, final ChatMessage message) {

        if (message.isMeSend()) {
            iv.setBackgroundResource(R.drawable.anim_chat_voice_right);
        } else {
            iv.setBackgroundResource(R.drawable.anim_chat_voice_left);
        }
        final AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
        iv.post(new Runnable() {
            @Override
            public void run() {

                animationDrawable.start();
            }
        });
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {//点击播放，再次点击停止播放
            // 开始播放录音
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    animationDrawable.stop();
                    // 恢复语音消息图标背景
                    if (message.isMeSend()) {
                        iv.setBackgroundResource(R.drawable.gxu);
                    } else {
                        iv.setBackgroundResource(R.drawable.gxx);
                    }
                }
            });
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(message.getFilePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            animationDrawable.stop();
            // 恢复语音消息图标背景
            if (message.isMeSend()) {
                iv.setBackgroundResource(R.drawable.gxu);
            } else {
                iv.setBackgroundResource(R.drawable.gxx);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    public class ChatViewHolder extends ViewHolder {
        @BindView(R.id.tv_chat_msg_username)
        public TextView chatNickname;//消息来源人昵称
        @BindView(R.id.tv_chat_msg_time)
        public TextView chatContentTime;//消息时间
        @BindView(R.id.iv_chat_avatar)
        public ImageView chatUserAvatar;//用户头像
        @BindView(R.id.tv_chat_msg_content_text)
        public TextView chatContentText;//文本消息
        @BindView(R.id.iv_chat_msg_content_image)
        public ImageView chatContentImage;//图片消息
        @BindView(R.id.iv_chat_msg_content_voice)
        public ImageView chatContentVoice;//语音消息
        @BindView(R.id.iv_chat_msg_content_loading)
        public ImageView chatContentLoading;//发送接收文件时的进度条

        public ChatViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

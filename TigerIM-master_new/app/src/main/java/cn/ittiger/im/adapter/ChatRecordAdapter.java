package cn.ittiger.im.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.List;

import cn.ittiger.im.R;
import cn.ittiger.im.adapter.viewholder.ChatRecordViewHolder;
import cn.ittiger.im.bean.ChatRecord;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.constant.EmotionType;
import cn.ittiger.im.ui.recyclerview.HeaderAndFooterAdapter;
import cn.ittiger.im.ui.recyclerview.ViewHolder;
import cn.ittiger.im.util.ChatTimeUtil;
import cn.ittiger.im.util.EmotionUtil;
import cn.ittiger.im.util.ImageLoaderHelper;
import cn.ittiger.util.PreferenceHelper;
import cn.ittiger.util.ValueUtil;

/**
 * 聊天记录列表适配器
 * @author: laohu on 2017/1/22
 * @site: http://ittiger.cn
 */
public class ChatRecordAdapter extends HeaderAndFooterAdapter<ChatRecord> {
    private Context mContext;
    private OnItemClick mOnItemClick;
    private float mTouch;
    private float mStartX;
    private float mStartY;
    private boolean mState = false;
    public ChatRecordAdapter(Context context, List<ChatRecord> list) {

        super(list);
        this.mContext = context;
        mTouch =  ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public void nameUpdate(RoomBean roomBean){
        if(roomBean.isValidate()){
            for(int i= 0;i<mList.size();i++){
                ChatRecord chatRecord = mList.get(i);
                if(chatRecord.isMulti() && chatRecord.getChatJid().equals(roomBean.getRoomJid())){
                    String nick = roomBean.getName();
                    if(nick.contains("@")){
                        nick.substring(0,nick.indexOf("@"));
                    }
                    chatRecord.setFriendNickname(roomBean.getName());
                    mList.remove(chatRecord);
                    mList.add(i,chatRecord);
                    notifyItemChanged(i,chatRecord);
                }
            }
        }
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.mOnItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_record_item_layout, parent, false);
        return new ChatRecordViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, final int position, final ChatRecord item) {

        final ChatRecordViewHolder viewHolder = (ChatRecordViewHolder) holder;
        if(viewHolder.mRoot.getTranslationX() != 0){
            viewHolder.mRoot.setTranslationX(0);
        }
        if(item.isMulti()){
            viewHolder.avatar.setImageResource(R.drawable.multi_icon);
        }else {
//            ImageLoaderHelper.displayImage(viewHolder.avatar, item.getFriendAvatar());
            viewHolder.avatar.setImageResource(R.mipmap.icon_my_head);
            ImageLoaderHelper.loadImg(viewHolder.avatar, item.getFriendNickname());
        }
        String nick = item.getFriendNickname();
        if(TextUtils.isEmpty(nick)){
            nick = item.getFriendUsername();
        }
        Log.i("====","===="+nick);
        if(nick.contains("@")){
           nick =  nick.substring(0,nick.indexOf("@"));
        }
        if(nick.contains("#")){
            nick = nick.substring(nick.lastIndexOf("#")+1,nick.length());
        }
        Log.i("====","===="+nick);
        String prefix = PreferenceHelper.getString("member");

        try {
            if (nick.startsWith(prefix)) {
                String username = nick.substring(prefix.length(), nick.length());
                viewHolder.nickName.setText(username);
            } else {
                viewHolder.nickName.setText(nick);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!ValueUtil.isEmpty(item.getLastMessage())) {
            if(viewHolder.message.getVisibility() == View.GONE) {
                viewHolder.message.setVisibility(View.VISIBLE);
            }
            SpannableString content = EmotionUtil.getInputEmotionContent(mContext, EmotionType.EMOTION_TYPE_CLASSIC, viewHolder.message, item.getLastMessage());
            viewHolder.message.setText(content);
        }
        viewHolder.chatTime.setText(ChatTimeUtil.getFriendlyTimeSpanByNow(item.getChatTime()));
        if(item.getUnReadMessageCount() == 0){
            viewHolder.messageCount.setVisibility(View.GONE);
        }else {
            String messageCount = item.getUnReadMessageCount() < 99 ? String.valueOf(item.getUnReadMessageCount()) : "...";
            viewHolder.messageCount.setText(messageCount);
            viewHolder.messageCount.setVisibility(View.VISIBLE);
        }
        viewHolder.mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mStartX = motionEvent.getX();
                        mStartY = motionEvent.getY();
                        mState = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currX = motionEvent.getX() - mStartX;
                        float currY = motionEvent.getY() - mStartY;

                        if( Math.abs(currX) > Math.abs(currY) &&  Math.abs(currX) > mTouch){
                            mState = true;
                            if(currX < 0){
                                viewHolder.mRoot.animate().translationX(-viewHolder.mDelete.getMeasuredWidth())
                                        .start();
                            }else {
                                viewHolder.mRoot.animate().translationX(0)
                                        .start();
                            }
                        }else {
                           mState = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!mState){
                            if(mOnItemClick!=null){
                                mOnItemClick.onClick(item);
                            }
                        }
                        mState = false;
                        break;
                }
                return true;
            }
        });
        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.mRoot.getTranslationX() != 0){
                    viewHolder.mRoot.setTranslationX(0);
                }
                if(mOnItemClick!=null){
                    mOnItemClick.onRemove(position);
                }
            }
        });
    }

    public void update(int position){
        if(mList!=null){
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClick{
        void onClick(ChatRecord chatRecord);
        void onRemove(int position);
    }
}

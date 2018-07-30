package cn.ittiger.im.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ittiger.im.R;
import cn.ittiger.im.activity.GroupMessageActivity;
import cn.ittiger.im.activity.MultiChatActivity;
import cn.ittiger.im.activity.MultiListActivity;
import cn.ittiger.im.activity.ShowImageActivity;
import cn.ittiger.im.activity.base.BaseMultiActivity;
import cn.ittiger.im.bean.RoomBean;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.DBHelper;
import cn.ittiger.im.util.LoginHelper;
import cn.ittiger.im.util.LruUtils;
import cn.ittiger.im.util.StringUtils;
import cn.ittiger.im.util.SystemUtils;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/27
 *      desc    :
 * </pre>
 */

public class MultiContactAdapter extends  RecyclerView.Adapter<MultiContactAdapter.MyHolder>{

    private List<RoomBean> mRoomBeans;
    private Context mContext;

    public MultiContactAdapter(Context mContext) {
        this.mContext = mContext;
        mRoomBeans = new ArrayList<>();
    }

    public MultiContactAdapter(List<RoomBean> roomBeans,Context mContext) {
        this.mRoomBeans = roomBeans;
        this.mContext = mContext;
    }

    public void addData(List<RoomBean> roomBeans) {
//        if(mRoomBeans == null || mRoomBeans.size()==0){
//            return;
//        }
        if(mRoomBeans == null){
            mRoomBeans = new ArrayList<>();
        }else {
            mRoomBeans.clear();
        }
        ok:for (RoomBean roomBean: roomBeans) {
            if(roomBean.isValidate() && !mRoomBeans.contains(roomBean)){
                for(RoomBean room:mRoomBeans){
                    if(room.getRoomJid().equals(roomBean.getRoomJid())){
                        continue ok;
                    }
                }
                mRoomBeans.add(roomBean);
            }
        }
        notifyDataSetChanged();
    }

    public void addData(RoomBean roomBean) {
        if(roomBean.isValidate()){
            if(mRoomBeans == null){
                mRoomBeans = new ArrayList<>();
            }
            Boolean shouldAdd = true;
            for(RoomBean room:mRoomBeans){
                if(room.getRoomJid().equals(roomBean.getRoomJid())){
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                mRoomBeans.add(0, roomBean);
                notifyDataSetChanged();
            }
        }
    }

    public void removeData(RoomBean roomBean){
        if(roomBean.isValidate()){
            if(mRoomBeans != null){
                for(RoomBean room:mRoomBeans){
                    if(room.getRoomJid().equals(roomBean.getRoomJid())){
                        mRoomBeans.remove(roomBean);
                        notifyDataSetChanged();
                        DBHelper.getInstance().getSQLiteDB().delete(roomBean);


                        if (!roomBean.getOwner().equals(LoginHelper.getUser().getUsername())) {
                            //非群主 退出群组

                            try {
                                Map<String, String> roomUser = new ArrayMap<>();
                                String userName = LoginHelper.getUser().getUsername();
                                roomUser.put(userName, userName);
                                SmackManager.getInstance().exitRoom(roomUser, roomBean.getRoomJid(), userName);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            //群主
                            //删除并退出群
                            Map<String, String> map = new HashMap<>();
                            map.put("groupname", roomBean.getRoomJid());
                            try {
                                SmackManager.getInstance().deleteRoom(map);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    }
                }

            }

        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.multi_item_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final RoomBean roomBean = mRoomBeans.get(position);

        holder.mImageView.setImageResource(R.drawable.multi_icon);
        //by jagtu 设置群头像
        if (!StringUtils.isNullOrEmpty(roomBean.getRoomimg())){
            byte[] bytes = Base64.decode(roomBean.getRoomimg(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap!=null) {
                bitmap = SystemUtils.createCircleImage(bitmap);
                holder.mImageView.setImageBitmap(bitmap);
            }
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String tag = (String) v.getTag();
            Log.e("lbb", "点击了头像"+tag);
            GroupMessageActivity.goActivity(mContext,roomBean);
            }
        });

        holder.mTextView.setText(roomBean.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiChatActivity.goActivity(mContext,roomBean);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setItems(new String[]{"删除并退出"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeData(roomBean);
                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRoomBeans.size();
    }

    public static class  MyHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;

        public MyHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.contact_avatar);
            mTextView = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }
}

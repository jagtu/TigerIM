package cn.ittiger.im.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.AddGroupMemberActivity;
import cn.ittiger.im.activity.GroupMessageActivity;
import cn.ittiger.im.activity.ShowImageActivity;
import cn.ittiger.im.util.LoginHelper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 联系人列表Item项ViewHolder
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contact_avatar)
    ImageView avatar;
    @BindView(R.id.contact_name)
    TextView name;

    public ContactViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public ImageView getImageView() {

        return avatar;
    }

    public TextView getTextView() {

        return name;
    }
}

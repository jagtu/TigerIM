package cn.ittiger.im.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.ui.CircleImageView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class CreateMultiChatViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contact_check)
    ImageView checkImageView;
    @BindView(R.id.contact_avatar)
    CircleImageView avatar;
    @BindView(R.id.contact_name)
    TextView name;

    public CreateMultiChatViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ImageView getCheckImageView() {

        return checkImageView;
    }

    public CircleImageView getImageView() {

        return avatar;
    }

    public TextView getTextView() {

        return name;
    }
}

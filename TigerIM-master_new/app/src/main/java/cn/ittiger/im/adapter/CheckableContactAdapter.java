package cn.ittiger.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.ittiger.im.R;
import cn.ittiger.im.adapter.viewholder.ContactIndexViewHolder;
import cn.ittiger.im.adapter.viewholder.CreateMultiChatViewHolder;
import cn.ittiger.im.app.App;
import cn.ittiger.im.bean.CheckableContactEntity;
import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.util.ImageLoaderHelper;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.util.PreferenceHelper;

/**
 * 可选联系人列表适配器
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class CheckableContactAdapter extends IndexStickyViewAdapter<CheckableContactEntity> {
    private Context mContext;

    public CheckableContactAdapter(Context context, List<CheckableContactEntity> originalList) {

        super(originalList);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_index_view, parent, false);
        return new ContactIndexViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.checkable_contact_item_view, parent, false);
        return new CreateMultiChatViewHolder(view);
    }

    @Override
    public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

        ContactIndexViewHolder viewHolder = (ContactIndexViewHolder) holder;
        viewHolder.getTextView().setText(indexName);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, CheckableContactEntity itemData) {

        CreateMultiChatViewHolder viewHolder = (CreateMultiChatViewHolder) holder;
        if(itemData.isChecked()) {
            viewHolder.getCheckImageView().setImageResource(R.drawable.vector_checked);
        } else {
            viewHolder.getCheckImageView().setImageResource(R.drawable.vector_uncheck);
        }
        viewHolder.getImageView().setImageResource(R.mipmap.icon_my_head);

        String name = itemData.getRosterEntry().getName();
        MemberBean bean = App.getInstance().mMemberBean;
        if (bean != null) {
            if (name.startsWith(bean.getPrefix())) {
                name = name.substring(bean.getPrefix().length());
            }
        }

        ImageLoaderHelper.loadCornerImg(viewHolder.getImageView(), name);

        viewHolder.getTextView().setText(name);
    }
}

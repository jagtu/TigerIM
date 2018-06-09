package cn.ittiger.im.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.ittiger.im.R;

public class ListViewDialog extends Dialog {

    private Context mContext;
    private ListView mListView;
    private ArrayAdapter<String> stringArrayAdapter;

    public ListViewDialog(Context context) {
        super(context);
        mContext = context;
        initView();
        initListView();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.content_dialog, null);
        mListView = (ListView) contentView.findViewById(R.id.lv);
        setContentView(contentView);
    }

    private void initListView() {
        stringArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_expandable_list_item_1);
        mListView.setAdapter(stringArrayAdapter);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public ArrayAdapter<String> getStringArrayAdapter() {
        return stringArrayAdapter;
    }

    public void setStringArrayAdapter(ArrayAdapter<String> stringArrayAdapter) {
        this.stringArrayAdapter = stringArrayAdapter;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        setHeight();
    }

    private void setHeight() {
        Window window = getWindow();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (window.getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.6)) {
            attributes.height = (int) (displayMetrics.heightPixels * 0.6);
        }
        window.setAttributes(attributes);
    }

    public interface OnItemClickListener{
        void onItemClick();
    }
}

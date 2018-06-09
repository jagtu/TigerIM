package cn.ittiger.im.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.base.BaseActivity;
import cn.ittiger.im.R;

/**
 * Created by Administrator on 2018\3\24 0024.
 */

public class SelectPicPopupWindow extends PopupWindow {
    TextView tvPopTickphoto;
    TextView tvPopAlbum;
    TextView tvPopCancel;
    LinearLayout popLayout;
    private View view;

    public SelectPicPopupWindow(Context mContext, View.OnClickListener itemsOnClick) {
       view = LayoutInflater.from(mContext).inflate(R.layout.layout_popwindow,null);
        tvPopTickphoto = (TextView) view.findViewById(R.id.tv_pop_tickphoto);
        tvPopAlbum = (TextView) view.findViewById(R.id.tv_pop_album);
        tvPopCancel = (TextView) view.findViewById(R.id.tv_pop_cancel);
        popLayout = (LinearLayout) view.findViewById(R.id.pop_layout);
        // 取消按钮
        tvPopCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置按钮监听
        tvPopTickphoto.setOnClickListener(itemsOnClick);
        tvPopAlbum.setOnClickListener(itemsOnClick);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener()

        {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置弹出窗体的背景
//        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);

    }
}

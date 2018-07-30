package cn.ittiger.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.im.util.LruUtils;
import cn.ittiger.im.util.StringUtils;

import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_START;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2018/5/4 10:25.<br/>
 */
public class ShowImageActivity extends AppCompatActivity {

    public static void start(Context activity, String url) {
        Intent intent = new Intent(activity, ShowImageActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    public static void startByUserName(Context activity, String userName) {
        Intent intent = new Intent(activity, ShowImageActivity.class);
        intent.putExtra("userName", userName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoView imageView = new PhotoView(this);
        imageView.enable();
        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageActivity.this.finish();
            }
        });;
        setContentView(imageView);

        String url = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("userName");
        if (StringUtils.isNullOrEmpty(url) && !StringUtils.isNullOrEmpty(name)) {
            Bitmap bitmap = LruUtils.getInstance().getMemoryCache().get(name);
            if (bitmap == null) {
                bitmap = SmackManager.getInstance().getUserImage(name);
            }
            if (bitmap != null) {
                LruUtils.getInstance().getMemoryCache().put(name, bitmap);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(FIT_CENTER);
            }
        }else {
            Glide.with(this)
                    .load(url)
                    .into(imageView);
        }
    }
}

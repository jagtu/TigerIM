package cn.ittiger.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoView imageView = new PhotoView(this);
        imageView.enable();
        setContentView(imageView);

        Glide.with(this)
                .load(getIntent().getStringExtra("url"))
                .into(imageView);
    }
}

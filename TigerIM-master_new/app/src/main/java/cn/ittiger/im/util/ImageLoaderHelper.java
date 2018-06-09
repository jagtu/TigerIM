package cn.ittiger.im.util;

import cn.ittiger.im.R;
import cn.ittiger.im.smack.SmackManager;
import cn.ittiger.util.PreferenceHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片参数帮助类
 * @author: laohu on 2016/12/24
 * @site: http://ittiger.cn
 */
public class ImageLoaderHelper {

    /*
    String imageUri = "http://site.com/image.png"; // 网络图片
    String imageUri = "file:///mnt/sdcard/image.png"; // sd卡图片
    String imageUri = "content://media/external/audio/albumart/13"; //  content provider
    String imageUri = "assets://image.png"; // assets文件夹图片
    String imageUri = "drawable://" + R.drawable.image; // drawable图片
    */
    private static volatile DisplayImageOptions sImageOptions;

    public static DisplayImageOptions getChatImageOptions() {

        if(sImageOptions == null) {
            synchronized (ImageLoaderHelper.class) {
                if(sImageOptions == null) {
                    sImageOptions = new DisplayImageOptions.Builder()
                            .cacheOnDisk(true)//图片下载后是否缓存到SDCard
                            .cacheInMemory(true)//图片下载后是否缓存到内存
                            .bitmapConfig(Bitmap.Config.RGB_565)//图片解码类型，推荐此种方式，减少OOM
                            .considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转）
                            .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                            .showImageOnFail(R.drawable.vector_default_image)//图片加载失败后显示的图片
                            .showImageOnLoading(R.drawable.vector_default_image)
                            .build();
                }
            }
        }
        return sImageOptions;
    }

    public static void displayImage(ImageView imageView, String url) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        displayImage(imageView, url, null);
    }

    public static void displayImage(ImageView imageView, String url, ImageLoadingListener imageLoadingListener) {

        ImageLoader.getInstance().displayImage(url, imageView, getChatImageOptions(), imageLoadingListener);
    }

    public static Bitmap decodeSampleBitmapFromBytes(byte[] data, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // inJustDecodeBounds为true时仅解析图片原始信息，并不会真正加载图片。
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 此时图片的宽高可以通过options.outWidth和options.outHeight获取到，我们
        // 可以根据自己的需求计算出采样比。
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // inJustDecodeBounds设置为fales，加载图片到内存中。
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void loadImg(final ImageView imageView,  String name) {
        final String userName = name;
        imageView.setTag(userName);
        Observable.just(imageView)
                .map(new Func1<ImageView, ImageView>() {
                    @Override
                    public ImageView call(ImageView imageView) {
                        String name = imageView.getTag() + "";
                        Bitmap bitmap = LruUtils.getInstance().getMemoryCache().get(name);
                        if (bitmap == null) {
                            bitmap = SmackManager.getInstance().getUserImage(name);
                            if (bitmap != null) {
                                LruUtils.getInstance().getMemoryCache().put(name, bitmap);
                            }
                        }
                        return imageView;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageView>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ImageView imageView) {
                        Bitmap bitmap = LruUtils.getInstance().getMemoryCache().get(imageView.getTag() + "");
                        if (bitmap != null && imageView.getTag().equals(userName)) {
                            imageView.setImageBitmap(bitmap);
                            Log.i("bitmap", "onNext: "+bitmap.getByteCount());
                        } else {
                            Log.i("iop", "getView: null img");
                        }
                    }
                });
    }
}

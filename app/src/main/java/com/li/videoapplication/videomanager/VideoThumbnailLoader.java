package com.li.videoapplication.videomanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoThumbnailLoader {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    ExecutorService executorService;

    Context mContext;

    int stub_id;

    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public VideoThumbnailLoader(Context context) {
        mContext = context;
        // 当进入listview时默认的图片，可换成你自己的默认图片
        stub_id = MResource.getIdByName(mContext, "drawable", "radio_fra_bottom_bg");
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    // 当进入listview时默认的图片，可换成你自己的默认图片

    // 最主要的方法
    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // 若没有的话则开启新线程加载图片
            imageView.setImageResource(stub_id);
            queuePhotoUrl(url, imageView);
        }
    }

    public void DisplayImage(String url, ImageView imageView, int width) {
        clearCache();
        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // 若没有的话则开启新线程加载图片
            imageView.setImageResource(stub_id);
            queuePhotoUrl(url, imageView);
        }
    }

    // 加载本地图片库的图片
    public void DisplayLoaclImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {

            // 若没有的话则开启新线程加载图片
            queueLocalPhotoUrl(url, imageView);
        }
    }


    private void queueLocalPhotoUrl(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLocalUrl(p));
    }

    // 加载本地图片
    class PhotosLocalUrl implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLocalUrl(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap srcBitmap = getThumb(getLoacalBitmap(photoToLoad.url));

            memoryCache.put(photoToLoad.url, srcBitmap);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(srcBitmap, photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 加载本地图片(缩略图)
     *
     * @param url
     * @return
     */

    public Bitmap getLoacalBitmap(String url) {
        //注释中的方法也可以按比例缩减图片比例
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        // 缩放倍数
        options.inSampleSize = 1;
        try {
            FileInputStream fis = new FileInputStream(url);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return BitmapFactory.decodeFile(url, options); // /把流转化为Bitmap图片

//			File file = new File(url);
//			return decodeFile(file);

    }

    public void DisplayThumbnailForLocalVideo(String dir, ImageView imageView) {
        imageViews.put(imageView, dir);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(dir);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            // 若没有的话则开启新线程加载图片
            imageView.setImageResource(stub_id);
            queuePhoto(dir, imageView);
        }
    }


    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private void queuePhotoUrl(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoaderUrl(p));
    }

    public void DisplayTimeForLocalVideo(String dir, TextView textView) {
        File file = new File(dir);
        if (file.length() == 0) {
            return;
        }
        mmr.setDataSource(dir);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
        int time = 0;
        if (duration != null) {
            time = Integer.parseInt(duration);
        }
        if (time == 0) {
            return;
        }
        long min = time / 1000 / 60;
        long sec = time / 1000 % 60;
        if (min < 10) {
            if (sec < 10) {
                textView.setText("0" + min + ":" + "0" + sec);
            } else {
                textView.setText("0" + min + ":" + sec);
            }
        } else {
            if (sec < 10) {
                textView.setText(min + ":" + "0" + sec);
            } else {
                textView.setText(min + ":" + sec);
            }
        }
        //textView.setText(min+":"+sec);
    }


    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        // 先从文件缓存中查找是否有
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;
        // 最后从指定的url中下载图片
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            FileOutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);

            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 350;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmapFromUrl(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    public Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;

        bitmap = GetThumbnailForLocalVideo(url);
        if (bitmap != null) {
            return getThumb(bitmap);
        }

        int bimId = MResource.getIdByName(mContext, "drawable", "fm_play_bg");
        Resources res = mContext.getResources();
        bitmap = BitmapFactory.decodeResource(res, bimId);

        return bitmap;
    }

    public Bitmap GetThumbnailForLocalVideo(String dir) {
        // Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(dir, 30);
        Bitmap bitmap = GetVideoMidFrme(dir);
        return bitmap;
    }

    /**
     * 从视频中生成一张图片
     *
     * @param dataPath 视频地址
     * @return
     */
    public Bitmap GetVideoMidFrme(String dataPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(dataPath);
        String time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = Integer.valueOf(time);
        Bitmap bitmap = retriever.getFrameAtTime(seconds / 2 * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }

    public Bitmap getThumb3(Bitmap srcBitmap) {
        // 封面大小处理
        int newWidth = 450;
        int newHeight = 254;
        Bitmap finallBitmap;

        int srcW, srcH;
        srcW = srcBitmap.getWidth();
        srcH = srcBitmap.getHeight();

        if (srcW < 500 && srcH < 500) // 直接按比压缩
        {
            Bitmap turnBmp;

            turnBmp = srcBitmap;

            int trunW = turnBmp.getWidth();
            int trunH = turnBmp.getHeight();

            Matrix matrix = new Matrix();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) newWidth) / trunW;
            float scaleHeight = ((float) newHeight) / trunH;
            matrix.postScale(scaleWidth, scaleHeight);
            finallBitmap = Bitmap.createBitmap(turnBmp, 0, 0, trunW, trunH,
                    matrix, true);
        } else // 截剪
        {
            int widleft = (srcW - newWidth) / 2;
            int Heighteft = (srcH - newHeight) / 2;
            finallBitmap = Bitmap.createBitmap(srcBitmap, widleft, Heighteft,
                    newWidth, newHeight);
        }
        return finallBitmap;
    }

    /**
     * 将位图进行局部截取
     *
     * @param srcBitmap
     * @return
     */
    private Bitmap getThumb(Bitmap srcBitmap) {
        int scrWidth = srcBitmap.getWidth();
        int scrHeight = srcBitmap.getHeight();
        Bitmap finallBitmap = null;
        if (scrWidth < scrHeight) {
            int widthleft = 0;
            int heightleft = (scrHeight - scrWidth * 9 / 16) / 2;
            finallBitmap = Bitmap.createBitmap(srcBitmap, widthleft,
                    heightleft, scrWidth, scrWidth * 9 / 16);
        } else {
            finallBitmap = srcBitmap;
        }
        return finallBitmap;
    }

    class PhotosLoaderUrl implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoaderUrl(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap srcBitmap = getBitmap(photoToLoad.url);

            // 封面大小处理
            int newWidth = 450;
            int newHeight = 254;
            Bitmap finallBitmap;

            int srcW, srcH;
            srcW = srcBitmap.getWidth();
            srcH = srcBitmap.getHeight();

            if (srcW < 720 && srcH < 720) // 直接按比压缩
            {
                Bitmap turnBmp;
                if (srcW < srcH) // 横屏
                {
                    Matrix mt = new Matrix();
                    mt.setRotate(90);
                    turnBmp = Bitmap.createBitmap(srcBitmap, 0, 0, srcW, srcH,
                            mt, true);
                } else {
                    turnBmp = srcBitmap;
                }
                int trunW = turnBmp.getWidth();
                int trunH = turnBmp.getHeight();

                Matrix matrix = new Matrix();
                // 计算缩放率，新尺寸除原始尺寸
                float scaleWidth = ((float) newWidth) / trunW;
                float scaleHeight = ((float) newHeight) / trunH;
                matrix.postScale(scaleWidth, scaleHeight);
                finallBitmap = Bitmap.createBitmap(turnBmp, 0, 0, trunW, trunH,
                        matrix, true);
            } else // 截剪
            {
                int widleft = (srcW - newWidth) / 2;
                int Heighteft = (srcH - newHeight) / 2;
                finallBitmap = Bitmap.createBitmap(srcBitmap, widleft,
                        Heighteft, newWidth, newHeight);
            }

            memoryCache.put(photoToLoad.url, finallBitmap);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(finallBitmap, photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}
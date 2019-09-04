package com.li.videoapplication.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.li.videoapplication.activity.ExApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/9/17 0017.
 */
public class DownloadUtils {

    private Context context;

    ExApplication exApplication;

    private static boolean showState = false;

    public DownloadUtils(Context context) {
        this.context = context;
        exApplication = new ExApplication(context);
    }

    public void asyncloadImage(ImageView iv_header, String path, File cache) {
        AsyncImageTask task = new AsyncImageTask(iv_header, cache);
        task.execute(path);
    }

    /**
     * 下载图片，显示下载的图片*
     */
    private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {

        private ImageView iv_header;
        private File cache;

        public AsyncImageTask(ImageView iv_header, File cache) {
            this.iv_header = iv_header;
            this.cache = cache;
        }

        // 后台运行的子线程子线程
        // params[0]==>path
        @Override
        protected Uri doInBackground(String... params) {
            try {
                return getImageURI(params[0], cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // 这个放在在ui线程中执行
        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            // 完成图片的绑定
            if (iv_header != null && result != null) {
                iv_header.setImageURI(result);
            }
        }
    }

    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public Uri getImageURI(String path, File cache) throws Exception {
        String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            System.out.println("图片存在本地缓存目录，不去服务器下载===" + Uri.fromFile(file));
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                System.out.println("从网络上获取图片===" + Uri.fromFile(file));
                showState = true;
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    public void deleteCache(File cache) {
        //清空缓存
        File[] files = cache.listFiles();
        for (File file : files) {
            file.delete();
        }
        cache.delete();
    }

}

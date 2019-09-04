package com.li.videoapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.fmscreenrecord.utils.MinUtil;
import com.li.videoapplication.activity.ExApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * Created by maple on 2014/9/15.
 */
public class ImgUtils {

    /**
     * 获取文件的名称
     * @param data
     * @return
     */
    public static String getName(String data){
        String filename[]= data.split("/");
        if (filename!=null) {
            return filename[filename.length-1];
        }
        return null;
    }

    /**
     * 图片按比例大小压缩方法
     * @param srcPath
     * @return
     */
    public static byte[] getImageByte(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        byte[] data = baos.toByteArray();
        return data;
    }


    /**
     * 将图片保存到本地文件
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            if (ExApplication.creatDir()) {

                stream = new FileOutputStream(
                        Environment.getExternalStorageDirectory() + "/yc/img"
                                + filename);
            } else {
                return false;
            }

        } catch (FileNotFoundException e) {
            Log.e("bitmap2file", e.toString());
            return false;
        }

        return bmp.compress(format, quality, stream);
    }

    /**
     * 裁剪图片
     * @param activity
     * @param uri 图片的路径
     * @param requestCode 请求参数
     */
    public static  void crop(Activity activity,Uri uri,int requestCode) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 根据视频地址获取视频封面
     * 并进行竖屏图片截取
     *
     * @param filepath
     * @return
     */
    public static String getImageFromVideoPath(String filepath) {

        //从视频中获取一张位图
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filepath);
        String time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = Integer.valueOf(time);
        Bitmap srcBitmap = retriever.getFrameAtTime(seconds / 2 * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        // 封面大小处理
        int newWidth = 450;
        int newHeight = 254;
        Bitmap finallBitmap;

        int srcW, srcH;
        srcW = srcBitmap.getWidth();
        srcH = srcBitmap.getHeight();

        if ((srcW < 720 && srcH < 720) || srcW > srcH) // 直接按比压缩
        {

            Bitmap turnBmp;
            if (srcW < srcH) // 竖屏
            {
                Matrix mt = new Matrix();
                mt.setRotate(90);
                turnBmp = Bitmap.createBitmap(srcBitmap, 0, 0, srcW, srcH, mt, true);
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
            finallBitmap = Bitmap.createBitmap(turnBmp, 0, 0, trunW, trunH, matrix, true);
        } else // 截剪
        {
            MinUtil.mylog("不直接压缩");
            int widleft = (srcW - newWidth) / 2;
            int Heighteft = (srcH - newHeight) / 2;
            finallBitmap = Bitmap.createBitmap(srcBitmap, widleft, Heighteft,
                    newWidth, newHeight);

        }

        File file = new File("/mnt/sdcard/LuPingDaShi/tmp");
        File file2 = new File(filepath);
        // 截取视频文件后缀名前的字符串
        final String[] fileName = file2.getName().split("\\.mp4");

        if (file.exists() == false) {
            file.mkdirs();
            file = null;
        }
        File f = new File("/mnt/sdcard/LuPingDaShi/tmp/" + fileName[0] + ".png");
        try {
            f.createNewFile();

        } catch (IOException e) {

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finallBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f.getPath();
    }

}

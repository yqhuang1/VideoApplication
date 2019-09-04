package com.li.videoapplication.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.LocalFile;
import com.li.videoapplication.entity.MissionEntity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2014/8/15.
 */
public class ExApplication {

    public static String MEMBER_ID = "";
    public static String nickname = "";
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    private static String state = Environment.getExternalStorageState();
    public static Bitmap headImg;
    public static Map mopMap;

    /**
     * 上传状态的判断
     */
    public static int UpdateState = 0;
    public final static int UPLOADING = 1;
    public final static int UPLOADED = 2;


    public static List<MissionEntity> missList = new ArrayList<MissionEntity>();

    /**
     * 判断点击录制的
     */
    public static int type = 0;
    public final static int HOMEONCLICK = 1;
    public final static int MISSIONFRAGMENTONCLICK = 2;
    public static String missionId = "";
    public static String timeLength = "0";

    /**
     * 记录选中的上传文件
     */
    public static LocalFile localFile;
    /**
     * 记录上传
     */
    public static List<LocalFile> localFileList = new ArrayList<LocalFile>();

    /**
     * 头像文件的路径
     */
    public static String headImgUrl = "";

    /**
     * 优酷
     */
    public static String accesstoken = "";

    public static String id = "3442d71ac40b6f05";
    public static String client_secret = " f85cf1256744b84d6c651442f2986946";
    public static String url = "http://121.41.128.6/index.html";

    public static Bitmap videoBitmap;

    public static String recordPath = "/mnt/sdcard/LuPingDaShi";//录屏大师路径
    public static String screenShotPath = "/mnt/sdcard/LuPingDaShi/Picture";//录屏大师截图路径

    /**
     * 播放优酷的
     * youkuURL
     */
    public static final String youkuURL = "http://www.17sysj.com/index.php/Wap/Video/videoPlay.html?vid=";
    /**
     * 播放优酷的
     * qiniuURL
     */
    public static final String qiniuURL = "http://www.17sysj.com/index.php/Wap/Video/shareWap/play/";
    /**
     * 最新的
     * 分享的链接URL
     */
    public static final String shareURL = "http://www.17sysj.com/video/";

    /**
     * 原版 标志
     */
    public static final String alone_id = "0";

    public ExApplication(Context context) {
        initImageLoader(context);
    }

    /**
     * 创建保存图标文件夹
     */
    public static boolean creatDir() {
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String saveDir = Environment.getExternalStorageDirectory()
                    + "/yc/img";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return true;
        }
        return false;
    }

    /**
     * 图片展示参数
     *
     * @return displayImageOptions
     */
    public DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.radio_fra_bottom_bg)
                .showImageForEmptyUri(R.drawable.radio_fra_bottom_bg)
                .showImageOnFail(R.drawable.radio_fra_bottom_bg)
                .cacheInMemory()
                .cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(2))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        return options;
    }

    /**
     * 圆角图片展示参数
     *
     * @return displayImageOptions
     */
    public DisplayImageOptions getRoundedOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.radio_fra_bottom_bg)
                .showImageForEmptyUri(R.drawable.radio_fra_bottom_bg)
                .showImageOnFail(R.drawable.radio_fra_bottom_bg)
                .cacheInMemory(true)
                .cacheOnDisc()
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        return options;
    }

    public DisplayImageOptions getHeadOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.persional_image_default)
                .showImageForEmptyUri(R.drawable.persional_image_default)
                .showImageOnFail(R.drawable.persional_image_default)
                .cacheInMemory()
                .cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }

    /**
     * 初始化图片下载
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void clear() {
        imageLoader.clearDiscCache();

    }

    /**
     * 友盟事件统计
     *
     * @param context
     * @param type    参数类型(如一个事件ID有不同类型参数时，type可以作为区分参考）
     * @param id      事件ID(该ID作为在友盟后台查看自定义事件ID的筛选值，在程序中请确保唯一性）
     */
    public static void upUmenEventValue(Context context, String type, String id) {
        mopMap = new HashMap<String, String>();
        mopMap.put("type", type);
        mopMap.put("key", type);
        MobclickAgent.onEventValue(context, id, mopMap, 1);
    }

    public static void upUmenEventValueAssort(Context context, String type, String id, String key) {
        mopMap = new HashMap<String, String>();
        mopMap.put("type", type);
        mopMap.put("key", key);
        MobclickAgent.onEventValue(context, id, mopMap, 1);
    }


}

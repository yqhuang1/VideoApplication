package com.fmscreenrecord.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;

import com.fmscreenrecord.utils.MResource;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;

/**
 * 存放全局变量及方法 Create：2014-08
 */
public class ExApplication {

	public static boolean isRecording = false; // 是否在录制中

	/** 用户ID ***/
	public static String MEMBER_ID = "";
	public static String nickname = "";
	public ImageLoader imageLoader = ImageLoader.getInstance();

	private static String state = Environment.getExternalStorageState();

	public static Bitmap headImg;

	/**
	 * 当前是否有视频处于上传状态
	 * 
	 */
	public static boolean videoUploading = false;

	/**
	 * 当前设备的分辨率宽度、高度
	 */
	public static int DEVW = 0;
	public static int DEVH = 0;
	/**
	 * banner图片地址
	 */
	public static String bannerPicUrl = null;
	/**
	 * banner附带网址
	 */
	public static String bannerUrl = null;
	/**
	 * 视频清晰度_超清: 0<br/>
	 * 作为程序内部清晰度标示
	 */
	public static String HQuality = "0";
	
	/**
	 * 视频清晰度_超清<br/>
	 * 作为界面清晰度标示
	 */
	public static String HQualityVaule = "超清";
	/**
	 * 视频清晰度_标清: 1<br/>
	 * 作为程序内部清晰度标示
	 */
	public static String SQuality = "1";
	/**
	 * 视频清晰度_标清:<br/>
	 * 作为界面清晰度标示
	 */
	public static String SQualityVaule = "标清";
	/**
	 * 画中画是否已经关闭
	 */
	public static boolean floatCameraClose = true;
	/** 记录浮窗窗口1的X坐标 ***/
	public static float moveX = -1;
	/** 记录浮窗窗口1的y坐标 ***/
	public static float moveY = -1;

	/**
	 * 后台合成视频服务是否开启判断
	 * 
	 */
	public static boolean isCompositionVideo = false;
	/**
	 * 延迟关闭退出录屏大师
	 */
	public static boolean islaterCloaseApp = false;

	/**
	 * 判断点击录制的
	 */
	public static int type = 0;
	public final static int HOMEONCLICK = 1;
	public final static int MISSIONFRAGMENTONCLICK = 2;
	public static String missionId = "";
	public static String timeLength = "0";

	/**
	 * 判断浮窗是否在屏幕左边
	 */
	public static boolean FloatViewLeft = true;
	/**
	 * 视频清晰度
	 */
	public static String videoQuality = "-1";
	/**
	 * 开始录屏时的时间毫秒数
	 */
	public static long recStartTime = 0;
	/**
	 * 停止录屏时的时间毫秒数
	 */
	public static long recEndTIme = 0;

	public static boolean firstExtandAnmin = true;

	/**
	 * 判断是否在SD卡android/data下面创建目录
	 */
	public static boolean ExpandSdCardAndroidData = false;

	/**
	 * 存储当前手机网络环境类型
	 */
	public static int NetType = -1;
	/**
	 * 头像文件的路径
	 */
	public static String headImgUrl = "";

	/**
	 * 屏幕方向 横为true 竖为fals
	 */
	public static boolean mConfiguration = false;

	/**
	 * 是否直接跳转到视频管理页
	 */
	public static boolean isgotovideomange = false;
	/**
	 * 是否直接跳转到云端视频管理页
	 */
	public static boolean isgotservervideomange = false;
	/**
	 * 手游视界热门分类接口mark
	 */
	public static String mark = null;
	/**
	 * 热门视频标题栏
	 */
	public static String titlename = null;
	public static String id = "3442d71ac40b6f05";
	public static String client_secret = " f85cf1256744b84d6c651442f2986946";
	public static String url = "http://121.41.128.6/index.html";
	/**
	 * 是否暂停录制视频
	 * 
	 */
	public static boolean pauseRecVideo = false;

	/**
	 * 是否在云端视频页隐藏右上角选择按钮
	 */
	public static boolean hideSelectButton= false;
	public Context mContext;

	public ExApplication(Context context) {
		initImageLoader(context);
		mContext = context;
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
				.showStubImage(
						MResource.getIdByName(mContext, "drawable",
								"fm_radio_fra_bottom_bg"))
				.showImageForEmptyUri(
						MResource.getIdByName(mContext, "drawable",
								"fm_radio_fra_bottom_bg"))
				.showImageOnFail(
						MResource.getIdByName(mContext, "drawable",
								"fm_radio_fra_bottom_bg")).cacheInMemory()
				.cacheOnDisc().displayer(new RoundedBitmapDisplayer(5)).build();
		return options;
	}

	/**
	 * 初始化图片下载
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		@SuppressWarnings("deprecation")
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(false).cacheOnDisc(false).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().discCacheFileCount(20)
				// 缓存文件的最大个数
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	public void clear() {
		imageLoader.clearDiscCache();

	}

	/**
	 * 检测是否安装某个应用
	 * 
	 * @param packageName
	 * @param context
	 * @return
	 */
	public static boolean isInstallApp(String packageName, Context context) {
		PackageInfo packageInfo;

		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {

			return false;
		} else {

			return true;
		}
	}
}
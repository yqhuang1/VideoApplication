package com.fmscreenrecord.frontcamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.imageFilters.SoftGlowFilter;
import com.fmscreenrecord.utils.MResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraView extends LinearLayout implements SurfaceHolder.Callback,
		OnClickListener, Camera.PreviewCallback {

	public int mWidth;
	public int mHeight;
	private static CameraViewManager manager;
	private PackageManager pkgManager;
	private LayoutInflater inflater;
	// private TrafficAdapter adapter;

	// private Button btBack;
	private ListView listView;
	private static Camera mCamera;
	private SurfaceView surfaceview = null; // 用于显示图像
	private SurfaceHolder surfaceholder = null; // 句柄
	private boolean bIfPreview = false; // 相机预览模式

	private SurfaceTexture surfacetexture;
	private Canvas canvas;

	private static int height = 240, width = 320;

	private int preX;
	public static int preY;
	public static int preXFirst;
	public static int preYFirst;

	private int x;
	private int y;
	private boolean isMove;
	public static Context mContext;

	private static boolean isClose = false;

	public static Handler handlerFromFloatService;

	// 关闭按钮
	//private static ImageView ivClose;

	public CameraView(Context context) {
		this(context, null);
		mContext = context;
	}

	@SuppressLint("NewApi")
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		pkgManager = context.getPackageManager();
		mContext = context;
		inflater = LayoutInflater.from(context);

		inflater.inflate(MResource.getIdByName(context, "layout",
				"fm_front_camera_view"), this);
		manager = CameraViewManager.getInstance(context);

		ViewGroup.LayoutParams params = findViewById(
				MResource.getIdByName(context, "id", "content"))
				.getLayoutParams();
		mWidth = params.width;
		mHeight = params.height;

//		ivClose = (ImageView) findViewById(MResource.getIdByName(context, "id",
//				"iv_close"));
//
//		ivClose.setOnClickListener(this);

		surfaceview = (SurfaceView) findViewById(MResource.getIdByName(context,
				"id", "surfaceview"));
		surfaceview.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceholder = surfaceview.getHolder();
		surfaceholder.addCallback(this); // Activity必须实现SurfaceHolder.Callback
		surfacetexture = new SurfaceTexture(10);
		initCamera3();
		isClose = false;

		handlerFromFloatService = new Handler() {
			@SuppressWarnings("deprecation")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {

					surfaceholder = null;
					try {
						if (mCamera != null) {
							mCamera.setPreviewCallbackWithBuffer(null);
							mCamera.stopPreview();
							mCamera.release();
							mCamera = null;
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					manager.dismiss();
					Intent intent = new Intent(mContext,
							FrontCameraService.class);
					mContext.stopService(intent);
					ExApplication.floatCameraClose = true;
					
				}
			}
		};

	}

	@Override
	public void onClick(View v) {
//		if (v == ivClose) {
//			closeFloatView();
//		}
	}

	/**
	 * 关闭画中画浮窗
	 */
	@SuppressWarnings("deprecation")
	public static void closeFloatView() {
		isClose = true;
		handlerFromFloatService.sendEmptyMessageDelayed(1, 2000);

	}

	@SuppressLint("NewApi")
	private void initCamera3() {
		if (!bIfPreview) {

			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Log.i("TAG", "inside the camera======1");
			int cameraCount = Camera.getNumberOfCameras(); // get cameras number
			Log.i("TAG", "inside the camera======2");
			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						mCamera = Camera.open(camIdx);
						Log.i("TAG", "inside the camera======3");
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (mCamera != null && !bIfPreview) {
			Camera.Parameters parameters = mCamera.getParameters();

			List<Camera.Size> supportedPreviewSizes = parameters
					.getSupportedPreviewSizes();
			if (null != supportedPreviewSizes
					&& 0 < supportedPreviewSizes.size()) {
				int suSize[] = new int[supportedPreviewSizes.size()];
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				
				Camera.Size size;
				int sizeheight;
				int sizewidth;
				for (int i = 0; i < supportedPreviewSizes.size(); i++) {
					 size = (Camera.Size) supportedPreviewSizes
							.get(i);
					 sizeheight = size.height;
					 sizewidth = size.width;
					suSize[i] = sizeheight;
					map.put(sizeheight, sizewidth);
				}
				Arrays.sort(suSize);
				Log.e("camerawidth", "" + map.get(suSize[0]));
				Log.e("cameraheight", "" + suSize[0]);
				width = map.get(suSize[0]);
				height = suSize[0];
				// parameters.setPictureSize(map.get(height[0]), height[0]);
			}

			parameters.setPreviewSize(width, height);
			// parameters.setExposureCompensation(1); //调曝光

			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);
			// mCamera.setPreviewCallback(mPreviewCallback);
			try {
				// mCamera.setPreviewDisplay(surfaceholder);
				mCamera.setPreviewTexture(surfacetexture); // ////////////
			} catch (Exception e) {
				Log.e("TAG", e.toString());
			}
			mCamera.startPreview();
			bIfPreview = true;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("TAG", "Surface Changed1");

		if (mCamera == null) {
			return;
		}
		mCamera.stopPreview();
		mCamera.setPreviewCallback(this);
		mCamera.startPreview();
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			if (mCamera != null) {
				// mCamera.setPreviewDisplay(surfaceholder);
				mCamera.setPreviewTexture(surfacetexture);
				// //////////////////////////
				mCamera.startPreview();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub

		if (camera == null) { // if after release ,here also called; so do if.
			return;
		}
		synchronized (surfaceholder) {
			if (data != null && isClose == false) {
				canvas = surfaceholder.lockCanvas();
				int RGBData[] = new int[width * height];
				decodeYUV420SP(RGBData, data, width, height); // 解码
				Bitmap bmsrc = Bitmap.createBitmap(RGBData, width, height,
						Config.ARGB_8888);

//				int newWidth = 110;
//				int newHeight = 110;
//				Matrix matrix = new Matrix();
//				float scaleWidth = ((float) newWidth) / width;
//				float scaleHeight = ((float) newHeight) / height;
//				matrix.postScale(scaleWidth, scaleHeight);
//				Bitmap bm = Bitmap.createBitmap(bmsrc, 0, 0, width, height,
//						matrix, true);

				int rotation = 0;
				switch (FrontCameraService.screenRotation) {
				case 0: {
					rotation = -90;
				}
					break;
				case 1: {
					rotation = 0;
				}
					break;
				case 3: {
					rotation = 180;
				}
					break;

				}
				if (rotation != 0) {
					Matrix m = new Matrix();
					try {
						m.setRotate(rotation, bmsrc.getWidth() / 2,
								bmsrc.getHeight() / 2);// 90就是我们需要选择的90度
						Bitmap bmp2 = Bitmap.createBitmap(bmsrc, 0, 0,
								bmsrc.getWidth(), bmsrc.getHeight(), m, true);
						bmsrc.recycle();
						bmsrc = bmp2;
					} catch (Exception ex) {
					}
				}

				Bitmap tmpBitmap;
				if (ImageCache.get("SoftGlowFilter") != null) {
					tmpBitmap = ImageCache.get("SoftGlowFilter");
				}
				tmpBitmap = new SoftGlowFilter(bmsrc, 1, 0.1f, 0.1f)
						.imageProcess().getDstBitmap();

//				Matrix m2 = new Matrix();
//				try {
//					m2.postScale(2, 2);
//					Bitmap bmp3 = Bitmap.createBitmap(tmpBitmap, 0, 0,
//							tmpBitmap.getWidth(), tmpBitmap.getHeight(), m2,
//							true);
//					bmsrc.recycle();
//					bmsrc = bmp3;
//				} catch (Exception ex) {
//				}

				canvas.drawBitmap(tmpBitmap, 0, 0, null);
				ImageCache.put("SoftGlowFilter", tmpBitmap);
				if (canvas != null) {
					surfaceholder.unlockCanvasAndPost(canvas);
				}
			}
		}

	}

	/**
	 * 解码
	 */
	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	/** 水平方向模糊度 */
	private static float hRadius = 3;
	/** 竖直方向模糊度 */
	private static float vRadius = 3;
	/** 模糊迭代度 */
	private static int iterations = 1;

	public static Bitmap BoxBlurFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		// /Drawable drawable = new BitmapDrawable(bitmap);
		return bitmap;
	}

	public static void blur(int[] in, int[] out, int width, int height,
			float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
						| (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	public static void blurFractional(int[] in, int[] out, int width,
			int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	public static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			preX = (int) event.getRawX();
			preY = (int) event.getRawY();
			preXFirst = preX;
			preYFirst = preY;
			isMove = false;
			break;
		}
		case MotionEvent.ACTION_UP: {

			break;
		}
		case MotionEvent.ACTION_MOVE: {

			x = (int) event.getRawX();
			y = (int) event.getRawY();

			// 消除手指点击误差
			if (((x - preXFirst) > -15 && (x - preXFirst) < 15)
					&& ((y - preYFirst) > -15 && (y - preYFirst) < 15)) {
				// notMoveCnt++;
				// if(notMoveCnt>=2)
				// { notMoveCnt = 0;
				isMove = false;
				// }
			} else {
				manager.move(CameraView.this, x - preX, y - preY);
				isMove = true;
			}

			preX = x;
			preY = y;
			break;
		}

		}

		return super.onTouchEvent(event);

	}

}

package com.fmscreenrecord.call;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.video.VideoInfo;


public class StopRecordCallBack {
	public StopRecordCallBack() {
		// TODO 自动生成的构造函数存根
	}

	public interface OnCallListener {
		public void onCall(VideoInfo info);
	}

	public void setOnCallListener(final OnCallListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Looper.prepare();
				while (true) {
				
					if (MainActivity.endRecord == true) {
						MainActivity.endRecord = false;
						VideoInfo info = new VideoInfo();
						info.setPath(MainActivity.path_dir);
						info.setDisplayName(MainActivity.videofilename);
						info.setTime(MainActivity.videolong);
						String s = null;
						s = MainActivity.path_dir;

						System.out.println(MainActivity.path_dir + "+++"
								+ MainActivity.videofilename + "===="
								+ MainActivity.videolong);
						// MainActivity.path_dir = null; //路径只读一次，故不更新
						MainActivity.videofilename = null;
						MainActivity.videolong = 0;
					
						listener.onCall(info);
					}
					try {
						Thread.sleep((int) (Math.random() * 50));
					} catch (InterruptedException e) {
						MinUtil.mylog(e.toString());
						e.printStackTrace();
					}
				}
				// Looper.loop(); // 循环消息队列
			}

		}).start();
	}

}

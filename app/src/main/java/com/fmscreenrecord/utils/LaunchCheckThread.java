package com.fmscreenrecord.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.app.ExApplication;
import com.li.videoapplication.utils.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 启动应用时进行root、存储空间、版本等检查
 * 
 * @author WYX
 * 
 */
public class LaunchCheckThread extends Thread {
	Context mContext;
	// root软件名称
	private String rootName;
	// root软件链接
	private String rootUri;
	// root提示语
	private String rootNote = "您的设备尚未开启root权限，请获取root权限后再重启本应用程序。";

	public LaunchCheckThread(Context mContext) {
		this.mContext = mContext;
	}

	public void run() {
		String msg = null;
		// 检查设备是否获取root权限
		if (!RootUtils.appRoot1()) {

			// 获取清单文件中UMENG_CHANNEL的value
			try {
				MinUtil.upUmenEventValue(mContext, "ROOT失败次数", "rootFail");
				ApplicationInfo appInfo = mContext.getPackageManager()
						.getApplicationInfo(mContext.getPackageName(),
								PackageManager.GET_META_DATA);

				msg = appInfo.metaData.getString("UMENG_CHANNEL");

				// 如果获取不到数据，说明value为int类型
				if (msg == null) {
					msg = appInfo.metaData.getInt("UMENG_CHANNEL") + "";

				}

			} catch (Exception e) {
				// 如果获取不到设备，重新设定默认值为“xiaomi”
				msg = "xiaomi";

				e.printStackTrace();
			}
			// 如果是小米商店或OPPO渠道的，将暂时不作MIUI检测
			if (msg.equals("xiaomi") || msg.equals("OPPO")) {

			} else {
				// 检测是否MIUI系统
				if (getManufacturer().equals("MIUI")) {
					msg = "MIUI";
				}
			}

		}

		if (msg != null) {

			// 根据不同标志提供不同链接的下载地址
			if (msg.equals("360") || msg.equals("feimo1")
					|| msg.equals("feimo2")) {
				rootName = "下载360超级ROOT";
				rootUri = "http://m.app.so.com/detail/index?pname=com.qihoo.permmgr&id=2304476";

				MainActivity.handler.sendEmptyMessage(1);
			} else if (msg.equals("yingyongbao")) {
				rootName = "下载KingRoot";
				rootUri = "http://android.myapp.com/myapp/detail.htm?apkName=com.kingroot.RushRoot";

			} else if (msg.equals("91") || msg.equals("baidu")
					|| msg.equals("anzhuoshichang")) {
				rootName = "下载百度一键ROOT";
				rootUri = "http://bs.baidu.com/easyroot/BaiduRoot_2001.apk";
				MainActivity.handler.sendEmptyMessage(1);
			} else if (msg.equals("wandoujia")) {
				rootName = "下载360超级ROOT";
				rootUri = "http://www.wandoujia.com/apps/com.qihoo.permmgr";
				MainActivity.handler.sendEmptyMessage(1);
			} else if (msg.equals("MIUI")) {
				rootNote = "您的系统是MIUI，请保证您的系统是小米的开发版系统" + "\n"
						+ "然后到“安全中心》授权管理》root权限管理》录屏大师》授权”" + "\n"
						+ "再到“安全中心》应用权限管理》录屏大师》悬浮窗权限》允许”即可正常使用";
				MainActivity.handler.sendEmptyMessage(2);
			} else if (msg.equals("xiaomi")) {
				// 检测后台ROOT权限是否开启
				if (JsonHelper.getRootNotify("xiaomi", mContext)) {
					if (getManufacturer().equals("MIUI")) {
						rootNote = "您的系统是MIUI，请保证您的系统是小米的开发版系统" + "\n"
								+ "然后到“安全中心》授权管理》root权限管理》录屏大师》授权”" + "\n"
								+ "再到“安全中心》应用权限管理》录屏大师》悬浮窗权限》允许”即可正常使用";
						MainActivity.handler.sendEmptyMessage(2);
					} else {
						rootName = "下载360超级ROOT";
						rootUri = "http://www.wandoujia.com/apps/com.qihoo.permmgr";
						MainActivity.handler.sendEmptyMessage(1);
					}

				}

			} else if (msg.equals("OPPO")) {
				// 检测后台ROOT权限是否开启
				if (JsonHelper.getRootNotify("OPPO", mContext)) {
					if (getManufacturer().equals("MIUI")) {// 检测是否MIUI系统
						rootNote = "您的系统是MIUI，请保证您的系统是小米的开发版系统" + "\n"
								+ "然后到“安全中心》授权管理》root权限管理》录屏大师》授权”" + "\n"
								+ "再到“安全中心》应用权限管理》录屏大师》悬浮窗权限》允许”即可正常使用";
						MainActivity.handler.sendEmptyMessage(2);
					} else {
						rootName = "下载360超级ROOT";
						rootUri = "http://www.wandoujia.com/apps/com.qihoo.permmgr";
						MainActivity.handler.sendEmptyMessage(1);
					}
				}
			} else {

				MainActivity.handler.sendEmptyMessage(2);

			}
		}
	}

	// 弹出对话框提示下载360root工具
	public void createCustomDialog(Context context) {

		Builder builder = null;
		View view = LayoutInflater.from(context).inflate(
				MResource.getIdByName(context, "layout", "fm_get_root_tool"),
				null);
		builder = new Builder(context);
		builder.setTitle("提示:");
		builder.setView(view);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(rootName,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse(rootUri);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						mContext.startActivity(intent);
					}
				});
		try {
			builder.create();
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取设备厂商
	public String getManufacturer() {

		String version;
		BufferedReader input = null;
		try {// 判断是否MIUI系统
			Process p = Runtime.getRuntime().exec(
					"getprop " + "ro.miui.ui.version.name");
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			version = input.readLine();
			input.close();
		} catch (IOException ex) {
			// 不能直接返回null(空)
			return "null";
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {

				}
			}
		}
		if (!version.isEmpty()) {// 判断是否小米系统
			return "MIUI";
		}
		if (ExApplication.isInstallApp("com.huawei.systemmanager", mContext)) {// 判断是否华为系统
			return "HUAWEI";
		}
		return "null";
	}

	// root权限提醒
	public void dialog_Root(Context context) {
		Builder builder = new Builder(context);
		builder.setMessage(rootNote);
		builder.setTitle("提示");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});
		try {
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tipManufacturer() {

		if (getManufacturer().equals("HUAWEI")) {
			new Builder(mContext)
					.setTitle("温馨提示")
					.setMessage(
							"您所使用的是华为设备，须在:" + "\n" + "\n"
									+ "手机管家 》 设置 》 悬浮窗管理 》录屏大师 》 打勾 " + "\n"
									+ "\n" + "将浮窗打开才能正常使用")
					.setPositiveButton("去设置",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.MAIN");
									try {

										intent.setClassName(
												"com.huawei.systemmanager",
												"com.huawei.systemmanager.SystemManagerMainActivity");
										mContext.startActivity(intent);
									} catch (Exception e) {
										try {
											intent.setClassName(
													"com.huawei.systemmanager",
													"com.huawei.systemmanager.mainscreen.MainScreenActivity");
											mContext.startActivity(intent);
										} catch (Exception ee) {
											MinUtil.showToast(mContext,
													"抱歉，您当前的系统版本需要手动打开浮窗");
										}
									}

								}
							})
					.setNegativeButton("知道了",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else if (getManufacturer().equals("MIUI")) {
			String note = null;

			note = "您的系统是MIUI，请到“安全中心》应用权限管理》录屏大师》悬浮窗权限》允许”即可正常使用";

			new Builder(mContext).setTitle("温馨提示").setMessage(note)

			.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new OpenFloatView().showInstalledAppDetails(mContext,
							mContext.getPackageName());
				}
			}).setNegativeButton("知道了", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
		}
	}

	/**
	 * 对话框提示用户检查手机存储空间
	 */
	public void AlertDialoshow() {
		Builder builder = new Builder(mContext);
		builder.setTitle("提示!");
		builder.setMessage("录屏大师检测不到手机的储存空间，请检查后再试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				((Activity) mContext).finish();
			}
		});
		// 设置对话框不会被用户取消掉
		builder.setCancelable(false);
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
		builder.show();
	}
}

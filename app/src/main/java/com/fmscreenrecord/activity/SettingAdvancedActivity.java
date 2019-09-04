package com.fmscreenrecord.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.RUtils;

public class SettingAdvancedActivity extends PreferenceActivity implements
		OnClickListener {
	private SharedPreferences sharedPreferences;

	private LinearLayout btBack;

	public static com.fmscreenrecord.activity.SettingAdvancedActivity last;

	@Override
	public void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_preference_list_advanced"));
		addPreferencesFromResource(MResource.getIdByName(getApplication(),
				"xml", "fm_advanced_settings"));

		last = com.fmscreenrecord.activity.SettingAdvancedActivity.this;

		btBack = (LinearLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "setting_ad_imageButton_back"));
		/*btVideo = (ImageButton) findViewById(MResource.getIdByName(
				getApplication(), "id", "setting_ad_imageButton1"));

		// btAbout = (ImageView) findViewById(R.id.main_about);
		btExit = (ImageButton) findViewById(MResource.getIdByName(
				getApplication(), "id", "setting_ad_imageButton3"));
		btUser = (ImageButton) findViewById(MResource.getIdByName(
				getApplication(), "id", "setting_ad_imageButton_user"));
		btSetting = (ImageButton) findViewById(MResource.getIdByName(
				getApplication(), "id", "setting_ad_imageButton2"));

		btUser.setOnClickListener(this);
		btVideo.setOnClickListener(this);
		btExit.setOnClickListener(this);
		btSetting.setImageDrawable(getResources().getDrawable(
				MResource.getIdByName(getApplication(), "drawable",
						"settings_bt_down")));*/
		btBack.setOnClickListener(this);

		sharedPreferences = getPreferenceManager().getSharedPreferences();

		final ListPreference save_format = (ListPreference) findPreference("save_format");
		save_format.setSummary(RUtils.getRString("fm_image_format_summary")
				+ "("
				+ getString("fm_current_value")
				+ sharedPreferences.getString("save_format",
						getString("fm_orig_size")) + ")");
		save_format
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						save_format
								.setSummary(getString("fm_image_format_summary")
										+ "("
										+ getString("fm_current_value")
										+ newValue + ")");
						return true;
					}
				});

		final ListPreference where_use = (ListPreference) findPreference("where_use");
		where_use.setSummary(RUtils.getRString("fm_where_use_tip") + "("
				+ RUtils.getRString("fm_current_value")
				+ sharedPreferences.getInt("video_fps", 15) + "fps)");
		where_use
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (newValue.equals(RUtils.getRString("fm_custom_fps"))) {
							showSelectFpsDialog(preference);
						} else {
							int fps = Integer.valueOf(newValue.toString()
									.replaceAll("^.*?([0-9]+).*?$", "$1"));
							sharedPreferences.edit().putInt("video_fps", fps)
									.commit();
							where_use.setSummary(RUtils
									.getRString("fm_where_use_tip")
									+ "("
									+ getString("fm_current_value")
									+ sharedPreferences.getInt("video_fps", 15)
									+ "fps)");
						}
						return true;
					}
				});

		final Preference useDebug = findPreference("use_debug");
		Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				boolean isChecked = (Boolean) newValue;
				if (!isChecked) {
//					com.fmscreenrecord.activity.SettingActivity.handlerStopService.sendEmptyMessage(1);
				} else {
//					SettingActivity.handlerStopService.sendEmptyMessage(2);
				}
				return true;
			}
		};
		useDebug.setOnPreferenceChangeListener(onPreferenceChangeListener);

	}

	public String getString(String name) {
		return RUtils.getRString(name);
	}

	private void showSelectFpsDialog(Preference preference) {
		Context context = this;
		View view = View.inflate(this, MResource.getIdByName(getApplication(),
				"layout", "fm_image_compress_view"), null);
		final SeekBar seek = (SeekBar) view.findViewById(MResource.getIdByName(
				getApplication(), "id", "seek_bar"));
		final TextView text = (TextView) view.findViewById(MResource
				.getIdByName(getApplication(), "id", "text"));
		final TextView text_summary = (TextView) view.findViewById(MResource
				.getIdByName(getApplication(), "id", "text_summary"));
		text_summary.setText(getString("fm_select_fps_summary"));
		seek.setMax(25);
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int value = progress + 5;
				text.setText(getString("fm_current_value") + value + " fps");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		int value = sharedPreferences.getInt("video_fps", 15);
		seek.setProgress(value - 5);
		text.setText(getString("fm_current_value") + value + " fps");
		new AlertDialog.Builder(context)
				.setTitle(getString("fm_please_select_value"))
				.setView(view)
				.setNegativeButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								int value = seek.getProgress() + 5;
								sharedPreferences.edit()
										.putInt("video_fps", value).commit();
								findPreference("where_use").setSummary(
										getString("fm_where_use_tip")
												+ "("
												+ getString("fm_current_value")
												+ sharedPreferences.getInt(
														"video_fps", 15)
												+ "fps)");
							}
						}).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		/*if (v == btVideo) {
			Intent intent = new Intent();
			intent.setClass(SettingAdvancedActivity.this, VideoPage.class);
			startActivity(intent);

			// ActivityAnimationSwitcherUtils.start(SettingAdvancedActivity.this);
		} else if (v == btExit) {
			if (FloatContentView.isRecordering) {
				runInBack();
			} else {
				dialog_Exit(SettingAdvancedActivity.this);
			}
		}

		else if (v == btUser) {

			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			// ActivityAnimationSwitcherUtils.start(SettingAdvancedActivity.this);
		} else */if (v == btBack) {
			finish();
		}
	}

	public void exitProgrames() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public boolean runInBack() {
		PackageManager pm = getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(
				new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME), 0);
		ActivityInfo ai = homeInfo.activityInfo;
		Intent startIntent = new Intent(Intent.ACTION_MAIN);
		startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
		startActivitySafely(startIntent);
		return true;
	}

	private void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
		}
	}
/*
	public void dialog_Exit(Context context) {
		btExit.setImageDrawable(getResources()
				.getDrawable(
						MResource.getIdByName(getApplication(), "drawable",
								"exit_down")));
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// NotificationService.isCancleNotification = true;
				NotificationManager mNotificationManager;
				mNotificationManager = (NotificationManager) SRApplication
						.Get().getSystemService(Context.NOTIFICATION_SERVICE);

				mNotificationManager.cancelAll();

				dialog.dismiss();
				SRApplication.Get()
						.stopService(
								new Intent(SRApplication.Get(),
										ScreenRECService.class));
				SRApplication.Get()
						.stopService(
								new Intent(SRApplication.Get(),
										FloatViewService.class));
				// SRApplication.Get().stopService(new
				// Intent(SRApplication.Get(), NotificationService.class));
				exitProgrames();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						btExit.setImageDrawable(getResources().getDrawable(
								MResource.getIdByName(getApplication(),
										"drawable", "exit")));
						dialog.dismiss();
					}
				});

		builder.create().show();
	}*/

}
package com.fmscreenrecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.ViewHolder;
import com.fmscreenrecord.utils.ViewHolderUtils;

public class ActivityAlertDialog extends Activity implements
		View.OnClickListener {
	private CharSequence title;
	private CharSequence content;
	private CharSequence button1;
	private CharSequence button2; // //
	private Boolean cancelable;
	private Intent button1Intent;
	private Intent button2Intent;
	private Holder mHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS); // 在标题栏显示进度条
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_activity_alert_dialog"));
		Intent itt = getIntent();
		title = itt.getCharSequenceExtra("title");
		content = itt.getCharSequenceExtra("content");
		button1 = itt.getCharSequenceExtra("button1");
		button2 = itt.getCharSequenceExtra("button2"); // ///
		cancelable = itt.getBooleanExtra("cancelable", true);
		button1Intent = itt.getParcelableExtra("button1Intent");
		button2Intent = itt.getParcelableExtra("button2Intent"); // ///
		mHolder = ViewHolderUtils.initViewHolder(getWindow().getDecorView(),
				Holder.class);
		mHolder.title.setText(title);
		mHolder.content.setText(content);
		mHolder.button1.setText(button1);
		mHolder.button2.setText("取消"); // ///
		mHolder.button1.setOnClickListener(this);
		mHolder.button2.setOnClickListener(this); // ///
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(getApplication(), "id",
				"button1")) {
			onButton1Clicked();
		} else if (v.getId() == MResource.getIdByName(getApplication(), "id",
				"button2")) {
			onButton2Clicked();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!cancelable) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onButton2Clicked() {
		// sendBroadcast(button2Intent); ///该服务不使用
		finish();
	}

	private void onButton1Clicked() {
		sendBroadcast(button1Intent);
		finish();
	}

	public static class Holder implements ViewHolder {
		public TextView title;
		public TextView content;
		public Button button1;
		public Button button2;

	}
}
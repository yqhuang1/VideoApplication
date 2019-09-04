package com.fmscreenrecord.record;

import android.content.Intent;

public class RecordAction {
	public static final String ACTION = "com.fmscreenrecorder.ACTION";

	public static Intent getActionIntent(String action) {
		Intent intent = new Intent(ACTION);
		intent.putExtra("action", action);
		return intent;
	}
}

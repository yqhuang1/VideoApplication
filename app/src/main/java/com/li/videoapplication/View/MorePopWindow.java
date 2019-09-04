package com.li.videoapplication.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.fmscreenrecord.utils.MResource;
import com.li.videoapplication.activity.SearchGameName;
import com.li.videoapplication.activity.SearchLifeName;
import com.li.videoapplication.activity.ShareActivity;

/**
 * 选择视频类型的泡泡窗口
 * 
 * @author WYX
 * 
 */
public class MorePopWindow extends PopupWindow implements OnClickListener {
	private View conentView;
	private LinearLayout game, life;
	private LinearLayout popup;
	private Context mContext;

	public MorePopWindow(final Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		conentView = inflater.inflate(
				MResource.getIdByName(context, "layout", "more_popup_dialog"),
				null);

		game = (LinearLayout) conentView.findViewById(MResource.getIdByName(
				context, "id", "fm_game_linearlayout"));
		life = (LinearLayout) conentView.findViewById(MResource.getIdByName(
				context, "id", "fm_life_linearlayout"));
		popup = (LinearLayout) conentView.findViewById(MResource.getIdByName(
				context, "id", "fm_popup_dialog"));
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(h);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);

		this.setBackgroundDrawable(dw);

		// 消失监听
		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				ShareActivity.handler.sendEmptyMessage(2);

			}
		});
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果

		this.setAnimationStyle(MResource.getIdByName(context, "style",
				"AnimationPreview"));
		game.setOnClickListener(this);
		popup.setOnClickListener(this);
		life.setOnClickListener(this);

	}

	/**
	 * 显示窗口
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {

			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
			ShareActivity.handler.sendEmptyMessage(3);
		}

		else {

			this.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == game) {// 手机游戏
			ShareActivity.isgotoselectgame = true;
			Intent intent = new Intent(mContext, SearchGameName.class);
			intent.putExtra("type", "game");
			((Activity) mContext).startActivityForResult(intent, 10);

			this.dismiss();
		} else if (v == life) {// 精彩生活
			ShareActivity.isgotoselectgame = true;
			Intent intent = new Intent(mContext, SearchLifeName.class);
			intent.putExtra("type", "life");
			((Activity) mContext).startActivityForResult(intent, 10);
			this.dismiss();
		}

		else if (v == popup) {// 其他区域

			this.dismiss();
		}
	}
}

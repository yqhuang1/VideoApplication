package com.fmscreenrecord.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.view.CircularImage;

import java.util.List;

/**
 * 打开第三方应用列表适配器
 * 
 * @author WYX
 * 
 */
public class PackageInfoAdapter extends BaseAdapter {

	List<PackageInfo> apps;
	ViewHolder holder = null;
	LayoutInflater inflater;
	Context mContext;
	int switchAdapter;

	public PackageInfoAdapter(Context context, List<PackageInfo> apps,
			int switchAdapter) {
		this.apps = apps;
		inflater = LayoutInflater.from(context);
		mContext = context;
		this.switchAdapter = switchAdapter;
	}

	@Override
	public int getCount() {
		if (switchAdapter == 0) {
			return apps.size();
		} else {
			return 3;
		}

	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = new ViewHolder();
		if (convertView == null) {

			convertView = inflater.inflate(MResource.getIdByName(mContext,
					"layout", "fm_packageinfo_gridviewa_item"), null);
			holder.packageName = (TextView) convertView.findViewById(MResource
					.getIdByName(mContext, "id", "fm_packageinfo_name_item"));
			holder.packageIcon = (CircularImage) convertView
					.findViewById(MResource.getIdByName(mContext, "id",
							"fm_packageinfo_img_item"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PackageInfo pinfo = apps.get(position);
		// 获取应用包名
		// String packageName = pinfo.packageName;

		// 获取应用icon
		Drawable dr = mContext.getPackageManager().getApplicationIcon(
				pinfo.applicationInfo);
		holder.packageIcon.setImageDrawable(dr);

		// 获取应用名称
		String label = "";
		try {
			label = mContext.getPackageManager()
					.getApplicationLabel(pinfo.applicationInfo).toString();
			holder.packageName.setText(label);
		} catch (Exception e) {
			Log.i("Exception", e.toString());
		}
		return convertView;
	}

	class ViewHolder {

		TextView packageName;
		CircularImage packageIcon;

	}
}

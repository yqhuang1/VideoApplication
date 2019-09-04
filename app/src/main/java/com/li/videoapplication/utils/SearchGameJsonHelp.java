package com.li.videoapplication.utils;

import com.fmscreenrecord.video.VideoInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 查询游戏类型JSON工具类(用来记录本地已经搜索过的游戏记录)
 * 
 * @author WYX
 * 
 */
public class SearchGameJsonHelp {

	/**
	 * 将搜索过的游戏类型记录转换成JSON
	 * @param data
	 * @return
	 */
	public JSONObject toJSON(ArrayList<VideoInfo> data) {
		int length = data.size();
		JSONObject json = new JSONObject();
		JSONObject JSONRecords[] = new JSONObject[length];
		JSONArray array = new JSONArray();

		try {
			for (int i = 0; i < length; i++) {
				JSONRecords[i] = new JSONObject();
				JSONRecords[i].put("gameId", data.get(i).getGameTypeId());
				JSONRecords[i].put("gameImageUrl", data.get(i).getImageUrl());
				JSONRecords[i].put("gameName", data.get(i).getGamename());
				array.put(i, JSONRecords[i]);
			}

			json.put("data", array);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 将String格式的JSON数据转换成ArrayList<VideoInfo>;
	 * @param jsonString
	 * @return
	 */
	public ArrayList<VideoInfo> parseJSONString(String jsonString) {

		// 游戏类型ID
		String id;
		// 游戏名称
		String name;
		// 游戏图片
		String imageUrl;

		JSONObject json;
		JSONObject temp;
		ArrayList<VideoInfo> data = new ArrayList<VideoInfo>();
		VideoInfo videoInfo = new VideoInfo();
		try {
			json = new JSONObject(jsonString);

			JSONArray arrayList = json.getJSONArray("data");
			for (int i = 0; i < arrayList.length(); i++) {
				videoInfo = new VideoInfo();
				temp = (JSONObject) arrayList.get(i);
			

				// 视频名称
				name = temp.getString("gameName");
				// 图片链接
				imageUrl = temp.getString("gameImageUrl");
				id = temp.getString("gameId");

				videoInfo.setGamename(name);
				videoInfo.setGameTypeId(id);
				videoInfo.setImageUrl(imageUrl);

				data.add(videoInfo);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return data;

	}
}

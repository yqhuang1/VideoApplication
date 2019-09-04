package com.li.videoapplication.utils;

import com.fmscreenrecord.video.VideoInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoUrlInfoUtil
{

	public static List<VideoInfo> getUploadVideoInfo(String vids) {
		List<VideoInfo> list=new ArrayList<VideoInfo>();
		VideoInfo video=null;
		
		String url = "https://openapi.youku.com/v2/videos/show_batch.json?"
				+ "client_id=b4e37d9ef69135ac" + "&video_ids=" + vids;

		// TODO Auto-generated method stub
		HttpGet request = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode()==200) {
				String result = EntityUtils.toString(response.getEntity());	
				try {
					JSONObject obj=new JSONObject(result);
					JSONArray array=obj.getJSONArray("videos");
					for (int i = 0; i < array.length(); i++) {
						JSONObject o=array.getJSONObject(i);
						video=new VideoInfo();
						video.setVideoId(o.get("id").toString());
						video.setTitle(o.get("title").toString());
						video.setThumbnail(o.get("bigThumbnail").toString());
						list.add(video);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return list;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
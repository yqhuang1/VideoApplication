package com.li.videoapplication.entity;

public class UploadVideoInfo 
{
	//请求时提交的,除用户ID外，其他相关的视频信息
	public static class PostVideoInfo
	{
		private String videoTitle;  //标题
		private String gameName;    //游戏名
		private String videoPath; //视频地址，用于取封面
		private int videoWaterChannel;  //打水印通道,0为不打水印，1为公有通道，2为私有通道

		public String getVideoPath() 
		{
			return videoPath;
		}

		public void setVideoPath(String path) 
		{
			this.videoPath = path;
		}
		
		public String getGameName() 
		{
			return gameName;
		}

		public void setGameName(String gameName) 
		{
			this.gameName = gameName;
		}
	    
	    public String getTitle() {
			return videoTitle;
		}
		
		public void setTitle(String title) {
			this.videoTitle = title;
		}
		
		
		public int getVideoWaterChannel() 
		{
			return videoWaterChannel;
		}

		public void setVideoWaterChannel(int videoWaterChannel) 
		{
			this.videoWaterChannel = videoWaterChannel;
		}
	}
	
	//请求完毕返回的token请求信息
	public static class BackVideoInfo
	{
		private String videoKey;     //游戏Key,上传到七牛上的名字
		
		private String videoUploadToken;  //返回的上传凭证
		
		private String msg;
		
		private String result;//获取成功与否
		
		
		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getMSG()
		{
			return msg;
		}
		
		public void setMSG(String msg) 
		{
			this.msg = msg;
		}
		
		public String getVideoKey()
		{
			return videoKey;
		}
		
		/**
		 * 
		 * @param videoKey 视频ID
		 */
		public void setVideoKey(String videoKey) 
		{
			this.videoKey = videoKey;
		}
		
		public String getVideoUploadToken()
		{
			return videoUploadToken;
		}
		
		public void setVideoUploadToken(String videoUploadToken) 
		{
			this.videoUploadToken = videoUploadToken;
		}
	}
	
}

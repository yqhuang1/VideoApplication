package com.li.videoapplication.callback;


public interface UpVideoCallBack {
    //分享方法
    public void share(String VideoId, String ImageUrl);

    /**
     * 回复上传窗口状态
     *
     * @param filepath   视频本地路径
     * @param gameName   游戏名称
     * @param videoTitle 视频标题
     * @param videoid    视频云端ID
     */
    public void recoveryWindow(String filepath, String gameName, String videoTitle, String videoid);

    public void recoveryWindow(String filepath);

    //显示进度
    public void showupdateStatus(double percentage, long uploadLastTimePoint, long uploadFileLength, long uploadLastOffset, long filelong, String filesize);

}

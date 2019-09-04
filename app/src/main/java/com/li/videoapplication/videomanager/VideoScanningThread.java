package com.li.videoapplication.videomanager;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.fmscreenrecord.video.VideoInfo;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoScanningThread extends Thread {

    private List<VideoInfo> mList;

    private VideoCallBack mCallBack;
    private File mFile;
    Context context;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public VideoScanningThread(Context context) {

    }

    public VideoScanningThread(Context context, File file, VideoCallBack callBack) {
        this.mFile = file;
        System.out.println("file:" + file);
        mList = new ArrayList<VideoInfo>();
        this.mCallBack = callBack;


    }

    public VideoScanningThread(Context context, File file) {
        this.mFile = file;
        System.out.println("file:" + file);
        mList = new ArrayList<VideoInfo>();
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        getVideoFile(mList, mFile);
        Collections.reverse(mList);  //将文件序号反转
        mCallBack.getList(mList);
    }

    public List<VideoInfo> GetVideoFile() {
        getVideoFile(mList, mFile);
        Collections.reverse(mList);  //将文件序号反转
        return mList;
    }


    private void getVideoFile(final List<VideoInfo> list, File file) {

        // 获得视频文件
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                        /*	|| name.equalsIgnoreCase(".3gp")
							|| name.equalsIgnoreCase(".wmv")
							|| name.equalsIgnoreCase(".ts")
							|| name.equalsIgnoreCase(".rmvb")
							|| name.equalsIgnoreCase(".mov")
							|| name.equalsIgnoreCase(".m4v")
							|| name.equalsIgnoreCase(".avi")
							|| name.equalsIgnoreCase(".m3u8")
							|| name.equalsIgnoreCase(".3gpp")
							|| name.equalsIgnoreCase(".3gpp2")
							|| name.equalsIgnoreCase(".mkv")
							|| name.equalsIgnoreCase(".flv")
							|| name.equalsIgnoreCase(".divx")
							|| name.equalsIgnoreCase(".f4v")
							|| name.equalsIgnoreCase(".rm")
							|| name.equalsIgnoreCase(".asf")
							|| name.equalsIgnoreCase(".ram")
							|| name.equalsIgnoreCase(".mpg")
							|| name.equalsIgnoreCase(".v8")
							|| name.equalsIgnoreCase(".swf")
							|| name.equalsIgnoreCase(".m2v")
							|| name.equalsIgnoreCase(".asx")
							|| name.equalsIgnoreCase(".ra")
							|| name.equalsIgnoreCase(".ndivx")
							|| name.equalsIgnoreCase(".xvid")*/) {
                        VideoInfo vi = new VideoInfo();

						/*
						mmr.setDataSource(MainActivity.path_dir + "/" +file.getName());
						String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
						//Log.d( "duration1", file.getAbsolutePath()+"/"+file.getName());

						//long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

						//ttt("/storage/emulated/0/SupperLulu/"+file.getName());
						int time = Integer.parseInt(duration);

						vi.setDuration(time);
						*/
                        vi.setDisplayName(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        list.add(vi);
                        return true;
                    }


                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }

                return false;
            }


        });

    }


    private String ttt(String filePath) {
        android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        String str = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        System.out.println(str + "  =====");
        return str;
    }
}
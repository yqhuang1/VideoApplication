package com.fmscreenrecord.VideoList;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.fmscreenrecord.utils.FileUtils;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.DB.VideoDB;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VideoScanningThrread extends Thread {

    private List<VideoInfo> mList;

    private VideoCallBack mCallBack;
    private File mFile;

    Context context;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public VideoScanningThrread(Context context) {
        this.context = context;
    }

    /**
     *
     * @param context
     *            上下文
     * @param PhoneFile
     *            手机内存文件路径
     *
     * @param callBack
     *            回调函数
     */
    public VideoScanningThrread(Context context, File PhoneFile,
                                VideoCallBack callBack) {
        this.mFile = PhoneFile;

        this.context = context;
        mList = new ArrayList<VideoInfo>();
        this.mCallBack = callBack;

    }

    public VideoScanningThrread(Context context, File file) {
        this.mFile = file;

        mList = new ArrayList<VideoInfo>();
        this.context = context;
    }

    @Override
    public void run() {

        super.run();

        VideoDB mVideoDB = new VideoDB(context);

        mList = mVideoDB.GetVideoList();

        File file;
        // 检查数据库中的文件是否实际存在
        Iterator<VideoInfo> sListIterator = mList.iterator();
        while (sListIterator.hasNext()) {// 如果本地文件不存在
            VideoInfo e = sListIterator.next();
            file = new File(e.getPath());
            if (!file.exists()) {
                // 移出列表
                sListIterator.remove();
                // 删除数据库记录
                mVideoDB.DelectForFilePath(e.getPath());
            }
        }

        mCallBack.getList(mList);
    }

    /****************************** 以下方法已废弃 ***************************************************/
    private List<VideoInfo> GetVideoFile() {
        getVideoFile(mList, mFile);

        mReverse(mList);
        // Collections.reverse(mList);

        return mList;
    }

    /**
     * 采用冒泡排序将mlist中的视频按照日期从后到前排序
     *
     * @param mlist
     *            所要排序list数组
     */
    private void mReverse(List<VideoInfo> mlist) {

        for (int i = mlist.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                long arr_j = getLongFromString(mlist.get(j).getDisplayName());
                long arr_jj = getLongFromString(mlist.get(j + 1)
                        .getDisplayName());
                VideoInfo a;
                if (arr_j < arr_jj) {
                    a = mlist.get(j);
                    mlist.set((j), mlist.get(j + 1));
                    mlist.set(j + 1, a);

                }
            }
        }
        mList = mlist;
    }

    /**
     * 将string类型的视频名称抽取为long类型的数值
     *
     * @param str
     *            视频名称 如：2015-04-02_14_33_11
     *
     * @return 返回一个long类型，如20150402143311
     */
    private long getLongFromString(String str) {

        str = str.trim();
        String str2 = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                // 如果该字符处于数字的 ASCII码中，则抽取出来
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }

            }
        }

        return Long.valueOf(str2);

    }

    // 获取视频文件路径并添加到list
    public static void getVideoFile(final List<VideoInfo> list, File file) {

        // 获得视频文件
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                int fileSize = (int) FileUtils.getFileSize(new File(file
                        .getAbsolutePath()));
                if (fileSize > 32) {

                    int i = name.indexOf('.');
                    if (i != -1) {
                        name = name.substring(i);
                        if (name.equalsIgnoreCase(".mp4")) {
                            VideoInfo vi = new VideoInfo();
                            // 获取并添加视频标题到vi
                            vi.setDisplayName(file.getName());
                            // 获取并添加视频路径到vi
                            vi.setPath(file.getAbsolutePath());
                            list.add(vi);
                            return true;
                            // TODO
                        }

                    } else if (file.isDirectory()) {
                        getVideoFile(list, file);
                    }
                }

                return false;
            }

        });

    }

    private String ttt(String filePath) {
        android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        String str = retriever
                .extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);

        return str;
    }
}
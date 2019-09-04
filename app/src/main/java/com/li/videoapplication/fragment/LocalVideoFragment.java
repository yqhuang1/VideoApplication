package com.li.videoapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fmscreenrecord.VideoList.VideoScanningThrread;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.video.ImageInfo;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.LocalVideoAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2015/8/10 0010.
 * 视频管理 本地视频
 */
public class LocalVideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static Context context;
    private List<VideoInfo> localVideoList;

    /**
     * 本地视频数据
     */
    public static List<VideoInfo> localVideoListData = new ArrayList<VideoInfo>();

    /**
     * 外部视频
     */
    private static String PREFS_NAME = "ext_video";
    /**
     * 记录已导入的外部视频
     */
    public static SharedPreferences spExtVideoCheck;
    /**
     * 已选择，将要导入的文件列表
     */
    public static List<VideoInfo> listCheckToLead = null;

    /**
     * 已选择，将要操作的本地视频列表
     */
    public static List<VideoInfo> localVideoListCheckToDel = null;


    private ListView listView;
    public static LocalVideoAdapter localVideoAdapter;
    private View view;

    private SimpleDateFormat dateFormat = null;

    // 数据库
    static VideoDB videoDB;
    // 进度条
    private static ProgressDialog progressDialog;

    public static Handler localVideoHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 取消全选
                    for (int i = 0; i < localVideoListData.size(); i++) {
                        localVideoAdapter.ListDelcheck.set(i, false);
                        localVideoListCheckToDel.clear();
                    }
                    localVideoAdapter.notifyDataSetChanged();
                    break;

                case 2:
                    // 获取数据库数据并刷新本地视频列表数据
                    if (videoDB == null) {
                        videoDB = new VideoDB(context);
                    }
                    localVideoListData = videoDB.GetVideoList();
                    localVideoAdapter.refresh(localVideoListData);

                    break;

                case 3://取消按钮
                    localVideoListCheckToDel.clear();
                    localVideoAdapter.notifyDataSetChanged();
                    break;

                case 5://保存到相册
                    SaveVideoToPhoto();
                    break;

                case 6://删除按钮

                    break;

                case 7://确定删除

                    break;
            }
        }
    };

    // TODO: Rename and change types and number of parameters
    public static LocalVideoFragment newInstance(String param1, String param2) {
        LocalVideoFragment fragment = new LocalVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LocalVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity();
        // 初始待删除的文件列表
        localVideoListCheckToDel = new ArrayList<VideoInfo>();
        // 初始化进度条
        creatProgressDialog();

        if (localVideoListCheckToDel != null) {
            localVideoListCheckToDel.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_manager, null);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.UK);
        localVideoList = new ArrayList<VideoInfo>();
        localVideoAdapter = new LocalVideoAdapter(context, localVideoList);

        listView = (ListView) view.findViewById(R.id.fragment_video_manager_list);
        listView.setAdapter(localVideoAdapter);
        listView.setOnScrollListener(new PauseOnScrollListener
                (ExApplication.imageLoader, true, true));//滑动暂停加载图片

        localVideoHandle.sendEmptyMessage(2);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static void SaveVideoToPhoto() {

        if (localVideoListCheckToDel.size() > 0) {
            // 设置进度条最大值
            progressDialog.setMax(localVideoListCheckToDel.size());
            AlertDialoshow(0);

        } else {

            MinUtil.showToast(context, "至少选中一个视频进行保存");
        }
    }

    /**
     * 迁移提示框
     */
    public static void AlertDialoshow(final int type) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否将文件保存到本地相册中")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LVProgressBarAsyncTask asyntask = new LVProgressBarAsyncTask();
                        asyntask.execute();
                        progressDialog.show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * 初始化进度条
     */
    private void creatProgressDialog() {
        // 创建ProgressDialog对象
        progressDialog = new ProgressDialog(context);

        // 设置进度条风格，风格为长形
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // 设置ProgressDialog 标题
        progressDialog.setTitle("提示");

        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("正在另存视频至本地相册，请稍候");

        // // 设置ProgressDialog 进度条进度
        // progressDialog.setProgress(100);

        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(false);
    }

    /**
     * 异步迁移视频至外置SD卡
     *
     * @author WYX
     */
    public static class LVProgressBarAsyncTask extends
            AsyncTask<Void, Integer, Integer> {

        protected Integer doInBackground(Void... params) {

            // String path_SDdir = StoreDirUtil.getSDDEfault(
            // getApplicationContext()).toString();
            String path_SDdir = "/mnt/sdcard/DCIM";
            // File file = null;
            // 单个视频文件
            File mFile = null;
            // 所有视频文件总大小
            long fileSize = 0;
            // SD卡剩余空间
            long sdSize;

            // 获得SD卡剩余空间大小
            sdSize = StoreDirUtil.getRomAvailableSize(context);

            try {
                // 获取将要移动的视频文件总大小
                for (VideoInfo x : localVideoListCheckToDel) {
                    mFile = new File(x.getPath());
                    fileSize = fileSize + mFile.length();
                }

                if (sdSize > fileSize) {
                    FileInputStream in;
                    BufferedInputStream bufferedIn = null;
                    BufferedOutputStream bufferedOut = null;
                    byte[] by;

                    // 开始对视频进行迁移
                    for (VideoInfo x : localVideoListCheckToDel) {

                        String filePath = x.getPath();

                        in = new FileInputStream(filePath);

                        // 截取文件名
                        String fileName = x.getDisplayName();
                        // 指定文件路径到外置SD卡下
                        FileOutputStream out = new FileOutputStream(
                                path_SDdir + "/" + fileName);

                        bufferedIn = new BufferedInputStream(in);
                        bufferedOut = new BufferedOutputStream(out);
                        by = new byte[20280];
                        while (bufferedIn.read(by) != -1) {
                            bufferedOut.write(by);
                        }
                        // 将缓冲区中的数据全部写出
                        bufferedOut.flush();

                        int i = 0;
                        if (i <= localVideoListCheckToDel.size()) {
                            i++;
                        }
                        publishProgress(i);
                    }

                    if (bufferedIn != null) {
                        bufferedIn.close();
                    }
                    if (bufferedOut != null) {
                        bufferedOut.close();
                    }

                    return 1;
                } else {

                    return 0;
                }
            } catch (IOException e) {

                e.printStackTrace();
                return -1;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            int vlaue = values[0];
            progressDialog.setProgress(vlaue);
        }

        protected void onPostExecute(Integer result) {
            progressDialog.cancel();
            if (result == 1) {
                Toast.makeText(context, "文件已成功保存到本地相册中!", 1).show();
            } else if (result == 0) {
                Toast.makeText(context, "您的内存空间不足，请清理后再试", Toast.LENGTH_LONG)
                        .show();
            } else if (result == -1) {
                Toast.makeText(context, "非常抱歉，文件迁移失败了", Toast.LENGTH_LONG)
                        .show();
            }

        }
    }

    /**
     * 删除按钮 视频*
     */
    protected static void dialogDelVideo(int size) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Html.fromHtml("确认删除这<font color=\"#15b4eb\"> "
                + size
                + " </font> 个视频?"));
        builder.setPositiveButton(Html.fromHtml("<font color=\"#15b4eb\"> "
                + "确认" + " </font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VideoManagerActivity.handlerViewChange.sendEmptyMessage(3);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();

    }

}

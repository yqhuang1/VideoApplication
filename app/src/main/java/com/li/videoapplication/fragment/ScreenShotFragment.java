package com.li.videoapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.video.ImageInfo;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.ScreenShotAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.utils.ImageComparator;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/29.
 * 视频管理 截图
 */
public class ScreenShotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static Context context;
    private ListView listView;
    public static ScreenShotAdapter screenShotAdapter;
    private View view;

    // 进度条
    private static ProgressDialog progressDialog;

    /**
     * 已选择，将要操作的图片列表
     */
    public static List<ImageInfo> imageListCheckToDel = null;
    /**
     * 是否第一次进入图片页
     */
    public static boolean isFirstToPic = true;
    /**
     * 图片数据 *
     */
    public static List<ImageInfo> screenShotList = new ArrayList<ImageInfo>();

    private static SharedPreferences sharedPreferences;

    // 数据库
    static VideoDB videoDB;

    public static Handler screenShotHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 取消全选
                    for (int i = 0; i < screenShotList.size(); i++) {
                        screenShotAdapter.ListDelcheck.set(i, false);
                        imageListCheckToDel.clear();
                    }
                    screenShotAdapter.notifyDataSetChanged();
                    break;

                case 2:// 刷新本地截图列表数据
                    if (context != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new getScreenShotListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new getScreenShotListTask().execute();
                        }
                    }
                    break;

                case 3://取消按钮
                    imageListCheckToDel.clear();
                    screenShotAdapter.notifyDataSetChanged();
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
    public static ScreenShotFragment newInstance(String param1, String param2) {
        ScreenShotFragment fragment = new ScreenShotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenShotFragment() {
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
        imageListCheckToDel = new ArrayList<ImageInfo>();
        // 初始化进度条
        creatProgressDialog();

        if (imageListCheckToDel != null) {
            imageListCheckToDel.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_manager, null);

        screenShotAdapter = new ScreenShotAdapter(context, screenShotList);

        listView = (ListView) view.findViewById(R.id.fragment_video_manager_list);
        listView.setAdapter(screenShotAdapter);
        listView.setOnScrollListener(new PauseOnScrollListener
                (ExApplication.imageLoader, true, true));//滑动暂停加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new getScreenShotListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new getScreenShotListTask().execute();
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 异步获取本地截图列表
     */
    private static class getScreenShotListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            screenShotList = GetPictureileName(context);
            if (screenShotList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("s".equals(s)) {
                screenShotAdapter.update(screenShotList);
            }
        }
    }

    /**
     * 获取本地图片信息
     *
     * @return
     */
    public static List<ImageInfo> GetPictureileName(Context context) {
        List<ImageInfo> list = new ArrayList<ImageInfo>();
        File file = new File(ExApplication.screenShotPath);
        // 图片路径
        String path = "";
        // 图片最后修改时间
        long lastTime = 0;

        if (!file.exists()) {
            file.mkdirs();
        }
        File[] subFile = file.listFiles();
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                if (filename.trim().toLowerCase().endsWith(".png")) {
                    ImageInfo ii = new ImageInfo();
                    path = file.getAbsolutePath() + File.separator + filename;

                    // 获取sharePreFerences中文件的最后修改时间
                    lastTime = sharedPreferences.getLong(filename, 0);

                    if (lastTime != 0) {
                        ii.setLastModified(lastTime);

                    } else {// 如果获取不到

                        lastTime = file.lastModified();// 获取文件最后修改时间
                        // 将文件最后修改时间存进sharedPreferences
                        sharedPreferences.edit().putLong(filename, lastTime)
                                .commit();
                        ii.setLastModified(lastTime);
                    }
                    ii.setPath(path);
                    ii.setDisplayName(filename);

                    list.add(ii);
                }
            }
        }
        // 图片重新排序
        Collections.sort(list, new ImageComparator());

        return list;
    }

    public static void SaveVideoToPhoto() {

        if (imageListCheckToDel.size() > 0) {
            progressDialog.setMax(imageListCheckToDel.size());
            AlertDialoshow();

        } else {

            MinUtil.showToast(context, "至少选中一个视频进行保存");
        }
    }

    /**
     * 迁移提示框
     */
    public static void AlertDialoshow() {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否将文件保存到本地相册中")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SSProgressBarAsyncTask asyntask = new SSProgressBarAsyncTask();
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
    public static class SSProgressBarAsyncTask extends
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
                for (ImageInfo i : imageListCheckToDel) {
                    mFile = new File(i.getPath());
                    fileSize = fileSize + mFile.length();
                }


                if (sdSize > fileSize) {
                    FileInputStream in;
                    BufferedInputStream bufferedIn = null;
                    BufferedOutputStream bufferedOut = null;
                    byte[] by;
                    // 开始对图片进行迁移
                    for (ImageInfo x : imageListCheckToDel) {
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
                        if (i <= imageListCheckToDel.size()) {
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

    public static class ThreadPic extends Thread {
        public void run() {
            try {
                Thread.sleep(500);
                screenShotList = new ArrayList<ImageInfo>();
                screenShotList = GetPictureileName(context);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}

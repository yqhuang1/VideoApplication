
package com.li.videoapplication.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.li.videoapplication.R;

import java.util.List;

public class DownloadListActivity extends Activity {
    public static final String DOWNLOADED = "isDownloaded";

    private static final String TAG = "DownloadListActivity";

    private ListView mDownloadingListView;

    private ListView mDownloadedListView;

    private Context mContext;

    private ImageButton mDownloadBackBtn;

    private Button mDownloadedBtn;

    private Button mDownloadingBtn;

    List<DownloadTask> mDownloadinglist;

    List<DownloadTask> mDownloadedlist;

    DownloadingAdapter mDownloadingAdapter;

    DownloadingAdapter mDownloadedAdapter;

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case 0:
//                    System.out.println("mDownloadinglist===" + mDownloadinglist.toString());
//                    if (mDownloadinglist != null) {
//                        for (DownloadTask task : mDownloadinglist) {
//                            if (task.getDownloadState().equals(DownloadState.DOWNLOADING)) {
//                                Log.i(TAG, "DOWNLOADING pause " + task.getFileName());
//                                DownloadTaskManager.getInstance(mContext).pauseDownload(task);
//                            }
//                        }
//                    } else {
//
//                    }
//                    break;
                case 1:

                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);

        mContext = this;

        mDownloadBackBtn = (ImageButton) findViewById(R.id.downloadlist_back);
        mDownloadBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DownloadListActivity) mContext).finish();
            }
        });

        mDownloadingBtn = (Button) findViewById(R.id.buttonDownloading);
        mDownloadedBtn = (Button) findViewById(R.id.buttonDownloaded);
        mDownloadingBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleView(false);
            }
        });
        mDownloadedBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleView(true);
            }
        });

        mDownloadingListView = (ListView) findViewById(R.id.downloadingListView);
        mDownloadedListView = (ListView) findViewById(R.id.downloadedListView);

        toggleView(getIntent().getBooleanExtra(DOWNLOADED, false));

        mDownloadedlist = DownloadTaskManager.getInstance(mContext).getFinishedDownloadTask();
        mDownloadedAdapter = new DownloadingAdapter(DownloadListActivity.this, 0, mDownloadedlist);

        mDownloadedListView.setAdapter(mDownloadedAdapter);
        mDownloadedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, "arg2" + arg2 + " mDownloadedlist" + mDownloadedlist.size());
                onDownloadFinishedClick(mDownloadedlist.get(arg2));
            }
        });
        mDownloadedListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                           final long arg3) {
                new AlertDialog.Builder(mContext)
                        .setItems(
                                new String[]{
                                        mContext.getString(R.string.download_delete_task),
                                        mContext.getString(R.string.download_delete_task_file)
                                }, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Toast.makeText(mContext,
                                                    R.string.download_deleted_task_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadedlist.get(arg2));
                                            mDownloadedlist.remove(arg2);
                                            mDownloadedAdapter.notifyDataSetChanged();
                                        } else if (which == 1) {
                                            Toast.makeText(mContext,
                                                    R.string.download_deleted_task_file_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadedlist.get(arg2));
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTaskFile(mDownloadedlist.get(arg2));
                                            mDownloadedlist.remove(arg2);
                                            mDownloadedAdapter.notifyDataSetChanged();
                                        }

                                    }
                                }).create().show();
                return true;
            }
        });

        // downloading list
        mDownloadinglist = DownloadTaskManager.getInstance(mContext).getDownloadingTask();
        mDownloadingAdapter = new DownloadingAdapter(DownloadListActivity.this, 0, mDownloadinglist);

        mDownloadingListView.setAdapter(mDownloadingAdapter);
        mDownloadingListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                DownloadTask task = mDownloadinglist.get(arg2);
                switch (task.getDownloadState()) {
                    case PAUSE:
                        Log.i(TAG, "PAUSE continue " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).continueDownload(task);
                        //addListener(task);
                        break;
                    case FAILED:
                        Log.i(TAG, "FAILED continue " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).continueDownload(task);
                        //addListener(task);
                        break;
                    case DOWNLOADING:
                        Log.i(TAG, "DOWNLOADING pause " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).pauseDownload(task);

                        break;
                    case FINISHED:
                        onDownloadFinishedClick(task);
                        break;
                    case INITIALIZE:

                        break;
                    default:
                        break;
                }

            }
        });

        mDownloadingListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                           final long arg3) {
                new AlertDialog.Builder(mContext)
                        .setItems(
                                new String[]{
                                        mContext.getString(R.string.download_delete_task),
                                        mContext.getString(R.string.download_delete_task_file)
                                }, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Toast.makeText(mContext,
                                                    mContext.getString(R.string.download_deleted_task_ok),
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadinglist.get(arg2));
                                            mDownloadinglist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        } else if (which == 1) {
                                            Toast.makeText(mContext,
                                                    mContext.getString(R.string.download_deleted_task_file_ok),
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadinglist.get(arg2));
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTaskFile(mDownloadinglist.get(arg2));
                                            mDownloadinglist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        }

                                    }
                                }).create().show();
                return true;
            }
        });

        for (final DownloadTask task : mDownloadinglist) {
            if (!task.getDownloadState().equals(DownloadState.FINISHED)) {
                Log.d(TAG, "add listener");
                addListener(task);
            }
        }

        //DownloadOperator.check(mContext);
    }

    private void toggleView(boolean isShowDownloaded) {
        if (isShowDownloaded) {
            mDownloadedBtn.setBackgroundResource(R.drawable.download_right_tab_selected);
            mDownloadingBtn.setBackgroundResource(R.drawable.download_left_tab);
            mDownloadedListView.setVisibility(View.VISIBLE);
            mDownloadingListView.setVisibility(View.GONE);
        } else {
            mDownloadedBtn.setBackgroundResource(R.drawable.download_right_tab);
            mDownloadingBtn.setBackgroundResource(R.drawable.download_left_tab_selected);
            mDownloadedListView.setVisibility(View.GONE);
            mDownloadingListView.setVisibility(View.VISIBLE);
        }
    }

    class MyDownloadListener implements DownloadListener {
        private DownloadTask task;

        public MyDownloadListener(DownloadTask downloadTask) {
            task = downloadTask;
        }

        @Override
        public void onDownloadFinish(String filepath) {
            Log.d(TAG, "onDownloadFinish");
            task.setDownloadState(DownloadState.FINISHED);
            task.setFinishedSize(task.getFinishedSize());
            task.setPercent(100);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                    mDownloadedAdapter.add(task);
                    mDownloadingAdapter.remove(task);
                    // toggleView(true);
                }
            });

        }

        @Override
        public void onDownloadStart() {
            task.setDownloadState(DownloadState.INITIALIZE);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadPause() {
            Log.d(TAG, "onDownloadPause");
            task.setDownloadState(DownloadState.PAUSE);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadStop() {
            Log.d(TAG, "onDownloadStop");
            task.setDownloadState(DownloadState.PAUSE);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadFail() {
            Log.d(TAG, "onDownloadFail");
            task.setDownloadState(DownloadState.FAILED);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadProgress(final long finishedSize, final long totalSize,
                                       int speed) {
            // Log.d(TAG, "download " + finishedSize);
            task.setDownloadState(DownloadState.DOWNLOADING);
            task.setFinishedSize(finishedSize);
            task.setTotalSize(totalSize);
            task.setPercent((int) (finishedSize * 100 / totalSize));
            task.setSpeed(speed);

            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void addListener(DownloadTask task) {
        DownloadTaskManager.getInstance(mContext).registerListener(task, new MyDownloadListener(task));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        toggleView(intent.getBooleanExtra(DOWNLOADED, false));

        super.onNewIntent(intent);
    }

    /**
     * You can overwrite this method to implement what you want do after download task item is clicked.
     *
     * @param task
     */
    public void onDownloadFinishedClick(DownloadTask task) {
        Log.d(TAG, task.getFilePath() + "/" + task.getFileName());
        Intent intent = DownloadOpenFile.openFile(task.getFilePath()
                + "/" + task.getFileName());
        if (null == intent) {
            Toast.makeText(mContext, R.string.download_file_not_exist, Toast.LENGTH_LONG).show();
        } else {
            mContext.startActivity(intent);
        }
    }

}

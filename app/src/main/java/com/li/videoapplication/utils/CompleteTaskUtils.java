package com.li.videoapplication.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.li.videoapplication.entity.DoTaskEntity;
import com.li.videoapplication.entity.MissionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 完成任务
 * Created by li on 2015/10/26.
 */
public class CompleteTaskUtils {

    private String task_id = "";
    private Context context;
    private String currentTime;
    private DoTaskEntity entity;
    private List<MissionEntity> list;

    public CompleteTaskUtils(Context context, String task_id) {
        this.context = context;
        this.task_id = task_id;
    }

    public void completeMission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new CompleteTaskAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new CompleteTaskAsync().execute();
        }
    }

    /**
     * 设置当前任务的最新完成时间
     */
    public void setLastCompleteMissionTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        currentTime = simpleDateFormat.format(date);
        SharePreferenceUtil.setPreference(context, task_id + "completeTime", currentTime);
    }

    /**
     * 获取当前任务的最后完成时间
     *
     * @return
     */
    public String getLastCompleteMissionTime() {
        return SharePreferenceUtil.getPreference(context, task_id + "completeTime");
    }


    public class CompleteTaskAsync extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            entity = JsonHelper.doMission117(task_id);
            if (entity != null) {
                if (entity.getaNum().equals(entity.getmNum())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                int pageId = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetMissionTask(task_id, pageId + "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetMissionTask(task_id, pageId + "").execute();
                }
            }
        }
    }

    /**
     * 获取任务列表异步请求
     */
    private class GetMissionTask extends AsyncTask<Void, Void, MissionEntity> {

        String task_id = "";
        String page = "";

        public GetMissionTask(String task_id, String page) {
            this.page = page;
            this.task_id = task_id;
        }

        @Override
        protected MissionEntity doInBackground(Void... params) {
            list = JsonHelper.getMissionList("", page);
            if (list != null) {
                for (MissionEntity missionEntity : list) {
                    if (missionEntity.getId().equals(task_id)) {
                        SharePreferenceUtil.setPreference(context, task_id + "task_flag", "已完成");
                        return missionEntity;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MissionEntity entity) {
            super.onPostExecute(entity);
            if (entity != null) {
                ToastUtils.showToast(context, "完成 " + entity.getTaskTypeName() + "：" + entity.getTitle()
                        + "，获取 " + entity.getReward());
            }
        }
    }


}

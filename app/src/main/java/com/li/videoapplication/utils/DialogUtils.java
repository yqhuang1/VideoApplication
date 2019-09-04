package com.li.videoapplication.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.li.videoapplication.R;

import java.io.File;

/**
 * Created by li on 2014/7/29.
 */
public class DialogUtils {

    public static Dialog loadingDialog = null;
    public static Dialog dialog = null;

//    public static void showWaitDialog(Context context){
//        dialog=new AlertDialog.Builder(context)
//                .setView(new ProgressBar(context))
//                .create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static void createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.home_loading_gif);
//        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使得ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
//        tipTextView.setText(msg);// 设置加载信息

        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.show();
    }

    public static void cancelLoadingDialog() {
        loadingDialog.cancel();
    }


    /**
     * 自定义提示框
     *
     * @param context
     * @param msg
     */
    public static void createAlterDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.alter_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.alter_dialog_view);// 加载布局
        TextView tipTextView = (TextView) v.findViewById(R.id.alter_dialog_msg);// 提示文字

        tipTextView.setText(msg);

        dialog = new Dialog(context, R.style.alter_dialog);// 创建自定义样式dialog

        dialog.setCancelable(true);// 可以用“返回键”取消
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        dialog.show();

    }

    public static void cancelAlterDialog() {
        dialog.cancel();
    }

    public static AlertDialog.Builder builder;

    /**
     * 提示下载安装
     *
     * @param context
     * @param path
     */
    public static void downloadDialog(final Context context, final String path) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(path)),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

//    public static void cancelWaitDialog(){
//        dialog.cancel();
//    }

    /**
     * 录音窗口
     */
    public Dialog recordDialog = null;

    public void showRecordDialog(Context context, String title) {
        recordDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(new ProgressBar(context))
                .create();
        recordDialog.show();

    }

    public void cancelRecordDialog() {
        recordDialog.cancel();
    }
}

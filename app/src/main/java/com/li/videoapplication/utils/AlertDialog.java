package com.li.videoapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li.videoapplication.R;

import java.io.File;

/**
 * 自定义安装提示对话框
 * Created by feimoLeo on 2015/1/26.
 */
public class AlertDialog extends Activity implements View.OnClickListener,View.OnTouchListener{
    Context context;
    android.app.AlertDialog ad;
    TextView titleView;
    TextView messageView;
    TextView cancel;
    TextView ensure;
    String path,title,msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.install_dialog);
        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        msg=intent.getStringExtra("msg");
        path=intent.getStringExtra("path");
        init();
        initDate();
    }

    public void init(){
        titleView=(TextView)findViewById(R.id.install_dialog_title);
        messageView=(TextView)findViewById(R.id.install_dialog_msg);
        cancel=(TextView)findViewById(R.id.install_dialog_cancel);
        ensure=(TextView)findViewById(R.id.install_dialog_ensure);
        cancel.setOnClickListener(this);
        ensure.setOnClickListener(this);
        cancel.setOnTouchListener(this);
        ensure.setOnTouchListener(this);
    }

    public void initDate(){
        titleView.setText(title);
        messageView.setText(msg);
    }

//    public AlertDialog(Context context,String path) {
//        // TODO Auto-generated constructor stub
//        this.path=path;
//        this.context=context;
//        ad=new android.app.AlertDialog.Builder(context).create();
//        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        ad.show();
//        //关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
//        Window window = ad.getWindow();
//        window.setContentView(R.layout.install_dialog);
//        window.setGravity(Gravity.CENTER);
//        titleView=(TextView)window.findViewById(R.id.install_dialog_title);
//        messageView=(TextView)window.findViewById(R.id.install_dialog_msg);
//        cancel=(TextView)window.findViewById(R.id.install_dialog_cancel);
//        ensure=(TextView)window.findViewById(R.id.install_dialog_ensure);
//        cancel.setOnClickListener(this);
//        ensure.setOnClickListener(this);
//        cancel.setOnTouchListener(this);
//        ensure.setOnTouchListener(this);
//
//    }


    /**
     * 关闭对话框
     */
    public void dismiss() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.install_dialog_cancel:
                dismiss();
                break;
            case R.id.install_dialog_ensure:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(path)),
                            "application/vnd.android.package-archive");
                MyApplication.getAppContext().startActivity(intent);
                dismiss();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            v.setBackgroundColor(Color.rgb(211,211,211));
        }
        if (event.getAction()==MotionEvent.ACTION_UP){
            v.setBackgroundColor(Color.rgb(255,255,255));
        }
        return false;
    }
}

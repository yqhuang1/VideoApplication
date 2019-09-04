package com.fmscreenrecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fmscreenrecord.floatview.FloatContentView;
import com.fmscreenrecord.service.FloatViewService;
import com.li.videoapplication.R;

public class StartBtnActivity extends Activity implements View.OnClickListener{

    private Button startBtn;
    private Button startLPDS;

    private SharedPreferences sp;
    static Intent intentfloatserver;
    public static boolean startFloatService = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_btn);

        startBtn = (Button) findViewById(R.id.start_button);
        startLPDS = (Button) findViewById(R.id.LPDS_button);

        startBtn.setOnClickListener(this);
        startLPDS.setOnClickListener(this);

        //长按开启设置页面
        startBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StartBtnActivity.this, com.fmscreenrecord.activity.SettingActivity.class);
                startActivity(intent);
                return true;
            }
        });

        sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_button:
                Toast.makeText(this, "长按开启设置页面", Toast.LENGTH_SHORT).show();
                boolean str = false;
                str = sp.getBoolean("FirstTimeUse", true);

                boolean notify = sp.getBoolean("PackageInfoGridviewNotify",
                        false);
                // 是否已经勾选不再弹出应用列表
                if (!notify) {
                    PackageInfoGridview.isInPackageInfo = false;
                    // 直接打开浮窗

                    intentfloatserver = new Intent();
                    intentfloatserver.setClass(StartBtnActivity.this,
                            FloatViewService.class);
                    startService(intentfloatserver);

                } else {// 打开第三方应用列表
                    Intent intent = new Intent(StartBtnActivity.this, PackageInfoGridview.class);
                    startActivity(intent);
                }

                startFloatService = true;

                StartBtnActivity.this.finish();

                FloatContentView.isFirstTimeUse = true;
                break;

            case R.id.LPDS_button:
                Intent intent = new Intent();
                intent.setClass(StartBtnActivity.this,
                        SettingActivity.class);
                startActivity(intent);
                finish();
                break;
        }


    }
}

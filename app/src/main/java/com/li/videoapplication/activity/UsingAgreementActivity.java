package com.li.videoapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.li.videoapplication.R;

/**
 * Created by feimoyuangong on 2015/5/28.
 * 隐私及免责声明 页面
 */
public class UsingAgreementActivity extends Activity {

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_using_agreement);
        backBtn = (ImageButton) findViewById(R.id.using_agreement_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

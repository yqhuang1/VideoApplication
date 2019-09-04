package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.utils.SharePreferenceUtil;
import com.umeng.message.PushAgent;

import java.io.File;
import java.util.ArrayList;

/**
 * 引导页面*
 */
public class IntroduceActivity extends Activity implements View.OnClickListener {

    private Context context;
    public ViewPager intro_vPager;
    public ImageView pg00, pg11, pg22, pg33;
    private Button goBtn;
    public int curr_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce);
        context = IntroduceActivity.this;
        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        intro_vPager = (ViewPager) findViewById(R.id.introduce_viewpager);
        pg00 = (ImageView) findViewById(R.id.introduce_page_now);
        pg11 = (ImageView) findViewById(R.id.introduce_page1);
        pg22 = (ImageView) findViewById(R.id.introduce_page2);
        pg33 = (ImageView) findViewById(R.id.introduce_page3);

        intro_vPager.setOnPageChangeListener(new Mypagechane());

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View view1 = layoutInflater.inflate(R.layout.introduce_01, null);
        View view2 = layoutInflater.inflate(R.layout.introduce_02, null);
        View view3 = layoutInflater.inflate(R.layout.introduce_03, null);
        View view4 = layoutInflater.inflate(R.layout.introduce_04, null);

        goBtn = (Button) view4.findViewById(R.id.introudce_button);
        goBtn.setOnClickListener(this);

        //添加view的arraylist
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }////Object��view�Ƿ�Ϊͬһ��view

            @Override
            public int getCount() {

                return views.size();
            }


            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }////��ǰview����Ҫ��ʱ����մ���

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }

            @Override
            public void finishUpdate(View arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public Parcelable saveState() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void startUpdate(View arg0) {
                // TODO Auto-generated method stub

            }


        };
        intro_vPager.setAdapter(pagerAdapter);

        /**初始化数据库**/
        VideoDB videoDB = new VideoDB(getApplicationContext());
        videoDB.InitDBData(new File("/mnt/sdcard/LuPingDaShi/"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.introudce_button:
                startButton(v);
                break;
        }
    }

    public class Mypagechane implements OnPageChangeListener {


        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }


        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }


        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 0:
                    pg00.setImageDrawable(getResources().getDrawable(R.drawable.active));
                    pg11.setImageDrawable(getResources().getDrawable(R.drawable.inactive));
                    break;

                case 1:
                    pg11.setImageDrawable(getResources().getDrawable(R.drawable.active));
                    pg00.setImageDrawable(getResources().getDrawable(R.drawable.inactive));
                    break;
                case 2:
                    pg22.setImageDrawable(getResources().getDrawable(R.drawable.active));
                    pg11.setImageDrawable(getResources().getDrawable(R.drawable.inactive));
                    break;
                case 3:
                    pg33.setImageDrawable(getResources().getDrawable(R.drawable.active));
                    pg22.setImageDrawable(getResources().getDrawable(R.drawable.inactive));
            }
            curr_index = arg0;
        }

    }

    public void startButton(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        IntroduceActivity.this.finish();
        SharePreferenceUtil.setPreference(this, "isFirst", "1");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

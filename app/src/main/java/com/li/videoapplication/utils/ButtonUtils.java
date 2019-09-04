package com.li.videoapplication.utils;

import android.view.MotionEvent;
import android.view.View;

import com.li.videoapplication.R;

/**
 * Created by user on 2014/12/5.
 */
public class ButtonUtils {

    //按钮点击效果
    public static void buttonEffect(View v,final int downId,final int upId){
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.setBackgroundResource(downId);
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.setBackgroundResource(upId);
                }
                return false;
            }
        });
    }
}

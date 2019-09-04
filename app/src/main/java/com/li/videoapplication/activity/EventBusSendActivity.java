package com.li.videoapplication.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.EventBusMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EventBusSendActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus_send);

        //1. 注册EventBus,参数是上下文.        注意:导入的EventBus请认准org.greenrobot.
        //注意:有注册就必须有解注册(一般在OnDestroy里执行解注册操作),防止内存泄漏,注册一个界面只能注册一次,否则报错
        //关于源码我只强调一点:EventBus拿到订阅方法,无法两种手段:1注解  2:反射
        EventBus.getDefault().register(this);

        //4. 使用EventBus发送事件,使用Post方法,参数也必须是EventBus消息对且要和接收的保持一致
        EventBus.getDefault().post(new EventBusMessage("易宸锋,易大师好帅"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(EventBusMessage eventBusMessage) {
        //在TextView显示接收的消息,从这个类里拿属性.
        Log.d("eventBusThread", "ThreadMode.MAIN " + Thread.currentThread().getName());

    }


    //ThreadMode.POSTING表示该方法和消息发送方在同一个线程.
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void MessageEventBus1(EventBusMessage eventBusMessage) {
        Log.d("eventBusThread", "ThreadMode.POSTING " + Thread.currentThread().getName());

    }


    /*ThreadMode.ASYNC也表示在后台执行(也就是子线程执行),可以异步并发处理
    (适用于多个线程任务处理,内部有线程池管理,比如请求网络时,用这个方法,他会自动创建线程去请求)
    无论发布者是在子线程还是主线程,该方法都会创建一个子线程,在子线程执行.*/
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void MessageEventBus2(EventBusMessage eventBusMessage) {
        Log.d("eventBusThread", "ThreadMode.ASYNC " + Thread.currentThread().getName());

    }


    //ThreadMode.BACKGROUND表示该方法在后台运行(也就是子线程),不能够并发处理
    //如果发布者在子线程,那么该方法就在子线程执行
    //如果发布者在主线程,那么该方法就会创建一个子线程,在子线程运行.
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void MessageEventBus3(EventBusMessage eventBusMessage) {
        Log.d("eventBusThread", "ThreadMode.BACKGROUND " + Thread.currentThread().getName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

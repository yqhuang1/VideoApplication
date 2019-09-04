package com.li.videoapplication.entity;

/**
 * Created by lenovo on 2017/6/29.
 */

public class EventBusMessage {

    //定义了发送的消息必须是String.
    public String Message;

    public EventBusMessage(String message) {
        this.Message = message;
    }

}

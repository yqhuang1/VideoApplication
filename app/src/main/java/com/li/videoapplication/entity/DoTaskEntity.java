package com.li.videoapplication.entity;

/**
 * 任务进行类
 * Created by Administrator on 2015/10/26 0026.
 */
public class DoTaskEntity {
    private boolean result = false;
    private String aNum = "";//任务总数
    private String mNum = "";//任务进行数
    private String msg = "";

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getaNum() {
        return aNum;
    }

    public void setaNum(String aNum) {
        this.aNum = aNum;
    }

    public String getmNum() {
        return mNum;
    }

    public void setmNum(String mNum) {
        this.mNum = mNum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

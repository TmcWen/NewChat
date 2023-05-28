package com.example.newchat.chat.unit;

import android.net.Uri;

public class Msg {

    /**
     * msg 为消息内容
     * headImage 为头像
     * isMs 为是否为’我‘发送
     */

    private String msg;
    private Uri headImage;
    private boolean isMe;

    public Msg(String msg, Uri headImage, boolean isMe) {
        this.msg = msg;
        this.headImage = headImage;
        this.isMe = isMe;
    }

    public Msg() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Uri getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Uri headImage) {
        this.headImage = headImage;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}

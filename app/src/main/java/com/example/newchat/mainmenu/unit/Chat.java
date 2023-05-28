package com.example.newchat.mainmenu.unit;

import android.net.Uri;

public class Chat {

    /*
    * 最近聊天记录
    * otherId为对方id
    * */

        private String name,lastMsg,otherId;
        private Uri headImage;
        private int isMultiple;

    public Chat(String name, String lastMsg, String otherId, Uri headImage, int isMultiple) {
        this.name = name;
        this.lastMsg = lastMsg;
        this.otherId = otherId;
        this.headImage = headImage;
        this.isMultiple = isMultiple;
    }

    public Chat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Uri getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Uri headImage) {
        this.headImage = headImage;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public int getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(int isMultiple) {
        this.isMultiple = isMultiple;
    }
}

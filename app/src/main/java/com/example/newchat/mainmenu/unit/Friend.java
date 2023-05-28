package com.example.newchat.mainmenu.unit;

import android.net.Uri;

public class Friend {
    private String name;
    private Uri headImage;
    private String friendId;
    private int isFriend;
    private int isMultiple;
    private int online;

    public Friend(String name, Uri headImage, String friendId, int isFriend, int isMultiple, int online) {
        this.online = online;
        this.name = name;
        this.headImage = headImage;
        this.friendId = friendId;
        this.isFriend = isFriend;
        this.isMultiple = isMultiple;
    }

    public Friend() {
    }

    public int getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(int isMultiple) {
        this.isMultiple = isMultiple;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Uri headImage) {
        this.headImage = headImage;
    }

    public String getfriendId() {
        return friendId;
    }

    public void setfriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}

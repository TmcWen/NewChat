package com.example.newchat.unit;

public class User {

    String id;
    String name;
    int headImage;
    int backGround;
    String age;
    String sex;
    String address;
    String birthday;
    String slogan;

    public User(String id, String name, int headImage, String age, String sex, String address, String birthday, String slogan) {
        this.id = id;
        this.name = name;
        this.headImage = headImage;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.birthday = birthday;
        this.slogan = slogan;
    }

    public User(String id, String name, int headImage, int backGround, String age, String sex, String address, String birthday, String slogan) {
        this.id = id;
        this.name = name;
        this.headImage = headImage;
        this.backGround = backGround;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.birthday = birthday;
        this.slogan = slogan;
    }

    public User() {
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeadImage() {
        return headImage;
    }

    public void setHeadImage(int headImage) {
        this.headImage = headImage;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getBackGround() {
        return backGround;
    }

    public void setBackGround(int backGround) {
        this.backGround = backGround;
    }
}

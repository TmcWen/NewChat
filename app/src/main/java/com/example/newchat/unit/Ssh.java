package com.example.newchat.unit;

import android.util.Log;

import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Ssh {

    public void firstReg(String uId) {
        ChannelSftp c = getC();
        String serverBackgroundFile = "/home/user/userBackground/background" + uId + ".jpg";
        String serverHeadFile = "/home/user/userHead/head" + uId + ".jpg";
        File file = new File("/data/user/0/com.example.newchat/cache/background" + uId + ".jpg");
        File file1 = new File("/data/user/0/com.example.newchat/cache/head" + uId + ".jpg");
        try {
            file.createNewFile();
            file1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("1", "初始化头像背景失败");
        }
        try {
            c.put("/data/user/0/com.example.newchat/cache/background" + uId + ".jpg", serverBackgroundFile);
            c.put("/data/user/0/com.example.newchat/cache/head" + uId + ".jpg", serverHeadFile);
        } catch (SftpException e) {
            e.printStackTrace();
            Log.e("1", "初始化头像背景上传失败");
        }

    }

    public void getUserBackground(String user_id, long uptime) {
        ChannelSftp c = getC();
        String serverFile = "/home/user/userBackground/background" + user_id + ".jpg";
        String local = "/data/user/0/com.example.newchat/files/background" + user_id + ".jpg";
        File file = new File(local);
        if (file.exists()) {
            MysqlDatabase mysqlDatabase = new MysqlDatabase();
            long sqltime = mysqlDatabase.getBackgroundUpdateTime(user_id);
            //如果数据库时间大于本地时间则下载服务器新的文件
            if (sqltime > uptime) {
                try {
                    c.get(serverFile, local);
                    System.out.println("成功下载id: " + user_id + "背景");
                } catch (SftpException e) {
                    System.out.println("失败下载id: " + user_id + "背景");
                    e.printStackTrace();
                }
            }
        }else {
            try {
                c.get(serverFile, local);
                System.out.println("成功下载id: " + user_id + "背景");
            } catch (SftpException e) {
                System.out.println("失败下载id: " + user_id + "背景");
                e.printStackTrace();
            }
        }
    }

    public void putUserBackground(String file) {
        ChannelSftp c = getC();
        String serverFile = "/home/user/userBackground/" + "background" + MainMenuActivity.USER_ID + ".jpg";
        try {
            c.put(file, serverFile);
            System.out.println("成功上传id: " + MainMenuActivity.USER_ID + "背景");
        } catch (SftpException e) {
            System.out.println("失败上传id: " + MainMenuActivity.USER_ID + "背景");
            e.printStackTrace();
        }
    }

    public void getUserHead(String user_id, long uptime) {
        ChannelSftp c = getC();
        String serverFile = "/home/user/userHead/head" + user_id + ".jpg";
        String local = "/data/user/0/com.example.newchat/files/head" + user_id + ".jpg";
        File file = new File(local);
        if (file.exists()) {
            MysqlDatabase mysqlDatabase = new MysqlDatabase();
            long sqltime = mysqlDatabase.getHeadUpdateTime(user_id);
            if (sqltime > uptime) {
                try {
                    c.get(serverFile, local);
                    System.out.println("成功下载id: " + user_id + "头像");
                } catch (SftpException e) {
                    System.out.println("失败下载id: " + user_id + "头像");
                    e.printStackTrace();
                }
            }
        } else {
            try {
                c.get(serverFile, local);
                System.out.println("成功下载id: " + user_id + "头像");
            } catch (SftpException e) {
                System.out.println("失败下载id: " + user_id + "头像");
                e.printStackTrace();
            }
        }
    }

    public void putUserHead(String file) {
        ChannelSftp c = getC();
        String serverFile = "/home/user/userHead/" + "head" + MainMenuActivity.USER_ID + ".jpg";
        try {
            c.put(file, serverFile);
            System.out.println("成功上传id: " + MainMenuActivity.USER_ID + "头像");
        } catch (SftpException e) {
            System.out.println("失败上传id: " + MainMenuActivity.USER_ID + "头像");
            e.printStackTrace();
        }
    }

    private ChannelSftp getC() {
        String user = "user";
        String password = "123456";
        String host = "127.0.0.1";
        int port = 1414;

        JSch jSch = new JSch();
        Session jsSession = null;
        ChannelSftp ch = null;

        try {
            jsSession = jSch.getSession(user, host, port);
            jsSession.setPassword(password);
            Properties c = new Properties();
            c.put("StrictHostKeyChecking", "no");
            jsSession.setConfig(c);
            jsSession.setTimeout(3000);
            jsSession.connect();
            ch = (ChannelSftp) jsSession.openChannel("sftp");
            ch.connect();
            ch.setFilenameEncoding("UTF-8");
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return ch;
    }

}

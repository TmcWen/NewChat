package com.example.newchat.db.info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newchat.R;
import com.example.newchat.chat.MsgActivity;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.unit.Ssh;
import com.example.newchat.unit.User;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewUserInfoName,textViewUserInfoUid,textViewUserInfoSlogan;
    private Button buttonUserInfoAddFriend,buttonUserInfoSendMsg;
    private ImageView imageViewUserInfoHeadImage,imageViewUserInfoBackground;
    private Intent i;
    private boolean friend = false;
    private User u;
    private CountDownLatch countDownLatch;
    private String other_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        init();
        search();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        button();
        textViewUserInfoUid.setText("Uid: " + u.getId().toString());
        textViewUserInfoName.setText(u.getName());
        textViewUserInfoSlogan.setText(u.getSlogan());
        File file = new File("/data/user/0/com.example.newchat/files/head" + u.getId() + ".jpg");
        Uri uri = Uri.parse(file.getPath());
        imageViewUserInfoHeadImage.setImageURI(uri);
        imageViewUserInfoHeadImage.setOnClickListener(this);
        File file1 = new File("/data/user/0/com.example.newchat/files/background" + u.getId() + ".jpg");
        Uri uri1 = Uri.parse(file1.getPath());
        imageViewUserInfoBackground.setImageURI(uri1);
        imageViewUserInfoBackground.setOnClickListener(this);
    }

    private void button() {
        buttonUserInfoSendMsg.setOnClickListener(this);
        buttonUserInfoAddFriend.setOnClickListener(this);
    }

    private void search() {
        new Thread(() -> {
            MysqlDatabase db = new MysqlDatabase();
            u = db.selectUser(i.getStringExtra("other_id"), 0);
            friend = db.selectFriend(MainMenuActivity.USER_ID, i.getStringExtra("other_id"));
            countDownLatch.countDown();
        }).start();
    }

    private void init(){
        u = new User();
        countDownLatch = new CountDownLatch(1);
        textViewUserInfoName = findViewById(R.id.textViewUserInfoName);
        textViewUserInfoUid = findViewById(R.id.textViewUserInfoUid);
        textViewUserInfoSlogan = findViewById(R.id.textViewUserInfoSlogan);
        buttonUserInfoAddFriend = findViewById(R.id.buttonUserInfoAddFriend);
        buttonUserInfoSendMsg = findViewById(R.id.buttonUserInfoSendMsg);
        imageViewUserInfoHeadImage = findViewById(R.id.imageViewUserInfoHeadImage);
        imageViewUserInfoBackground = findViewById(R.id.imageViewUserInfoBackground);
        i = getIntent();
        other_id = i.getStringExtra("other_id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUserInfoSendMsg:
                if (other_id.equals(MainMenuActivity.USER_ID)){
                    System.out.println("该用户为自己");
                } else {
                    if (friend) {
                        Intent intent = new Intent(this, MsgActivity.class);
                        intent.putExtra("other_id", i.getStringExtra("friend_id"));
                        startActivity(intent);
                        break;
                    }else {
                        Toast.makeText(this, "您还不是对方的好友哦", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            case R.id.buttonUserInfoAddFriend:
                if (other_id.equals(MainMenuActivity.USER_ID)){
                    System.out.println("该用户为自己");
                } else {
                    if (friend) {
                        Toast.makeText(this, "您已经是对方好友了，请勿重复添加", Toast.LENGTH_SHORT).show();
                    }else {
                        //添加好友功能
                        MysqlDatabase db = new MysqlDatabase();
                        AtomicBoolean is = new AtomicBoolean(true);
                        new Thread(() -> {
                            if (!db.selectFriend(MainMenuActivity.USER_ID, u.getId())) {
                                db.addFriend(MainMenuActivity.USER_ID, other_id, 0);
                            } else {
                                is.set(false);
                            }
                            countDownLatch.countDown();
                        }).start();
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!is.get()) {
                            Toast.makeText(this, "请勿重复添加", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.imageViewUserInfoHeadImage:
                new Thread(() -> {
                    Ssh ssh = new Ssh();
                    ssh.getUserHead(other_id, 0);
                }).start();
                Toast.makeText(this, "开始更新" + other_id + "头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageViewUserInfoBackground:
                new Thread(() -> {
                    Ssh ssh = new Ssh();
                    ssh.getUserBackground(other_id, 0);
                }).start();
                break;
        }
    }
}
package com.example.newchat.db.info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.db.info.adapter.NewFriendAdapter;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.mainmenu.unit.Friend;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NewFriendActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private View titleBar;
    private NewFriendAdapter adapter;
    private Context context;
    private List<Friend> list = null;
    private ImageView imageViewExit,imageViewMore;
    private TextView textViewNewFriend;
    private List<Friend> friendList = null;
    private CountDownLatch countDownLatch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        init();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        imageViewExit.setOnClickListener(this);
        adapter = new NewFriendAdapter((LinkedList<Friend>) list, context);
        listView.setAdapter(adapter);
        for (int i = 0; i < friendList.size(); i++) {
            if (friendList.get(i).getIsFriend() == 0) {
                adapter.add(friendList.get(i));
            }
        }

    }

    private void init() {
        countDownLatch = new CountDownLatch(1);
        list = new LinkedList<>();
        friendList = new LinkedList<>();
        listView = findViewById(R.id.listViewNewFriend);
        titleBar = findViewById(R.id.includeNewFriend);
        imageViewMore = titleBar.findViewById(R.id.imageViewTitleBarMore);
        imageViewMore.setVisibility(View.GONE);
        imageViewExit = titleBar.findViewById(R.id.imageViewTitleBar);
        textViewNewFriend = titleBar.findViewById(R.id.textViewTitleBar);
        textViewNewFriend.setText("新朋友");
        context = this;
        SharedPreferences s = getSharedPreferences("saveInfo", MODE_PRIVATE);
        MysqlDatabase db = new MysqlDatabase();
        new Thread(() -> {
            friendList = db.selectNewFriendList(MainMenuActivity.USER_ID);
            countDownLatch.countDown();
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewTitleBar:
                finish();
                break;
        }
    }
}
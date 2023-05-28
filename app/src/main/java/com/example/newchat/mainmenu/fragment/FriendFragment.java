package com.example.newchat.mainmenu.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.newchat.R;
import com.example.newchat.chat.MsgActivity;
import com.example.newchat.chat.MultiMsgActivity;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.db.info.NewFriendActivity;
import com.example.newchat.db.info.UserInfoActivity;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.mainmenu.adapter.FriendAdapter;
import com.example.newchat.mainmenu.unit.Friend;
import com.example.newchat.unit.User;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FriendFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {

    private ListView listViewFriend;
    private List<Friend> list = null;
    private List<Friend> friendList = null;
    private Context context;
    private FriendAdapter adapter = null;
    private View root;
    private ImageButton imageButtonFriendSearch,imageButtonNewFriend;
    private LayoutInflater i;
    private CountDownLatch countDownLatch;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friend, container, false);
        init();
        adapter = new FriendAdapter((LinkedList<Friend>) list, context);
        listViewFriend.setAdapter(adapter);
        listViewFriend.setOnItemClickListener(this);
        listViewFriend.setOnItemLongClickListener(this);
        imageButtonFriendSearch.setOnClickListener(this);
        imageButtonNewFriend.setOnClickListener(this);
        i = inflater;
        return root;
    }

    private void init() {
        handler = new Handler();
        countDownLatch = new CountDownLatch(1);
        listViewFriend = root.findViewById(R.id.listViewFriend);
        imageButtonFriendSearch = root.findViewById(R.id.imageButtonFriendSearch);
        imageButtonNewFriend = root.findViewById(R.id.imageButtonNewFriend);
        context = this.getContext();
        list = new LinkedList<>();
        friendList = new LinkedList<>();
        sharedPreferences = context.getSharedPreferences("userImage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Friend friend = (Friend) adapter.getItem(i);
        MysqlDatabase database = new MysqlDatabase();
        if (friend.getIsMultiple() == 1) {
            //多人先不写
            new Thread(() -> {
                database.insertChatList(MainMenuActivity.USER_ID, friend.getfriendId(), 1);
            }).start();
            Intent intent = new Intent(context, MultiMsgActivity.class);
            intent.putExtra("other_id", friend.getfriendId());
            intent.putExtra("name", friend.getName());
            startActivity(intent);
        } else {
            new Thread(() -> {
                database.insertChatList(MainMenuActivity.USER_ID, friend.getfriendId(), 0);
            }).start();
            Intent intent = new Intent(context, MsgActivity.class);
            intent.putExtra("other_id", friend.getfriendId());
            intent.putExtra("name", friend.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonFriendSearch:
                //点击用户搜索
                //绑定自定义dialog
                View dialogView = i.inflate(R.layout.dialog_chat, null, false);
                EditText editTextDialog = dialogView.findViewById(R.id.editTextDialog);
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                //绑定自定义xml
                b.setView(dialogView);
                b.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editTextDialog.getText().toString().trim().equals("")) {
                            new Thread(() -> {
                                //子线程查询数据，并且等待返回数据
                                MysqlDatabase db = new MysqlDatabase();
                                if (db.selectUserId(editTextDialog.getText().toString())) {
                                    //拿到返回数据，打开用户信息界面
                                    Intent i = new Intent(context, UserInfoActivity.class);
                                    User u = db.selectUser(editTextDialog.getText().toString(), 0);
                                    i.putExtra("user_id", u.getId());
                                    i.putExtra("other_id", editTextDialog.getText().toString());
                                    startActivity(i);
                                }
                            }).start();
                        }else {
                            Toast.makeText(context, "请输入正确的id", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setTitle("用户搜索");
                b.show();
                break;
            case R.id.imageButtonNewFriend:
                //打开新朋友界面
                Intent intent = new Intent(context, NewFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        System.out.println("进入好友");
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10000);
                adapter.clear();
                MysqlDatabase db = new MysqlDatabase();
                new Thread(() -> {
                    friendList = db.selectFriendList(MainMenuActivity.USER_ID);
                    countDownLatch.countDown();
                }).start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < friendList.size(); j++) {
                    if (friendList.get(j).getIsFriend() == 1) {
                        adapter.add(friendList.get(j));
                    }
                }
            }
        };
        handler.post(runnable);
        super.onResume();
    }

    @Override
    public void onStop() {
        System.out.println("退出好友");
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    //todo
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setTitle("是否删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(() -> {
                            MysqlDatabase db = new MysqlDatabase();
                            db.deleteFriend(friendList.get(position).getfriendId());
                            db.deleteChatList(friendList.get(position).getfriendId(), friendList.get(position).getIsMultiple());
                            countDownLatch.countDown();
                        }).start();
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();

        return false;
    }
}

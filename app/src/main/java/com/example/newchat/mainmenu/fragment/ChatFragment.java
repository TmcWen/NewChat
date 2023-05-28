package com.example.newchat.mainmenu.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.newchat.R;
import com.example.newchat.chat.MsgActivity;
import com.example.newchat.chat.MultiMsgActivity;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.mainmenu.unit.Chat;
import com.example.newchat.mainmenu.adapter.ChatAdapter;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listViewChat;
    private List<Chat> list = null;
    private List<Chat> chatList = null;
    private Context context;
    private ChatAdapter adapter = null;
    private View root;
    private ImageView imageViewChat;
    private Intent intent;
    private SQLiteDatabase db;
    CountDownLatch countDownLatch;
    private Handler handler;
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chat, container, false);
        init();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new ChatAdapter((LinkedList<Chat>) list, context);
        listViewChat.setAdapter(adapter);
        listViewChat.setOnItemClickListener(this);
        listViewChat.setOnItemLongClickListener(this);
        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = inflater.inflate(R.layout.dialog_create_multiple, null, false);
                EditText editTextDialogName = dialogView.findViewById(R.id.editTextDialogName);
                EditText editTextDialogSlogan = dialogView.findViewById(R.id.editTextDialogSlogan);
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setView(dialogView);
                b.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AtomicInteger i = new AtomicInteger();
                        if (!editTextDialogName.getText().toString().trim().equals("")) {
                            new Thread(() -> {
                                MysqlDatabase db = new MysqlDatabase();
                                i.set(db.insertMMsg(editTextDialogName.getText().toString(), editTextDialogSlogan.getText().toString()));
                                countDownLatch.countDown();
                            }).start();
                            try {
                                countDownLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (i.get() == 0) {
                                Toast.makeText(context, "名称以存在，请换一个", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(context, "名称不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setTitle("创建多人聊天");
                b.show();
            }
        });
        return root;

    }

    private void init() {
        handler = new Handler();
        countDownLatch = new CountDownLatch(1);
        listViewChat = root.findViewById(R.id.listViewChat);
        imageViewChat = root.findViewById(R.id.imageButtonChat);
        context = this.getContext();
        list = new LinkedList<>();
        chatList = new LinkedList<>();
        intent = getActivity().getIntent();
        MysqlDatabase database = new MysqlDatabase();
        new Thread(() -> {
            list = database.selectChatList(MainMenuActivity.USER_ID);
            countDownLatch.countDown();
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        System.out.println("进入聊天");
        Chat chat = (Chat) adapter.getItem(i);
        if (chat.getIsMultiple() == 0) {
            Intent intent = new Intent(context, MsgActivity.class);
            intent.putExtra("other_id", chat.getOtherId());
            intent.putExtra("name", chat.getName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(context, MultiMsgActivity.class);
            intent.putExtra("other_id", chat.getOtherId());
            intent.putExtra("name", chat.getName());
            startActivity(intent);
        }
//        System.out.println("other_id" + chat.getOther_id());
    }

    @Override
    public void onResume() {
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                MysqlDatabase database = new MysqlDatabase();
                new Thread(() -> {
                    chatList = database.selectChatList(MainMenuActivity.USER_ID);
                    countDownLatch.countDown();
                }).start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (chatList.size() != list.size()) {
                    if (chatList.size() > list.size()) {
                        List<Chat> l = chatList.subList(list.size(), chatList.size());
                        for (int i = 0; i < chatList.size() - list.size(); i++) {
                            adapter.add(l.get(i));
                        }
                    } else {
                        adapter.clear();
                        for (int i = 0; i < chatList.size(); i++) {
                            adapter.add(chatList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        handler.post(runnable);
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setTitle("是否删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MysqlDatabase database = new MysqlDatabase();
                        new Thread(() -> {
                            database.deleteChatList(list.get(position).getOtherId(), list.get(position).getIsMultiple());
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

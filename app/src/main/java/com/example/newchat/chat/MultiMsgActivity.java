package com.example.newchat.chat;

import static android.view.LayoutInflater.*;
import static com.example.newchat.mainmenu.MainMenuActivity.USER_ID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newchat.R;
import com.example.newchat.chat.adapter.MsgAdapter;
import com.example.newchat.chat.unit.Msg;
import com.example.newchat.db.MysqlDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;

public class MultiMsgActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextMultiMsgSendMsg;
    private Button buttonMultiMsgSend;
    private ImageButton imageButtonMultiMsg;
    private View bar;
    private ImageView imageViewExit,imageViewMore;
    private ListView listViewMsg;
    private List<Msg> list = null;
    private List<Msg> msgList = null;
    private Context context;
    private MsgAdapter adapter = null;
    private Runnable runnable;
    private Handler handler;
    private Intent intent;
    private String other_id;
    private CountDownLatch countDownLatch;
    private TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_msg);

        init();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new MsgAdapter((LinkedList<Msg>) list, context);
        listViewMsg.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //初始按钮
        imageButtonMultiMsg.setVisibility(View.VISIBLE);
        buttonMultiMsgSend.setVisibility(View.GONE);
        editTextMultiMsgSendMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //监听输入框内容
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextMultiMsgSendMsg.getText().toString().equals("")){
                    imageButtonMultiMsg.setVisibility(View.VISIBLE);
                    buttonMultiMsgSend.setVisibility(View.GONE);
                }else {
                    imageButtonMultiMsg.setVisibility(View.GONE);
                    buttonMultiMsgSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageViewExit.setOnClickListener(this);
        imageViewMore.setOnClickListener(this);
        buttonMultiMsgSend.setOnClickListener(this);

    }

    private void init() {
        countDownLatch = new CountDownLatch(1);
        handler = new Handler();
        intent = getIntent();
        other_id = intent.getStringExtra("other_id");
        bar = findViewById(R.id.includeMultiMsg);
        imageViewExit = bar.findViewById(R.id.imageViewTitleBar);
        imageViewMore = bar.findViewById(R.id.imageViewTitleBarMore);
        textViewName = bar.findViewById(R.id.textViewTitleBar);
        textViewName.setText(intent.getStringExtra("name"));
        editTextMultiMsgSendMsg = findViewById(R.id.editTextMultiMsgSendMsg);
        buttonMultiMsgSend = findViewById(R.id.buttonMultiMsgSend);
        imageButtonMultiMsg = findViewById(R.id.imageButtonMultiMsg);
        listViewMsg = findViewById(R.id.listViewMultiMsg);
        context = this;
        list = new LinkedList<>();
        MysqlDatabase database = new MysqlDatabase();
        new Thread(() -> {
            list = database.selectsMsg(USER_ID, other_id, 1);
            countDownLatch.countDown();
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewTitleBar:
                finish();
                break;
            case R.id.buttonMultiMsgSend:
                MysqlDatabase database = new MysqlDatabase();
                new Thread(() -> {
                    database.addsMsg(USER_ID, other_id, editTextMultiMsgSendMsg.getText().toString(), 1);
                }).start();
                editTextMultiMsgSendMsg.setText("");
                break;
            case R.id.imageButtonMultiMsg:

                break;
            case R.id.imageViewTitleBarMore:
                PopupMenu popupMenu = new PopupMenu(context, imageViewMore);
                popupMenu.getMenuInflater().inflate(R.menu.menu_multi, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_invite:
                                View dialogView = View.inflate(context, R.layout.dialog_chat, null);
                                EditText editTextDialogName = dialogView.findViewById(R.id.editTextDialog);
                                AlertDialog.Builder b = new AlertDialog.Builder(context);
                                b.setView(dialogView);
                                b.setPositiveButton("邀请", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AtomicInteger i = new AtomicInteger();
                                        if (!editTextDialogName.getText().toString().trim().equals("")) {
                                            MysqlDatabase db = new MysqlDatabase();
                                            new Thread(() -> {
                                                i.set(db.inviteMMsg(editTextDialogName.getText().toString(), other_id));
                                            }).start();
                                            if (i.get() == 0) {
                                                Toast.makeText(context, "错误", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
                                            }
                                            adapter.notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(context, "名称不能为空", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setTitle("输入要邀请的用户id");
                                b.show();
                                break;
                            case R.id.menu_exit:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                AlertDialog alertDialog = builder.setTitle("是否退出？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MysqlDatabase database = new MysqlDatabase();
                                                new Thread(() -> {
                                                    database.exitMMsg(other_id);
                                                }).start();
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                                alertDialog.show();
                                break;
                            case R.id.menu_changeInfo:
                                //多人信息修改
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                MysqlDatabase database = new MysqlDatabase();
                new Thread(() -> {
                    msgList = database.selectsMsg(USER_ID, other_id, 1);
                    countDownLatch.countDown();
                }).start();

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (msgList.size() != list.size()) {
                    List<Msg> l = msgList.subList(list.size(), msgList.size());
                    for (int i = 0; i < l.size(); i++) {
                        adapter.add(l.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onStop() {
        System.out.println("停止handler刷新数据");
        handler.removeCallbacks(runnable);
        super.onStop();
    }

}
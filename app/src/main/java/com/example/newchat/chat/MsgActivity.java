package com.example.newchat.chat;

import static com.example.newchat.mainmenu.MainMenuActivity.USER_ID;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.newchat.R;
import com.example.newchat.chat.adapter.MsgAdapter;
import com.example.newchat.chat.unit.Msg;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.MainMenuActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class MsgActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextMsgSendMsg;
    private Button buttonMsgSend;
    private ImageButton imageButtonMsg;
    private View bar;
    private ImageView imageView;
    private ListView listViewMsg;
    private List<Msg> list = null;
    private List<Msg> msgList = null;
    private Context context;
    private MsgAdapter adapter = null;
    private Intent intent;
    private CountDownLatch countDownLatch;
    private Handler handler;
    private String other_id;
    private Runnable runnable;
    private TextView textViewName;
    private int CALL = 0;

    private String appId = "a544ee2393c045b6ba7ee6c8c125f9bd";
    private String channelName = "testName";
    private String token = "007eJxTYFj9evuSv+c9DvDfOL5fKPFDuKib4cKrGmyTfb/neL5bO0VIgSHR1MQkNdXI2NI42cDENMksKdE8NdUs2SLZ0Mg0zTIppTvOMZlrg1Ny0I15jIwMEAjiczCUpBaX+CXmpjIwAAB5zSOt";
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
        }
    };

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

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
        imageButtonMsg.setVisibility(View.VISIBLE);
        buttonMsgSend.setVisibility(View.GONE);
        editTextMsgSendMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //监听输入框内容
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextMsgSendMsg.getText().toString().equals("")){
                    imageButtonMsg.setVisibility(View.VISIBLE);
                    buttonMsgSend.setVisibility(View.GONE);
                }else {
                    imageButtonMsg.setVisibility(View.GONE);
                    buttonMsgSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageView.setOnClickListener(this);
        buttonMsgSend.setOnClickListener(this);
        imageButtonMsg.setOnClickListener(this);

    }

    private void init() {
        handler = new Handler();
        countDownLatch = new CountDownLatch(1);
        intent = getIntent();
        other_id = intent.getStringExtra("other_id");
        bar = findViewById(R.id.includeMsg);
        imageView = bar.findViewById(R.id.imageViewTitleBar);
        textViewName = bar.findViewById(R.id.textViewTitleBar);
        textViewName.setText(intent.getStringExtra("name"));
        editTextMsgSendMsg = findViewById(R.id.editTextMsgSendMsg);
        buttonMsgSend = findViewById(R.id.buttonMsgSend);
        imageButtonMsg = findViewById(R.id.imageButtonMsg);
        listViewMsg = findViewById(R.id.listViewMsg);
        context = this;
        list = new LinkedList<>();
        MysqlDatabase database = new MysqlDatabase();
        new Thread(() -> {
            list = database.selectsMsg(USER_ID, other_id, 0);
            countDownLatch.countDown();
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewTitleBar:
                finish();
                break;
            case R.id.buttonMsgSend:
                MysqlDatabase database = new MysqlDatabase();
                new Thread(() -> {
                    database.addsMsg(USER_ID, other_id, editTextMsgSendMsg.getText().toString(), 0);
                }).start();
                editTextMsgSendMsg.setText("");
                break;
            case R.id.imageButtonMsg:
                showDialog();
                break;
            case R.id.textViewDialogBottomChosePhoto:
                //
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
                    initializeAndJoinChannel();
                    CALL = 1;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //使用子线程获得数据
        runnable = new Runnable() {
            @Override
            public void run() {
                //设置刷新延迟1000毫秒
                handler.postDelayed(this, 1000);
                MysqlDatabase database = new MysqlDatabase();
                new Thread(() -> {
                    msgList = database.selectsMsg(USER_ID, other_id, 0);
                    countDownLatch.countDown();
                }).start();

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果msglist有变化，也就是聊天数据产生了变化，便刷新listview。如果没变化则继续
                if (msgList.size() != list.size()) {
                    List<Msg> l = msgList.subList(list.size(), msgList.size());
                    for (int i = 0; i < l.size(); i++) {
                        adapter.add(l.get(i));
                    }
                    //刷新listview
                    adapter.notifyDataSetChanged();
                }
            }
        };
        //启动子线程
        handler.post(runnable);
    }

    @Override
    protected void onStop() {
        System.out.println("停止handler刷新数据");
        handler.removeCallbacks(runnable);
        if (CALL == 1) {
            mRtcEngine.leaveChannel();
        }
        super.onStop();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        View view = View.inflate(context, R.layout.dialog_bottom_chose, null);
        TextView textViewDialogBottomChoseShot = view.findViewById(R.id.textViewDialogBottomChoseShot);
        TextView textViewDialogBottomChosePhoto = view.findViewById(R.id.textViewDialogBottomChosePhoto);
        TextView textViewDialogBottomCancel = view.findViewById(R.id.textViewDialogBottomCancel);
        textViewDialogBottomChosePhoto.setText("电话");
        textViewDialogBottomChoseShot.setVisibility(View.GONE);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.main_menu_animStyle);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        textViewDialogBottomChosePhoto.setOnClickListener(this);
        textViewDialogBottomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void initializeAndJoinChannel() {
        System.out.println("进入电话");
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mHandler;
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        ChannelMediaOptions options = new ChannelMediaOptions();
        // 设置频道场景为 BROADCASTING。
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        // 将用户角色设置为 BROADCASTER。
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;

        // 使用临时 Token 加入频道。
        // 你需要自行指定用户 ID，并确保其在频道内的唯一性。
        mRtcEngine.joinChannel(token, channelName, Integer.parseInt(USER_ID), options);
    }

}
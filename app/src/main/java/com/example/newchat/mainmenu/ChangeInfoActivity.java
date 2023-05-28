package com.example.newchat.mainmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.loper7.date_time_picker.DateTimePicker;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangeInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextChangeInfoName,editTextChangeInfoSex,editTextChangeInfoSlogan;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMan,radioButtonWoman;
    private Button buttonChangInfo;
    private ImageView imageViewChangeInfoTitleBar,imageViewChangeInfoTitleMore;
    private TextView textViewChangeInfoBirthday,textViewChangeInfoTitleBar;
    private View bar;
    private String birthdayDate;
    private String userInfo = null;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        init();
        imageViewChangeInfoTitleMore.setVisibility(View.GONE);
        buttonChangInfo.setOnClickListener(this);
        textViewChangeInfoBirthday.setOnClickListener(this);
        imageViewChangeInfoTitleBar.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                userInfo = radioButton.getText().toString();
            }
        });
    }

    private void init() {
        bar = findViewById(R.id.includeChangeInfo);
        radioGroup = findViewById(R.id.radioGroupChangeInfo);
        radioButtonMan = findViewById(R.id.radioButtonChangeInfoMan);
        radioButtonWoman = findViewById(R.id.radioButtonChangeInfoWoman);
        editTextChangeInfoName = findViewById(R.id.editTextChangeInfoName);
        editTextChangeInfoSex = findViewById(R.id.editTextChangeInfoSex);
        editTextChangeInfoSlogan = findViewById(R.id.editTextChangeInfoSlogan);
        buttonChangInfo = findViewById(R.id.buttonChangeInfo);
        imageViewChangeInfoTitleBar = bar.findViewById(R.id.imageViewTitleBar);
        imageViewChangeInfoTitleMore = bar.findViewById(R.id.imageViewTitleBarMore);
        textViewChangeInfoTitleBar = bar.findViewById(R.id.textViewTitleBar);
        textViewChangeInfoBirthday = findViewById(R.id.textViewChangeInfoBirthday);
        sharedPreferences = getSharedPreferences("saveInfo", MODE_PRIVATE);
        textViewChangeInfoTitleBar.setText("信息");
        if (sharedPreferences.getString("birthday", "").equals("")) {

        }
        textViewChangeInfoBirthday.setText(sharedPreferences.getString("birthday", ""));
        editTextChangeInfoName.setText(sharedPreferences.getString("name", ""));
        editTextChangeInfoSlogan.setText(sharedPreferences.getString("slogan", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonChangeInfo:
                String name = editTextChangeInfoName.getText().toString();
                String sex = userInfo;
                String birthday = birthdayDate;
                String slogan = editTextChangeInfoSlogan.getText().toString();
                MysqlDatabase db = new MysqlDatabase();
                new Thread(() -> {
                    //数据修改上传服务器
                    db.updateUserInfo(sharedPreferences.getString("id", ""), name, sex, slogan, birthday);
                }).start();
                //数据修改保存本地缓存
                sharedPreferences.edit().putString("name", name).putString("sex", sex).putString("slogan", slogan).putString("birthday", birthday).apply();
                Toast.makeText(this, sharedPreferences.getString("name", ""), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.textViewChangeInfoBirthday:
                //生日选择
                new CardDatePickerDialog.Builder(this)
                        .showBackNow(false)
                        .setTitle("选择生日")
                        .setPickerLayout(R.layout.date_time_pick)
                        .setOnChoose("确定", aLong -> {
                            Date date = new Date(aLong);
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            birthdayDate = df.format(date);
                            textViewChangeInfoBirthday.setText(birthdayDate);
                            return null;
                        })
                        .build().show();
                break;
            case R.id.imageViewTitleBar:
                finish();
        }
    }
}
package com.example.newchat.login;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.unit.User;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextInputId,editTextInputPassword;
    private Button buttonLogin;
    private TextView textViewRegister,textViewForgetPassword;
    private SharedPreferences sharedPreferences;
    private CountDownLatch countDownLatch;
    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,}, 1);
        init();
        login();

        buttonLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewForgetPassword.setOnClickListener(this);
    }

    private void login() {
        //设置全局id
        if (sharedPreferences.getBoolean("signIn", false)) {
            Intent i = new Intent(this, MainMenuActivity.class);
            i.putExtra("id", sharedPreferences.getString("id", null));
            startActivity(i);
            finish();
        }

    }

    private void init(){
        countDownLatch = new CountDownLatch(1);
        sharedPreferences = getSharedPreferences("saveInfo", MODE_PRIVATE);
        editTextInputId = findViewById(R.id.editTextLoginInputId);
        editTextInputPassword = findViewById(R.id.editTextLoginInputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgetPassword = findViewById(R.id.textViewForgetPassWord);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                //判断账号密码格式
                if (editTextInputId.getText().toString().trim().equals("") || editTextInputPassword.getText().toString().trim().equals("")){
                    //如果有不对则输出信息
                    Toast.makeText(this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                }else {
                    //账号密码复合正则则进入下一步
                    //用于后续输出登录成功或者失败的判断
                    String password = editTextInputPassword.getText().toString();
                    String id = editTextInputId.getText().toString();
                    new Thread(() -> {
                        MysqlDatabase db = new MysqlDatabase();
                        if (db.selectUser(id, password)) {
                            //使用user获得登录的用户的服务器数据，并且存入本地数据
                            User u = db.selectUser(editTextInputId.getText().toString(), 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("signIn", true);
                            editor.putString("id", editTextInputId.getText().toString());
                            editor.putString("name", u.getName());
                            editor.putString("sex", u.getSex());
                            editor.putString("slogan", u.getSlogan());
                            editor.putString("birthday", u.getBirthday());
                            //从本地读取头像
                            File file = new File(getApplicationContext().getExternalCacheDir(), "headCache.jpg");
                            if(!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            editor.apply();
                            //登录成功跳转到主界面
                            Intent i = new Intent(this, MainMenuActivity.class);
                            i.putExtra("id", editTextInputId.getText().toString());
                            startActivity(i);
                            finish();
                            login = true;
                        }else {
                            login = false;
                        }
                        countDownLatch.countDown();
                    }).start();
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //输出登录信息
                    if (!login){
                        editTextInputPassword.setText("");
                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.textViewRegister:
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.textViewForgetPassWord:
                Intent i1 = new Intent(this, ForgetPasswordActivity.class);
                startActivity(i1);
                break;
        }
    }

}
package com.example.newchat.login;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.unit.MailCAPTCHA;
import com.example.newchat.unit.Ssh;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextRegisterInputEmail,editTextRegisterInputPassword,editTextRegisterInputPassword1,editTextRegisterInputCAPTCHA;
    private Button buttonRegister,buttonRegisterCAPTACH;
    private String captcha;
    private CountDownLatch countDownLatch;
    private int succReg;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        buttonRegister.setOnClickListener(this);
        buttonRegisterCAPTACH.setOnClickListener(this);

    }

    private void init(){
        countDownLatch = new CountDownLatch(1);
        editTextRegisterInputEmail = findViewById(R.id.editTextRegisterInputEmail);
        editTextRegisterInputPassword = findViewById(R.id.editTextRegisterInputPassword);
        editTextRegisterInputPassword1 = findViewById(R.id.editTextRegisterInputPassword1);
        editTextRegisterInputCAPTCHA = findViewById(R.id.editTextRegisterInputCAPTCHA);
        buttonRegisterCAPTACH = findViewById(R.id.buttonRegisterCAPTCHA);
        buttonRegister = findViewById(R.id.buttonRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegister:
                //判断用户输入的邮箱是否符合邮箱的正则表达式
                if (editTextRegisterInputEmail.getText().toString().matches("\\w+@\\w+(\\.\\w{2,3})*\\.\\w{2,3}")){
                    //判断用户输入的密码是否符合密码的正则表达式
                    if ((!editTextRegisterInputPassword.getText().toString().equals("") && !editTextRegisterInputPassword1.getText().toString().equals("")) && editTextRegisterInputPassword.getText().toString().equals(editTextRegisterInputPassword1.getText().toString()) && editTextRegisterInputPassword.getText().toString().length() >= 6){
                        //判断用户输入的验证码与生成的验证码
                        if (editTextRegisterInputCAPTCHA.getText().toString().equals(captcha)) {
                            new Thread(() ->{
                                //使用子线程进行创建用户操作
                                MysqlDatabase db = new MysqlDatabase();
                                succReg= db.insert(editTextRegisterInputEmail.getText().toString(), editTextRegisterInputPassword.getText().toString());
                                if (succReg == 1){
                                    id = db.returnId(editTextRegisterInputEmail.getText().toString());
                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText(null, id);
                                    clipboardManager.setPrimaryClip(clipData);
                                    db.insertOnlineUser(id);
                                    Ssh ssh = new Ssh();
                                    ssh.firstReg(id);
                                }
                                countDownLatch.countDown();
                            }).start();
                            try {
                                countDownLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (succReg == 1){
                                Toast.makeText(getApplicationContext(), "注册成功， ID为：" + id + "，以复制到粘贴板", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(this, "注册失败，邮箱已注册", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonRegisterCAPTCHA:
                //邮箱验证
                if (editTextRegisterInputEmail.getText().toString().matches("\\w+@\\w+(\\.\\w{2,3})*\\.\\w{2,3}")) {
                    MailCAPTCHA mailCAPTCHA = new MailCAPTCHA();
                    captcha = mailCAPTCHA.sendCAPTCHA(editTextRegisterInputEmail.getText().toString());
                    new CountDownTimer(10000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            buttonRegisterCAPTACH.setClickable(false);
                            buttonRegisterCAPTACH.setText(millisUntilFinished / 1000 + "s");
                        }

                        @Override
                        public void onFinish() {
                            buttonRegisterCAPTACH.setText("再次获取");
                            buttonRegisterCAPTACH.setClickable(true);
                            cancel();
                        }
                    }.start();
                }else {
                    Toast.makeText(RegisterActivity.this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
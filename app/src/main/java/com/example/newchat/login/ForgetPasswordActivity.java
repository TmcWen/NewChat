package com.example.newchat.login;

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

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText editTextForgetPasswordInputEmail,editTextForgetPasswordInputCaptcha,editTextForgetPasswordInputNewPassword,editTextForgetPasswordInputNewPassword1;
    private Button buttonForgetPassword,buttonForgetPasswordCAPTCHA;
    private String captcha = "asda2sd1a23s1s32d1a23s1d2a3d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();
        buttonForgetPasswordCAPTCHA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextForgetPasswordInputEmail.getText().toString().matches("\\w+@\\w+(\\.\\w{2,3})*\\.\\w{2,3}")){
                    MailCAPTCHA mailCAPTCHA = new MailCAPTCHA();
                    captcha = mailCAPTCHA.sendCAPTCHA(editTextForgetPasswordInputEmail.getText().toString());
                    new CountDownTimer(10000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            buttonForgetPasswordCAPTCHA.setClickable(false);
                            buttonForgetPasswordCAPTCHA.setText(millisUntilFinished / 1000 + "s");
                        }

                        @Override
                        public void onFinish() {
                            buttonForgetPasswordCAPTCHA.setText("再次获取");
                            buttonForgetPasswordCAPTCHA.setClickable(true);
                            cancel();
                        }
                    }.start();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "输入正确的邮箱", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用正则判断用户输入内容
                boolean b = !editTextForgetPasswordInputEmail.getText().toString().trim().equals("")&&!editTextForgetPasswordInputCaptcha.getText().toString().trim().equals("")&&!editTextForgetPasswordInputNewPassword.getText().toString().trim().equals("")&&!editTextForgetPasswordInputNewPassword1.getText().toString().trim().equals("")&&editTextForgetPasswordInputCaptcha.getText().toString().equals(captcha)&&editTextForgetPasswordInputNewPassword.getText().toString().equals(editTextForgetPasswordInputNewPassword1.getText().toString())&&editTextForgetPasswordInputNewPassword.getText().toString().trim().length()>6;
                if (b) {
                    Toast.makeText(ForgetPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    MysqlDatabase mysqlDatabase = new MysqlDatabase();
                    new Thread(() -> {
                        //子线程修改数据库
                        mysqlDatabase.updatePassword(editTextForgetPasswordInputEmail.getText().toString(), editTextForgetPasswordInputNewPassword.getText().toString());
                    }).start();
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "输入有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
    }

    private void init(){
        editTextForgetPasswordInputEmail = findViewById(R.id.editTextForgetPasswordInputEmail);
        editTextForgetPasswordInputCaptcha = findViewById(R.id.editTextForgetPasswordInputCaptcha);
        editTextForgetPasswordInputNewPassword = findViewById(R.id.editTextForgetPasswordInputNewPassword);
        editTextForgetPasswordInputNewPassword1 = findViewById(R.id.editTextForgetPasswordInputNewPassword1);
        buttonForgetPasswordCAPTCHA = findViewById(R.id.buttonForgetPasswordCAPTCHA);
        buttonForgetPassword = findViewById(R.id.buttonForgetPassword);
    }

}
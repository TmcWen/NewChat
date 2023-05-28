package com.example.newchat.mainmenu.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.login.LoginActivity;
import com.example.newchat.mainmenu.ChangeInfoActivity;
import com.example.newchat.mainmenu.MainMenuActivity;
import com.example.newchat.unit.GlideEngine;
import com.example.newchat.unit.Ssh;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelfFragment extends Fragment implements View.OnClickListener {
    private ImageView imageViewSelfHeadImage,imageViewSelfBackgroundImage;
    private TextView textViewSelfName,textViewSelfUid,textViewSelfSex,textViewSelfBirthday,textViewSelfAddress,textViewSelfAge;
    private View root;
    private Button logout;
    private ImageButton imageButtonSelfChange;
    private Uri uri;
    private SharedPreferences sharedPreferences;
    private int I = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_self, container, false);
        init();
        logout.setOnClickListener(this);
        imageButtonSelfChange.setOnClickListener(this);
        imageViewSelfHeadImage.setOnClickListener(this);
        imageViewSelfBackgroundImage.setOnClickListener(this);

        return root;
    }

    private void init() {
        imageViewSelfBackgroundImage = root.findViewById(R.id.imageViewSelfBackgroundImage);
        imageViewSelfHeadImage = root.findViewById(R.id.imageViewSelfHeadImage);
        textViewSelfName = root.findViewById(R.id.textViewSelfName);
        textViewSelfUid = root.findViewById(R.id.textViewSelfUid);
        textViewSelfSex = root.findViewById(R.id.textViewSelfSex);
        textViewSelfBirthday = root.findViewById(R.id.textViewSelfBirthday);
        File f = new File(getContext().getFilesDir() + "/head" + MainMenuActivity.USER_ID + ".jpg");
        uri = Uri.parse(f.getPath());
        imageViewSelfHeadImage.setImageURI(uri);
        File f1 = new File(getContext().getFilesDir() + "/background" + MainMenuActivity.USER_ID + ".jpg");
        uri = Uri.parse(f1.getPath());
        imageViewSelfBackgroundImage.setImageURI(uri);
        logout = root.findViewById(R.id.buttonSelfLogout);
        imageButtonSelfChange = root.findViewById(R.id.imageButtonSelfChange);
        sharedPreferences = getActivity().getSharedPreferences("saveInfo", Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelfLogout:
                SharedPreferences s = getActivity().getSharedPreferences("saveInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor e = s.edit();
                e.putBoolean("signIn", false);
                e.apply();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                break;
            case R.id.imageButtonSelfChange:
                Intent intent = new Intent(getContext(), ChangeInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.imageViewSelfHeadImage:
                I = 1;
                showDialogBottomChose();
                break;
            case R.id.imageViewSelfBackgroundImage:
                I = 0;
                showDialogBottomChose();
                break;
        }
    }

    private void showDialogBottomChose() {
        final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        View view = View.inflate(getContext(), R.layout.dialog_bottom_chose, null);
        //设置自定义view
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //设置dialog的位置
        window.setGravity(Gravity.BOTTOM);
        //设置dialog动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置dialog的layout
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //显示dialog
        dialog.show();
        //动态权限申请
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        //显示拍照还是相册选择界面
        dialog.findViewById(R.id.textViewDialogBottomChoseShot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(getContext())
                        //打开摄像头
                        .openCamera(SelectMimeType.ofImage())
                        //返回activity的数据
                        .forResultActivity(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            //返回方法
                            public void onResult(ArrayList<LocalMedia> result) {
                                //判断点击来自头像还是背景
                                if (I == 0) {
                                    //路径获取
                                    //是背景
                                    String path = result.get(0).getRealPath();
                                    File f = new File(path);
                                    File f1 = new File(getContext().getFilesDir().toString() + "/background" + MainMenuActivity.USER_ID + ".jpg");
                                    //判断图片文件是否存在
                                    if (f1.exists()) {

                                    } else {
                                        //不存在则创建
                                        try {
                                            f1.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        copyFile(f, f1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //上传图片至服务器
                                    new Thread(() ->{
                                        //子线程执行数据更新
                                        Ssh s = new Ssh();
                                        s.putUserBackground(f1.getPath());
                                        MysqlDatabase db = new MysqlDatabase();
                                        long l = db.updateBackground(MainMenuActivity.USER_ID);
                                        //debug
                                        if (l == 0)
                                            Toast.makeText(getContext(), "更新背景时间失败", Toast.LENGTH_SHORT).show();
                                        sharedPreferences.edit().putLong("backgroundUpdateTime", l);
                                    }).start();
                                    Uri u = Uri.parse(f1.getPath());
                                    imageViewSelfBackgroundImage.setImageURI(u);
                                } else {
                                    //是头像
                                    String path = result.get(0).getRealPath();
                                    File f = new File(path);
                                    File f1 = new File(getContext().getFilesDir().toString() + "/head" + MainMenuActivity.USER_ID + ".jpg");
                                    if (f1.exists()) {

                                    } else {
                                        try {
                                            f1.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        copyFile(f, f1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //上传图片至服务器
                                    new Thread(() ->{
                                        Ssh s = new Ssh();
                                        s.putUserHead(f1.getPath());
                                        MysqlDatabase db = new MysqlDatabase();
                                        long l = db.updateHead(MainMenuActivity.USER_ID);
                                        if (l == 0)
                                            Toast.makeText(getContext(), "更新头像时间失败", Toast.LENGTH_SHORT).show();
                                        sharedPreferences.edit().putLong("headUpdateTime", l);
                                    }).start();
                                    //设置imageview的uri值
                                    Uri u = Uri.parse(f1.getPath());
                                    imageViewSelfHeadImage.setImageURI(u);
                                }
                            }
                            //点击取消按钮或者其余区域
                            @Override
                            public void onCancel() {
                                System.out.println("取消拍照");
                            }
                        });
                dialog.dismiss();
            }
        });

        //从相册选择
        dialog.findViewById(R.id.textViewDialogBottomChosePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开图片选择器
                PictureSelector.create(getContext())
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        //设置最大数量
                        .setMaxSelectNum(1)
                        //选择返回
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                //来自头像还是背景
                                if (I == 0) {
                                    String path = result.get(0).getRealPath();
                                    File f = new File(path);
                                    File f1 = new File(getContext().getFilesDir().toString() + "/background" + MainMenuActivity.USER_ID + ".jpg");
                                    if (f1.exists()) {

                                    } else {
                                        try {
                                            f1.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        copyFile(f, f1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //上传图片至服务器
                                    new Thread(() ->{
                                        Ssh s = new Ssh();
                                        s.putUserBackground(f1.getPath());
                                        MysqlDatabase db = new MysqlDatabase();
                                        long l = db.updateBackground(MainMenuActivity.USER_ID);
                                        if (l == 0) System.out.println("更新背景时间失败");
                                        sharedPreferences.edit().putLong("backgroundUpdateTime", l);
                                    }).start();
                                    //设置背景图片uri
                                    Uri u = Uri.parse(f1.getPath());
                                    imageViewSelfBackgroundImage.setImageURI(u);
                                } else {
                                    String path = result.get(0).getRealPath();
                                    File f = new File(path);
                                    File f1 = new File(getContext().getFilesDir().toString() + "/head" + MainMenuActivity.USER_ID + ".jpg");
                                    if (f1.exists()) {

                                    } else {
                                        try {
                                            f1.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        copyFile(f, f1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //上传图片至服务器
                                    new Thread(() ->{
                                        Ssh s = new Ssh();
                                        s.putUserHead(f1.getPath());
                                        MysqlDatabase db = new MysqlDatabase();
                                        long l = db.updateHead(MainMenuActivity.USER_ID);
                                        if (l == 0) System.out.println("更新头像时间失败");
                                        sharedPreferences.edit().putLong("headUpdateTime", l);
                                    }).start();
                                    Uri u = Uri.parse(f1.getPath());
                                    imageViewSelfHeadImage.setImageURI(u);
                                }
                            }
                            //取消拍照
                            @Override
                            public void onCancel() {
                                System.out.println("退出相册");
                            }
                        });
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.textViewDialogBottomCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    //这里返回就会调用
    @Override
    public void onResume() {
        super.onResume();
        //同步本地信息
        textViewSelfName.setText(sharedPreferences.getString("name", ""));
        textViewSelfSex.setText(sharedPreferences.getString("sex", ""));
        textViewSelfBirthday.setText(sharedPreferences.getString("birthday", ""));
        textViewSelfUid.setText("UID: " + sharedPreferences.getString("id", ""));
    }

    public static void copyFile(File sourceFile,File targetFile)
            throws IOException{
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

}

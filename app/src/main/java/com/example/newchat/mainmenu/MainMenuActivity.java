package com.example.newchat.mainmenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.fragment.ChatFragment;
import com.example.newchat.mainmenu.fragment.FriendFragment;
import com.example.newchat.mainmenu.fragment.SelfFragment;
import com.example.newchat.mainmenu.fragment.adapter.MyViewpager2Adapater;
import com.example.newchat.unit.Ssh;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainMenuActivity extends AppCompatActivity {
    public static String USER_ID = null;

    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationViewMainMenu;
    private List<Fragment> fragmentList;
    private Fragment chatFragment,friendFragment,selfFragment;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        init();
        //本地头像更新
        new Thread(() -> {
            MysqlDatabase db = new MysqlDatabase();
            db.updateOnlineUser(1);
            long l1 = db.getHeadUpdateTime(MainMenuActivity.USER_ID);
            long l2 = db.getBackgroundUpdateTime(MainMenuActivity.USER_ID);
            long l3= sharedPreferences.getLong("headUpdateTime", 0);
            long l4= sharedPreferences.getLong("backgroundUpdateTime", 0);
            if (l1 < l3) {
                Ssh ssh = new Ssh();
                ssh.getUserHead(MainMenuActivity.USER_ID, 0);
            }
            if (l2 < l4) {
                Ssh ssh = new Ssh();
                ssh.getUserBackground(MainMenuActivity.USER_ID, 0);
            }

            //加载好友头像以及好友背景
            Set<String> set = db.getAllFriendId();
            if (set.size() != 0) {
                Ssh ssh =new Ssh();
                for (String s : set) {
                    ssh.getUserHead(s, 0);
                    ssh.getUserBackground(s, 0);
                }
            }

        }).start();

        //初始化fragment并且添加到list
        chatFragment = new ChatFragment();
        friendFragment = new FriendFragment();
        selfFragment = new SelfFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(chatFragment);
        fragmentList.add(friendFragment);
        fragmentList.add(selfFragment);

        //viewpager2绑定adapter并且设置滑动监听
        viewPager2.setAdapter(new MyViewpager2Adapater(this, fragmentList));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationViewMainMenu.getMenu().getItem(position).setChecked(true);
            }
        });

        //底部导航栏点击绑定
        bottomNavigationViewMainMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.itemMenuChat:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.itemMenuFriend:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.itemMenuSelf:
                        viewPager2.setCurrentItem(2);
                        break;
                }

                return true;
            }
        });
    }

    private void init(){
        sharedPreferences = getSharedPreferences("saveInfo", MODE_PRIVATE);
        viewPager2 = findViewById(R.id.viewPager2MainMenu);
        bottomNavigationViewMainMenu = findViewById(R.id.bottomNavigationMainMenu);
        USER_ID = getIntent().getStringExtra("id");
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(() ->{
            MysqlDatabase db = new MysqlDatabase();
            db.updateOnlineUser(0);
        }).start();
    }
}
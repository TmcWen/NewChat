package com.example.newchat.db.info.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newchat.R;
import com.example.newchat.db.MysqlDatabase;
import com.example.newchat.mainmenu.unit.Chat;
import com.example.newchat.mainmenu.unit.Friend;
import com.example.newchat.unit.User;

import java.util.LinkedList;

public class NewFriendAdapter extends BaseAdapter {

    private LinkedList<Friend> mData;
    private Context context;

    public NewFriendAdapter(LinkedList<Friend> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void add(Friend friend) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_new_friend, viewGroup, false);
        ImageView imageViewNewFriendHeadImage = view.findViewById(R.id.imageViewNewFriendHeadImage);
        TextView textViewNewFriendName = view.findViewById(R.id.textViewNewFriendName);
        ImageButton imageButtonNewFriendAgree = view.findViewById(R.id.imageButtonNewFriendAgree);
        imageViewNewFriendHeadImage.setImageURI(mData.get(i).getHeadImage());
        textViewNewFriendName.setText(mData.get(i).getName());
        //记得补完添加好友
        imageButtonNewFriendAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String other_id = mData.get(i).getfriendId();
                SharedPreferences s = context.getSharedPreferences("saveInfo", Context.MODE_PRIVATE);
                String user_id = s.getString("id", null);
                MysqlDatabase db = new MysqlDatabase();
                new Thread(() -> {
                    db.updateFriend(user_id, other_id);
                }).start();
                imageButtonNewFriendAgree.setVisibility(View.GONE);
            }
        });

        return view;
    }


}

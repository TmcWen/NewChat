package com.example.newchat.mainmenu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newchat.R;
import com.example.newchat.mainmenu.unit.Chat;
import com.example.newchat.mainmenu.unit.Friend;

import java.util.LinkedList;

public class FriendAdapter extends BaseAdapter {

    private LinkedList<Friend> mData;
    private Context context;

    public FriendAdapter(LinkedList<Friend> mData, Context context) {
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

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
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
        ViewHolder holder = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_friend, viewGroup, false);
        holder = new ViewHolder();
        int i1 = mData.get(i).getOnline();
        holder.imageViewFriendHeadImage = view.findViewById(R.id.imageViewFriendHeadImage);
        holder.textViewFriendName = view.findViewById(R.id.textViewFriendName);
        holder.imageViewFriendHeadImage.setImageURI(mData.get(i).getHeadImage());
        holder.textViewFriendName.setText(mData.get(i).getName());
        if (i1 == 0) {
            holder.textViewFriendName.setTextColor(Color.WHITE);
            System.out.println(mData.get(i).getfriendId() + "不在线");
        } else {
            holder.textViewFriendName.setTextColor(Color.parseColor("#FF9800"));
            System.out.println(mData.get(i).getfriendId() + "在线");
        }
        return view;
    }

    static class ViewHolder{
        ImageView imageViewFriendHeadImage;
        TextView textViewFriendName;
    }

}

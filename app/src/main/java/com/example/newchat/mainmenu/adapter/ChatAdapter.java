package com.example.newchat.mainmenu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newchat.R;
import com.example.newchat.mainmenu.unit.Chat;

import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private LinkedList<Chat> mData;
    private Context context;

    public ChatAdapter(LinkedList<Chat> mData, Context context) {
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

    public void add(Chat chat) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(chat);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_chat, viewGroup, false);
        holder = new ViewHolder();
        holder.textViewChatLastMsg = view.findViewById(R.id.textViewChatLastMsg);
        holder.textViewChatName = view.findViewById(R.id.textViewChatName);
        holder.imageViewChatHeadImage = view.findViewById(R.id.imageViewChatHeadImage);
        holder.imageViewChatHeadImage.setImageURI(mData.get(i).getHeadImage());
        holder.textViewChatName.setText(mData.get(i).getName());
        holder.textViewChatLastMsg.setText(mData.get(i).getLastMsg());
        notifyDataSetChanged();
        return view;
    }

    static class ViewHolder{
        ImageView imageViewChatHeadImage;
        TextView textViewChatName,textViewChatLastMsg;
    }
}

package com.example.newchat.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newchat.R;
import com.example.newchat.chat.unit.Msg;
import com.example.newchat.mainmenu.unit.Chat;

import java.util.LinkedList;

public class MsgAdapter extends BaseAdapter {
    private LinkedList<Msg> mData;
    private Context context;

    public MsgAdapter(LinkedList<Msg> mData, Context context) {
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

    public void add(Msg msg) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //判断发言用户是否为自己
        if (mData.get(i).isMe()){
            //是自己
            view = LayoutInflater.from(context).inflate(R.layout.item_msg_right, viewGroup, false);
            ImageView imageViewMsgLeftHeadImage = view.findViewById(R.id.imageViewMsgRightHeadImage);
            TextView textViewMsgLeftText = view.findViewById(R.id.textViewMsgRightText);
            //设置头像为右边item
            imageViewMsgLeftHeadImage.setImageURI(mData.get(i).getHeadImage());
            //设置信息为右边item
            textViewMsgLeftText.setText(mData.get(i).getMsg());
            return view;
        } else {
            //不是自己
            view = LayoutInflater.from(context).inflate(R.layout.item_msg_left, viewGroup, false);
            ImageView imageViewMsgLeftHeadImage = view.findViewById(R.id.imageViewMsgLeftHeadImage);
            TextView textViewMsgLeftText = view.findViewById(R.id.textViewMsgLeftText);
            //设置头像为左边item
            imageViewMsgLeftHeadImage.setImageURI(mData.get(i).getHeadImage());
            //设置信息为左边item
            textViewMsgLeftText.setText(mData.get(i).getMsg());
            //返回
            return view;
        }

    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
}

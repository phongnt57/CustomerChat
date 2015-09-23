package com.phongnt.phong.chattingtostranger.adapter;

/**
 * Created by phong on 9/3/2015.
 */


import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.utils.TimeUtils;
import com.quickblox.chat.model.QBChatMessage;


public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<QBChatMessage> messagesItems;
    private int myID;

    public ChatAdapter(Context context, List<QBChatMessage> navDrawerItems,int myID) {
        this.context = context;
        this.messagesItems = navDrawerItems;
        this.myID = myID;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        QBChatMessage m = messagesItems.get(position);



        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if(messagesItems.get(position).getSenderId()==null)
        {
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);

        }        else
        if (messagesItems.get(position).getSenderId()==myID) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(m.getBody());
        if (messagesItems.get(position).getSenderId()==null){
            lblFrom.setText("Me");

        }else
        {
            Date date = new Date(m.getDateSent()*1000);
            TimeUtils utils = new TimeUtils(date);


            lblFrom.setText(utils.getTime());



        }


        return convertView;
    }

    public void add(QBChatMessage message) {
        if(messagesItems.size()>0){
            messagesItems.add(0, message);
        }else {
            messagesItems.add(message);
        }

    }
    public void addtoLast(QBChatMessage message) {

            messagesItems.add(message);


    }

    public void add(List<QBChatMessage> messages) {
        messagesItems.addAll(messages);
    }

}

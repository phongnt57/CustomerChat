package com.phongnt.phong.chattingtostranger.adapter;

/**
 * Created by phong on 9/1/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.phongnt.phong.chattingtostranger.utils.TimeUtils;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;

import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;


import java.util.Date;
import java.util.List;



public class DialogAdapter extends BaseAdapter {
    private List<QBDialog> dataSource;
    private LayoutInflater inflater;
    private Context ctx;

    public DialogAdapter(List<QBDialog> dataSource, Activity ctx) {
        this.dataSource = dataSource;
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    public List<QBDialog> getDataSource() {
        return dataSource;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        // initIfNeed view
        //
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_dialog, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.roomName);
            holder.lastMessage = (TextView)convertView.findViewById(R.id.lastMessage);
            holder.groupType = (TextView)convertView.findViewById(R.id.textViewGroupType);
            holder.status = (View)convertView.findViewById(R.id.status);
            holder.roomImage = (ImageView)convertView.findViewById(R.id.roomImage);
            holder.date = (TextView)convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set data
        //
        QBDialog dialog = dataSource.get(position);

        Date date = new Date(dialog.getLastMessageDateSent()*1000);
        TimeUtils utils = new TimeUtils(date);
        holder.date.setText(utils.getDay());
        //dialog.getLastMessageDateSent()

        if(dialog.getType().equals(QBDialogType.GROUP)){
            holder.name.setText(dialog.getName());

        }else{
            // get opponent name for private dialog
            //
            //ChatService.getInstance().getOpponentIDForPrivateDialog(dialog)!=null
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
            QBUser user = ChatService.getInstance().getDialogsUsers().get(opponentID);
            if(user != null){
                holder.name.setText(user.getLogin() == null ? user.getEmail() : user.getLogin());
                long currentTime = System.currentTimeMillis();
                long userLastRequestAtTime;
                if(user.getLastRequestAt()!=null){
                    userLastRequestAtTime  = user.getLastRequestAt().getTime();
                }else {
                     userLastRequestAtTime = 0;

                }

                // if user didn't do anything last 5 minutes (5*60*1000 milliseconds)
                if((currentTime - userLastRequestAtTime) > 5*60*1000){
                    // user is offline now
                    holder.status.setBackgroundResource(R.drawable.circle_dot_offline);
                }
                else {
                    holder.status.setBackgroundResource(R.drawable.circle_dot_online);
                }


                if(user.getFileId()!=null) {
                    int userProfilePictureID = user.getFileId(); // user - an instance of QBUser class
                    QBContent.getFile(userProfilePictureID, new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            Picasso.with(ctx).load(qbFile.getPublicUrl()).placeholder(R.drawable.user1)
                                    .into(holder.roomImage)
                            ;
                        }

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(List<String> list) {

                        }
                    });
                }


            }
        }

        holder.lastMessage.setText(dialog.getLastMessage());

        holder.groupType.setText(dialog.getType().toString());



        return convertView;
    }

    private static class ViewHolder{
        TextView name;
        TextView lastMessage;
        TextView groupType;
        View status;
        ImageView roomImage;
        TextView date;
    }
}

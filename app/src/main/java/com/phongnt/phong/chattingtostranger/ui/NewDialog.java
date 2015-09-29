package com.phongnt.phong.chattingtostranger.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.adapter.UserAdapter;

import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class NewDialog extends BaseActivity implements QBEntityCallback<ArrayList<QBUser>> {

    private static final int PAGE_SIZE = 10;

    private int listViewIndex;
    private int listViewTop;
    private int currentPage = 0;
    int size;
    private List<QBUser> users = new ArrayList<QBUser>();

    private ListView usersList;
    private ImageButton createChatButton;
    private ProgressBar progressBar;
    private UserAdapter usersAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dialog);

        usersList = (ListView) findViewById(R.id.listViewUser);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        createChatButton = (ImageButton) findViewById(R.id.createChatButton);
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatService.getInstance().addDialogsUsers(usersAdapter.getSelected());


                // Create new group dialog
                //
                QBDialog dialogToCreate = new QBDialog();
                dialogToCreate.setName(usersListToChatName());
                if (usersAdapter.getSelected().size() == 1) {
                    dialogToCreate.setType(QBDialogType.PRIVATE);
                } else {
                    dialogToCreate.setType(QBDialogType.GROUP);
                }
                dialogToCreate.setOccupantsIds(getUserIds(usersAdapter.getSelected()));

                QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        if (usersAdapter.getSelected().size() == 1) {
                            startSingleChat(dialog);
                        } else {
                            startGroupChat(dialog);
                        }
                    }

                    @Override
                    public void onError(List<String> errors) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(NewDialog.this);
                        dialog.setMessage("dialog creation errors: " + errors).create().show();
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNextPage();

            }
        });


        if(isSessionActive()){
            loadFirstPage();
        }
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(NewDialog.this, Dialog.class);
        startActivity(i);
        finish();
    }


    public static QBPagedRequestBuilder getQBPagedRequestBuilder(int page) {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(PAGE_SIZE);

        return pagedRequestBuilder;
    }


    @Override
    public void onSuccess(ArrayList<QBUser> newUsers, Bundle bundle){

        // save users
        //
        size = newUsers.size();
        if(newUsers.size()>0 )
        users.addAll(newUsers);
    // Prepare users list for simple adapter.
        //
        usersAdapter.notifyDataSetChanged();
        usersList.setAdapter(usersAdapter);
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(){

    }

    @Override
    public void onError(List<String> errors){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("get users errors: " + errors).create().show();
    }


    private String usersListToChatName(){
        String chatName = "";
        for(QBUser user : usersAdapter.getSelected()){
            String prefix = chatName.equals("") ? "" : ", ";
            chatName = chatName + prefix + user.getLogin();
        }
        return chatName;
    }

    public void startSingleChat(QBDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

        ChatActivity.start(this, bundle);
    }

    private void startGroupChat(QBDialog dialog){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

        ChatActivity.start(this, bundle);
    }
    private void loadFirstPage(){
        QBPagedRequestBuilder pagedRequest = new QBPagedRequestBuilder();
        pagedRequest.setPage(0);
        pagedRequest.setPerPage(PAGE_SIZE);
        QBUsers.getUsers(pagedRequest, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                size = qbUsers.size();
                for(int i =0;i<qbUsers.size();i++)
                {
                    users.add(qbUsers.get(i));
                }
                usersAdapter = new UserAdapter(users,getApplicationContext());
                usersList.setAdapter(usersAdapter);

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(List<String> list) {

            }
        });
    }

    private void loadNextPage() {
        ++currentPage;
        if(size==PAGE_SIZE )

        QBUsers.getUsers(getQBPagedRequestBuilder(currentPage), this);

    }

    public static ArrayList<Integer> getUserIds(List<QBUser> users){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(QBUser user : users){
            ids.add(user.getId());
        }
        return ids;
    }

    //
    // ApplicationSessionStateCallback
    //

    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    loadNextPage();
                }
            }
        });
    }
}

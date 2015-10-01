package com.phongnt.phong.chattingtostranger.ui;

/**
 * Created by phong on 9/17/2015.
 */
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.adapter.DialogAdapter;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;



public class InBoxFragment extends Fragment{
   SwipeRefreshLayout swipeRefreshLayout;
    ListView listViewDialog;
    ProgressBar progressBar;
    private  TabActivity tabActivity;
    DatabaseHandler databaseHandler;
    MaterialDialog dialog;

    public InBoxFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabActivity = (TabActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        listViewDialog = (ListView)rootView.findViewById(R.id.listViewDialogs);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        databaseHandler = new DatabaseHandler(getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        dialog = new MaterialDialog.Builder(getActivity())
                .title(null)
                .content("Wating")
                .progress(true, 0).build();
        dialog.setCancelable(false);
        ChatService.initIfNeed(getActivity());

        if(tabActivity.isSessionActive()) {
            Log.e("session", "active");
            getDialogs();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getDialogs();

                }
            });
        }
        return rootView;
    }

    private void getDialogs(){
        progressBar.setVisibility(View.VISIBLE);

        // Get dialogs
        //
        ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {
                progressBar.setVisibility(View.GONE);

                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>) object;

                // build list view
                //
                buildListView(dialogs);
            }

            @Override
            public void onError(List errors) {
                progressBar.setVisibility(View.GONE);
                Log.e("error", "getdialog");
                AlertDialog.Builder dialogs = new AlertDialog.Builder(getActivity());
                dialogs.setMessage("Chat login errors: " + errors.get(0).toString()).create().show();



            }
        });
    }

    private void getRefreshDialogs(){


        // Get dialogs
        //
        ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {
                progressBar.setVisibility(View.GONE);

                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>) object;
                swipeRefreshLayout.setRefreshing(false);

                // build list view
                //
                buildListView(dialogs);
            }

            @Override
            public void onError(List errors) {
                progressBar.setVisibility(View.GONE);
                Log.e("error", "getdialog");
                AlertDialog.Builder dialogs = new AlertDialog.Builder(getActivity());
                dialogs.setMessage("Chat login errors: " + errors.get(0).toString()).create().show();



            }
        });
    }

    void buildListView(List<QBDialog> dialogs){
        final DialogAdapter adapter = new DialogAdapter(dialogs, getActivity());
        listViewDialog.setAdapter(adapter);

        // choose dialog
        //
        listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, selectedDialog);

                // Open chat activity
                ChatActivity.start(getActivity(), bundle);

            }
        });
    }

}
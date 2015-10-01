package com.phongnt.phong.chattingtostranger.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.data.User;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class Splash extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);


        databaseHandler = new DatabaseHandler(this);


            ChatService.initIfNeed(getApplicationContext());
            if(databaseHandler.getLoginCount()>0)
            {
                progressBar.setVisibility(View.VISIBLE);
                User user = databaseHandler.getUser();
                QBUser qbUser= new QBUser();
                if(user.getUser().contains("@")) qbUser.setEmail(user.getUser());
                else  qbUser.setLogin(user.getUser());
                qbUser.setPassword(user.getPass());

                QBAuth.createSession(qbUser, new QBEntityCallbackImpl<QBSession>() {
                    @Override
                    public void onSuccess(QBSession result, Bundle params) {
                        super.onSuccess(result, params);
                        
                        progressBar.setVisibility(View.GONE);
                        Intent in = new Intent(Splash.this,TabActivity.class);
                        startActivity(in);
                        finish();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        progressBar.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Splash.this);
                        dialog.setMessage("chat login errors: " + errors.get(0)).create().show();
                        Log.e("token ", "no");
                        super.onError(errors);
                    }
                });


            }
            else {
                Log.e("wait 2 s", "oj");
                Intent in = new Intent(Splash.this,Login.class);
                startActivity(in);
                finish();





            }







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

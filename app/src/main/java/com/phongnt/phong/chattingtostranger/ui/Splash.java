package com.phongnt.phong.chattingtostranger.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.data.User;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class Splash extends AppCompatActivity {
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        databaseHandler = new DatabaseHandler(this);


            ChatService.initIfNeed(getApplicationContext());
            if(databaseHandler.getLoginCount()>0)
            {
                User user = databaseHandler.getUser();
                QBUser qbUser= new QBUser();
                if(user.getUser().contains("@")) qbUser.setEmail(user.getUser());
                else  qbUser.setLogin(user.getUser());
                qbUser.setPassword(user.getPass());

                ChatService.getInstance().login(qbUser, new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {

                        Log.e("login", "ok");

                      Intent intent = new Intent(Splash.this, TabActivity.class);
                        startActivity(intent);

                        finish();


                    }

                    @Override
                    public void onError(List list) {
                        Toast.makeText(getApplicationContext(),"Some error, login again",Toast.LENGTH_SHORT).show();
                        Log.e("login", "no");
                        Intent in = new Intent(Splash.this,Login.class);
                        startActivity(in);
                        finish();

                    }
                });



            }
            else {
                Log.e("wait 2 s","oj");

                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(Splash.this, Login.class);
                        startActivity(i);


                        finish();
                    }
                }, 2000);





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

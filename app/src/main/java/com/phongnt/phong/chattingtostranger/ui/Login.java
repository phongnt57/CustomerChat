package com.phongnt.phong.chattingtostranger.ui;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.List;

public class Login extends Activity {
    EditText editTextuser;
    EditText editTextpass;
    Button buttonLogin;
    ProgressBar progressBar;
    private LoginButton loginButtonFb;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getKeyHash();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.phongnt.phong.chattingtostranger",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", "1");

        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "2");

        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        editTextuser = (EditText) findViewById(R.id.username);
        editTextpass = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginButtonFb = (LoginButton) findViewById(R.id.login_button_fb);
        //getKeyHash();

        progressBar.setVisibility(View.GONE);

        //set up and login
        ChatService.initIfNeed(getApplicationContext());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final QBUser user = new QBUser();
                final String username = editTextuser.getText().toString();
                final String password = editTextpass.getText().toString();
                user.setLogin(username);
                user.setPassword(password);

                ChatService.getInstance().login(user, new QBEntityCallbackImpl() {


                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(Login.this, TabActivity.class);
                        startActivity(intent);
                        Log.e("login", "ok");
                        SharedPreferences pre = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pre.edit();
                        edit.putString("username", username);
                        edit.putString("password", password);
                        edit.commit();

                        finish();
                      /*  progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(Login.this, Dialog.class);
                        startActivity(intent);
                        finish();
                        Log.e("login", "ok");
                        SharedPreferences pre=getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor edit=pre.edit();
                        edit.putString("username", username);
                        edit.putString("password", password);
                        edit.commit();*/


                    }

                    @Override
                    public void onError(List list) {
                        Log.e("login", "no");
                        progressBar.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
                        dialog.setMessage("Chat login errors: " + list.get(0).toString()).create().show();

                    }
                });

            }
        });

        loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("result", "ok");
                String facebookAccessToken = loginResult.getAccessToken().getToken();
                Log.e("result token",facebookAccessToken);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.e("result","fb err");

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.phongnt.phong.chattingtostranger", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}

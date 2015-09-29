package com.phongnt.phong.chattingtostranger.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.phongnt.phong.chattingtostranger.utils.Const;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText dateOfBith;
    private ImageButton displayphoto;
    private Button submitInfo;
    private EditText username;
    private EditText password;
    private EditText email;
    DatabaseHandler databaseHandler;
    MaterialDialog dialog;
    String signUpUsername ;
    String signUppass;

    // photo varriable
    String photo_url;
    String path="";
    Drawable photo_drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dateOfBith = (EditText)findViewById(R.id.dateOfBirth);
        displayphoto = (ImageButton)findViewById(R.id.displayphoto);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        photo_url = resIdToUri(this,R.drawable.noname).toString();
        setDateTimeField();
        ChatService.initIfNeed(getApplicationContext());
        databaseHandler = new DatabaseHandler(this);
        dateOfBith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });


        registerForContextMenu(displayphoto);
        displayphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("open", "ok");
                openContextMenu(v);
            }
        });


        submitInfo = (Button)findViewById(R.id.submitInfo);
        submitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new MaterialDialog.Builder(SignUp.this)
                        .title(null)
                        .content("Wating")
                        .progress(true, 0)
                        .show();



                QBAuth.createSession(new QBEntityCallbackImpl<QBSession>(){
                    @Override
                    public void onSuccess(QBSession result, Bundle params) {
                        super.onSuccess(result, params);
                        signUpUsername = username.getText().toString();
                        signUppass = password.getText().toString();
                        String signUpemail = email.getText().toString();
                        String signUpdateOfbith = dateOfBith.getText().toString();
                        final QBUser qbUser = new QBUser(signUpUsername, signUppass);
                        qbUser.setEmail(signUpemail);
                        StringifyArrayList<String> tags = new StringifyArrayList<String>();
                        tags.add(signUpdateOfbith);
                        //qbUser.setTags(tags);


                        QBUsers.signUp(qbUser, new QBEntityCallbackImpl<QBUser>() {
                            @Override
                            public void onSuccess(QBUser result, Bundle params) {
                                Log.e("sign up ", "ok");
                                super.onSuccess(result, params);
                                QBUser userLogin = new QBUser();
                                userLogin.setLogin(signUpUsername);
                                userLogin.setPassword(signUppass);
                                ChatService.getInstance().login(userLogin, new QBEntityCallbackImpl() {


                                    @Override
                                    public void onSuccess() {
                                        if(dialog.isShowing())
                                            dialog.dismiss();

                                        databaseHandler.addUser(qbUser.getLogin() != null ? qbUser.getLogin() : qbUser.getEmail(),
                                        qbUser.getPassword());
                                        Intent intent = new Intent(SignUp.this, TabActivity.class);
                                        startActivity(intent);

                                        finish();


                                    }

                                    @Override
                                    public void onError(List list) {
                                        Log.e("login", "no");
                                        if(dialog.isShowing())
                                            dialog.dismiss();

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                                        dialog.setMessage("Chat login errors: " + list.get(0).toString()).create().show();

                                    }
                                });

                            }

                            @Override
                            public void onError(List<String> errors) {
                                super.onError(errors);
                                if(dialog.isShowing())
                                    dialog.dismiss();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                                dialog.setMessage("Sign in  errors: " + errors.get(0)).create().show();
                                Log.e("error sign up", errors.get(0));

                            }
                        });
                    }

                    @Override
                    public void onError(List<String> errors) {
                        super.onError(errors);
                        Log.e("error session",errors.get(0));
                        if(dialog.isShowing())
                            dialog.dismiss();
                    }
                });

            }
        });
    }

    private void setDateTimeField() {


        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateOfBith.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }

    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(Const.ANDROID_RESOURCE + context.getPackageName()
                + Const.FORESLASH + resId);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Post Image");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.camer_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.take_photo:
                //Toast.makeText(context, "Selected Take Photo", Toast.LENGTH_SHORT).show();
                takePhoto();
                break;

            case R.id.choose_gallery:
                //Toast.makeText(context, "Selected Gallery", Toast.LENGTH_SHORT).show();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);

                break;

            case R.id.share_cancel:
                closeContextMenu();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public void takePhoto()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File folder = new File(Environment.getExternalStorageDirectory() + "/LoadImg");

        if(!folder.exists())
        {
            folder.mkdir();
        }
        final Calendar c = Calendar.getInstance();
        String new_Date= c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
        path=String.format(Environment.getExternalStorageDirectory() +"/LoadImg/%s.png","LoadImg("+new_Date+")");
        File photo = new File(path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1)
        {
            if(data!=null) {
                Uri photoUri = data.getData();
                if (photoUri != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.v("Load Image", "Gallery File Path=====>>>" + filePath);
                    photo_url = filePath;
                    //Log.v("Load Image", "Image List Size=====>>>"+image_list.size());

                    //updateImageTable();
                    new GetImages().execute();
                }
            }
        }

        if(requestCode==2)
        {
            Log.v("Load Image", "Camera File Path=====>>>"+path);
            photo_url = path;
            //Log.v("Load Image", "Image List Size=====>>>"+image_list.size());
            // updateImageTable();
            new GetImages().execute();

        }
    }

    public void updateImageTable()
    {


        displayphoto.setImageDrawable(photo_drawable);

    }

    public Drawable loadImagefromurl(Bitmap icon)
    {
        Drawable d=new BitmapDrawable(icon);
        return d;
    }

    public class GetImages extends AsyncTask<Void, Void, Void>
    {
        public ProgressDialog progDialog=null;

        protected void onPreExecute()
        {
            // progDialog=ProgressDialog.show(getApplicationContext(), "", "Loading...",true);
        }
        @Override
        protected Void doInBackground(Void... params)
        {

            Bitmap bitmap = BitmapFactory.decodeFile(photo_url.toString().trim());
            bitmap = Bitmap.createScaledBitmap(bitmap,500, 500, true);
            Drawable d=loadImagefromurl(bitmap);
            photo_drawable = d;


            return null;
        }

        protected void onPostExecute(Void result)
        {
            /*if(progDialog.isShowing())
            {
                progDialog.dismiss();
            }*/
            updateImageTable();
        }
    }
}
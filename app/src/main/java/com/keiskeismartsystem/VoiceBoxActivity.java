package com.keiskeismartsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.helper.AppSession;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.ImageCompression;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.User;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class VoiceBoxActivity extends AppCompatActivity implements AsyncResponse {

    private static final int RESULT_LOAD_IMG = 1002;
    private static UserSession _userSession;
    private static String _name, _email, _handphone, _title, _description, _photo, _anonymous = "1", _user_id = "0", imgDecodableString = "", temp_path="";
    private static AlertDialog _dbuilder;
    private static EditText _et_title, _et_description;
    private static Button _btn_submit;
    private static ImageButton _btn_image;
    private static ConnectionDetector _conn;
    private static ProgressDialog _progress;
    private static AppSession _appSession;
    private UserSession _user_session;
    private static TextView _tv_path;
    int selected = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_box);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        _appSession = new AppSession(getApplicationContext());
        _user_session = new UserSession(getApplicationContext());
        _conn = new ConnectionDetector(getApplicationContext());
        _tv_path = (TextView) findViewById(R.id.tv_path_file);
        _et_title = (EditText) findViewById(R.id.et_title);
        _et_description = (EditText) findViewById(R.id.et_description);
        _btn_image = (ImageButton) findViewById(R.id.btn_image);
        _btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
        _btn_submit = (Button) findViewById(R.id.btn_submit);
        _btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = _et_title.getText().toString();
                String description = _et_description.getText().toString();
                if(isValidTitle(title) && isValidQuestion(description)){
                    _title = title;
                    _description = description;
                    submitVoiceBox(v);
                }
            }
        });

        _userSession = new UserSession(getApplicationContext());
        if(!_userSession.isUserLoggedIn()){
            Log.v("keiskei_debug", "tidak login");
            _anonymous = "1";
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            aBuilder.setView(inflater.inflate(R.layout.dialog_voice_box, null))
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(VoiceBoxActivity.this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
//            aBuilder.show();
            _dbuilder = aBuilder.create();
            _dbuilder.show();
            EditText et_name = (EditText) _dbuilder.findViewById(R.id.et_name);
            EditText et_email = (EditText) _dbuilder.findViewById(R.id.et_email);
            EditText et_handphone = (EditText) _dbuilder.findViewById(R.id.et_handphone);
            et_name.setText(_appSession.getAnonName());
            et_email.setText(_appSession.getAnonEmail());
            et_handphone.setText(_appSession.getAnonHandphone());
            _dbuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText et_name = (EditText) _dbuilder.findViewById(R.id.et_name);
                            EditText et_email = (EditText) _dbuilder.findViewById(R.id.et_email);
                            EditText et_handphone = (EditText) _dbuilder.findViewById(R.id.et_handphone);
                            String name = et_name.getText().toString();
                            String email = et_email.getText().toString();
                            String handphone = et_handphone.getText().toString();
                            if(isValidName(name, et_name) && isValidEmail(email, et_email)){
                                _name = name;
                                _email = email;
                                _handphone = handphone;
                                _dbuilder.cancel();
                            }
                        }
                    });
        }else{
            User user = _userSession.getUserSessionData();
            _name = user.getName();
            _email = user.getEmail();
            _handphone = user.getTelephone();
            _anonymous = "0";
            _user_id = Integer.toString(user.getID());
            findViewById(R.id.tab).setVisibility(View.GONE);
            Log.v("keiskei_debug", "login");
        }
        findViewById(R.id.support).setSelected(true);
        selected = R.id.support;
    }
    public void submitVoiceBox(View v){
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(getApplicationContext(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

//        try {
////            _photo = URLEncoder.encode(imgDecodableString, "utf-8");
            imgDecodableString = "";
//
//        }catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return;
//        }
        _progress = new ProgressDialog(v.getContext());
        _progress.setCancelable(true);
        _progress.setMessage("Submit..");
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.show();

        String[] params = new String[]{ "SUBMIT_VB", _name, _email, _handphone, _title, _description,
                _anonymous, _appSession.getGCMID(), _user_id, _photo};
        sendImageLoopj(params);
//        ClientSocket cs = new ClientSocket(this);
//        cs.delegate = this;
        _photo = "";
        _tv_path.setText("");
//        cs.execute(params);
    }
    public void sendImageLoopj(String[] params){
        RequestParams data = new RequestParams();
        data.put("name", params[1]);
        data.put("email", params[2]);
        data.put("handphone", params[3]);
        data.put("title", params[4]);
        data.put("description", params[5]);
        data.put("is_anonymous", params[6]);
        data.put("gcm_id", params[7]);
        data.put("user_id", params[8]);
        if(!temp_path.isEmpty()){
            try
            {
                File temp_file = new File(temp_path);
                data.put("photo", temp_file);
                temp_path = "";
            }catch(Exception e){

            }
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post( "https://keiskei.co.id/m/voicebox/storetwo", data, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String resp = "";
                try {
                    resp = response.getString("RESP");
                    if(resp.equals("SCSVCBX")){
                        String data;
                        data = response.getString("MESSAGE");
                        _progress.dismiss();
                        _et_title.setText("");
                        _et_description.setText("");

                        Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(resp.equals("FLDRGSTR")){
                        String data;
                        data = response.getString("MESSAGE");
                        _progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        _progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan server.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


    }

    private boolean isValidEmail(String email_t, EditText et_email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email_t);
        if (matcher.matches()) {
            et_email.setError(null);
            return true;
        }
        else {
            et_email.setError("Email tidak valid");
            return false;
        }
    }
    private  boolean isValidName(String username_t, EditText et_name){
        if (username_t != null && username_t.length() > 5) {
            et_name.setError(null);
            return true;
        }else if (username_t == null ){
            et_name.setError("Nama Wajib diisi");
        }else {
            et_name.setError("Nama Minimal 6 karakter");
        }
        return false;
    }
    private  boolean isValidTitle(String title){
        if (title != null && title.length() > 5) {
            _et_title.setError(null);
            return true;
        }else if (title == null ){
            _et_title.setError("Judul Wajib diisi");
        }else {
            _et_title.setError("Judul Minimal 6 karakter");
        }
        return false;
    }
    private  boolean isValidQuestion(String question){
        if (question != null && question.length() > 5) {
            _et_description.setError(null);
            return true;
        }else if (question == null ){
            _et_description.setError("Pertanyaan Wajib diisi");
        }else {
            _et_description.setError("Pertanyaan Minimal 6 karakter");
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voice_box, menu);
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
        if(id == android.R.id.home){
            if(_user_session.isUserLoggedIn()){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, LandingActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void processFinish(String output){
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = "";
        try {
            resp = json.getString("RESP");
            if(resp.equals("SCSVCBX")){
                String data;
                data = json.getString("MESSAGE");
                _progress.dismiss();
                _et_title.setText("");
                _et_description.setText("");

                Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                toast.show();
            }
            else if(resp.equals("FLDRGSTR")){
                String data;
                data = json.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String path = cursor.getString(columnIndex);
                cursor.close();
                temp_path = path;

                Bitmap icon = BitmapFactory.decodeFile(path);
                ImageHelper ih = new ImageHelper();
                ImageCompression imageCompression = new ImageCompression(getApplicationContext());
                temp_path = imageCompression.compressImage(path);
                _tv_path.setText(path);
            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Terjadi kesalahan" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public void onBackPressed() {
        if(_user_session.isUserLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public void changeBigFragment(View view){
        switch (view.getId()) {
            case R.id.product:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.product;
                startActivity(new Intent(VoiceBoxActivity.this, LandingActivity.class));
                break;
            case R.id.login:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.login;
                startActivity(new Intent(VoiceBoxActivity.this, LoginActivity.class));
                break;
            case R.id.register:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.register;
                startActivity(new Intent(VoiceBoxActivity.this, RegisterActivity.class));
                break;
            case R.id.chat:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.chat;
                startActivity(new Intent(VoiceBoxActivity.this, ChatActivity.class));
                break;
            case R.id.support:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.support;
                startActivity(new Intent(VoiceBoxActivity.this, VoiceBoxActivity.class));
                break;
        }
    }
}

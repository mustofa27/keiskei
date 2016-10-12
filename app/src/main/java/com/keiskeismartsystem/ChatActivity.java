package com.keiskeismartsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.adapter.ChatAdapter;
import com.keiskeismartsystem.dbsql.ChatTransact;
import com.keiskeismartsystem.dbsql.NotifTransact;
import com.keiskeismartsystem.helper.AppSession;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.ImageCompression;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.Chat;
import com.keiskeismartsystem.model.Notif;
import com.keiskeismartsystem.model.User;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;

public class ChatActivity extends AppCompatActivity implements AsyncResponse {
    private static final int RESULT_LOAD_IMG = 1001;
    private static ListView _lv_chats;
    private ArrayList<Chat> _chats;
    private static ImageButton _btn_send, _btn_image;
    private static EditText _et_message;
    private static String _name, _email, _handphone, _message, _is_anonymous = "1", _gcm_id, _user_id = "0", _photo = "", imgDecodableString, temp_path="";
    private static AlertDialog _dbuilder;

    private UserSession _user_session;
    private AppSession _app_session;
    private ChatTransact _chatTransact;
    private ConnectionDetector _conn;
    private ChatAdapter cadapter;
    GCMReceiverChat gcmReceiverChat = null;
    Boolean flagR = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        _chats = new ArrayList<Chat>();
        _user_session = new UserSession(getApplicationContext());
        _app_session = new AppSession(getApplicationContext());
        _chatTransact = new ChatTransact(getApplicationContext());
        _conn = new ConnectionDetector(getApplicationContext());

        _lv_chats = (ListView) findViewById(R.id.lv_chats);
        _et_message = (EditText) findViewById(R.id.et_message);
        _et_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEND)) {
                    String message = _et_message.getText().toString();
                    if (message != null && !message.isEmpty()) {

                        sentMessage(false);

                        Log.v("keiskeidebug inilho", "inilho");
                    }
                }
                return false;
            }
        });
        _btn_send = (ImageButton) findViewById(R.id.btn_send);
        _btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = _et_message.getText().toString();
                if (message != null && !message.isEmpty()) {
                    sentMessage(false);
                }
            }
        });
//        _btn_image = (ImageButton) findViewById(R.id.btn_image);

//        _btn_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendImage();
//            }
//        });


        chatHistory();
        gcmReceiverChat = new GCMReceiverChat();
//        IntentFilter filter = new IntentFilter("com.keiskeismartsystem.GCMMESSAGE");
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.v("keiskeidebug", "Nerima pesan baru");
//                Bundle results = intent.getExtras();
//                char flag = results.getChar("FLAG");
//                Log.v("keiskeidebug", "Nerima pesan baru FLAG" + flag);
//                switch (flag) {
//                    case 'C':
//                        Chat chat_t = (Chat) results.getSerializable(Chat.KEY);
//                        Log.v("keiskeidebug", "Nerima pesan baru DSPLAY" + chat_t.getName());
//                        displayMessage(chat_t);
//                        break;
//                }
//
//            }
//        }, filter);
        if(!_user_session.isUserLoggedIn()) {
            Log.v("keiskei_debug", "tidak login");
            _is_anonymous = "1";
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
                    Intent intent = new Intent(ChatActivity.this, LandingActivity.class);
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
            et_name.setText(_app_session.getAnonName());
            et_email.setText(_app_session.getAnonEmail());
            et_handphone.setText(_app_session.getAnonHandphone());
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
                            Log.v("keiskeidebugisian", name + email + handphone);
                            if (isValidName(name, et_name) && isValidEmail(email, et_email)) {
                                Log.v("keiskeidebugisian", name + email + handphone);
                                _name = name;
                                _email = email;
                                _handphone = handphone;
                                _app_session.setAnonName(name);
                                _app_session.setAnonEmail(email);
                                _app_session.setAnonHandphone(handphone);
                                _dbuilder.cancel();
                            }
                        }
                    });

        }else{
            User user = _user_session.getUserSessionData();
            _name = user.getName();
            _email = user.getEmail();
            _handphone = user.getTelephone();
        }
    }
    private void sendImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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
    public void sentMessage(Boolean is_file){
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(getApplicationContext(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        _message = _et_message.getText().toString();
        _et_message.setText("");
        if (_user_session.isUserLoggedIn())
        {
            User user = _user_session.getUserSessionData();
            _is_anonymous = "0";
            _user_id = user.getID() + "";
        }
        String path = "";
        if(is_file){
//            try {
//                _photo = URLEncoder.encode(imgDecodableString, "utf-8");
//                ImageHelper ih = new ImageHelper();
//                Bitmap image = ih.StringToBitMap(imgDecodableString);
//                path = ih.storeImage(getApplicationContext(), image);
//                imgDecodableString = "";
//
//
//            }catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return;
//            }
            _photo = temp_path;
            String[] params = new String[]{ "SEND_CHAT", _message, _is_anonymous,
                    _app_session.getGCMID(), _user_id, temp_path,  _name, _email, _handphone};
            sendImageLoopj(params);
            temp_path = "";
            _photo = "";
        }else{
            String[] params = new String[]{ "SEND_CHAT", _message, _is_anonymous,
                    _app_session.getGCMID(), _user_id, _name, _email, _handphone};
            _photo = "";
            sendImageLoopjDua(params);
//            ClientSocket cs = new ClientSocket(this);
//            cs.delegate = this;
//            cs.execute(params);
        }


    }
    public void sendImageLoopjDua(String[] params){
        RequestParams data = new RequestParams();
        data.put("description", params[1]);
        data.put("is_anonymous", params[2]);
        data.put("gcm_id", params[3]);
        data.put("user_id", params[4]);
        data.put("name", params[5]);
        data.put("email", params[6]);
        data.put("handphone", params[7]);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post( "http://smartv2.lapantiga.com/m/chat/storetwo", data, new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("keiskeidebug", "loopj2 chat " + statusCode);
                Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan server.", Toast.LENGTH_SHORT);
                toast.show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan server.", Toast.LENGTH_SHORT);
                toast.show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("keiskeidebug", "loopj chat " + statusCode);

                String resp = "";
                try {
                    resp = response.getString("RESP");
                    if(resp.equals("SCSCHT")){
                        String data;
                        data = response.getString("DATA");
                        JSONObject notif_t = new JSONObject(data);
                        Chat chat = new Chat();
                        try {
                            chat.setSid(Integer.parseInt(notif_t.getString("id")));
                            try {
                                String name = notif_t.getString("name");
                                chat.setName(name);
                            }catch (Exception e){
                                chat.setName("");
                            };

                            try {
                                String description_t = notif_t.getString("description");
                                chat.setDescription(description_t);
                            }catch (Exception e){
                                chat.setDescription("");
                            };

                            try {
                                String photo_t = notif_t.getString("photo");
                                chat.setPhotoExt(photo_t);
                            }catch (Exception e){
                                chat.setPhotoExt("");
                            };
                            try {
                                String photo_t = notif_t.getString("path_user");
                                chat.setPhotoInt(photo_t);
                            }catch (Exception e){
                                chat.setPhotoInt("");
                            };
                            try {
                                String is_admin_t = notif_t.getString("is_admin");
                                char is_admin_c = is_admin_t.charAt(0);
                                chat.setIsAdmin(is_admin_c);
                            }catch (Exception e){
                                chat.setIsAdmin('0');
                            };
                            try {
                                String is_admin_t = notif_t.getString("read_flag");
                                char is_admin_c = is_admin_t.charAt(0);
                                chat.setReadFlag(is_admin_c);
                            }catch (Exception e){
                                chat.setReadFlag('0');
                            };

                            try {
                                String created_at_t = notif_t.getString("created_at");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = format.parse(created_at_t);
                                chat.setCreatedAt(date);
                            }catch (ParseException e){

                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();
                                chat.setCreatedAt(date);
                            }
                            String realpath = chat.getPhotoInt();
                            if(realpath != null || realpath.isEmpty()){
                                File file = new File(realpath);
                                if (file.exists()){
                                    BitmapFactory.Options bmo = new BitmapFactory.Options();
                                    try {
                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                                        ImageHelper ih = new ImageHelper();
                                        String path_t = ih.storeImage(getApplicationContext(), bitmap);
                                        chat.setPhotoInt(path_t);
                                    }catch (OutOfMemoryError e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            _chatTransact.insert(chat);
                            displayMessage(chat);

                        }catch (Exception e){
                            Log.v("keiskeidebug", e.toString());
                            e.printStackTrace();
                        }
                    }
                    else if(resp.equals("FLDRGSTR")){
                        String data;
                        data = response.getString("MESSAGE");
                        Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    public void sendImageLoopj(String[] params){
        RequestParams data = new RequestParams();
        data.put("description", params[1]);
        data.put("is_anonymous", params[2]);
        data.put("gcm_id", params[3]);
        data.put("user_id", params[4]);

        data.put("name", params[6]);
        data.put("email", params[7]);
        data.put("handphone", params[8]);

        String path = params[5];
        Log.v("keiskeidebug", "belum " + path);


        if(!path.isEmpty()){
            try
            {

                File temp_file = new File(path);
                data.put("photo", temp_file);
                path = "";
                Log.v("keiskeidebug", "masuk file " + path);
            }catch(Exception e){

                Log.v("keiskeidebug", "gagal file " + path);
            }
        }
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.post( "http://smartv2.lapantiga.com/m/chat/storetwo", data, new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("keiskeidebug", "loopj2 chat " + statusCode);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan server.", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("keiskeidebug", "loopj chat " + statusCode);

                String resp = "";
                try {
                    resp = response.getString("RESP");
                    if(resp.equals("SCSCHT")){
                        String data;
                        data = response.getString("DATA");
                        JSONObject notif_t = new JSONObject(data);
                        Chat chat = new Chat();
                        try {
                            chat.setSid(Integer.parseInt(notif_t.getString("id")));
                            try {
                                String name = notif_t.getString("name");
                                chat.setName(name);
                            }catch (Exception e){
                                chat.setName("");
                            };

                            try {
                                String description_t = notif_t.getString("description");
                                chat.setDescription(description_t);
                            }catch (Exception e){
                                chat.setDescription("");
                            };

                            try {
                                String photo_t = notif_t.getString("photo");
                                chat.setPhotoExt(photo_t);
                            }catch (Exception e){
                                chat.setPhotoExt("");
                            };
                            try {
                                String photo_t = notif_t.getString("path_user");
                                chat.setPhotoInt(photo_t);
                            }catch (Exception e){
                                chat.setPhotoInt("");
                            };
                            try {
                                String is_admin_t = notif_t.getString("is_admin");
                                char is_admin_c = is_admin_t.charAt(0);
                                chat.setIsAdmin(is_admin_c);
                            }catch (Exception e){
                                chat.setIsAdmin('0');
                            };
                            try {
                                String is_admin_t = notif_t.getString("read_flag");
                                char is_admin_c = is_admin_t.charAt(0);
                                chat.setReadFlag(is_admin_c);
                            }catch (Exception e){
                                chat.setReadFlag('0');
                            };

                            try {
                                String created_at_t = notif_t.getString("created_at");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = format.parse(created_at_t);
                                chat.setCreatedAt(date);
                            }catch (ParseException e){

                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();
                                chat.setCreatedAt(date);
                            }
                            String realpath = chat.getPhotoInt();
                            if(realpath != null || realpath.isEmpty()){
                                File file = new File(realpath);
                                if (file.exists()){
                                    BitmapFactory.Options bmo = new BitmapFactory.Options();
                                    try {
                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                                        ImageHelper ih = new ImageHelper();
                                        String path_t = ih.storeImage(getApplicationContext(), bitmap);
                                        chat.setPhotoInt(path_t);
                                    }catch (OutOfMemoryError e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            _chatTransact.insert(chat);
                            displayMessage(chat);

                        }catch (Exception e){
                            Log.v("keiskeidebug", e.toString());
                            e.printStackTrace();
                        }
                    }
                    else if(resp.equals("FLDRGSTR")){
                        String data;
                        data = response.getString("MESSAGE");
                        Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void displayMessage(Chat chat) {

        cadapter.add(chat);

        cadapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        _lv_chats.setSelection(_lv_chats.getCount() - 1);
    }
    private void chatHistory(){
        List<Chat> chats = _chatTransact.all();
        for (Chat chat : chats){
            _chats.add(chat);
        }

        cadapter = new ChatAdapter(ChatActivity.this, new ArrayList<Chat>());
        _lv_chats.setAdapter(cadapter);
        if (_chats != null)
        for(int i=0; i<_chats.size(); i++) {
            Chat chat = _chats.get(i);
            displayMessage(chat);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
        if(id == R.id.action_attach){
            sendImage();
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

    @Override
    public void processFinish(String output) {
        Log.v("keiskeidebug chat", output);
        JSONObject json = new JSONObject();

        try {
            json = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = "";
        try {
            resp = json.getString("RESP");
            if(resp.equals("SCSCHT")){
                String data;
                data = json.getString("DATA");
                JSONObject notif_t = new JSONObject(data);
                Chat chat = new Chat();
                try {
                    chat.setSid(Integer.parseInt(notif_t.getString("id")));
                    try {
                        String name = notif_t.getString("name");
                        chat.setName(name);
                    }catch (Exception e){
                        chat.setName("");
                    };

                    try {
                        String description_t = notif_t.getString("description");
                        chat.setDescription(description_t);
                    }catch (Exception e){
                        chat.setDescription("");
                    };

                    try {
                        String photo_t = notif_t.getString("photo");
                        chat.setPhotoExt(photo_t);
                    }catch (Exception e){
                        chat.setPhotoExt("");
                    };
                    try {
                        String photo_t = notif_t.getString("path_user");
                        chat.setPhotoInt(photo_t);
                    }catch (Exception e){
                        chat.setPhotoInt("");
                    };
                    try {
                        String is_admin_t = notif_t.getString("is_admin");
                        char is_admin_c = is_admin_t.charAt(0);
                        chat.setIsAdmin(is_admin_c);
                    }catch (Exception e){
                        chat.setIsAdmin('0');
                    };
                    try {
                        String is_admin_t = notif_t.getString("read_flag");
                        char is_admin_c = is_admin_t.charAt(0);
                        chat.setReadFlag(is_admin_c);
                    }catch (Exception e){
                        chat.setReadFlag('0');
                    };

                    try {
                        String created_at_t = notif_t.getString("created_at");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = format.parse(created_at_t);
                        chat.setCreatedAt(date);
                    }catch (ParseException e){

                        Calendar c = Calendar.getInstance();
                        Date date = c.getTime();
                        chat.setCreatedAt(date);
                    }
                    String realpath = chat.getPhotoInt();
                    if(realpath != null || realpath.isEmpty()){
                        File file = new File(realpath);
                        if (file.exists()){
                            BitmapFactory.Options bmo = new BitmapFactory.Options();
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                                ImageHelper ih = new ImageHelper();
                                String path_t = ih.storeImage(getApplicationContext(), bitmap);
                                chat.setPhotoInt(path_t);
                            }catch (OutOfMemoryError e){
                                e.printStackTrace();
                            }
                        }
                    }
                    _chatTransact.insert(chat);
                    displayMessage(chat);

                }catch (Exception e){
                    Log.v("keiskeidebug", e.toString());
                    e.printStackTrace();
                }
            }
            else if(resp.equals("FLDRGSTR")){
                String data;
                data = json.getString("MESSAGE");
                Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                toast.show();
            }else{
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
                ImageCompression imageCompression = new ImageCompression(getApplicationContext());
                temp_path = imageCompression.compressImage(path);

                cursor.close();

//                Bitmap icon = BitmapFactory.decodeFile(path);
//                ImageHelper ih = new ImageHelper();
//                icon = ih.resizeBitmap(icon, 600, 400);
//                imgDecodableString = ih.BitMapToString(icon);
                sentMessage(true);
            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!flagR){
            IntentFilter filter = new IntentFilter("com.keiskeismartsystem.GCMMESSAGE");
            registerReceiver(gcmReceiverChat, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(flagR) {
            unregisterReceiver(gcmReceiverChat);
            flagR = false;
        }

    }
    public class GCMReceiverChat extends BroadcastReceiver {
        public GCMReceiverChat() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            Bundle results = intent.getExtras();
            char flag = results.getChar("FLAG");
            Log.v("keiskeidebug", "Nerima pesan baru FLAG" + flag);
            switch (flag) {
                case 'C':
                    Chat chat_t = (Chat) results.getSerializable(Chat.KEY);
                    Log.v("keiskeidebug", "Nerima pesan baru DSPLAY" + chat_t.getName());
                    displayMessage(chat_t);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(_user_session.isUserLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        }

    }
}
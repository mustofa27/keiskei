package com.keiskeismartsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.GCM.GCMClientManager;
import com.keiskeismartsystem.helper.AppSession;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.User;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    private static EditText _username, _password;
    private static ProgressDialog _progress;
    int selected = 0;

    private static UserSession userSession;
    private static ConnectionDetector _conn;
    GCMClientManager gcmClientManager;
    public static final String PROJECT_NUMBER = "503282155389";
    private static String _registrationId = "";
    private static AppSession _appSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _appSession = new AppSession(getApplicationContext());
        _username = (EditText)findViewById(R.id.username);
        _password = (EditText)findViewById(R.id.password);
        _password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login(v);
                }
                return false;
            }
        });

        userSession = new UserSession(getApplicationContext());
        _conn = new ConnectionDetector(getApplicationContext());
        findViewById(R.id.login).setSelected(true);
        selected = R.id.login;
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

    public void login(View v){
        String username_t = _username.getText().toString();
        String password_t = _password.getText().toString();
        if (isValidUsername(username_t) && isValidPassword(password_t)){
            if(!_conn.isConnectedToInternet()){
                Toast toast = Toast.makeText(getApplicationContext(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            gcmClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {
                    // Link to web
                    _registrationId = registrationId;
                    _appSession.setGCMID(registrationId);
                    Log.i("GCMClientManager ini", registrationId);
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });
            _progress = new ProgressDialog(v.getContext());
            _progress.setCancelable(true);
            _progress.setMessage("Login..");
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progress.setProgress(0);
            _progress.setMax(100);
            _progress.show();
            String[] params = new String[]{ "LOGIN", username_t, password_t, _registrationId};
            ClientSocket cs = new ClientSocket(this);
            cs.delegate = this;
            cs.execute(params);
        }
    }
    private boolean isValidUsername(String username) {
        if (username != null && username.length() > 5) {
            _username.setError(null);
            return true;
        }else if (username == null ){
            _username.setError("Username Wajib diisi");
        }else {
            _username.setError("Username Minimal 6 karakter");
        }
        return false;
    }
    public void changeBigFragment(View view){
        switch (view.getId()) {
            case R.id.product:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.product;
                startActivity(new Intent(LoginActivity.this, LandingActivity.class));
                break;
            case R.id.login:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.login;
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                break;
            case R.id.register:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.register;
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.chat:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.chat;
                startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                break;
            case R.id.support:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.support;
                startActivity(new Intent(LoginActivity.this, VoiceBoxActivity.class));
                break;
        }
    }
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 5) {
            _password.setError(null);
            return true;
        }else if (pass == null ){
            _password.setError("Password Wajib diisi");
        }else {
            _password.setError("Password Minimal 6 karakter");
        }
        return false;
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
            if(resp.equals("SCSLOGIN")){
                String data;
                data = json.getString("DATA");
                JSONObject user_t = new JSONObject(data);
                User user = new User();
                int total = Integer.parseInt(json.getString("TOTAL_BUY"));
                try {
                    user.setID(Integer.parseInt(user_t.getString("id")));
                    user.setCode(user_t.getString("code"));
                    user.setEmail(user_t.getString("email"));
                    user.setName(user_t.getString("name"));
                    user.setUsername(user_t.getString("username"));
                    user.setGCMID(user_t.getString("gcm_id"));
                    String _gender = user_t.getString("gender");
                    user.setGender(_gender.charAt(0));
                    user.setTelephone(user_t.getString("mobile"));
                    user.setAddress(user_t.getString("address"));
                    user.setPhoto(user_t.getString("photo"));
                    user.setPinBB(user_t.getString("pinbb"));
                    user.setTotalBought(total);
                    user.setWebsite(user_t.getString("website"));
                    user.setInstagram(user_t.getString("instagram"));
                    user.setFB(user_t.getString("facebook"));
                    user.setBonus(user_t.getString("bonus"));
                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.v("keiskeidebug", "exception login object");
                }
                Log.v("keiskeidebug", "isi user : " + user.getWebsite() + user.getInstagram() + user.getFB() + user.getPinBB());
                userSession.createSessionLogin(user);
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Anda berhasil masuk ke sistem.", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(this, SplashScreen.class);
                startActivity(intent);
            }
            else{
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Username dan password tidak valid.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

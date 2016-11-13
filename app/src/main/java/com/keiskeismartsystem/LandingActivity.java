package com.keiskeismartsystem;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.GCM.GCMClientManager;
import com.keiskeismartsystem.dbsql.CityTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.fragment.DashboardFragment;
import com.keiskeismartsystem.fragment.NotificationFragment;
import com.keiskeismartsystem.fragment.ProductList;
import com.keiskeismartsystem.fragment.ProfileFragment;
import com.keiskeismartsystem.fragment.SettingFragment;
import com.keiskeismartsystem.helper.AppSession;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.City;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LandingActivity extends AppCompatActivity {
    private static AppSession _appSession;
    private static UserSession _userSession;
    private static CityTransact _cityTransact;
    private static Context _context;
    private static ConnectionDetector _connection;
    int selected = 0;
    //private static Button _btn_chat,_btn_voicebox;
    private FragmentTabHost mTabHost;
    GCMClientManager gcmClientManager;

    private static ProgressDialog _progress;
    private static final String _base_url = "https://keiskei.co.id/";
    public static final String PROJECT_NUMBER = "503282155389";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);
        _context = getApplicationContext();
        _connection = new ConnectionDetector(getApplicationContext());
        _cityTransact = new CityTransact(getApplicationContext());

        if(!_connection.isConnectedToInternet()){
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle("Info");
            ad.setMessage("Tidak ada koneksi internet.");
            ad.setIcon(android.R.drawable.ic_dialog_alert);
            ad.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            ad.show();
            Log.v("keiskeidebug", "masuk not connected");
            return;
        }

        _userSession = new UserSession(_context);
        _appSession = new AppSession(_context);
        int count_city = 0;
        try{
            List<City> cities = _cityTransact.all();
            count_city = cities.size();
            Log.v("keiskeidebug", count_city + "");
        }catch (Exception e)
        {

        }


        if(_userSession.isUserLoggedIn())
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        //if(!_appSession.checkFirstTime() || count_city == 0)
        if(false)
        {
            _appSession.setFirstTime();
            _cityTransact = new CityTransact(_context);
            _cityTransact.truncate();
            _progress = new ProgressDialog(this);
            _progress.setCancelable(false);
            _progress.setMessage("Initialization...");
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progress.setProgress(0);
            _progress.setMax(100);
            _progress.show();
            SendHttpRequestTask t = new SendHttpRequestTask();
            String url = _base_url + "m/init";
            Log.v("keiskeidebug", url);
            String[] params = new String[]{url};
            t.execute(params);
//            downloadJSON();

        }

        refreshGCM();
        ProductList pFragment = new ProductList();
        FragmentManager fragmentManager1 = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
        fragmentTransaction1.replace(R.id.content_container, pFragment );
        fragmentTransaction1.addToBackStack(null);
        fragmentTransaction1.commit();
        findViewById(R.id.product).setSelected(true);
        selected = R.id.product;
        /*_btn_chat = (Button) findViewById(R.id.btn_chat);
        _btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        _btn_voicebox = (Button) findViewById(R.id.btn_voice_box);
        _btn_voicebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, VoiceBoxActivity.class);
                startActivity(intent);
            }
        });*/
    }

    public void changeBigFragment(View view){
        switch (view.getId()) {
            case R.id.product:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.product;
                startActivity(new Intent(LandingActivity.this, LandingActivity.class));
                break;
            case R.id.login:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.login;
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                break;
            case R.id.register:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.register;
                startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
                break;
            case R.id.chat:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.chat;
                startActivity(new Intent(LandingActivity.this, ChatActivity.class));
                break;
            case R.id.support:
                if(selected!=0)
                    findViewById(selected).setSelected(false);
                view.setSelected(true);
                selected = R.id.support;
                startActivity(new Intent(LandingActivity.this, VoiceBoxActivity.class));
                break;
        }
    }
    public void refreshGCM(){
        gcmClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                // Link to web
                String gcm_id = registrationId;
                _appSession.setGCMID(gcm_id);
                GCMRegistration t = new GCMRegistration();
                String url = "m/gcmstore";
                Log.v("keiskeidebug", url);
                //ANONYMOUS
                String[] params = new String[]{url, gcm_id, "1", "0"};
                t.execute(params);
                Log.i("GCMClientManager ini", registrationId);
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
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
    /*public void login(View v){
        startActivity(new Intent(LandingActivity.this, LoginActivity.class));
    }*/
    /*public void register(View v){
        startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
    }*/
    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            String resp = "";
            HttpClient request = HttpClient.get(params[0]);
            if (request.ok())
            {
                resp = request.body();
            }else{
                resp = "{RESP : 'ERROR' }";
            }
            Log.v("keiskeidebug", request.code() + "");

            try {
                URL url = new URL("https://keiskei.co.id/m/init/city");
                in = new BufferedInputStream(url.openStream());
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                        + "/Android/data/"
                        + _context.getPackageName()
                        + "/Files");
                if (! mediaStorageDir.exists()){
                    if (! mediaStorageDir.mkdirs()){
                        return null;
                    }
                }

                File file = new File(mediaStorageDir, "init_data.json");
                fout = new FileOutputStream(file);

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1){
                    fout.write(data, 0, count);
                }
            }
            catch (SocketTimeoutException e){
                resp = "{RESP : 'ERROR' }";
            }
            catch (MalformedURLException e){
                resp = "{RESP : 'ERROR' }";
                e.printStackTrace();
            }catch (IOException e){
                resp = "{RESP : 'ERROR' }";
                e.printStackTrace();
            }


            return resp;
        }

        @Override
        protected void onPostExecute(String output) {
            Log.v("keiskeidebug", output);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = null;

            try {
                resp = json.getString("RESP");
                Bundle bundle = new Bundle();
                if(resp.equals("SCSINIT")){
                    JSONObject city_json = new JSONObject();
                    String city_str = loadCityJSON();
                    city_json = new JSONObject(city_str);
                    JSONArray city_t = city_json.getJSONArray("CITY");
                    for (int i = 0; i < city_t.length(); i++){
                        JSONObject tmp = city_t.getJSONObject(i);
                        City city = new City();
                        try {
                            city.setSid(Integer.parseInt(tmp.getString("id")));
                            city.setProvince(Integer.parseInt(tmp.getString("ms_province_id")));
                            city.setName(tmp.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        Log.v("keiskeidebug insertcity", city.getName());
                        _cityTransact.insert(city);
                    }
                    _progress.dismiss();
                    _appSession.setFirstTime();
                    Toast toast = Toast.makeText(_context, "Inisialisasi aplikasi berhasil.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    _progress.dismiss();
                    Log.v("keiskeidebug", output);
                    Toast toast = Toast.makeText(_context, "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class GCMRegistration extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            String resp = "";
            String url = _base_url + args[0];
            HttpClient request = HttpClient.post(url);
            request.part("gcm_id", args[1]);
            request.part("is_anonymous", args[2]);
            request.part("user_id", args[3]);
            Log.v("keiskeidebug", "as" + url);
            if (request.ok())
            {
                resp = request.body();
            }else{
                resp = "{RESP : 'ERROR' }";
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String output) {

        }
    }

    public String loadCityJSON(){
        String json = "";
        try{
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + _context.getPackageName()
                    + "/Files");
            File file = new File(mediaStorageDir, "init_data.json");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            json = sb.toString();

        }catch (Exception e){
            json = "";
        }
        return json;
    }
}

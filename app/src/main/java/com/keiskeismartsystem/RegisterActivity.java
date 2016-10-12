package com.keiskeismartsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.keiskeismartsystem.GCM.GCMClientManager;
import com.keiskeismartsystem.dbsql.CityTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.helper.AppSession;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.City;
import com.keiskeismartsystem.model.User;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {
    private static ProgressDialog _progress;
    private static final String _base_url = "http://smartv2.lapantiga.com/";
    private static Spinner _city;

    private static AppSession _appSession;

    private static EditText _email, _username, _handphone;
    String city_id;
    private static CityTransact _ct;
    private static ConnectionDetector _conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _ct = new CityTransact(getApplicationContext());
        _conn = new ConnectionDetector(getApplicationContext());
        _username = (EditText) findViewById(R.id.username);
        _email = (EditText) findViewById(R.id.email);
        _handphone = (EditText) findViewById(R.id.handphone);
        _city = (Spinner) findViewById(R.id.city);
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();

            return;
        }
        int count_city = 0;
        try{
            List<City> cities2 = _ct.all();
            count_city = cities2.size();
            Log.v("keiskeidebug", count_city + "");
        }catch (Exception e)
        {

        }
        _appSession = new AppSession(getApplicationContext());
        if(!_appSession.checkFirstTime() || count_city == 0)
        {
            _appSession.setFirstTime();
            _ct = new CityTransact(getApplicationContext());
            _ct.truncate();
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
        List<String> items = new ArrayList<String>();
        List<City> cities = _ct.all();
        for (int i = 0; i < cities.size(); i++){
            items.add(cities.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        _city.setAdapter(adapter);
        _city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                ArrayList<WhereHelper> whList = new ArrayList<WhereHelper>();
                WhereHelper wh = new WhereHelper("name", item);
                whList.add(wh);
                try {
                    List<City> cityL = _ct.get(whList);
                    for (City city_t : cityL) {
                        city_id = Integer.toString(city_t.getSid());
                    }
                } catch (Exception e) {
                    city_id = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
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
                URL url = new URL("http://smartv2.lapantiga.com/m/init/city");
                in = new BufferedInputStream(url.openStream());
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                        + "/Android/data/"
                        + getApplicationContext().getPackageName()
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
                        _ct.insert(city);
                    }
                    _progress.dismiss();
                    _appSession.setFirstTime();
                    Toast toast = Toast.makeText(getApplicationContext(), "Inisialisasi aplikasi berhasil.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    _progress.dismiss();
                    Log.v("keiskeidebug", output);
                    Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
    public void register(View v){
        String username = _username.getText().toString();
        String email = _email.getText().toString();
        String handphone = _handphone.getText().toString();
        if(isValidUsername(username) && isValidEmail(email) && isValidHandphone(handphone)){
            if(!_conn.isConnectedToInternet()){
                Toast toast = Toast.makeText(getApplicationContext(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            _progress = new ProgressDialog(v.getContext());
            _progress.setCancelable(true);
            _progress.setMessage("Register..");
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progress.setProgress(0);
            _progress.setMax(100);
            _progress.show();
            String[] params = new String[]{ "REGISTER", username, email, handphone, city_id};
            ClientSocket cs = new ClientSocket(this);
            cs.delegate = this;
            cs.execute(params);
        }

    }
    private  boolean isValidUsername(String username_t){
        if (username_t != null && username_t.length() > 5) {
            _username.setError(null);
            return true;
        }else if (username_t == null ){
            _username.setError("Username Wajib diisi");
        }else {
            _username.setError("Username Minimal 6 karakter");
        }
        return false;
    }

    private  boolean isValidHandphone(String name_t){
        if (name_t != null && name_t.length() > 5) {
            _handphone.setError(null);
            return true;
        }else if (name_t == null ){
            _handphone.setError("Handphone Wajib diisi");
        }else {
            _handphone.setError("Handphone Minimal 6 karakter");
        }
        return false;
    }

    private boolean isValidEmail(String email_t) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email_t);
        if (matcher.matches()) {
            _email.setError(null);
            return true;
        }
        else {
            _email.setError("Email tidak valid");
            return false;
        }
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
            if(resp.equals("SCSRGSTR")){
                String data;
                data = json.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
    public String loadCityJSON(){
        String json = "";
        try{
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + getApplicationContext().getPackageName()
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

package com.alvarisi.keiskeismartsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.alvarisi.keiskeismartsystem.adapter.TransactionAdapter;
import com.alvarisi.keiskeismartsystem.dbsql.TransactionTransact;
import com.alvarisi.keiskeismartsystem.dbsql.WhereHelper;
import com.alvarisi.keiskeismartsystem.helper.UserSession;
import com.alvarisi.keiskeismartsystem.model.Notif;
import com.alvarisi.keiskeismartsystem.model.Transaction;
import com.alvarisi.keiskeismartsystem.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ReportActivity extends AppCompatActivity {
    private static TransactionTransact _tt;
    private static UserSession _us;
    private static ProgressDialog _progress;

    private static ListView _lv_transactions;
    private static TransactionAdapter _ta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        _lv_transactions = (ListView) findViewById(R.id.lv_transactions);
        _lv_transactions.setEmptyView(findViewById(android.R.id.empty));
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        _tt = new TransactionTransact(getApplicationContext());
        _us = new UserSession(getApplicationContext());
        refreshlist();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_attach){
            refreshData();
        }
        if(id == android.R.id.home){
            if(_us.isUserLoggedIn()){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, LandingActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshlist(){
        ArrayList<Transaction> arrTransactions= new ArrayList<Transaction>();
        List<Transaction> transactions = _tt.all();
        for (Transaction transaction : transactions){
            arrTransactions.add(transaction);
        }
        _ta = new TransactionAdapter(getApplicationContext(), arrTransactions);

        _lv_transactions.setAdapter(_ta);
    }
    public void refreshData(){
        _progress = new ProgressDialog(this);
        _progress.setCancelable(true);
        _progress.setMessage("Loading..");
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.show();
        User user = _us.getUserSessionData();
        String[] params = new String[]{ user.getEmail() };
        sendImageLoopj(params);
    }

    public void sendImageLoopj(String[] params){
        RequestParams data = new RequestParams();
        data.put("email", params[0]);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("https://www.keiskei.co.id/m/transactions/get", data, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String resp = "";
                try {
                    resp = response.getString("RESP");
                    if (resp.equals("SCSTRNSCTN")) {
                        JSONArray jsonArray = response.optJSONArray("DATA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tmp = jsonArray.getJSONObject(i);
                            ArrayList<WhereHelper> whDB = new ArrayList<WhereHelper>();
                            WhereHelper wh = new WhereHelper("server_id", tmp.optString("id"));
                            whDB.add(wh);
                            List<Transaction> lh = _tt.get(whDB);
                            if (lh.size() > 0)
                                continue;
                            int refid = Integer.parseInt(tmp.optString("id"));

                            Transaction transaction = new Transaction();
                            transaction.setSid(refid);
                            transaction.setCode(tmp.getString("code"));
                            transaction.setShip(tmp.getString("ship"));
                            transaction.setTotal(Integer.parseInt(tmp.getString("total")));
                            transaction.setTgl(tmp.getString("tanggal"));

                            _tt.insert(transaction);
                        }
                        _progress.dismiss();
                        refreshlist();
                        Toast toast = Toast.makeText(getApplicationContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (resp.equals("FLDTRNSCTN")) {
                        String data;
                        data = response.getString("MESSAGE");
                        _progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        _progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

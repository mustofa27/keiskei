package com.keiskeismartsystem;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.keiskeismartsystem.adapter.CartListAdapter;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private List<Product> productList;
    ListView listView;
    UserSession userSession;
    ProgressDialog _progress;
    private static ConnectionDetector _conn;
    CartListAdapter adapter;
    public static final String _base_url = "http://www.smartv2.lapantiga.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        userSession = new UserSession(this);
        _conn = new ConnectionDetector(this);
        listView = (ListView) findViewById(R.id.list_cart);
        productList = new ArrayList<Product>();
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(this,"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();
        }
        _progress = new ProgressDialog(this);
        _progress.setCancelable(true);
        _progress.setMessage("Getting data..");
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.show();
        new LoadCart().execute();
    }
    private class LoadCart extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/cart/get/" + userSession.getUserSessionData().getID();
                HttpClient request = HttpClient.get(url);
                if (request.ok())
                {
                    resp = request.body();
                }else{
                    resp = "{RESP : 'ERROR' }";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }

        protected void onPostExecute(String output) {
            Log.v("keiskeidebug", output);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = null;
            _progress.dismiss();
            try {
                resp = json.getString("RESP");
                Bundle bundle = new Bundle();
                //_productTransact.truncate();
                final List<Product> products = new ArrayList<Product>();
                if(resp.equals("SCSGCRT")){
                    JSONArray city_t = json.getJSONArray("DATA");
                    for (int i = 0; i < city_t.length(); i++){
                        JSONObject tmp = city_t.getJSONObject(i);
                        Product product = new Product();
                        try {
                            product.setSid(Integer.parseInt(tmp.getString("id")));
                            product.setCode(tmp.getString("code"));
                            product.setTitle(tmp.getString("title"));
                            product.setDescription(tmp.getString("description"));
                            product.setPhotoExt(tmp.getString("image"));
                            product.setHarga(tmp.getString("harga"));
                            product.setKategori(tmp.getString("kategori"));
                            product.setJumlah(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        productList.add(product);
                    }
                    if(productList.size()!=0){
                        populateList();
                    }
                }
                else{
                    Toast toast = Toast.makeText(CartActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void populateList()
    {
        adapter = new CartListAdapter(CartActivity.this,productList);
        listView.setAdapter(adapter);
    }
}

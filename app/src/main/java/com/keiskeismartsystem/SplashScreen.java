package com.keiskeismartsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.keiskeismartsystem.dbsql.ProductTransact;
import com.keiskeismartsystem.fragment.ProductDetail;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.Product;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends Activity {
    public static final String _base_url = "http://www.smartv2.lapantiga.com/";
    private static ProductTransact _productTransact;
    private static UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        _productTransact = new ProductTransact(this.getApplicationContext());
        /*Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,LandingActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();*/
        userSession = new UserSession(getApplicationContext());
        new LoadHotProduct().execute();
    }

    private class LoadHotProduct extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = "";
                if(!userSession.isUserLoggedIn())
                    url = _base_url + "m/product/get/0";
                else {
                    _productTransact.truncate();
                    url = _base_url + "m/product/get/" + userSession.getUserSessionData().getID();
                }
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

            try {
                resp = json.getString("RESP");
                Bundle bundle = new Bundle();
                //_productTransact.truncate();
                final List<Product> products = new ArrayList<Product>();
                if(resp.equals("SCSPRDCT")){
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        _productTransact.insert(product);
                        products.add(product);
                    }
                }
                else{
                    Toast toast = Toast.makeText(SplashScreen.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(userSession.isUserLoggedIn()){
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(SplashScreen.this, LandingActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

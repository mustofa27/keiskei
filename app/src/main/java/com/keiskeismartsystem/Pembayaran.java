package com.keiskeismartsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Pembayaran extends AppCompatActivity {
    ProgressDialog _progress;
    private List<Product> productList;
    ListView listView;
    UserSession userSession;
    private Integer total_harga;
    private static ConnectionDetector _conn;
    public static final String _base_url = "https://keiskei.co.id/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
    }
    private class LoadCheckout extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/checkout/" + userSession.getUserSessionData().getID();
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
                if(resp.equals("SCSCKT")){
                    JSONObject konten = new JSONObject(json.getString("DATA"));
                    JSONArray payment = json.getJSONArray("payment");
                    JSONArray kurir = json.getJSONArray("courier");
                    JSONArray ongkir = json.getJSONArray("harga");
                    int berat = json.getInt("berat");
                    int harga = json.getInt("harga_barang");
                    JSONArray provinsi = json.getJSONArray("province");
                    /*for (int i = 0; i < city_t.length(); i++){
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
                            product.setJumlah(Integer.valueOf(tmp.getString("jumlah")));
                            total_harga+=Integer.valueOf(tmp.getString("harga"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        productList.add(product);
                    }
                    if(productList.size()!=0){

                    }*/
                }
                else{
                    new AlertDialog.Builder(Pembayaran.this).setTitle("Status Keranjang")
                            .setMessage(json.getString("MESSAGE")).setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

package com.keiskeismartsystem.adapter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.CartActivity;
import com.keiskeismartsystem.R;
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

/**
 * Created by mustofa on 10/13/2016.
 */
public class CartListAdapter extends ArrayAdapter<Product> {
    CartActivity activity;
    List<Product> products;
    ProgressDialog _progress;
    ArrayList<String> sat = new ArrayList<String>();
    Spinner qty;
    String _base_url = "https://keiskei.co.id/";
    UserSession userSession;
    public CartListAdapter(CartActivity activity, List<Product> productList, UserSession userSession){
        super(activity,R.layout.list_product,productList);
        this.activity = activity;
        products = productList;
        this.userSession = userSession;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.list_cart, parent, false);
        final Product currentProduct = products.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gambar_cart);
        Picasso.with(activity).load(_base_url + currentProduct.getPhotoExt())
                .placeholder(R.drawable.im_picture)
                .error(R.drawable.im_picture)
                 .into(imageView);
        ((TextView) convertView.findViewById(R.id.nama_produk)).setText(currentProduct.getTitle());
        qty = (Spinner) convertView.findViewById(R.id.jumlah);
        for(int i = 1 ; i < 200 ; i++){
            sat.add(String.valueOf(i));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item,sat);
        qty.setAdapter(arrayAdapter);
        qty.setSelection(currentProduct.getJumlah()-1);
        qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = position+1;
                int j = currentProduct.getJumlah();
                if(i != j) {
                    _progress = new ProgressDialog(activity);
                    _progress.setCancelable(true);
                    _progress.setMessage("Submitting data..");
                    _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    _progress.setProgress(0);
                    _progress.setMax(100);
                    _progress.show();
                    new EditJumlah(currentProduct, position+1, currentProduct.getJumlah()).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((TextView) convertView.findViewById(R.id.harga_cart)).setText("RP " + currentProduct.getHarga() + ",00");
        Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity).setTitle("Confirmation").setMessage("Are you sure to delete this item from your cart?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _progress = new ProgressDialog(activity);
                                _progress.setCancelable(true);
                                _progress.setMessage("Submitting data..");
                                _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                _progress.setProgress(0);
                                _progress.setMax(100);
                                _progress.show();
                                new DeleteKomponen(currentProduct.getSid()).execute();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        return convertView;
    }
    private class DeleteKomponen extends AsyncTask<String, Void, String> {

        int id;
        public DeleteKomponen(int id){
            this.id = id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/cart/del/" + userSession.getUserSessionData().getID() + "/" + id;
                HttpClient request = HttpClient.get(url);
                request.connectTimeout(5000);
                request.readTimeout(10000);
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
                if(resp.equals("SCSDEL")){
                    activity.recreate();
                }
                else{
                    Toast.makeText(activity,json.getString("MESSAGE"),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private class EditJumlah extends AsyncTask<String, Void, String> {

        int jumlah,bef;
        Product product;
        public EditJumlah(Product product, int jumlah, int bef){
            this.product = product;
            this.jumlah = jumlah;
            this.bef = bef;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/cart/edit/" + userSession.getUserSessionData().getID() + "/" + product.getSid() + "/" + jumlah;
                HttpClient request = HttpClient.get(url);
                request.connectTimeout(5000);
                request.readTimeout(10000);
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
                if(resp.equals("SCSEDT")){
                    activity.changeTotal(Integer.valueOf(product.getHarga())*(jumlah-bef));
                    product.setJumlah(jumlah);
                }
                else{
                    Toast.makeText(activity,json.getString("MESSAGE"),Toast.LENGTH_LONG).show();
                    qty.setSelection(bef-1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
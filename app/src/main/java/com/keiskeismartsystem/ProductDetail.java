package com.keiskeismartsystem;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.dbsql.ProductTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.Product;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductDetail extends AppCompatActivity implements AsyncResponse {
    private static ConnectionDetector _conn;
    Product product;
    ProductTransact productTransact;
    TextView nm_produk, nm_kategori, nom_harga, nm_kode, desc;
    String _base_url = "https://keiskei.co.id/";
    Button button;
    private static ProgressDialog _progress;
    private UserSession _user_session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        if (getIntent().hasExtra("id")) {
            productTransact = new ProductTransact(this);
            ArrayList<WhereHelper> whereHelpers = new ArrayList<WhereHelper>();
            whereHelpers.add(new WhereHelper("server_id", String.valueOf(getIntent().getIntExtra("id",0))));
            product = productTransact.first(whereHelpers);
            _user_session = new UserSession(this);
            _conn = new ConnectionDetector(this);
        }
        if (product != null) {
            ImageView imageView = (ImageView) findViewById(R.id.im_product);
            Picasso.with(this).load(_base_url + product.getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(imageView);
            nm_produk = (TextView) findViewById(R.id.nm_produk);
            nm_produk.setText(product.getTitle().toString());
            nm_kategori = (TextView) findViewById(R.id.nm_kategori);
            nm_kategori.setText(product.getKategori());
            nm_kode = (TextView) findViewById(R.id.nm_kode);
            nm_kode.setText(product.getCode());
            nom_harga = (TextView) findViewById(R.id.nom_harga);
            nom_harga.setText("RP " + product.getHarga() + ",00");
            desc = (TextView) findViewById(R.id.desc);
            desc.setText(product.getDescription());
            button = (Button) findViewById(R.id.cart);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!_conn.isConnectedToInternet()){
                        Toast toast = Toast.makeText(ProductDetail.this,"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    _progress = new ProgressDialog(view.getContext());
                    _progress.setCancelable(true);
                    _progress.setMessage("Submit..");
                    _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    _progress.setProgress(0);
                    _progress.setMax(100);
                    _progress.show();
                    String[] params = new String[]{ "BUY", String.valueOf(_user_session.getUserSessionData().getID()), String.valueOf(product.getSid())};
                    ClientSocket cs = new ClientSocket(ProductDetail.this.getApplicationContext());
                    cs.delegate = ProductDetail.this;
                    cs.execute(params);


                }
            });
            if (!_user_session.isUserLoggedIn()) {
                button.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void processFinish(String output) {
        JSONObject response = new JSONObject();
        try {
            response = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = "";
        try {
            resp = response.getString("RESP");
            if (resp.equals("SCSCRT")) {
                String data;
                data = response.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(this, data, Toast.LENGTH_SHORT);
                toast.show();
            } else if (resp.equals("FLDCRT")) {
                String data;
                data = response.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(this, data, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                _progress.dismiss();
                Toast toast = Toast.makeText(this, "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

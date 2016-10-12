package com.keiskeismartsystem.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.model.Product;

import java.util.List;

/**
 * Created by mustofa on 10/13/2016.
 */
public class ProductListAdapter extends ArrayAdapter<Product> {
    Activity activity;
    List<Product> products;
    public ProductListAdapter(Activity activity, List<Product> productList){
        super(activity,R.layout.list_product,productList);
        this.activity = activity;
        products = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.list_product, parent, false);
        final Product currentProduct = products.get(position);
        TextView nama = (TextView) convertView.findViewById(R.id.nama_produk);
        nama.setText(currentProduct.getTitle());
        TextView kategori = (TextView) convertView.findViewById(R.id.kategori_produk);
        kategori.setText(currentProduct.getDescription());
        TextView harga = (TextView) convertView.findViewById(R.id.harga_produk);
        harga.setText(currentProduct.getCode());
        LinearLayout view = (LinearLayout) convertView.findViewById(R.id.group);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(activity,DetailOutlet.class);
                intent.putExtra("kd_outlet", currentOutlet.getKode());
                startActivity(intent);*/
                Toast.makeText(activity,"harusnya menuju detail produk",Toast.LENGTH_LONG).show();
            }
        });
        return convertView;
    }
}
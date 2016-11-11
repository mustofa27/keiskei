package com.keiskeismartsystem.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.fragment.ProductDetail;
import com.keiskeismartsystem.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mustofa on 10/13/2016.
 */
public class CartListAdapter extends ArrayAdapter<Product> {
    FragmentActivity activity;
    List<Product> products;
    String _base_url = "http://www.smartv2.lapantiga.com/";
    public CartListAdapter(FragmentActivity activity, List<Product> productList){
        super(activity,R.layout.list_product,productList);
        this.activity = activity;
        products = productList;
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
        ((EditText) convertView.findViewById(R.id.jumlah)).setText(String.valueOf(currentProduct.getJumlah()));
        ((TextView) convertView.findViewById(R.id.harga_cart)).setText("RP " + currentProduct.getHarga() + ",00");
        return convertView;
    }
}
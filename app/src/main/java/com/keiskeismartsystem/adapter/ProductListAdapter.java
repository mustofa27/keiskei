package com.keiskeismartsystem.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.fragment.ProductDetail;
import com.keiskeismartsystem.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mustofa on 10/13/2016.
 */
public class ProductListAdapter extends ArrayAdapter<List<Product>> {
    FragmentActivity activity;
    List<List<Product>> products;
    String _base_url = "https://keiskei.co.id/";
    public ProductListAdapter(FragmentActivity activity, List<List<Product>> productList){
        super(activity,R.layout.list_product,productList);
        this.activity = activity;
        products = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final List<Product> currentProduct = products.get(position);
        if(currentProduct.size() == 2) {
            /*if(convertView == null)
                */convertView = activity.getLayoutInflater().inflate(R.layout.list_product, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gambar_produk);
            Picasso.with(activity).load(_base_url + currentProduct.get(0).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, com.keiskeismartsystem.ProductDetail.class);
                    intent.putExtra("id", currentProduct.get(0).getSid());
                    activity.startActivity(intent);
                }
            });
            imageView = (ImageView) convertView.findViewById(R.id.gambar_produk1);
            Picasso.with(activity).load(_base_url + currentProduct.get(1).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, com.keiskeismartsystem.ProductDetail.class);
                    intent.putExtra("id", currentProduct.get(1).getSid());
                    activity.startActivity(intent);
                }
            });
        }
        else {
            /*if(convertView == null)
                */convertView = activity.getLayoutInflater().inflate(R.layout.single_list_product, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gambar_produk_single);
            Picasso.with(activity).load(_base_url + currentProduct.get(0).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, com.keiskeismartsystem.ProductDetail.class);
                    intent.putExtra("id", currentProduct.get(0).getSid());
                    activity.startActivity(intent);
                }
            });
        }
        return convertView;
    }
}
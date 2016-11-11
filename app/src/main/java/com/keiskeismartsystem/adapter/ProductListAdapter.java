package com.keiskeismartsystem.adapter;

import android.app.Activity;
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
    String _base_url = "http://www.smartv2.lapantiga.com/";
    public ProductListAdapter(FragmentActivity activity, List<List<Product>> productList){
        super(activity,R.layout.list_product,productList);
        this.activity = activity;
        products = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.list_product, parent, false);
        final List<Product> currentProduct = products.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gambar_produk);
        Picasso.with(activity).load(_base_url + currentProduct.get(0).getPhotoExt())
                .placeholder(R.drawable.im_picture)
                .error(R.drawable.im_picture)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(activity,DetailOutlet.class);
                intent.putExtra("kd_outlet", currentOutlet.getKode());
                startActivity(intent);*/
                Bundle bundle = new Bundle();
                bundle.putInt("id", currentProduct.get(0).getSid());
                ProductDetail productDetail = new ProductDetail();
                productDetail.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_container, productDetail).commit();
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
                /*Intent intent = new Intent(activity,DetailOutlet.class);
                intent.putExtra("kd_outlet", currentOutlet.getKode());
                startActivity(intent);*/
                Bundle bundle = new Bundle();
                bundle.putInt("id", currentProduct.get(1).getSid());
                ProductDetail productDetail = new ProductDetail();
                productDetail.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_container, productDetail).commit();
            }
        });
        return convertView;
    }
}
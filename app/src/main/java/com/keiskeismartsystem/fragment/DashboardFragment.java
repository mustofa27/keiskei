package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.dbsql.ProductTransact;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.City;
import com.keiskeismartsystem.model.Product;
import com.keiskeismartsystem.model.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.FileHandler;

public class DashboardFragment extends Fragment {
    public static final String _base_url = "http://www.smartv2.lapantiga.com/";
    private static ImageView _im_user, _iv_product_1, _iv_product_2, _iv_product_3;
    private static TextView _t_buy, _t_code, _t_bonus;
    private static Button _chat, _voice_box;
    private static View _rootView;
    private static int imageProfil = 160;

    Bitmap bitmap_t;

    private static UserSession _us;
    private static User _user;
    private static Context _context;
    private static ImageHelper ih;
    private static ProductTransact _productTransact;
    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _us = new UserSession(getActivity().getApplicationContext());
        _user = _us.getUserSessionData();
        _context = getActivity().getApplicationContext();
        _productTransact = new ProductTransact(getActivity().getApplicationContext());
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpinya = metrics.densityDpi;
        imageProfil = (int) ((int) dpinya * 0.6f);
        Log.v("keiskeidebug", dpinya + " densitinya");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        _im_user = (ImageView) _rootView.findViewById(R.id.im_user);
        _iv_product_1 = (ImageView) _rootView.findViewById(R.id.iv_product_1);
        _iv_product_2 = (ImageView) _rootView.findViewById(R.id.iv_product_2);
        _iv_product_3 = (ImageView) _rootView.findViewById(R.id.iv_product_3);
        _chat = (Button) _rootView.findViewById(R.id.btn_chat);
        _chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ((MainActivity) getActivity()).changeFragment("chat", v, bundle);
            }
        });
        _voice_box = (Button) _rootView.findViewById(R.id.btn_voicebox);
        _voice_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    ((MainActivity) getActivity()).changeFragment("voice_box", v, bundle);
            }
        });
        ih = new ImageHelper();
        if(_user.getPhoto().isEmpty())
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
            bitmap = ih.getRoundedCornerBitmap(bitmap, imageProfil);
            _im_user.setImageBitmap(bitmap);
//            bitmap.recycle();
        }else{
            String temp_path = "";
            try{
                temp_path = _user.getPathUserInt();
            }catch (NullPointerException e){

            }
            if (temp_path.isEmpty())
            {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
                bitmap = ih.getRoundedCornerBitmap(bitmap, imageProfil);
                _im_user.setImageBitmap(bitmap);
                if(_user.getPhoto() != null)
                    if(!_user.getPhoto().equalsIgnoreCase("null"))
                        new LoadImage().execute(_user.getPhoto());

            }else{
                File file = new File(_user.getPathUserInt());
                if (file.exists()){
                    BitmapFactory.Options bmo = new BitmapFactory.Options();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                        Bitmap icon = ih.resizeBitmap(bitmap, imageProfil, imageProfil);
                        icon = ih.getRoundedCornerBitmap(icon, imageProfil);
                        _im_user.setImageBitmap(icon);
                    }catch (OutOfMemoryError e){
                        e.printStackTrace();
                    }
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
                    bitmap = ih.getRoundedCornerBitmap(bitmap, imageProfil);
                    _im_user.setImageBitmap(bitmap);
                }

            }
        }
        _t_buy = (TextView) _rootView.findViewById(R.id.t_buy);
        _t_buy.setText(_user.getTotalBought() + "");
        _t_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ((MainActivity) getActivity()).changeFragment("transaction", v, bundle);
            }
        });
        _t_code = (TextView) _rootView.findViewById(R.id.t_member);
        Log.v("keiskeidebug code:", _user.getCode());
        _t_code.setText(_user.getCode());
        _t_bonus = (TextView) _rootView.findViewById(R.id.t_bonus);
        _t_bonus.setText(_user.getBonus());
        loadhotproduct();
        return _rootView;
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {

            try {
                Log.v("keiskeidebug", "http://www.smartv2.lapantiga.com/data/user/photo/" + args[0]);
                bitmap_t = BitmapFactory.decodeStream((InputStream)new URL("http://www.smartv2.lapantiga.com/data/user/photo/" + args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap_t;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                Bitmap icon = ih.resizeBitmap(image, imageProfil, imageProfil);
                icon = ih.getRoundedCornerBitmap(icon, imageProfil);
                _im_user = (ImageView) _rootView.findViewById(R.id.im_user);
                _im_user.setImageBitmap(icon);
                String path = ih.storeImage(_context, icon);
                _user.setPathUserInt(path);
                _us.updateUserSession(_user);
//                icon.recycle();
//                icon = null;
//                image.recycle();
            }else{

                Toast.makeText(_context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class LoadHotProduct extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/product/hot";
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
                _productTransact.truncate();
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        _productTransact.insert(product);

                    }
                    final String url_t = "http://www.smartv2.lapantiga.com/products/detail/";
                    final List<Product> products = _productTransact.all();
                    Log.v("keiskeidebug", products.get(0).getPhotoExt());
                    Picasso.with(_context).load(_base_url + products.get(0).getPhotoExt())
                            .placeholder(R.drawable.im_picture)
                            .error(R.drawable.im_picture)
                            .into(_iv_product_1, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    _iv_product_1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url_t + products.get(0).getCode()));
                                                startActivity(bi);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    Picasso.with(_context).load(_base_url + products.get(1).getPhotoExt())
                            .placeholder(R.drawable.im_picture)
                            .error(R.drawable.im_picture)
                            .into(_iv_product_2, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    _iv_product_2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url_t + products.get(1).getCode()));
                                                startActivity(bi);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError() {

                                }
                            });
                    Picasso.with(_context).load(_base_url + products.get(2).getPhotoExt())
                            .placeholder(R.drawable.im_picture)
                            .error(R.drawable.im_picture)
                            .into(_iv_product_3, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    _iv_product_3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url_t + products.get(2).getCode()));
                                                startActivity(bi);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
                else{
                    Toast toast = Toast.makeText(_context, "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    protected void loadhotproduct(){
        int count = _productTransact.countAll();
        if(count == 0){
            new LoadHotProduct().execute("photo");

        }else{
            List<Product> products = _productTransact.all();
            Picasso.with(_context).load(_base_url + products.get(0).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(_iv_product_1);
            Picasso.with(_context).load(_base_url + products.get(1).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(_iv_product_2);
            Picasso.with(_context).load(_base_url + products.get(2).getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(_iv_product_3);
            new LoadHotProduct().execute("photo");
        }
    }
}

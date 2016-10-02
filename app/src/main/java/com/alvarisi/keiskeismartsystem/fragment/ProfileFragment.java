package com.alvarisi.keiskeismartsystem.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarisi.keiskeismartsystem.MainActivity;
import com.alvarisi.keiskeismartsystem.R;
import com.alvarisi.keiskeismartsystem.helper.ImageHelper;
import com.alvarisi.keiskeismartsystem.helper.UserSession;
import com.alvarisi.keiskeismartsystem.model.User;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ProfileFragment extends Fragment {
    private static ImageView _im_user;
    private static TextView _tv_name, _tv_email, _tv_handphone, _tv_pinbb, _tv_website, _tv_instagram, _tv_facebook;
    Bitmap bitmap_t;
    UserSession _us;
    User user;
    private static Context _context;
    private static View _rootView;
    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _us = new UserSession(getActivity().getApplicationContext());
        user = _us.getUserSessionData();
        _context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        _tv_name = (TextView) _rootView.findViewById(R.id.tv_name);
        _tv_email = (TextView) _rootView.findViewById(R.id.tv_email);
        _tv_handphone = (TextView) _rootView.findViewById(R.id.tv_handphone);
        _tv_pinbb = (TextView) _rootView.findViewById(R.id.tv_pinbb);
        _im_user = (ImageView) _rootView.findViewById(R.id.im_user);
        _tv_website = (TextView) _rootView.findViewById(R.id.tv_website);
        _tv_instagram = (TextView) _rootView.findViewById(R.id.tv_instagram);
        _tv_facebook = (TextView) _rootView.findViewById(R.id.tv_facebook);


        if(user.getName().isEmpty())
        {
            _tv_name.setText("Nama Anda");
        }else{
            _tv_name.setText(user.getName());
        }

        _tv_email.setText(user.getEmail());
        _tv_handphone.setText(user.getTelephone());
        Log.v("keiskeidebug", "isi di profil " + user.getWebsite() + user.getInstagram() + user.getFB() + user.getPinBB());

        if (user.getPinBB().isEmpty())
        {
            _tv_pinbb.setText("Pin BB");
        }else{
            _tv_pinbb.setText(user.getPinBB());
        }
        if (user.getWebsite() == null)
        {
            _tv_website.setText("Website");
        }else{
            _tv_website.setText(user.getWebsite());
        }
        if (user.getFB() == null)
        {
            _tv_facebook.setText("Facebook");
        }else{
            _tv_facebook.setText(user.getFB());
        }
        if (user.getInstagram() == null)
        {
            _tv_instagram.setText("Instagram");
        }else{
            _tv_instagram.setText(user.getInstagram());
        }

        if(user.getPhoto().isEmpty())
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
            ImageHelper ih = new ImageHelper();
            bitmap = ih.getRoundedCornerBitmap(bitmap, 70);
            _im_user.setImageBitmap(bitmap);
        }else{
            String temp_path = "";
            try{
                temp_path = user.getPathUserInt();
            }catch (NullPointerException e){

            }
            if (temp_path.isEmpty())
            {
                ImageHelper ih = new ImageHelper();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
                bitmap = ih.getRoundedCornerBitmap(bitmap, 100);
                _im_user.setImageBitmap(bitmap);
                if(user.getPhoto() != null)
                    if(!user.getPhoto().equalsIgnoreCase("null"))
                        new LoadImage().execute(user.getPhoto());
            }else{
                File file = new File(user.getPathUserInt());
                BitmapFactory.Options bmo = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                ImageHelper ih = new ImageHelper();
                Bitmap icon =  ih.resizeBitmap(bitmap, 160, 160);
                icon = ih.getRoundedCornerBitmap(icon, 120);
                _im_user.setImageBitmap(icon);
//                bitmap.recycle();
//                icon.recycle();
            }
        }
        return _rootView;
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {

            try {
                Log.v("keiskeidebug", "https://www.keiskei.co.id/data/user/photo/" + args[0]);
                bitmap_t = BitmapFactory.decodeStream((InputStream)new URL("https://www.keiskei.co.id/data/user/photo/" + args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap_t;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                try {
                    ImageHelper ih = new ImageHelper();
                    Bitmap icon = ih.resizeBitmap(image, 160, 160);
                    icon = ih.getRoundedCornerBitmap(icon, 120);
                    _im_user = (ImageView) _rootView.findViewById(R.id.im_user);
                    _im_user.setImageBitmap(icon);
                    String path = ih.storeImage(_context, icon);
                    user.setPathUserInt(path);
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }else{

                Toast.makeText(_context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

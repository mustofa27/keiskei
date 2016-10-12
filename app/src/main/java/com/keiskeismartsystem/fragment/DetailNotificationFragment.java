package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.LoginActivity;
import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.adapter.ListSettingAdapter;
import com.keiskeismartsystem.dbsql.NotifTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.Notif;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.facebook.FacebookSdk;

public class DetailNotificationFragment extends Fragment {
    private static View _rootView;
    private Notif notif;
    private static ImageButton _ib_add;
    private static ImageView _im_notif;
    private Context _context;
    private NotifTransact _notifTransact;
    private static AlertDialog alertDialog;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    public static final String _base_url = "http://www.smartv2.lapantiga.com/";


    public DetailNotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(Notif.KEY)){
            notif = (Notif) getArguments().getSerializable(Notif.KEY);
            Log.v("keiskeidebug", "di detailfg" + notif.getTitle());
        }
        _context = getActivity().getApplicationContext();
        _notifTransact = new NotifTransact(_context);
        FacebookSdk.sdkInitialize(_context);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(_context, "Sukses", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(_context, "Batal", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(_context, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_detail_notification, container, false);
        _im_notif = (ImageView)_rootView.findViewById(R.id.iv_notif);
        _im_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                String[] choice = {"Simpan Gambar"};
                aBuilder.setTitle("Pilihan").setItems(choice, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Bitmap bm = ((BitmapDrawable)_im_notif.getDrawable()).getBitmap();
                                storeImage(bm);
                                Toast.makeText(_context, "Gambar berhasil disimpan.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog = aBuilder.create();
                alertDialog.show();
            }
        });
        _ib_add = (ImageButton)_rootView.findViewById(R.id.ib_share);

        _ib_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                String[] choice = {"Facebook", "Lainnya"};
                aBuilder.setTitle("Bagikan").setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    if (notif.getPhotoExt() != null){
                                        ShareLinkContent content = new ShareLinkContent.Builder()
                                                .setContentUrl(Uri.parse("https://smartv2.lapantiga.com.com"))
                                                .setContentTitle("adfasf")
                                                .setContentDescription("adfas")
                                                .build();
                                        shareDialog.show(content);

                                    }else{
                                        ShareLinkContent content = new ShareLinkContent.Builder()
                                                .setContentUrl(Uri.parse("https://smartv2.lapantiga.com.com"))
                                                .setContentTitle(notif.getTitle())
                                                .setContentDescription(notif.getDescription())
                                                .build();
                                        shareDialog.show(content);

                                    }
                                }


                                break;
                            case 1:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, notif.getDescription());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                        }
                    }
                });
                alertDialog = aBuilder.create();
                alertDialog.show();

            }
        });
        if(notif != null){

            String temp = null;
            try {
                temp = notif.getPhotoInt();
            }catch (NullPointerException e)
            {

            }
            if (temp == null){
                String tempExt = null;
                try {
                    tempExt = notif.getPhotoExt();
                }catch (NullPointerException e){

                }
                Toast.makeText(_context, "Gambar1 : " + _base_url + "data/notification/" + tempExt, Toast.LENGTH_SHORT).show();
                if(tempExt != null)
                {
                        Picasso.with(this._context).load(_base_url + "data/notification/" + tempExt)
                                .resize(300, 400)
                                .into(_im_notif);

//                    new LoadImage().execute(notif.getPhotoExt());
                }
            }else{
                    File nphoto = new File(notif.getPhotoInt());
                    if(nphoto.exists()){
                        BitmapFactory.Options bmo = new BitmapFactory.Options();
                        ImageHelper ih = new ImageHelper();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(nphoto.getAbsolutePath(), bmo);
                            Bitmap icon = ih.getRoundedCornerBitmap(bitmap, 70);
                            _im_notif.setImageBitmap(icon);
                        }catch (OutOfMemoryError e){
                            e.printStackTrace();
                        }
                    }else{
                        String tempExt = "";
                        try {
                            tempExt = notif.getPhotoExt();
                        }catch (NullPointerException e)
                        {

                        }
                        Toast.makeText(_context, "Gambar : " + _base_url + "data/notification/" + tempExt, Toast.LENGTH_SHORT).show();
                        if(!tempExt.isEmpty())
                        {
                                Picasso.with(this._context).load(_base_url + "data/notification/" + tempExt)
//                                        .placeholder(R.drawable.im_picture)
//                                        .error(R.drawable.im_picture)
                                        .resize(300, 400)
                                        .into(_im_notif);
//                            new LoadImage().execute(notif.getPhotoExt());
                        }
                    }
            }

            TextView description = ((TextView) _rootView.findViewById(R.id.tv_notif));
            description.setText(notif.getDescription());
        }

        return _rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_notification_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap_t =  null;
            try {
                Log.v("keiskeidebug", "http://www.smartv2.lapantiga.com/data/notification/" + args[0]);
                bitmap_t = BitmapFactory.decodeStream((InputStream) new URL("http://www.smartv2.lapantiga.com/data/notification/" + args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap_t;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                try {
                    ImageHelper ih = new ImageHelper();
                    String path = ih.storeImage(_context, image);
                    if(notif != null) {
                        notif.setPhotoInt(path);
                        _notifTransact.update(notif);
                    }
                }catch (NullPointerException e){

                }catch (Exception e)
                {

                }

            }else{

//                Toast.makeText(_context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void storeImage(Bitmap image) {
        if(isExternalStorageReadable()) {


            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.v("keiskeidebug",
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                Toast.makeText(_context, "Error creating media file, check storage permissions", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(_context, "File not found:", Toast.LENGTH_SHORT).show();
                Log.v("keiskeidebug", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Toast.makeText(_context, "Error accessing file:", Toast.LENGTH_SHORT).show();
                Log.v("keiskeidebug", "Error accessing file: " + e.getMessage());
            }
        }else{
            Toast.makeText(_context, "Gak ada SD:", Toast.LENGTH_SHORT).show();

        }

    }
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/keiskei/"
//                + getActivity().getApplicationContext().getPackageName()
            );

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        Log.v("keiskeidebug", "Path file disimpan : " + mediaStorageDir.getPath() + File.separator + mImageName);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

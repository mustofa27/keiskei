package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.keiskeismartsystem.LoginActivity;
import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.User;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class EditProfilFragment extends Fragment  implements AsyncResponse {

    private static final int RESULT_LOAD_IMG = 1001;
    private static EditText _et_name, _et_email, _et_handphone, _et_pinbb, _et_website, _et_instagram, _et_facebook;
    private static ImageView _iv_user;
    private static Button _save, bt_back;
    private static View _rootView;
    private static Context _context;
    private static String imgDecodableString, temp_path = "";
    private static boolean is_change_photo = false;
    private ProgressDialog _progress;
    Bitmap bitmap_t,user_bitmap;
    UserSession _us;
    User user;
    private static ConnectionDetector _conn;
    public EditProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _us = new UserSession(getActivity().getApplicationContext());
        _conn = new ConnectionDetector(getActivity().getApplicationContext());
        user = _us.getUserSessionData();
        _context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_edit_profil, container, false);
        _et_name = (EditText) _rootView.findViewById(R.id.et_nama);
        _et_email = (EditText) _rootView.findViewById(R.id.et_email);
        _et_handphone = (EditText) _rootView.findViewById(R.id.et_handphone);
        _et_pinbb = (EditText) _rootView.findViewById(R.id.et_pinbb);
        _et_website = (EditText) _rootView.findViewById(R.id.et_website);
        _et_instagram = (EditText) _rootView.findViewById(R.id.et_instagram);
        _et_facebook = (EditText) _rootView.findViewById(R.id.et_facebook);
        _save = (Button) _rootView.findViewById(R.id.bt_save);
        bt_back = (Button) _rootView.findViewById(R.id.bt_back);
        _iv_user = (ImageView) _rootView.findViewById(R.id.im_user);
        _iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto(v);
            }
        });
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(v);
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batalEdit(v);
            }
        });
        setContentForm();
        ImageHelper ih = new ImageHelper();
        if(user.getPhoto().isEmpty())
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
            bitmap = ih.getRoundedCornerBitmap(bitmap, 120);
            _iv_user.setImageBitmap(bitmap);
        }else{
            if (user.getPathUserInt().isEmpty())
            {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_default_new);
                bitmap = ih.getRoundedCornerBitmap(bitmap, 120);
                _iv_user.setImageBitmap(bitmap);
                new LoadImage().execute(user.getPhoto());
            }else{
                File file = new File(user.getPathUserInt());
                BitmapFactory.Options bmo = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
                Bitmap icon = ih.resizeBitmap(bitmap, 160, 160);
                icon = ih.getRoundedCornerBitmap(icon, 120);
                _iv_user.setImageBitmap(icon);
            }
        }
        return _rootView;
    }
    public void batalEdit(View v){
        Bundle bundle = new Bundle();
        ((MainActivity) getActivity()).changeFragment("profile_fragment", v, bundle);
    }
    public void setContentForm(){
        if(!user.getName().isEmpty()) {
            _et_name.setText(user.getName());
        }
        _et_email.setText(user.getEmail());
        _et_handphone.setText(user.getTelephone());
        if(!user.getPinBB().isEmpty()) {
            _et_pinbb.setText(user.getPinBB());
        }
        if (user.getWebsite() == null)
        {
            _et_website.setText("");
        }else{
            _et_website.setText(user.getWebsite());
        }
        if (user.getFB() == null)
        {
            _et_facebook.setText("");
        }else{
            _et_facebook.setText(user.getFB());
        }
        if (user.getInstagram() == null)
        {
            _et_instagram.setText("");
        }else{
            _et_instagram.setText(user.getInstagram());
        }
    }

    public void saveProfile(View v){
        String  name = _et_name.getText().toString(),
                email = _et_email.getText().toString(),
                handphone = _et_handphone.getText().toString(),
                pinbb = _et_pinbb.getText().toString();
        if(isValidEmail(email) && isValidHandphone(handphone)){
            if(!_conn.isConnectedToInternet()){
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            _progress = new ProgressDialog(v.getContext());
            _progress.setCancelable(false);
            _progress.setMessage("Requesting...");
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progress.setProgress(0);
            _progress.setMax(100);
            _progress.show();
            String photo_urlcoded = "";
            String photo_flag = "0";
            String facebook = _et_facebook.getText().toString();
            String website = _et_website.getText().toString();
            String instagram = _et_instagram.getText().toString();
            if(is_change_photo){
//                try {
//                    photo_urlcoded = URLEncoder.encode(imgDecodableString, "utf-8");
//                    photo_flag = "1";
//                    ImageHelper ih = new ImageHelper();
//                    Bitmap image = ih.StringToBitMap(imgDecodableString);
//                    String path = ih.storeImage(_context, image);
//                    user.setPathUserInt(path);
//                }catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                String id = Integer.toString(user.getID());

                String[] params = new String[]{ "UPDATE_PROFILE",id, name, email, handphone, pinbb, imgDecodableString, website, instagram, facebook };
                sendImageLoopj(params);
                //temp_path = "";
            }else{

                String id = Integer.toString(user.getID());
                String[] params = new String[]{ "UPDATE_PROFILE",id, name, email, handphone, pinbb, website, instagram, facebook};
                ClientSocket cs = new ClientSocket(getActivity().getApplicationContext());
                cs.delegate = this;
                cs.execute(params);
            }


        }
    }
    private class SendImageLoopjDua extends AsyncTask<String, Void, String> {
        String[] params;
        SendImageLoopjDua(String[] params){
            this.params = params;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = "https://keiskei.co.id/m/updateprofiletwo";
                HttpClient request = HttpClient.post(url);
                request.connectTimeout(5000);
                request.readTimeout(25000);
                request.part("id", params[1]);
                request.part("name", params[2]);
                request.part("email", params[3]);
                request.part("handphone", params[4]);
                request.part("pinbb", params[5]);

                request.part("website", params[7]);
                request.part("instagram", params[8]);
                request.part("facebook", params[9]);

                String path = params[6];

                if(!path.isEmpty()){
                    request.part("photo", path);
                    /*try
                    {
                        File temp_file = new File(path);
                        request.part("photo", temp_file);
                        path = "";
                    }catch(Exception e){
                        Log.v("keiskeidebug", "salah loklsadf");
                    }*/
                }
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
            JSONObject response = new JSONObject();
            try {
                response = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = "";
            try {
                resp = response.getString("RESP");
                if(resp.equals("SCSUPDTPRFL")){
                    _progress.dismiss();

                    String data;
                    data = response.getString("DATA");
                    JSONObject user_t = new JSONObject(data);
                    User user = new User();
                    int total = Integer.parseInt(response.getString("TOTAL_BUY"));
                    try {
                        user.setID(Integer.parseInt(user_t.getString("id")));
                        user.setCode(user_t.getString("code"));
                        user.setEmail(user_t.getString("email"));
                        user.setName(user_t.getString("name"));
                        user.setUsername(user_t.getString("username"));
                        user.setGCMID(user_t.getString("gcm_id"));
                        String _gender = user_t.getString("gender");
                        user.setGender(_gender.charAt(0));
                        user.setTelephone(user_t.getString("mobile"));
                        user.setAddress(user_t.getString("address"));
                        user.setPhoto(user_t.getString("photo"));
                        user.setTotalBought(total);
                        user.setPathUserInt(temp_path);
                        user.setPinBB(user_t.getString("pinbb"));
                        user.setWebsite(user_t.getString("website"));
                        user.setInstagram(user_t.getString("instagram"));
                        user.setFB(user_t.getString("facebook"));
                        user.setBonus(user_t.getString("bonus"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("keiskeidebug", "error di profil");
                    }
                    Log.v("keiskeidebug", "sukses di profil");

                    _us.updateUserSession(user);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Profil berhasil diperbarui.", Toast.LENGTH_SHORT);
                    toast.show();
                    Bundle bundle = new Bundle();

                    ((MainActivity) getActivity()).changeFragment("profile_fragment", _rootView, bundle);

                }else if(resp.equals("FLDUPDTPRFL")){
                    String data;
                    data = response.getString("MESSAGE");
                    _progress.dismiss();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), data, Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    _progress.dismiss();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                toast.show();
                _progress.dismiss();
            }
        }
    }
    public void sendImageLoopj(String[] params){
        new SendImageLoopjDua(params).execute();
    }
    private  boolean isValidHandphone(String name_t){
        if (name_t != null && name_t.length() > 5) {
            _et_handphone.setError(null);
            return true;
        }else if (name_t == null ){
            _et_handphone.setError("Handphone Wajib diisi");
        }else {
            _et_handphone.setError("Handphone Minimal 6 karakter");
        }
        return false;
    }

    private boolean isValidEmail(String email_t) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email_t);
        if (matcher.matches()) {
            _et_email.setError(null);
            return true;
        }
        else {
            _et_email.setError("Email tidak valid");
            return false;
        }
    }
    public void changePhoto(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,RESULT_LOAD_IMG );
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = _context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String path = cursor.getString(columnIndex);
                temp_path = path;
                cursor.close();
                Log.v("keiskeidebug", "path image : " + path);
                Bitmap icon = BitmapFactory
                        .decodeFile(path);
                ImageHelper ih = new ImageHelper();
                imgDecodableString = ih.BitMapToString(icon);
                icon = ih.resizeBitmap(icon, 160, 160);
                icon = ih.getRoundedCornerBitmap(icon, 120);
                _iv_user.setImageBitmap(icon);
                is_change_photo = true;
            } else {
                Toast.makeText(_context, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(_context, "Something went wrong" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }
    public void processFinish(String output){
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = "";
        try {
            resp = json.getString("RESP");
            if(resp.equals("SCSUPDTPRFL")){
                _progress.dismiss();
                String data;
                data = json.getString("DATA");
                JSONObject user_t = new JSONObject(data);
                User user = new User();
                int total = Integer.parseInt(json.getString("TOTAL_BUY"));
                try {
                    user.setID(Integer.parseInt(user_t.getString("id")));
                    user.setCode(user_t.getString("code"));
                    user.setEmail(user_t.getString("email"));
                    user.setName(user_t.getString("name"));
                    user.setUsername(user_t.getString("username"));
                    user.setGCMID(user_t.getString("gcm_id"));
                    String _gender = user_t.getString("gender");
                    user.setGender(_gender.charAt(0));
                    user.setTelephone(user_t.getString("mobile"));
                    user.setAddress(user_t.getString("address"));
                    user.setPhoto(user_t.getString("photo"));
                    user.setTotalBought(total);
                    user.setPinBB(user_t.getString("pinbb"));
                    user.setWebsite(user_t.getString("website"));
                    user.setInstagram(user_t.getString("instagram"));
                    user.setFB(user_t.getString("facebook"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                _us.updateUserSession(user);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Profil berhasil diperbarui.", Toast.LENGTH_SHORT);
                toast.show();


            }else if(resp.equals("FLDUPDTPRFL")){
                String data;
                data = json.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), data, Toast.LENGTH_SHORT);
                toast.show();
            }else{
                _progress.dismiss();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            Bundle bundle = new Bundle();

            ((MainActivity) getActivity()).changeFragment("profile_fragment", _rootView, bundle);
        }
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        protected Bitmap doInBackground(String... args) {

            try {
                bitmap_t = BitmapFactory.decodeStream((InputStream)new URL("https://keiskei.co.id/data/user/photo/" + args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap_t;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                ImageHelper ih = new ImageHelper();
                Bitmap icon = ih.resizeBitmap(image, 160, 160);
                icon = ih.getRoundedCornerBitmap(icon, 120);
                _iv_user = (ImageView) _rootView.findViewById(R.id.im_user);
                _iv_user.setImageBitmap(icon);
                String path = ih.storeImage(_context, image);
                user.setPathUserInt(path);
            }else{
                //Toast.makeText(_context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

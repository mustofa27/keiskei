package com.keiskeismartsystem.socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.keiskeismartsystem.library.HttpClient;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zeta on 10/13/2015.
 */
public class ClientSocket extends AsyncTask<String, Void, String> {
    public static final String _base_url = "https://keiskei.co.id/";
    public AsyncResponse delegate=null;

    private Context context;
    public ClientSocket(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... args) {
        String resp = "";
        try {
            switch (args[0]){
                case "LOGIN":
                    String url = _base_url + "m/login";
                    HttpClient request = HttpClient.post(url);
                    request.part("username", args[1]);
                    request.part("password", args[2]);
                    request.part("gcm_id", args[3]);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = "{RESP : 'ERROR' }";
                    }
                    break;
                case "REGISTER":
                    url = _base_url + "m/register";
                    request = HttpClient.post(url);
                    request.part("username", args[1]);
                    request.part("email", args[2]);
                    request.part("handphone", args[3]);
                    request.part("ms_city_id", args[4]);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = "{RESP : 'ERROR' }";
                    }
                    break;
                case "UPDATE_PROFILE":
                    url = _base_url + "m/updateprofile";
                    Log.v("keiskeidebug", url);
                    request = HttpClient.post(url);
                    request.part("id",  args[1]);
                    request.part("name",  args[2]);
                    request.part("email",  args[3]);
                    request.part("handphone", args[4]);
                    request.part("pinbb",  args[5]);
                    request.part("website", args[6]);
                    request.part("instagram", args[7]);
                    request.part("facebook", args[8]);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = "{RESP : 'ERROR' }";
                    }
                    break;
                case "BUY":
                    url = _base_url + "m/cart/set/"+args[1]+"/"+args[2];
                    Log.v("keiskeidebug", url);
                    request = HttpClient.get(url);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = "{RESP : 'ERROR' }";
                    }
                    break;
                case "SUBMIT_VB":
                    url = _base_url + "m/voicebox/store";
                    Log.v("keiskeidebug", url);
                    request = HttpClient.post(url);
                    request.part("name",  args[1]);
                    request.part("email",  args[2]);
                    request.part("handphone",  args[3]);
                    request.part("title", args[4]);
                    request.part("description",  args[5]);
                    request.part("is_anonymous",  args[6]);
                    request.part("gcm_id",  args[7]);
                    request.part("user_id",  args[8]);
                    request.part("photo",  args[9]);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = request.body();
                    }
                    break;
                case "SEND_CHAT":
                    url = _base_url + "m/chat/store";
                    Log.v("keiskeidebug gg", url);
                    request = HttpClient.post(url);
                    request.part("description",  args[1]);
                    request.part("is_anonymous",  args[2]);
                    request.part("gcm_id",  args[3]);
                    request.part("user_id", args[4]);
                    request.part("photo",  args[5]);
                    request.part("path_user", args[6]);
                    request.part("name",  args[7]);
                    request.part("email",  args[8]);
                    request.part("handphone",  args[9]);
                    if (request.ok())
                    {
                        resp = request.body();
                    }else{
                        resp = request.code() + "";
                    }

                    break;
                default:
                    break;
            }
            Log.v("keiskeidebug", resp);
            return  resp;

        }
        catch (HttpClient.HttpClientException e){
            e.getCause();
            return "{RESP : 'ERROR' }";
        }
//        catch (Exception e)
//        {
//            Log.v("keiskeidebug exception", resp + e.getMessage());
//            return "{RESP : 'ERROR' }";
////            new String("Exc : " + e.getMessage());
//        }
    }


    @Override
    protected void onPostExecute(String s) {
        Log.v("keiskeidebug onpost", s);
        delegate.processFinish(s);
    }
}

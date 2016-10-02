package com.alvarisi.keiskeismartsystem.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alvarisi.keiskeismartsystem.LoginActivity;
import com.alvarisi.keiskeismartsystem.model.User;

/**
 * Created by zeta on 10/15/2015.
 */
public class AppSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "KEISKEIapp";
    private static final String IS_FIRST_TIME = "IsFirstTime";

    public AppSession(Context context){
        this._context = context;
        pref = this._context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setFirstTime(){
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.commit();
    }
    public void setGCMID(String gcmid){
        editor.putString("gcm_id", gcmid);
        editor.commit();
    }
    public String getGCMID(){
        String gcm = pref.getString("gcm_id", "");
        return gcm;
    }

    public boolean checkFirstTime(){
        Boolean ift =  pref.getBoolean(IS_FIRST_TIME, false);

        return ift;
    }

    public void setAnonName(String name){
        editor.putString("anon_name", name);
        editor.commit();
    }
    public String getAnonName(){
        return pref.getString("anon_name","");
    }
    public void setAnonEmail(String email){
        editor.putString("anon_email", email);
        editor.commit();
    }
    public String getAnonEmail(){
        return pref.getString("anon_email","");
    }
    public void setAnonHandphone(String hp){
        editor.putString("anon_hp", hp);
        editor.commit();
    }
    public String getAnonHandphone(){
        return pref.getString("anon_hp","");
    }
    public void clear(){
        editor.clear();
        editor.commit();
    }
}

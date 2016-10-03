package com.keiskeismartsystem.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.keiskeismartsystem.LoginActivity;
import com.keiskeismartsystem.model.User;

/**
 * Created by zeta on 10/13/2015.
 */
public class UserSession {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        Context _context;

        int PRIVATE_MODE = 0;

        private static final String PREFER_NAME = "KEISKEIuser";
        private static final String IS_USER_LOGIN = "IsUserLoggedIn";

        public UserSession(Context context){
            this._context = context;
            pref = this._context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        public void createSessionLogin(User user){
            editor.putString("name", user.getName());
            editor.putString("email", user.getEmail());
            editor.putString("username", user.getUsername());
            editor.putString("photo", user.getPhoto());
            editor.putString("gcm_id", user.getGCMID());
            editor.putString("code", user.getCode());
            editor.putString("pinbb", user.getPinBB());
            editor.putInt("total", user.getTotalBought());
            editor.putString("path_int", user.getPathUserInt());
//            String _gender = user.getGender() + "";
//            editor.putString("gender", _gender);
            editor.putString("address", user.getAddress());
            editor.putString("handphone", user.getTelephone());
            editor.putString("website", user.getWebsite());
            editor.putString("instagram", user.getInstagram());
            editor.putString("facebook", user.getFB());
            editor.putString("bonus", user.getBonus());
            editor.putInt("id", user.getID());
            editor.putBoolean(IS_USER_LOGIN, true);
            editor.commit();
        }
        public void updateUserSession(User user){
            editor.putString("name", user.getName());
            editor.putString("email", user.getEmail());
            editor.putString("username", user.getUsername());
            editor.putString("photo", user.getPhoto());
            editor.putString("gcm_id", user.getGCMID());
            editor.putString("code", user.getCode());
            editor.putString("pinbb", user.getPinBB());
            editor.putString("path_int", user.getPathUserInt());
            editor.putInt("total", user.getTotalBought());
//            String _gender = user.getGender() + "";
//            editor.putString("gender", _gender);
            editor.putString("address", user.getAddress());
            editor.putString("handphone", user.getTelephone());
            editor.putInt("id", user.getID());

            editor.putBoolean(IS_USER_LOGIN, true);
            editor.putString("website", user.getWebsite());
            editor.putString("instagram", user.getInstagram());
            editor.putString("facebook", user.getFB());
            editor.putString("bonus", user.getBonus());
            editor.commit();
        }
        public void setGCMID(String gcmid){
            editor.putString("gcm_id", gcmid);
            editor.commit();
        }

        public boolean checkLogin(){
            if(!this.isUserLoggedIn())
            {
                Intent intent = new Intent(_context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);
                return true;
            }
            return false;
        }
        public boolean isUserLoggedIn(){
            return pref.getBoolean(IS_USER_LOGIN, false);
        }

        public User getUserSessionData(){
            User user = new User();
            user.setName(pref.getString("name", ""));
            user.setEmail(pref.getString("email", ""));
            user.setUsername(pref.getString("username", ""));
            user.setPhoto(pref.getString("photo", ""));
            user.setAddress(pref.getString("address", ""));
            user.setTelephone(pref.getString("handphone", ""));
            user.setCode(pref.getString("code", ""));
            user.setPinBB(pref.getString("pinbb", ""));
            user.setGCMID(pref.getString("gcm_id", ""));
            user.setID(pref.getInt("id", 0));
            user.setPathUserInt(pref.getString("path_int", ""));
            user.setInstagram(pref.getString("instagram", ""));
            user.setFB(pref.getString("facebook", ""));
            user.setWebsite(pref.getString("website", ""));
            user.setTotalBought(pref.getInt("total", 0));
            user.setBonus(pref.getString("bonus", ""));
            return user;

        }
        public void logout(){
            editor.clear();
            editor.commit();
        }
}

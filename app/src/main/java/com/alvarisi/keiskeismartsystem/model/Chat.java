package com.alvarisi.keiskeismartsystem.model;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zeta on 11/3/2015.
 */
public class Chat implements Serializable {
    public static final String KEY = "CHAT_DATA";
    private int _id, _sid;
    private String _name, _photo_int, _photo_ext, _description;
    private char _is_admin, _read_flag;
    private Date _created_at;
    public Chat(){
        _name = "";
        _photo_int = "";
        _photo_ext = "";
        _description = "";
        _is_admin = '0';
        _read_flag = '0';
        _created_at = null;

    }
    public void setId(int id){
        this._id = id;
    }
    public int getId(){
        return this._id;
    }
    public void setSid(int sid){
        this._sid = sid;
    }
    public int getSid(){
        return this._sid;
    }
    public void setName(String name){
        this._name = name;
    }
    public String getName(){
        return this._name;
    }
    public void setDescription(String description){
        this._description = description;
    }
    public String getDescription(){
        return this._description;
    }
    public void setPhotoInt(String photoInt){
        this._photo_int = photoInt;
    }
    public String getPhotoInt(){
        return this._photo_int;
    }
    public void setPhotoExt(String photoExt){
        this._photo_ext = photoExt;
    }
    public String getPhotoExt(){
        return this._photo_ext;
    }

    public void setIsAdmin(char is_admin){
        this._is_admin = is_admin;
    }
    public char getIsAdmin(){
        return this._is_admin;
    }

    public void setReadFlag(char readFlag){
        this._read_flag = readFlag;
    }
    public char getReadFlag(){
        return this._read_flag;
    }

    public void setCreatedAt(Date created_at){
        _created_at = created_at;
    }
    public Date getCreatedAt(){
        return _created_at;
    }
}

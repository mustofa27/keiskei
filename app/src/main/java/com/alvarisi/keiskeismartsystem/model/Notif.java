package com.alvarisi.keiskeismartsystem.model;

import java.io.Serializable;

/**
 * Created by zeta on 11/1/2015.
 */
public class Notif implements Serializable {
    public static final String KEY = "NOTIF_DATA";
    private int _id, _sid;
    private String _title, _description, _photo_int, _photo_ext, _read;
    public Notif(){

    }
    public Notif(int sid, String title, String photo_int, String photo_ext)
    {
        this._sid = sid;
        this._title = title;
        this._photo_int = photo_int;
        this._photo_ext = photo_ext;
    }
    public void setId(int sid){
        this._id = sid;
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
    public void setTitle(String title){
        this._title = title;
    }
    public String getTitle(){
        return this._title;
    }
    public void setPhotoInt(String photo_int){
        this._photo_int = photo_int;
    }
    public String getPhotoInt(){
        return this._photo_int;
    }
    public void setPhotoExt(String photo_int){
        this._photo_ext = photo_int;
    }
    public String getPhotoExt(){
        return this._photo_ext;
    }
    public void setDescription(String description){
        this._description= description;
    }
    public String getDescription(){
        return this._description;
    }
    public void setRead(String read){
        this._read = read;
    }
    public String getRead(){
        return this._read;
    }

}

package com.keiskeismartsystem.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zeta on 11/4/2015.
 */
public class Product implements Serializable {
    public static final String KEY = "PRODUCT_DATA";
    private int _id, _sid, jumlah = 0;
    private String _title, _photo_int, _photo_ext, _description, _code, harga, kategori;
    public Product(){
        _code = "";
        _title = "";
        _photo_int = "";
        _photo_ext = "";
        _description = "";
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
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
    public void setTitle(String title){
        this._title = title;
    }
    public String getTitle(){
        return this._title;
    }
    public void setCode(String code){
        this._code = code;
    }
    public String getCode(){
        return this._code;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}

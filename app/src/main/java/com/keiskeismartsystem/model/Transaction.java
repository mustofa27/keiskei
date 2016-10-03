package com.keiskeismartsystem.model;

import java.io.Serializable;

/**
 * Created by zeta on 11/22/2015.
 */
public class Transaction implements Serializable {
    public static final String KEY = "TRANSACTION_DATA";
    private int _id, _sid, _total;
    private String _ship, _tgl, _code;
    public Transaction(){
        _code = "";
        _ship = "";
        _tgl = "";

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
    public void setTotal(int total){
        this._total = total;
    }
    public int getTotal(){
        return this._total;
    }

    public void setShip(String ship){
        this._ship = ship;
    }
    public String getShip(){
        return this._ship;
    }
    public void setCode(String code){
        this._code = code;
    }
    public String getCode(){
        return this._code;
    }
    public void setTgl(String tgl){
        this._tgl = tgl;
    }
    public String getTgl(){
        return this._tgl;
    }

}

package com.alvarisi.keiskeismartsystem.model;

/**
 * Created by zeta on 10/15/2015.
 */
public class City {
    public static final String KEY = "CITY_DATA";
    private int _id, _sid, _ms_province_id;
    private String _name;
    public City(){

    }
    public City(int sid, String name, int province)
    {
        this._sid = sid;
        this._name = name;
        this._ms_province_id = province;
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
    public void setProvince(int province){
        this._ms_province_id = province;
    }
    public int getProvince(){
        return this._ms_province_id;
    }
    public void setName(String name){
        this._name = name;
    }
    public String getName(){
        return this._name;
    }

}

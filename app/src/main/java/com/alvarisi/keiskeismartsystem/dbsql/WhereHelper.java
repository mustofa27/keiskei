package com.alvarisi.keiskeismartsystem.dbsql;

/**
 * Created by zeta on 10/15/2015.
 */
public class WhereHelper {
    private String _key, _value;

    public WhereHelper(String key, String value){
        this._key = key;
        this._value = value;
    }
    public void setKey(String key){
        this._key = key;
    }
    public String getKey(){
        return this._key;
    }
    public void setValue(String value){
        this._value= value;
    }
    public String getValue(){
        return this._value;
    }
}

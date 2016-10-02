package com.alvarisi.keiskeismartsystem.dbsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.alvarisi.keiskeismartsystem.dbhandler.DBHandler;
import com.alvarisi.keiskeismartsystem.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeta on 10/15/2015.
 */
public class CityTransact {
    private Context _context;
    private static final String TABLE_NAME = "cities";
    public CityTransact(Context context){
        _context = context;
    }
    public List<City> all(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.all(TABLE_NAME, "");
        List<City> cities = new ArrayList<City>();
        if (cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                city.setName(cursor.getString((cursor.getColumnIndex("name"))));
                city.setProvince(Integer.parseInt(cursor.getString((cursor.getColumnIndex("ms_province_id")))));
                city.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                cities.add(city);
            }while (cursor.moveToNext());
        }
        return cities;
    }
    public List<City> get(ArrayList<WhereHelper> whereHelpers){
        String where = "";
        if (whereHelpers.size() == 1){
            where += whereHelpers.get(0).getKey() + " = '" + whereHelpers.get(0).getValue() + "'";
        }else if(whereHelpers.size() > 1){
            for(int i = 0; i < whereHelpers.size(); i++){
                where += whereHelpers.get(i).getKey() + "='" + whereHelpers.get(i).getValue() + "'";
                if(i != whereHelpers.size()-1){
                    where += " AND ";
                }
            }
        }
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.get(TABLE_NAME, where, "");
        List<City> cities = new ArrayList<City>();
        if (cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                city.setName(cursor.getString((cursor.getColumnIndex("name"))));
                city.setProvince(Integer.parseInt(cursor.getString((cursor.getColumnIndex("ms_province_id")))));
                city.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));

                cities.add(city);
            }while (cursor.moveToNext());

        }
        return cities;
    }
    public long insert(City city)
    {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", city.getSid());
        contentValues.put("name", city.getName());
        Log.v("keiskeidebug dbinsert", city.getName());
        contentValues.put("ms_province_id", city.getProvince());
        long result = dbHandler.insert(TABLE_NAME, contentValues);
        return result;
    }
    public void truncate() {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        dbHandler.truncate(TABLE_NAME);
    }
    public int countAll(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        int count = dbHandler.countAll(TABLE_NAME, "");
        return count;
    }
}

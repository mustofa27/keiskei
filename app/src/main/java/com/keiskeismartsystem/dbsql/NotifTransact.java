package com.keiskeismartsystem.dbsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.keiskeismartsystem.dbhandler.DBHandler;
import com.keiskeismartsystem.model.Notif;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeta on 11/1/2015.
 */
public class NotifTransact {
    private Context _context;
    private static final String TABLE_NAME = "notifications";
    public NotifTransact(Context context){
        _context = context;
    }
    public List<Notif> all(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.all(TABLE_NAME, " ORDER BY id DESC");
        List<Notif> notifs = new ArrayList<Notif>();
        if (cursor.moveToFirst()){
            do{
                Notif notif = new Notif();
                notif.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                notif.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                notif.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
                notif.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                notif.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                notif.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                notif.setRead(cursor.getString((cursor.getColumnIndex("read_flag"))));
                notifs.add(notif);
            }while (cursor.moveToNext());
        }
        dbHandler.close();
        return notifs;
    }
    public List<Notif> get(ArrayList<WhereHelper> whereHelpers){
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
        List<Notif> notifs = new ArrayList<Notif>();
        if (cursor.moveToFirst()){
            do{
                Notif notif = new Notif();
                notif.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                notif.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                notif.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
                notif.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                notif.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                notif.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                notif.setRead(cursor.getString((cursor.getColumnIndex("read_flag"))));
                notifs.add(notif);
            }while (cursor.moveToNext());

        }
        dbHandler.close();
        return notifs;
    }
    public Notif first(ArrayList<WhereHelper> whereHelpers){
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
        Notif notif = new Notif();
        if (cursor.moveToFirst()){

                notif.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                notif.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                notif.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
                notif.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                notif.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                notif.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                notif.setRead(cursor.getString((cursor.getColumnIndex("read_flag"))));
        }
        dbHandler.close();
        return notif;

    }
    public long insert(Notif notif)
    {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", notif.getSid());
        contentValues.put("title", notif.getTitle());
        contentValues.put("description", notif.getDescription());
        contentValues.put("photo_int", notif.getPhotoInt());
        contentValues.put("photo_ext", notif.getPhotoExt());
        contentValues.put("read_flag", "0");
        Log.v("keiskeidebug", "notif insert");
        long result = dbHandler.insert(TABLE_NAME, contentValues);
        dbHandler.close();
        return result;
    }
    public void update(Notif notif){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", notif.getSid());
        contentValues.put("title", notif.getTitle());
        contentValues.put("description", notif.getDescription());
        contentValues.put("photo_int", notif.getPhotoInt());
        contentValues.put("photo_ext", notif.getPhotoExt());
        contentValues.put("read_flag", notif.getRead());

        dbHandler.update(TABLE_NAME, contentValues, " id = " + notif.getId());
    }
    public void truncate() {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        dbHandler.truncate(TABLE_NAME);
        dbHandler.close();
    }
    public int countAll(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        int count = dbHandler.countAll(TABLE_NAME, "");
        dbHandler.close();
        return count;
    }
    public int delete(ArrayList<WhereHelper> whereHelpers){
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
        int result = dbHandler.delete(TABLE_NAME, where);
        dbHandler.close();
        return result;
    }
}

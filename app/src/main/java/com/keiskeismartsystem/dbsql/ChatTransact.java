package com.keiskeismartsystem.dbsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.keiskeismartsystem.dbhandler.DBHandler;
import com.keiskeismartsystem.model.Chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * keiskei smartsystem_V2.
 */
public class ChatTransact {
    private Context _context;
    private static final String TABLE_NAME = "chats";
    public ChatTransact(Context context){
        _context = context;
    }
    public List<Chat> all(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.all(TABLE_NAME, " ");
        List<Chat> chats = new ArrayList<Chat>();
        if (cursor.moveToFirst()){
            do{
                Chat chat = new Chat();
                chat.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                chat.setName(cursor.getString((cursor.getColumnIndex("name"))));
                chat.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                chat.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                chat.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                String is_admin = cursor.getString(cursor.getColumnIndex("is_admin"));
                chat.setIsAdmin(is_admin.charAt(0));
                String read_flag = cursor.getString(cursor.getColumnIndex("read_flag"));
                chat.setReadFlag(read_flag.charAt(0));
                String created_at_t = cursor.getString((cursor.getColumnIndex("created_at")));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = format.parse(created_at_t);
                    chat.setCreatedAt(date);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                chats.add(chat);
            }while (cursor.moveToNext());
        }
        dbHandler.close();
        return chats;
    }
    public List<Chat> get(ArrayList<WhereHelper> whereHelpers){
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
        List<Chat> chats = new ArrayList<Chat>();
        if (cursor.moveToFirst()){
            do{
                Chat chat = new Chat();
                chat.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                chat.setName(cursor.getString((cursor.getColumnIndex("name"))));
                chat.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                chat.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                chat.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                String is_admin = cursor.getString(cursor.getColumnIndex("is_admin"));
                chat.setIsAdmin(is_admin.charAt(0));
                String read_flag = cursor.getString(cursor.getColumnIndex("read_flag"));
                chat.setReadFlag(read_flag.charAt(0));
                String created_at_t = cursor.getString((cursor.getColumnIndex("created_at")));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = format.parse(created_at_t);
                    chat.setCreatedAt(date);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                chats.add(chat);
            }while (cursor.moveToNext());

        }
        dbHandler.close();
        return chats;
    }
    public Chat first(ArrayList<WhereHelper> whereHelpers){
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
        Chat chat = new Chat();
        if (cursor.moveToFirst()) {

            chat.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
            chat.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
            chat.setName(cursor.getString((cursor.getColumnIndex("name"))));
            chat.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
            chat.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
            chat.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
            String is_admin = cursor.getString(cursor.getColumnIndex("is_admin"));
            chat.setIsAdmin(is_admin.charAt(0));
            String read_flag = cursor.getString(cursor.getColumnIndex("read_flag"));
            chat.setReadFlag(read_flag.charAt(0));
            String created_at_t = cursor.getString((cursor.getColumnIndex("created_at")));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(created_at_t);
                chat.setCreatedAt(date);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        dbHandler.close();
        return chat;

    }
    public long insert(Chat chat)
    {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", chat.getSid());
        contentValues.put("description", chat.getDescription());
        contentValues.put("photo_int", chat.getPhotoInt());
        contentValues.put("photo_ext", chat.getPhotoExt());
        contentValues.put("is_admin", chat.getIsAdmin() + "");
        contentValues.put("read_flag", chat.getReadFlag() + "");
        contentValues.put("name", chat.getName());
        Date date = chat.getCreatedAt();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String temp = "";
        if (date != null){
            temp = format.format(date);
        }
        String created_at_t = temp;
        contentValues.put("created_at", created_at_t);
        long result = dbHandler.insert(TABLE_NAME, contentValues);
        dbHandler.close();
        return result;
    }
    public void update(Chat chat){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", chat.getSid());
        contentValues.put("description", chat.getDescription());
        contentValues.put("photo_int", chat.getPhotoInt());
        contentValues.put("photo_ext", chat.getPhotoExt());
        contentValues.put("is_admin", chat.getIsAdmin() + "");
        contentValues.put("read_flag", chat.getReadFlag() + "");
        contentValues.put("name", chat.getName());
        Date date = chat.getCreatedAt();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String temp = "";
        if (date != null){
            temp = format.format(date);
        }
        String created_at_t = temp;
        contentValues.put("created_at", created_at_t);

        dbHandler.update(TABLE_NAME, contentValues, " id = " + chat.getId());
        dbHandler.close();
    }
    public void truncate() {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        dbHandler.truncate(TABLE_NAME);
    }
    public int countAll(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        int count = dbHandler.countAll(TABLE_NAME, "");
        dbHandler.close();
        return count;
    }
}

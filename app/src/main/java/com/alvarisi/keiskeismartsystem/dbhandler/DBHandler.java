package com.alvarisi.keiskeismartsystem.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zeta on 10/15/2015.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "keiskei.db",
            TABLE_CITY = "cities",
            TABLE_NOTIFICATION = "notifications",
            TABLE_PRODUCT = "products",
            TABLE_CHAT = "chats",
            TABLE_TRANSACTION = "transactions",
            COLUMN_ID = "id",
            COLUMN_CODE = "code",
            COLUMN_SID = "server_id",
            COLUMN_NAME = "name",
            COLUMN_TITLE = "title",
            COLUMN_DESCRIPTION = "description",
            COLUMN_PHOTO = "photo",
            COLUMN_PHOTO_INT = "photo_int",
            COLUMN_PHOTO_EXT = "photo_ext",
            COLUMN_PROVINCE = "ms_province_id",
            COLUMN_IS_ADMIN = "is_admin",
            COLUMN_READ_FLAG = "read_flag",
            COLUMN_CREATED_AT = "created_at",
            COLUMN_TGL = "tanggal",
            COLUMN_SHIP = "ship",
            COLUMN_TOTAL = "total"
                    ;

    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SID + " INTEGER, " +
            COLUMN_CODE + " TEXT, " +
            COLUMN_SHIP + " TEXT, " +
            COLUMN_TGL + " TEXT, " +
            COLUMN_TOTAL + " INTEGER)"
            ;
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CODE + " TEXT, " +
            COLUMN_SID + " INTEGER, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_PHOTO_INT + " TEXT, " +
            COLUMN_PHOTO_EXT + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT)"
            ;
    private static final String CREATE_TABLE_CHAT = "CREATE TABLE " + TABLE_CHAT + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SID + " INTEGER, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_PHOTO_INT + " TEXT, " +
            COLUMN_PHOTO_EXT + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IS_ADMIN + " TEXT, " +
            COLUMN_READ_FLAG + " TEXT, " +
            COLUMN_CREATED_AT + " TEXT )"
            ;
    private static final String CREATE_TABLE_CITY = "CREATE TABLE " + TABLE_CITY + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_SID + " INTEGER, " +
            COLUMN_PROVINCE + " INTEGER )"
            ;
    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SID + " INTEGER, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_READ_FLAG + " TEXT, " +
            COLUMN_PHOTO_INT + " TEXT, " +
            COLUMN_PHOTO_EXT + " TEXT )"
            ;
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public  void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_CHAT);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_TRANSACTION);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }
    public long insert(String table,  ContentValues contentValues)
    {
        Log.v("keiskeidebug", "insert data");
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(table, null, contentValues);
        return id;
    }
    public Cursor all(String table, String order)
    {
        String query = "SELECT * FROM " + table + " ";
        if (!order.isEmpty())
        {
            query += order;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return  cursor;
    }
    public Cursor get(String table, String filter, String order)
    {
        String query = "SELECT * FROM " + table + " ";
        if (!filter.isEmpty())
        {
            query += " where " + filter;
        }
        if (!order.isEmpty())
        {
            query += " " + order;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return  cursor;
    }

    public int update(String table, ContentValues contentValues, String filter)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = db.update(table, contentValues, filter, null);
        return id;
    }
    public int delete(String table, String filter)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(table, filter, null);
        db.close();
        return result;
    }
    public int countAll(String table, String where){

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select count(*) from " + table ;
        if(!where.isEmpty()){
            sql = "select count(*) from " + table  + " where " + where;
        }
        Cursor cursorCount = db.rawQuery(sql, null);
        cursorCount.moveToFirst();
        int count = cursorCount.getInt(0);
        cursorCount.close();
        return count;
    }
    public Cursor statementRaw(String query)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
    public boolean truncate(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(table, null, null);
        db.execSQL("VACUUM");
        return  result==1;
    }
}


package com.keiskeismartsystem.dbsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.keiskeismartsystem.dbhandler.DBHandler;
import com.keiskeismartsystem.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeta on 11/22/2015.
 */
public class TransactionTransact {
    private Context _context;
    private static final String TABLE_NAME = "transactions";
    public TransactionTransact(Context context){
        _context = context;
    }
    public List<Transaction> all(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.all(TABLE_NAME, " ORDER BY id DESC");
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (cursor.moveToFirst()){
            do{
                Transaction transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                transaction.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                transaction.setCode(cursor.getString((cursor.getColumnIndex("code"))));
                transaction.setTotal(Integer.parseInt(cursor.getString((cursor.getColumnIndex("total")))));
                transaction.setShip(cursor.getString((cursor.getColumnIndex("ship"))));
                transaction.setTgl(cursor.getString((cursor.getColumnIndex("tanggal"))));
                transactions.add(transaction);
            }while (cursor.moveToNext());
        }
        dbHandler.close();
        return transactions;
    }
    public List<Transaction> get(ArrayList<WhereHelper> whereHelpers){
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
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (cursor.moveToFirst()){
            do{
                Transaction transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                transaction.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
                transaction.setCode(cursor.getString((cursor.getColumnIndex("code"))));
                transaction.setTotal(Integer.parseInt(cursor.getString((cursor.getColumnIndex("total")))));
                transaction.setShip(cursor.getString((cursor.getColumnIndex("ship"))));
                transaction.setTgl(cursor.getString((cursor.getColumnIndex("tanggal"))));
                transactions.add(transaction);
            }while (cursor.moveToNext());

        }
        dbHandler.close();
        return transactions;
    }
    public Transaction first(ArrayList<WhereHelper> whereHelpers){
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
        Transaction transaction = new Transaction();
        if (cursor.moveToFirst()){

            transaction.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
            transaction.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
            transaction.setCode(cursor.getString((cursor.getColumnIndex("code"))));
            transaction.setTotal(Integer.parseInt(cursor.getString((cursor.getColumnIndex("total")))));
            transaction.setShip(cursor.getString((cursor.getColumnIndex("ship"))));
            transaction.setTgl(cursor.getString((cursor.getColumnIndex("tanggal"))));
        }
        dbHandler.close();
        return transaction;

    }
    public long insert(Transaction transaction)
    {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", transaction.getSid() + "");
        contentValues.put("code", transaction.getCode());
        contentValues.put("ship", transaction.getShip());
        contentValues.put("tanggal", transaction.getTgl());
        contentValues.put("total", transaction.getTotal() + "");
        Log.v("keiskeidebug", "transaction insert");
        long result = dbHandler.insert(TABLE_NAME, contentValues);
        dbHandler.close();
        return result;
    }
    public void update(Transaction transaction){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", transaction.getSid() + "");
        contentValues.put("code", transaction.getCode());
        contentValues.put("ship", transaction.getShip());
        contentValues.put("tanggal", transaction.getTgl());
        contentValues.put("total", transaction.getTotal() + "");

        dbHandler.update(TABLE_NAME, contentValues, " id = " + transaction.getId());
        dbHandler.close();
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
}

package com.alvarisi.keiskeismartsystem.dbsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.alvarisi.keiskeismartsystem.dbhandler.DBHandler;
import com.alvarisi.keiskeismartsystem.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeta on 11/4/2015.
 */
public class ProductTransact {
    private Context _context;
    private static final String TABLE_NAME = "products";
    public ProductTransact(Context context){
        _context = context;
    }
    public List<Product> all(){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        Cursor cursor = dbHandler.all(TABLE_NAME, " ");
        List<Product> products = new ArrayList<Product>();
        if (cursor.moveToFirst()){
            do{
                Product product = new Product();
                product.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                product.setCode(cursor.getString((cursor.getColumnIndex("code"))));
                product.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
                product.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                product.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                product.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));
                products.add(product);
            }while (cursor.moveToNext());
        }
        dbHandler.close();
        return products;
    }
    public List<Product> get(ArrayList<WhereHelper> whereHelpers){
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
        List<Product> products = new ArrayList<Product>();
        if (cursor.moveToFirst()){
            do{
                Product product = new Product();
                product.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
                product.setCode(cursor.getString((cursor.getColumnIndex("code"))));
                product.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
                product.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
                product.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
                product.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));

                products.add(product);
            }while (cursor.moveToNext());

        }
        dbHandler.close();
        return products;
    }
    public Product first(ArrayList<WhereHelper> whereHelpers){
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
        Product product = new Product();
        if (cursor.moveToFirst()) {

            product.setId(Integer.parseInt(cursor.getString((cursor.getColumnIndex("id")))));
            product.setSid(Integer.parseInt(cursor.getString((cursor.getColumnIndex("server_id")))));
            product.setCode(cursor.getString((cursor.getColumnIndex("code"))));
            product.setTitle(cursor.getString((cursor.getColumnIndex("title"))));
            product.setDescription(cursor.getString((cursor.getColumnIndex("description"))));
            product.setPhotoInt(cursor.getString((cursor.getColumnIndex("photo_int"))));
            product.setPhotoExt(cursor.getString((cursor.getColumnIndex("photo_ext"))));

        }
        dbHandler.close();
        return product;

    }
    public long insert(Product product)
    {
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", product.getSid());
        contentValues.put("code", product.getCode());
        contentValues.put("description", product.getDescription());
        contentValues.put("photo_int", product.getPhotoInt());
        contentValues.put("photo_ext", product.getPhotoExt());
        contentValues.put("title", product.getTitle());

        long result = dbHandler.insert(TABLE_NAME, contentValues);
        dbHandler.close();
        return result;
    }
    public void update(Product product){
        DBHandler dbHandler = new DBHandler(this._context, null, null, 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("server_id", product.getSid());
        contentValues.put("code", product.getCode());
        contentValues.put("description", product.getDescription());
        contentValues.put("photo_int", product.getPhotoInt());
        contentValues.put("photo_ext", product.getPhotoExt());
        contentValues.put("title", product.getTitle());

        dbHandler.update(TABLE_NAME, contentValues, " id = " + product.getId());
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

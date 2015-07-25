package com.miaotu.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.miaotu.activity.BaseActivity;
import com.miaotu.model.Account;
import com.miaotu.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayden on 2015/7/25.
 */
public class LoginDbManager {
    private LoginDbHelper dbhelper;
    private SQLiteDatabase db;

    public LoginDbManager(Context context){
        dbhelper = new LoginDbHelper(context, "login.db");
    }

    /**
     * 添加数据
     * @param username
     * @param password
     */
    public void save(String username, String password){
        String sql = "insert into account(username,password) values(?,?)";
        db = dbhelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.execSQL(sql, new Object[]{username, password});
            db.setTransactionSuccessful();
        }catch (Exception e){
            LogUtil.e("LoginDbManager","插入Account表失败");
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Account> query(){
        List<Account> accounts = new ArrayList<>();
        db = dbhelper.getWritableDatabase();
        String sql = "select * from account;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Account account = new Account();
            account.setId(cursor.getInt(cursor.getColumnIndex("id")));
            account.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            account.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            accounts.add(account);
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return accounts;
    }

    /**
     * 查询用户名
     * @return
     */
    public List<String> queryName(){
        List<String> accounts = new ArrayList<>();
        db = dbhelper.getReadableDatabase();
        String sql = "select username from account;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            accounts.add(cursor.getString(cursor.getColumnIndex("username")));
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return accounts;
    }

    /**
     * 查询用户名
     * @return
     */
    public boolean isExit(String name){
        boolean flag = false;
        List<String> accounts = new ArrayList<>();
        db = dbhelper.getReadableDatabase();
        String sql = "select username from account where username = ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{name});
        flag = cursor.moveToFirst();
        db.close();
        cursor.close();
        return flag;
    }

    /**
     * 删除数据库数据
     */
    public void delete(){
        db = dbhelper.getWritableDatabase();
        String sql = "delete from account";
        db.execSQL(sql);
        db.close();
    }
}

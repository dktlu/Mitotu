package com.miaotu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jayden on 2015/7/25.
 */
public class LoginDbHelper extends SQLiteOpenHelper{

    private static final int  VERSON = 1;//默认的数据库版本

    public LoginDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LoginDbHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    public LoginDbHelper(Context context, String name){
        this(context, name, VERSON);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS account(id INTEGER PRIMARY KEY, username varchar(50), password varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        System.out.println("update database");
    }
}

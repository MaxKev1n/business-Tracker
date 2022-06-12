package com.mobile_application.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDb extends SQLiteOpenHelper {
    private String myAccount;

    public LocalDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String account) {
        super(context, name, factory, version);
        myAccount = account;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "create table " + myAccount + "(curdate text primary key, listcount integer, studytime integer);";
        sqLiteDatabase.execSQL(createTable);
        /*String selectTable = "select count(*) from sqlite_master where type='table' and name='" + myAccount + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(selectTable, null);
        if(cursor.moveToNext()) {
            if(cursor.getInt(0) > 0) {
                System.out.println("CREATE SUCCESS");
            }
            else {
                System.out.println("CREATE FAIL");
            }
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

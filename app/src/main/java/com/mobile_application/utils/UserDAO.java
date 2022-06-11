package com.mobile_application.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobile_application.utils.LocalDb;

import com.mobile_application.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public int select(String account, String password) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return 0;
            }
            state = conn.createStatement();
            String sql = "select * from user where account = '" + account + "' and password = '" + password + "'";
            res = state.executeQuery(sql);
            if(res.next()) {
                return 1;
            }
            else {
                return 2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(res != null) {
                res.close();
            }
            if(state != null) {
                state.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
        return 0;
    }

    public int add(String account, String name, String password) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return 0;
            }
            state = conn.createStatement();
            String sql = "select * from user where account =" + account;
            res = state.executeQuery(sql);
            if(res.next()) {
                return 2; //exist same account user
            }
            else {
                sql = "insert into user (account, name, password) values(" + account + "," + name + "," + password + ");";
                int r = state.executeUpdate(sql);
                if(r != 0){
                    return 1; //insert success
                }
                else{
                    return 3;//insert fail
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(res != null) {
                res.close();
            }
            if(state != null) {
                state.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
        return 0;
    }

    public void insertUserSign(SQLiteDatabase db, String myAccount, String curDate, int listCount, int studyTime) {
        ContentValues values = new ContentValues();
        values.put("curdate", curDate);
        values.put("listcount", listCount);
        values.put("studytime", studyTime);
        db.insert(myAccount, null, values);
    }

    public List<String> selectUserSign(SQLiteDatabase db, String myAccount, String curDate) {
        String selectTable = "select * from " + "'" + myAccount + "'" + " where curdate = " + "'" + curDate + "';";
        Cursor cursor = db.rawQuery(selectTable, null);
        List<String> res = new ArrayList<>();
        if(cursor.moveToNext()){
            int listCountIndex = cursor.getColumnIndex("listcount");
            int studyTimeIndex = cursor.getColumnIndex("studytime");
            res.add(cursor.getString(listCountIndex));
            res.add(cursor.getString(studyTimeIndex));
        }
        return res;
    }
}

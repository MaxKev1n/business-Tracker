package com.mobile_application.utils;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mobile_application.Config;
import com.mobile_application.utils.LocalDb;

import com.mobile_application.utils.JDBCUtils;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDAO {
    private static final String TAG = "UserDAO";
    //MYSQl
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
            String sql = "select * from `user` where account = '" + account + "' and password = '" + password + "'";
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
        return 2;
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
            String sql = "select * from `user` where account ='" + account +"'";
            res = state.executeQuery(sql);
            if(res.next()) {
                return 2; //exist same account user
            }
            else {
                sql = "insert into `user` (account, name, password) values('" + account + "','" + name + "','" + password + "');";
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

    public List<List<String>> selectRemoteData(String account) throws SQLException {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return null;
            }
            state = conn.createStatement();
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'app' and TABLE_NAME ='" + account + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                sql = "select * from " + account + ";";
                res = state.executeQuery(sql);
                List<List<String>> listRes = new ArrayList<>();
                while(res.next()) {
                    List<String> temp = new ArrayList<>();
                    temp.add(res.getString("curdate"));
                    temp.add(res.getString("studytime"));
                    temp.add(res.getString("time"));
                    listRes.add(temp);
                }
                return listRes;
            }
            else {
                sql = "CREATE TABLE `app`.`" + account + "` (`curdate` VARCHAR(45) NOT NULL,`studytime` INT NULL, `time` VARCHAR(45) NOT NULL,PRIMARY KEY (`time`));";
                state.executeUpdate(sql);
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
        return null;
    }

    public int updateRemoteData(String account, String curDate, String studyTime, String time) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return 0;
            }
            state = conn.createStatement();
            String selectSql = "select * from " + account + " where time = '" + time + "';";
            res = state.executeQuery(selectSql);
            if(!res.next()) {
                String sql = "insert into `" + account + "` (curdate, studytime, time) values('" + curDate + "','" + studyTime + "','" + time + "');";
                state.executeUpdate(sql);
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

    public Blob selectUserImg(String account) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return null;
            }
            state = conn.createStatement();
            String sql = "select * from user where account = '" + account + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                Blob img = res.getBlob("image");
                return img;
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
        return null;
    }

    public void updateUserImg(String account, Bitmap img) throws Exception {
        Connection conn = null;
        PreparedStatement state = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return;
            }
            Blob blob = conn.createBlob();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            blob.setBytes(1, data);
            String sql = "UPDATE `app`.`user` SET image = ? where account = ?;";
            state = conn.prepareStatement(sql);
            state.setBlob(1, blob);
            state.setString(2, account);
            state.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(state != null) {
                state.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
    }

    public void updateUserView(String account, Boolean isView) throws Exception {
        Connection conn = null;
        PreparedStatement state = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return;
            }
            String sql = "UPDATE `app`.`user` SET view = ? where account = ?;";
            state = conn.prepareStatement(sql);
            int view = isView ? 1 : 0;
            state.setInt(1, view);
            state.setString(2, account);
            state.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(state != null) {
                state.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
    }

    public void updateUserDate(String account, Date date) throws Exception {
        Connection conn = null;
        PreparedStatement state = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return;
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curDate = df.format(date);
            String sql = "UPDATE `app`.`user` SET lastdate = ? where account = ?;";
            state = conn.prepareStatement(sql);
            state.setString(1, curDate);
            state.setString(2, account);
            state.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(state != null) {
                state.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
    }

    public String selectUserName(String account) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return null;
            }
            state = conn.createStatement();
            String sql = "select * from user where account = '" + account + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                String name = res.getString("name").toString();
                return name;
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
        return null;
    }

    public int selectUserView(String account) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return 2;
            }
            state = conn.createStatement();
            String sql = "select * from user where account = '" + account + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                int view = Integer.valueOf(res.getString("view").toString());
                return view;
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
        return 2;
    }

    public List<String> selectOtherData(String account) throws SQLException {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return null;
            }
            state = conn.createStatement();
            String selectSql = "SELECT * FROM user where account = '" + account + "' and view = '1';";
            res = state.executeQuery(selectSql);
            if(!res.next()) {
                return null;
            }
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'app' and TABLE_NAME ='" + account + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                int totalList = 0;
                int totalTime = 0;
                sql = "select * from " + account + ";";
                res = state.executeQuery(sql);
                List<String> listRes = new ArrayList<>();
                while(res.next()) {
                    totalList ++;
                    totalTime += Integer.valueOf(res.getString("studytime"));
                }
                listRes.add(String.valueOf(totalList));
                listRes.add(String.valueOf(totalTime));
                return listRes;
            }
            else {
                return null;
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
        return null;
    }

    public void insertRemoteListItem(String account, String content, String time) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return;
            }
            state = conn.createStatement();
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'app' and TABLE_NAME ='" + account + "_list" + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                sql = "insert into " + account + "_list" + " (content, time) values('" + content + "','" + time + "');";
                state.executeUpdate(sql);
            }
            else {
                sql = "CREATE TABLE `app`.`" + account + "_list" + "` (`content` VARCHAR(45) NOT NULL,`time` VARCHAR(45) NOT NULL,PRIMARY KEY (`content`));";
                state.executeUpdate(sql);
                sql = "insert into " + account + "_list" + " (content, time) values('" + content + "','" + time + "');";
                state.executeUpdate(sql);
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
    }

    public List<List<String>> selectRemoteListItem(String account) throws SQLException {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return null;
            }
            state = conn.createStatement();
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'app' and TABLE_NAME ='" + account + "_list" +"';";
            res = state.executeQuery(sql);
            if(res.next()) {
                sql = "select * from " + account + "_list" + ";";
                res = state.executeQuery(sql);
                List<List<String>> listRes = new ArrayList<>();
                while(res.next()) {
                    List<String> temp = new ArrayList<>();
                    temp.add(res.getString("content"));
                    temp.add(res.getString("time"));
                    listRes.add(temp);
                }
                return listRes;
            }
            else {
                sql = "CREATE TABLE `app`.`" + account + "_list" + "` (`content` VARCHAR(45) NOT NULL,`time` VARCHAR(45) NOT NULL,PRIMARY KEY (`content`));";
                state.executeUpdate(sql);
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
        return null;
    }

    public void deleteRemoteListItem(String account, String content) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return;
            }
            state = conn.createStatement();
            String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'app' and TABLE_NAME ='" + account + "_list" + "';";
            res = state.executeQuery(sql);
            if(res.next()) {
                sql = "DELETE FROM "+ account + "_list" + " WHERE (`content` = '" + content + "');";
                state.executeUpdate(sql);
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
    }

    //SQLite
    public void insertUserSign(SQLiteDatabase db, String myAccount, String curDate, int studyTime, String time) {
        String selectTable = "select count(*) from sqlite_master where type='table' and name='" + myAccount + "'";
        try {
            Cursor cursor = db.rawQuery(selectTable, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    String selectSql = "select * from " + "'" + myAccount + "'" + " where time = " + "'" + time + "';";
                    cursor = db.rawQuery(selectSql, null);
                    if(!cursor.moveToNext()) {
                        ContentValues values = new ContentValues();
                        values.put("curdate", curDate);
                        values.put("studytime", studyTime);
                        values.put("time", time);
                        db.insert(myAccount, null, values);
                    }
                }
                else {
                    String createTable = "create table '" + myAccount + "' (curdate text, studytime integer, time text primary key);";
                    db.execSQL(createTable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> selectUserSign(SQLiteDatabase db, String myAccount, String curDate) {
        String selectTable = "select count(*) from sqlite_master where type='table' and name='" + myAccount + "'";
        try {
            Cursor cursor = db.rawQuery(selectTable, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    Log.d(TAG, "exist table");
                }
                else {
                    String createTable = "create table '" + myAccount + "' (curdate text, studytime integer, time text primary key);";
                    db.execSQL(createTable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String selectSql = "select * from " + "'" + myAccount + "'" + " where curdate = " + "'" + curDate + "';";
        try {
            Cursor cursor = db.rawQuery(selectSql, null);
            List<String> res = new ArrayList<>();
            int listCount = 0;
            int studyTime = 0;
            while(cursor.moveToNext()) {
                int studyTimeIndex = cursor.getColumnIndex("studytime");
                studyTime += Integer.valueOf(cursor.getString(studyTimeIndex));
                listCount++;
            }
            res.add(String.valueOf(listCount));
            res.add(String.valueOf(studyTime));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Integer> selectUserTotal(SQLiteDatabase db, String myAccount) {
        String selectSql = "select * from " + "'" + myAccount + "';";
        Cursor cursor = db.rawQuery(selectSql, null);
        int totalList = 0;
        int totalTime = 0;
        int studyTimeIndex = cursor.getColumnIndex("studytime");
        while(cursor.moveToNext()) {
            totalList ++;
            totalTime += Integer.valueOf(cursor.getString(studyTimeIndex));
        }
        List<Integer> res = new ArrayList<>();
        res.add(totalList);
        res.add(totalTime);
        return res;
    }

    public void updateUserData(SQLiteDatabase db, String account, String date, String studyTime, String time) throws Exception {
        String selectSql = "select * from " + "'" + account + "'" + " where curdate = " + "'" + date + "';";
        Cursor cursor = db.rawQuery(selectSql, null);
        List<String> res = new ArrayList<>();
        if(cursor.moveToNext()) {
            int studyTimeIndex = cursor.getColumnIndex("studytime");
            int localStudyTime = Integer.valueOf(cursor.getString(studyTimeIndex).toString());
            int updateStudyTime = localStudyTime > Integer.valueOf(studyTime) ? localStudyTime : Integer.valueOf(studyTime);
            if(updateStudyTime > localStudyTime) {
                String updateSql = "replace into " + account + "(curdate, studytime) values ('" + date + "', '" + updateStudyTime + "');";
                db.execSQL(updateSql);
            }
        }
        else {
            insertUserSign(db, account, date, Integer.valueOf(studyTime), time);
        }
    }

    public List<List<String>> selectLocalData(SQLiteDatabase db, String account) throws SQLException {
        try {
            String sql = "select count(*) from sqlite_master where type='table' and name='" + account + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    sql = "select * from '" + account + "';";
                    cursor = db.rawQuery(sql, null);
                    List<List<String>> listRes = new ArrayList<>();
                    int curDateIndex = cursor.getColumnIndex("curdate");
                    int studyTimeIndex = cursor.getColumnIndex("studytime");
                    int timeIndex = cursor.getColumnIndex("time");
                    while(cursor.moveToNext()) {
                        List<String> temp = new ArrayList<>();
                        temp.add(cursor.getString(curDateIndex));
                        temp.add(cursor.getString(studyTimeIndex));
                        temp.add(cursor.getString(timeIndex));
                        listRes.add(temp);
                    }
                    return listRes;
                }
                else {
                    String createTable = "create table '" + account + "' (curdate text, studytime integer, time text primary key);";
                    db.execSQL(createTable);
                    return null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertListItem(SQLiteDatabase db, String account, String content, String time) throws  SQLException{
        String selectTable = "select count(*) from sqlite_master where type='table' and name='" + account + "_list" + "'";
        try {
            Cursor cursor = db.rawQuery(selectTable, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    ContentValues values = new ContentValues();
                    values.put("content", content);
                    values.put("time", time);
                    db.insert(account + "_list", null, values);
                }
                else {
                    String createTable = "create table '" + account + "_list" + "' (content text primary key, time text);";
                    db.execSQL(createTable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> selectListItem(SQLiteDatabase db, String account) throws SQLException {
        try {
            String sql = "select count(*) from sqlite_master where type='table' and name='" + account + "_list" + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    sql = "select * from '" + account + "_list" + "';";
                    cursor = db.rawQuery(sql, null);
                    List<List<String>> listRes = new ArrayList<>();
                    int contentIndex = cursor.getColumnIndex("content");
                    int timeIndex = cursor.getColumnIndex("time");
                    while(cursor.moveToNext()) {
                        List<String> temp = new ArrayList<>();
                        temp.add(cursor.getString(contentIndex));
                        temp.add(cursor.getString(timeIndex));
                        listRes.add(temp);
                    }
                    return listRes;
                }
                else {
                    String createTable = "create table '" + account + "_list" + "' (content text primary key, time text);";
                    db.execSQL(createTable);
                    return null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteListItem(SQLiteDatabase db, String account, String content, String curDate, String studyTime, String time) {
        String selectTable = "select count(*) from sqlite_master where type='table' and name='" + account + "_list" + "'";
        try {
            Cursor cursor = db.rawQuery(selectTable, null);
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    db.delete(account + "_list", "content = ?", new String[] {content});
                    //update user table
                    String selectUser = "select count(*) from sqlite_master where type='table' and name='" + account + "'";
                    cursor = db.rawQuery(selectUser, null);
                    if(cursor.moveToNext() && cursor.getInt(0) > 0) {
                        ContentValues values = new ContentValues();
                        values.put("curdate", curDate);
                        values.put("studytime", studyTime);
                        values.put("time", time);
                        db.insert(account, null, values);
                    }
                    else {
                        String createTable = "create table '" + account + "' (content text primary key, time text);";
                        db.execSQL(createTable);
                    }
                }
                else {
                    String createTable = "create table '" + account + "' (curdate text, studytime integer, time text primary key);";
                    db.execSQL(createTable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //both
    public void synchronizeRecord(String account, SQLiteDatabase db) throws Exception {
        List<List<String>> listRes = selectRemoteData(account);
        if(listRes != null) {
            for(int i = 0;i < listRes.size();i++){
                List<String> temp = listRes.get(i);
                String date = temp.get(0).toString();
                String studyTime = temp.get(1).toString();
                String time = temp.get(2).toString();
                insertUserSign(db, account, date, Integer.valueOf(studyTime), time);
            }
        }

        List<List<String>> localListRes = selectLocalData(db, account);
        if(localListRes != null) {
            for(int i = 0;i < localListRes.size();i++){
                List<String> temp = localListRes.get(i);
                String date = temp.get(0).toString();
                String studyTime = temp.get(1).toString();
                String time = temp.get(2).toString();
                updateRemoteData(account, date, studyTime, time);
            }
        }
    }

    public void synchronizeListItem( SQLiteDatabase db, String account) throws Exception {
        List<List<String>> listRes = selectListItem(db, account);
        for(int i = 0;i < listRes.size();i++) {
            String content = listRes.get(i).get(0);
            String time = listRes.get(i).get(1);
            insertRemoteListItem(account, content, time);
        }

        List<List<String>> remoteListRes = selectRemoteListItem(account);
        if(remoteListRes != null) {
            for(int i = 0;i < remoteListRes.size();i++){
                List<String> temp = remoteListRes.get(i);
                String content = temp.get(0).toString();
                String time = temp.get(1).toString();
                insertListItem(db, account, content, time);
            }
        }
    }
}

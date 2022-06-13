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
            String sql = "select * from `user` where account ='" + account +"'";
            res = state.executeQuery(sql);
            if(res.next()) {
                return 2; //exist same account user
            }
            else {
                sql = "insert into `user` (account, name, password) values('" + account + "','" + name + "','" + password + "');";
                Log.d(TAG, sql);
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
            Log.d(TAG, sql);
            res = state.executeQuery(sql);
            if(res.next()) {
                sql = "select * from " + account + ";";
                Log.d(TAG, sql);
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

    public int updateUser(String account, String time, String studyTime) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            if(conn == null) {
                return 0;
            }
            state = conn.createStatement();
            String sql = "UPDATE `app`.`"+ account +"` SET `studytime` = '"+ studyTime + "' WHERE (`time` = '"+ time + "')";
            res = state.executeQuery(sql);

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
            Log.d(TAG, sql);
            res = state.executeQuery(sql);
            if(res.next()) {
                int totalList = 0;
                int totalTime = 0;
                sql = "select * from " + account + ";";
                res = state.executeQuery(sql);
                List<String> listRes = new ArrayList<>();
                while(res.next()) {
                    totalList += Integer.valueOf(res.getString("listcount"));
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

    //SQLite
    public void insertUserSign(SQLiteDatabase db, String myAccount, String curDate, int studyTime, String time) {
        ContentValues values = new ContentValues();
        values.put("curdate", curDate);
        values.put("studytime", studyTime);
        values.put("time", time);
        db.insert(myAccount, null, values);
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
                    String createTable = "create table '" + myAccount + "' (curdate text primary key, studytime integer, time text);";
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

    //both
    public void synchronizeRecord(String account, SQLiteDatabase db) throws Exception {
        List<List<String>> listRes = selectRemoteData(account);
        if(listRes != null) {
            for(int i = 0;i < listRes.size();i++){
                List<String> temp = listRes.get(i);
                String date = temp.get(0).toString();
                String studyTime = temp.get(2).toString();
                String time = temp.get(3).toString();
                updateUserData(db, account, date, studyTime, time);
            }
        }
    }
}

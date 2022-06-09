package com.mobile_application.utils;

import com.mobile_application.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {
    public boolean select(String account, String password) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            state = conn.createStatement();
            String sql = "select * from user where account = " + account + " and password = " + password;
            res = state.executeQuery(sql);
            if(res.next()) {
                return true;
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
        return false;
    }

    public int add(String account, String name, String password) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet res = null;
        try {
            conn = JDBCUtils.getConn();
            state = conn.createStatement();
            String sql = "select * from user where account = account";
            res = state.executeQuery(sql);
            if(res.next()) {
                return 0; //exist same account user
            }
            else {
                sql = "insert into user (account, name, password) values(" + account + "," + name + "," + password + ");";
                int r = state.executeUpdate(sql);
                if(r != 0){
                    return 1; //insert success
                }
                else{
                    return 2;//insert fail
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
        return 2;
    }
}

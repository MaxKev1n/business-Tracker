package com.mobile_application.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtils {
    private static String driver = "com.mysql.jdbc.Driver";

    public static Connection getConn(){

        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            String url = "jdbc:mysql://ip:port/db_name?useSSL=False";
            connection = DriverManager.getConnection(url, "username", "pasword");

        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
}

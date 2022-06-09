package com.mobile_application.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtils {
    private static String driver = "com.mysql.jdbc.Driver";

    public static Connection getConn(){

        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            String url = "jdbc:mysql://150.158.15.138:3306/app?useSSL=False";
            connection = DriverManager.getConnection(url, "root", "njust");

        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
}

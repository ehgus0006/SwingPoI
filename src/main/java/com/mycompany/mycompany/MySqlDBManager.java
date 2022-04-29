/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mycompany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LG
 */
public class MySqlDBManager extends KdhDBManager {
    
//    protected static KdhDBManager _instance;

    public static KdhDBManager getInstance(){
        System.out.println("In MySqlDBManager getInstance() called");

        if(_instance==null)
            _instance = new MySqlDBManager();
        return _instance;
    }
    
    public MySqlDBManager(){
//        super();
        System.out.println("In MySqlDBManager MySqlDBManager constructor() called");
        _instance = this;
    }

    public void setupDriver() {
        System.out.println("In MySqlDBManager setupDriver() called");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("드라이버 적재 성공");
                
        } catch (Exception e) {

        }
    }

    public void getConnection() {
        System.out.println("In MySqlDBManager getConnection() called");
	String url = "jdbc:mysql://localhost:3306/projecta?useSSL=false";
//        String url = "jdbc:mysql://127.0.0.1:3306/projecta";
        String id = "root";
        String password = "rlaehgus12!";
        
        try {
         
            _con = (Connection)DriverManager.getConnection(url, id, password);
            System.out.println("데이터베이스 연결 성공");
        } 
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("연결에 실패하였습니다.");
        }
    }
    
    

}

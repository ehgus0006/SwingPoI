/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mycompany;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author LG
 */
public abstract class KdhDBManager {

    protected static KdhDBManager _instance;
    protected static Connection _con;

    public static KdhDBManager getInstance() {
        System.out.println("In MySqlDBManager getInstance() called");

        if (_instance == null) {
            _instance = new MySqlDBManager();
        }
        return _instance;
    }

    public KdhDBManager() {
        System.out.println("In KdhDBManager KdhDBManager constructor() called");

        try {
            setupDriver();

        } catch (Exception e) {
        } finally {
        }

        try {
            getConnection();
        } catch (Exception e) {
        }
    }

    public abstract void setupDriver();

    public abstract void getConnection();

    public ArrayList<String[]> executeQueries(String sql, int count) {
        ArrayList<String[]> al = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = _con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] values = new String[count];
                for (int i = 0; i < count; i++) {
                    values[i] = rs.getString(i + 1);
                }
                al.add(values);
            }
        } catch (Exception e) {

        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }

        return al;
    }

    public void executeQueries(String sql, JComboBox combo, int viewIndex) {
        Statement stmt = null;
        try {
            combo.removeAllItems();
            stmt = _con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int count = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] values = new String[count];
                for (int i = 0; i < count; i++) {
                    values[i] = rs.getString(i + 1);
                }
                KeyValues obj = new KeyValues(values, viewIndex);
                combo.addItem(obj);
            }
        } catch (Exception e) {

        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    void executeQueries(String sql, JTable jTableTbl) {
        System.out.println("들어옴");
        Statement stmt = null;
        try {
            DefaultTableModel tableModel = (DefaultTableModel)jTableTbl.getModel();
            tableModel.setRowCount(0);
            
            stmt = _con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            
            int count = rs.getMetaData().getColumnCount();
            System.out.println("colume Count" + count);
                    
            while (rs.next()) {
                String[] values = new String[count];
                for (int i = 0; i < count; i++) {
                    values[i] = rs.getString(i + 1);
                   
                    System.out.println("value[i] 는"+values[i]);
                }
                
                tableModel.addRow(values);
            }
        } catch (Exception e) {

        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    void execute(String sql) {
        Statement stmt = null;
        try {
              stmt = _con.createStatement();
              stmt.execute(sql);

        } catch (Exception e) {
        }finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }
}

package com.mycompany.mycompany;
//import com.sun.rowset.CachedRowSetImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import javax.sql.rowset.CachedRowSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author YongHwanSeo
 */
public abstract class DBManager {
    protected static DBManager _instance;
    protected static Connection _con = null;
    protected static boolean _isError = false;
    public String errorMessage ="";
    private long lastCallTime = 0;
    protected Timer _timer_checkConnection = null;
    private int sessionTime = 1800000;// 30분, 60000;// 1분
    
    public static DBManager getInstance() {
//        try {
//            if (_instance == null) {
//                _instance = new DBManager();
//            }
//        } catch (Exception ex) {
//            System.out.println("SQLServerDBManager(0) " + ex.getMessage());
//        }

        return _instance;
    }

    public DBManager() { //throws Exception {
        try {
            _timer_checkConnection = new Timer(sessionTime, new ActionListener() { // sessionTime = 30분
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                System.out.println("DB connect Check");
                                if(System.currentTimeMillis() - lastCallTime < sessionTime) { 
                                    lastCallTime = System.currentTimeMillis();
                                    return;
                                }
                                lastCallTime = System.currentTimeMillis();
                                if(_con.isValid(2000)) {
                                    freeConnection(_con);
                                    System.out.println("    DB connection closed by Timer !");
                                }
                            } catch (Exception e1) {
                                System.out.println("DB connection check : " + e1.getMessage());
                            }                            
                        }
                    });
        } catch (Exception d) {
            System.out.println("DB connection check timer : " + d.getMessage());
        }
        // Connection을 초기화
        try {
            setupDriver();
        } catch (Exception e) {
            System.out.println("DBManager(0) - setupDriver() : " + e.getMessage());
            _isError = true;
//            throw e;
            return;
        }
        try {
            getConnection();
        } catch (Exception e) {
            System.out.println("DBManager(1) - getConnection() : " + e.getMessage());
            _isError = true;
//            throw e;
            return;
        }

    }
    
    public abstract void setupDriver() throws Exception;
    public abstract Connection getConnection() throws Exception;

    public boolean isConnected()  {
        _isError = true;
        try {
            lastCallTime = System.currentTimeMillis();
            if(_con.isValid(2000)) {
                System.out.println("Connection is Still valid!!");
                _isError = false;
                return true;
            } else { // 세션 닫혔을경우 강제 종료 돌입
//                JOptionPane.showMessageDialog(Agriwork.agriwork,
//                    "프로그램을 재시동 하세요.",
//                    "데이터베이스 세션 종료",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return false;
            }
                
        } catch (Exception e) {
            _isError = true;
        }
        
//        try {  // 자동 재접속 시도
//            System.out.println("Trying getConnection..");
//            getConnection();
//            if(_con.isValid(2000)) {
//                _isError = false;
//                return true;
//            }
//        } catch (Exception e) {
//            _isError = true;
//        }
        return false;
    }
    
    public boolean execute(String sql)
    {
        _isError = false;
        if(isConnected()==false)
            return false;
        
        Statement stmt=null;
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return false;
        }
        
        boolean ret=false;
        try {
            ret = stmt.execute(sql);
        } catch (Exception e) {
            _isError = true;
            ret = false;
        } finally {
            freeConnection(stmt);
        }
        return ret;
    }
    
    public int executeUpdate(String sql, String 작업종류)
    {
        _isError = false;
        if(isConnected()==false) {
//            errorMessage = "No Connection";
            _isError = true;
            return -1;
        }
        
        Statement stmt=null;
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
//            errorMessage = "#1-" + e.getMessage();
            _isError = true;
            return -1;
        }

        int ret=0;
        
        
        try {
            ret = stmt.executeUpdate(sql);
        } catch (Exception e) {
//            errorMessage = "#2-" + e.sgetMessage();
            _isError = true;
            ret = -1;
        } finally {
            freeConnection(stmt);
        }
        return ret;
    }

    public ResultSet executeQuery(String sql)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return null;
        
        Statement stmt;        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return null;
        }

        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
        } catch (Exception e) {
            _isError = true;
        } finally {
            freeConnection(stmt);
        }
        return ret;
    }

    public String[] executeQuery(String sql, int count)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return null;

        Statement stmt;        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return null;
        }
        
        ResultSet ret=null;
        String[] values=null;
        try {            
            ret = stmt.executeQuery(sql);
            if(ret.next())
            {
                values = new String[count];
                for(int i=0; i<count;i++)
                    values[i] = ret.getString(i+1);
            }
        } catch (Exception e) {
            _isError = true;
        } finally {
            freeConnection(stmt, ret);
        }
        return values;
    }
    
    public ArrayList<String[]> executeQueries(String sql, int count)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return null;

        Statement stmt;        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return null;
        }
        
        ResultSet ret=null;
        ArrayList<String[]> al = new ArrayList<String[]>();
        try {            
            ret = stmt.executeQuery(sql);
            while(ret.next())
            {
                String[] values = new String[count];
                for(int i=0; i<count;i++)
                    values[i] = ret.getString(i+1);
                al.add(values);
            }
        } catch (Exception e) {
            _isError = true;
            System.out.println(e.getMessage());
        } finally {
            freeConnection(stmt, ret);
        }
        return al;
    }
    
    public void executeQuery(String sql, JTable table)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false) {
            return;
        }

        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return;
        }
        
        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
            
            int columnCount = ret.getMetaData().getColumnCount();
            DefaultTableModel tbModel = (DefaultTableModel) table.getModel();
            //To remove previously added rows
            tbModel.setRowCount(0);
//            while(table.getRowCount() > 0) 
//            {
//                tbModel.removeRow(0);
//            }
            
//            //Create new table model
//            tbModel = new DefaultTableModel();
//            //Retrieve meta data from ResultSet
//            ResultSetMetaData metaData = ret.getMetaData();
//            //Get all column names from meta data and add columns to table model
//            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
//                tbModel.addColumn(metaData.getColumnLabel(columnIndex));
//            }
            
            
            Object[] row = new Object[columnCount];
            while(ret.next())
            {  
//                Object[] row = new Object[columns];
                for (int i = 1; i <= columnCount; i++)
                {  
                    row[i - 1] = ret.getString(i);//getObject(i);//getString(i);
                }
                tbModel.addRow(row);
            }
//            //Now add that table model to your table and you are done :D
//            table.setModel(tbModel);
        } catch (Exception e) {
            _isError = true;
            System.out.println(e);
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }
    
    public void executeQuery(String sql, JTable table, boolean isModelRefresh)
    {
        _isError = false;
        if(isConnected()==false)
            return;
        
        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return;
        }

        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
            
            int columnCount = ret.getMetaData().getColumnCount();
            DefaultTableModel tbModel = (DefaultTableModel) table.getModel();
            //To remove previously added rows
            tbModel.setRowCount(0);
//            while(table.getRowCount() > 0) 
//            {
//                tbModel.removeRow(0);
//            }
            
            //Create new table model
            tbModel = new DefaultTableModel();
            //Retrieve meta data from ResultSet
            ResultSetMetaData metaData = ret.getMetaData();
            //Get all column names from meta data and add columns to table model
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                tbModel.addColumn(metaData.getColumnLabel(columnIndex));
            }
            
            
            Object[] row = new Object[columnCount];
            while(ret.next())
            {  
//                Object[] row = new Object[columns];
                for (int i = 1; i <= columnCount; i++)
                {  
                    row[i - 1] = ret.getObject(i);
                }
                tbModel.addRow(row);
            }
            //Now add that table model to your table and you are done :D
            table.setModel(tbModel);
        } catch (Exception e) {
            _isError = true;
            System.out.println(e);
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }

    public void executeQueryWithCheckBox(String sql, JTable table, int whereCheckBox)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return;

        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return;
        }
        
        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
            
            int columnCount = ret.getMetaData().getColumnCount();
            DefaultTableModel tbModel = (DefaultTableModel) table.getModel();
            tbModel.setRowCount(0);          
            
            Object[] row = new Object[columnCount];
            Boolean checked = new Boolean(false);
            if(whereCheckBox == 0) {
                row[0] = checked;
                while(ret.next())
                {                    
                    for (int i = 1; i < columnCount; i++)
                    {  
                        row[i] = ret.getString(i+1);
                    }
                    tbModel.addRow(row);
                }
            } else {
                while(ret.next())
                {
                    for (int i = 1; i <= columnCount; i++)
                    {  
                        if((i-1)!=whereCheckBox)
                            row[i-1] = ret.getObject(i);
                        else
                            row[i-1] = checked;
                    }                    
                    tbModel.addRow(row);
                }
            }
//            //Now add that table model to your table and you are done :D
//            table.setModel(tbModel);
        } catch (Exception e) {
            _isError = true;
            System.out.println(e.getMessage());
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }
    public void executeQueryWithCheckBox(String sql, JTable table, int whereCheckBox, String trueValue)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return;

        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return;
        }
        
        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
            int columnCount = ret.getMetaData().getColumnCount();
            DefaultTableModel tbModel = (DefaultTableModel) table.getModel();
            tbModel.setRowCount(0);
            Object[] row = new Object[columnCount];
            Boolean checked = new Boolean(true);
            Boolean unChecked = new Boolean(false);
            if(whereCheckBox == 0) {

                while(ret.next())
                {                    
                    if(ret.getString(1).equals(trueValue))
                        row[0] = checked;
                    else
                        row[0] = unChecked;
                    for (int i = 1; i < columnCount; i++)
                    {  
                        row[i] = ret.getString(i+1);
                    }
                    tbModel.addRow(row);
                }
            } else {
                while(ret.next())
                {
                    for (int i = 1; i <= columnCount; i++)
                    {  
                        if((i-1)!=whereCheckBox)
                            row[i-1] = ret.getObject(i);
                        else {
                            if(ret.getObject(i).toString().equals(trueValue))
                                row[i-1] = checked;
                            else
                                row[i-1] = unChecked;                            
                        }
                    }                    
                    tbModel.addRow(row);
                }
            }
//            //Now add that table model to your table and you are done :D
//            table.setModel(tbModel);
        } catch (Exception e) {
            _isError = true;
            System.out.println(e.getMessage());
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }    
//    public void executeQueryWithCheckBox(String sql, JComboBox combo)
//    {
//        _isError = false;
//        if(isConnected()==false)
//            return;
//        
//        ResultSet ret=null;
//        Statement stmt;        
//        
//        try {
//            stmt = _con.createStatement();
//        } catch (Exception e) {
//            _isError = true;
//            return;
//        }
//        try {            
//            ret = stmt.executeQueryWithCheckBox(sql);
//            combo.removeAllItems();
//            while(ret.next())
//            {
//                combo.addItem(ret.getString(1));
//            }
//        } catch (Exception e) {
//            _isError = true;
//        } finally {
//            freeConnection(stmt, ret);
//        }
//        return;
//    }
    
    public void executeQuery(String sql, JComboBox combo) // 지역, 기관
    {
//        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return;

        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return;
        }
        
        ResultSet ret=null;
        try {
            combo.removeAllItems();
            ret = stmt.executeQuery(sql);            
            while(ret.next())
            {
//                KeyValue obj = new KeyValue(ret.getString(1), ret.getString(2));
//                combo.addItem(obj);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            _isError = true;
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }

    public void executeQuery(String sql, JComboBox combo, int count, int keyIndex, int viewIndex)
    {
        System.out.println(sql);
        _isError = false;
        if(isConnected()==false)
            return;
        
        Statement stmt;        
        
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            _isError = true;
            return;
        }
        
        ResultSet ret=null;
        try {            
            ret = stmt.executeQuery(sql);
            combo.removeAllItems();
            while(ret.next())
            {
                String[] values = new String[count];
                for(int i=0; i<count;i++)
                    values[i] = ret.getString(i+1);
//                KeyValues obj = new KeyValues(values, keyIndex, viewIndex);
//                combo.addItem(obj);
            }

        } catch (Exception e) {
            _isError = true;
            System.out.println(e.getMessage());
        } finally {
            freeConnection(stmt, ret);
        }
        return;
    }
    
    public int executeScalarQuery(String sql)
    {
        _isError = false;
        if(isConnected()==false) {
            return -1;
        }
        
        ResultSet ret=null;
        Statement stmt=null;
        try {
            stmt = _con.createStatement();
        } catch (Exception e) {
            _isError = true;
            return -1;
        }
        
        int scalarValue=-1;
        try {
            ret = stmt.executeQuery(sql);
            if(ret.next())
                scalarValue = Integer.parseInt(ret.getString(1));
//            scalarValue = ret.getInt(1);
        } catch (Exception e) {
            _isError = true;
        } finally {
            freeConnection(stmt, ret);
        }
        return scalarValue;
    }
    
    public void freeConnection(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(1) : " + e.getMessage());
        }
    }

    public void freeConnection(Connection con, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(2) : " + e.getMessage());
        }
    }

    public void freeConnection(Connection con, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(3) : " + e.getMessage());
        }
    }

    public void freeConnection(Connection con, Statement stmt) {
        try {
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(4) : " + e.getMessage());
        }
    }

    public void freeConnection(Connection con) {
        try {
            if (con != null) con.close();
            System.out.println("Connection Closed!!");
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(5) : " + e.getMessage());
        }
        try {            
            System.out.println("timer Stopped!!");
            _timer_checkConnection.stop();
        } catch (Exception e) {
        }
    }

    public void freeConnection(Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(2) : " + e.getMessage());
        }
    }

    public void freeConnection(Statement stmt) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(6) : " + e.getMessage());
        }
    }

    public void freeConnection(PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(2) : " + e.getMessage());
        }
    }
    
    public void freeConnection(PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(7) : " + e.getMessage());
        }
    }

    public void freeConnection(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.out.println("DBManager - freeConnection(8) : " + e.getMessage());
        }
    }
    
    public void freeConnection() {
        freeConnection(_con);
    }
}
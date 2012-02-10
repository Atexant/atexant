package ru.tsu.inf.atexant.storages;

import ru.tsu.inf.atexant.AtexantApp;

import java.sql.*;
/**
 *
 * @author sufix
 */
public class SQLAccess {
    private volatile static SQLAccess instance;
    private Connection connection;
    PreparedStatement statement;
    
    private SQLAccess() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        
        
        String host = AtexantApp.localProps.getProperty("mysql_host");
        String user = AtexantApp.localProps.getProperty("mysql_user");
        String password = AtexantApp.localProps.getProperty("mysql_password");
        String dbname = AtexantApp.localProps.getProperty("mysql_dbname");
        
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?user=" + user + "&password=" + password);
    }
    
    public ResultSet executeSql(String sql) throws Exception {
        return executeSql(sql, null);
    }
    
    public ResultSet executeSql(String sql, String[] params) throws Exception {
        return executeSql(sql, params, false);
    }
    
    public ResultSet executeSql(String sql, String[] params, boolean isDataModifying) throws Exception {
        synchronized (this) {
            statement = connection.prepareStatement(sql);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setString(i+1, params[i]);
                }
            }
            
            if (isDataModifying) {
                statement.executeUpdate();
                return null;
            }
            
            return statement.executeQuery();
        }
    }
    
    public void close() {
        try {
            statement.close();
            connection.close();
        } catch(Exception e) {
            
        }
    }
 
    public static SQLAccess getInstance() throws Exception {
        if (instance == null) {
            synchronized (SQLAccess.class) {
                if (instance == null) {
                    instance = new SQLAccess();
                }
            }
        }
        
        return instance;
    }

    public static SQLAccess createInstance() throws Exception {
        return new SQLAccess();
    }
    
}

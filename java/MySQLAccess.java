
import java.sql.*;
/**
 *
 * @author sufix
 */
public class MySQLAccess {
    private volatile static MySQLAccess instance;
    private Connection connection;
    PreparedStatement statement;
    
    private MySQLAccess() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        
        String host = "localhost";
        String user = "root";
        String password = "76d18e98n";
        String dbname = "atexant";
        
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?user=" + user + "&password=" + password);
    }
    
    public ResultSet executeSql(String sql) throws Exception {
        return executeSql(sql, null);
    }
    
    public ResultSet executeSql(String sql, String[] params) throws Exception {
        return executeSql(sql, null, false);
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
 
    public static MySQLAccess getInstance() throws Exception {
        if (instance == null) {
            synchronized (MySQLAccess.class) {
                if (instance == null) {
                    instance = new MySQLAccess();
                }
            }
        }
        
        return instance;
    }
    
    
}

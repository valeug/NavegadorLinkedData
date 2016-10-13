package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCMySQLConnection {

	private static JDBCMySQLConnection instance = new JDBCMySQLConnection();
    public static final String URL = "jdbc:mysql://localhost:3306/tesis_db";
    public static final String USER = "root";
    public static final String PASSWORD = "secret";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
    
    private JDBCMySQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
        
    private Connection createConnection() {
    	 
        Connection connection = null;
        try {
            //Step 3: Establish Java MySQL connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }   
    
    public static JDBCMySQLConnection getInstance()   {
        return instance;
    }
    
    public static Connection getConnection() {
        return instance.createConnection();
    }
    
    
}

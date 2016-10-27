package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.JDBCMySQLConnection;
import model.Class;

public class ClassDAO {
	
	static Connection myConnec;
    static Statement myStat;
	
    private static Connection getConnection() {        
    	Connection con = JDBCMySQLConnection.getInstance().getConnection();		
        return con;
    }

	public static List<Class> getAllClasses(){
		
		String query = "SELECT * FROM class";
    	List<Class> classList = new ArrayList<Class>();
    	Class c = null;

    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				c = new Class();
				c.setIdClass(myres.getInt("id_class"));
				c.setName(myres.getString("name"));
				c.setUri(myres.getString("uri"));
				c.setIdDataset(myres.getInt("id_dataset"));				
				classList.add(c);
				//System.out.println("Nombre: " + myres.getString("name"));
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return classList;		
	}
	
	public static List<Class> getAllClassesByDataset(int idDataset){
		
		String query = "SELECT * FROM class WHERE id_dataset = " + idDataset;
    	List<Class> classList = new ArrayList<Class>();
    	Class c = null;

    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				c = new Class();
				c.setIdClass(myres.getInt("id_class"));
				c.setName(myres.getString("name"));
				c.setUri(myres.getString("uri"));
				c.setIdDataset(myres.getInt("id_dataset"));				
				classList.add(c);
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return classList;		
	}
	
	public static List<String> getAllClassesUrisByDataset(int idDataset){
		
		String query = "SELECT * FROM class WHERE id_dataset = " + idDataset;
    	List<String> uris = new ArrayList<String>();
    	String aux = null;

    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				aux = myres.getString("uri");			
				uris.add(aux);
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return uris;		
	}
	
}

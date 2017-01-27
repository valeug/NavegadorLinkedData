package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.JDBCMySQLConnection;
import model.Class;
import model.Property;

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
	
	public static void storeClassxProperty(int idClass, int idProperty){
		
    	String query = " SELECT id_class_x_property "+
					" FROM class_x_property " + 
					" ORDER BY id_class_x_property DESC LIMIT 1 ";
    		
    	int idMax=-1;
		try {    		
			myConnec = getConnection();
			myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				idMax = myres.getInt("id_class_x_property");
				System.out.println("MAX class_x_property ID: " + idMax);
				break;
			}
			myres.close();			
			myConnec.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	
    	try {    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();			
    		
    		if(idMax==-1) idMax = 500;
    		else idMax += 1; //el id es 1 mas que el mayor id en la BD
		
    		//build query
    		String sql = "";

			sql = "INSERT INTO class_x_property " + 
					" (id_class_x_property, id_class, id_property) " +
					" values ('" + idMax +"', '" + idClass +"', '" + idProperty +"') ";
	
			System.out.println("query:\n"+sql);
			
			//Execute SQL query   
			myStat.executeUpdate(sql);
			System.out.println("Insert complete.");
			myConnec.close();			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int searchClass(String classUri){ //return dataset
		
		System.out.println("classUri: " + classUri);
		
		String query =  " SELECT id_dataset "+
					  	" FROM class" +
					  	" WHERE uri= \"" + classUri +"\""+
					  	";";
		
    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);
			
			int dataset = 0;
			
			while(myres.next()){
				dataset = myres.getInt("id_dataset");
				break;
			}
			
			myres.close();			
			myConnec.close();
			
			return dataset;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
		return 0;
	}
}

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

public class PropertyDAO {

	static Connection myConnec;
    static Statement myStat;
	
    private static Connection getConnection() {        
    	Connection con = JDBCMySQLConnection.getInstance().getConnection();		
        return con;
    }
    
    public static List<Property> getAllPropertiesByClass(int classId){
		
		String query = "SELECT * "+
						"FROM property "+ 
						"WHERE id_property IN (SELECT id_property "+
											"  FROM class_x_property" + 
											"  WHERE id_class = "+classId+")";
		
    	List<Property> pList = new ArrayList<Property>();
    	Property p = null;

    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				
				p = new Property();
				p.setId(myres.getInt("id_property"));
				p.setName(myres.getString("name"));
				p.setUri(myres.getString("uri"));
				p.setIs_mapping(myres.getInt("is_mapping"));	
				p.setTarget(myres.getInt("target"));
				pList.add(p);				
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return pList;		
	}
    
    public static List<Property> getAllPropertiesByClassUri(String uri){
		
		String query = "SELECT * "+
						"FROM property "+ 
						"WHERE id_property IN (SELECT id_property "+
											"  FROM class_x_property" + 
											"  WHERE id_class IN (SELECT id_class "+
																	"  FROM class" + 
																	"  WHERE uri = \"" + uri + "\" ))";
		
		System.out.println("QUERY DAO: " + query);
    	List<Property> pList = new ArrayList<Property>();
    	Property p = null;

    	try {    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				
				p = new Property();
				p.setId(myres.getInt("id_property"));
				p.setName(myres.getString("name"));
				p.setUri(myres.getString("uri"));
				p.setIs_mapping(myres.getInt("is_mapping"));	
				p.setTarget(myres.getInt("target"));
				pList.add(p);
				System.out.println("DAO - Nombre: " + p.getName());
				//System.out.println("Nombre: " + myres.getString("name"));
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return pList;		
	}
}

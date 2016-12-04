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
    
    public static void storeProperty(Property p){
		//SELECT MAX(id) FROM tablename;
    	String query = "SELECT id_property "+
					"FROM property " + 
					"ORDER BY id_property DESC LIMIT 1";
    		
    	int idMax=-1;
		try {    		
			myConnec = getConnection();
			myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);
			
			//System.out.println("myres: " +myres);
			while(myres.next()){

				idMax = myres.getInt("id_property");
				System.out.println("MAX property ID: " + idMax);
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
    		
    		p.setId(idMax);
			
    		//build query
    		String sql = "";
    		if(p.getIs_mapping()!=-1){
    			sql = "INSERT INTO property " + 
						" (id_property, name, description, uri, is_mapping, target) " +
						" values ('" + p.getId() +"', '" + p.getName() +"', '" + p.getDescription() +"', '" + p.getUri() +"', '" + p.getIs_mapping() +"', '" + p.getTarget() +"') ";
    		}
    		else {
    			sql = "INSERT INTO property " + 
						" (id_property, name, description, uri) " +
						" values ('" + p.getId() +"', '" + p.getName() +"', '" + p.getDescription() +"', '" + p.getUri() +"') ";
			
    		}			
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
}

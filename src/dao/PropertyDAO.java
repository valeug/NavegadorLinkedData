package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.JDBCMySQLConnection;
import model.Class;
import model.Dataset;
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
				//p.setConsolidated(myres.getInt("consolidated"));
				
				//AQUI BUSCAR EL VALOR DE CONSOLIDATED DE CADA CLASE X PROPIEDADES
		    	query = "SELECT consolidated "+
						"FROM class_x_property "+ 
						"WHERE id_class="+ classId+" and id_property = "+ p.getId();
		    	
		    	try {
		    		
		    		myConnec = getConnection();
		    		myStat = myConnec.createStatement();
					ResultSet myres2 = myStat.executeQuery(query);

					while(myres2.next()){
						p.setConsolidated(myres2.getInt("consolidated"));
						break;									
					}
					myres2.close();					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
				p.setConsolidated(myres.getInt("consolidated"));
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
    
    public static int storeProperty(Property p){
		//SELECT MAX(id) FROM tablename;
    	String query = "SELECT id_property "+
					" FROM property " + 
					" ORDER BY id_property DESC LIMIT 1";
    		
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
						" (id_property, name, description, uri, is_mapping, target, consolidated) " +
						" values ('" + p.getId() +"', '" + p.getName() +"', '" + p.getDescription() +"', '" + p.getUri() +"', '" + p.getIs_mapping() +"', '" + p.getTarget() +"', '" + p.getConsolidated() +"') ";
    		}
    		else {
    			sql = "INSERT INTO property " + 
						" (id_property, name, description, uri, consolidated) " +
						" values ('" + p.getId() +"', '" + p.getName() +"', '" + p.getDescription() +"', '" + p.getUri() +  "', '"  + p.getConsolidated() +"') ";
			
    		}			
			System.out.println("query:\n"+sql);
			
			//Execute SQL query   
			myStat.executeUpdate(sql);
			System.out.println("Insert complete.");

			myConnec.close();
			
			return idMax;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return -1;
	}
    
    
    public static List<Property> getAllPropertyGroupedByDataset(){
		//SELECT MAX(id) FROM tablename;
    	String query = "SELECT id_dataset FROM dataset";
    	
    	System.out.println("QUERY DAO: " + query);
    	List<String> datasetList = new ArrayList<String>();
		try {    		
			myConnec = getConnection();
			myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);
			
			int cant =0;
			while(myres.next()){
				int s = myres.getInt("id_dataset");	
				System.out.println("idddd: " + s);
				datasetList.add(""+s);
				cant++;
			}
			System.out.println("cant: "+ cant);
			myres.close();			
			myConnec.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		System.out.println("dataset size: "+datasetList.size());
		
		List<Property> pList = new ArrayList<Property>();
		for(int i=0; i<datasetList.size(); i++){	
			query = " SELECT *" +
					" FROM property" +
					" WHERE id_property IN (SELECT id_property " +
						 					" FROM class_x_property" +
						 					" WHERE id_class IN (SELECT id_class" +
						 										" FROM class" +
						 										" WHERE id_dataset = " + datasetList.get(i) +                                     
						 										" ));";
			
	    	try {    		
	    		myConnec = getConnection();
				myStat = myConnec.createStatement();
				ResultSet myres = myStat.executeQuery(query);
				
				while(myres.next()){
					Property p = new Property();
					p.setId(myres.getInt("id_property"));
					p.setName(myres.getString("name"));
					p.setUri(myres.getString("uri"));
					p.setIs_mapping(myres.getInt("is_mapping"));	
					p.setTarget(myres.getInt("target"));
					
					
					p.setConsolidated(myres.getInt("consolidated"));
					p.setDataset(datasetList.get(i).charAt(0)-'0');
					pList.add(p);

				}
				myres.close();			
				myConnec.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    	return pList;
	}
    
    public static void updateConsolidate(int idProperty, int idDataset, int flag){
		
		String query = "  UPDATE class_x_property " +
						" SET consolidated=" + flag +
						" WHERE id_property="+idProperty +" AND id_class IN ( SELECT id_class " + 
																		     " FROM class "+
																		     " WHERE id_dataset="+idDataset+
																			" )";
		
		try {
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();    		
    		myStat.executeUpdate(query);
    		
			System.out.println("Updated!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

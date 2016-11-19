package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.JDBCMySQLConnection;
import model.Dataset;

public class DatasetDAO {
	
	static Connection myConnec;
    static Statement myStat;
	
    private static Connection getConnection() {        
    	Connection con = JDBCMySQLConnection.getInstance().getConnection();		
        return con;
    }
	
    
    public static Dataset getDatasetById(int id){
    	
    	String query = "SELECT * "
    				+ " FROM dataset"
    				+ " WHERE id_dataset=" + id;
    	
    	Dataset dataset = null;
    	
    	try {
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);
			
			while(myres.next()){
				dataset = new Dataset();
				dataset.setId(myres.getInt("id_dataset"));
				dataset.setName(myres.getString("name"));
				dataset.setDescription(myres.getString("description"));
				dataset.setSparqlEndpoint(myres.getString("sparql_endpoint"));
				dataset.setUri(myres.getString("uri"));
				dataset.setStatus(myres.getInt("status"));			
				//System.out.println("Nombre: " + myres.getString("name"));
			}
			myres.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return dataset;
    }

    public static List<Dataset> getAllDatasets(){
    	
    	String query = "SELECT * FROM dataset";
    	List<Dataset> datasetList = new ArrayList<Dataset>();
    	Dataset dataset = null;

    	try {
    		
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);

			while(myres.next()){
				dataset = new Dataset();
				dataset.setId(myres.getInt("id_dataset"));
				dataset.setName(myres.getString("name"));
				dataset.setDescription(myres.getString("description"));
				dataset.setSparqlEndpoint(myres.getString("sparql_endpoint"));
				dataset.setStatus(myres.getInt("status"));
				dataset.setUri(myres.getString("uri"));
				datasetList.add(dataset);
				System.out.println("Nombre: " + myres.getString("name"));
			}
			myres.close();
			
			myConnec.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return datasetList;
    }
    
    public static List<Dataset> getDatasetByStatus(int status){
    	
    	String query = "SELECT * "
    				+ " FROM dataset"
    				+ " WHERE status="+status;
    	List<Dataset> datasetList = new ArrayList<Dataset>();
    	Dataset dataset = null;
    	
    	try {
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();
			ResultSet myres = myStat.executeQuery(query);
			
			while(myres.next()){
				dataset = new Dataset();
				dataset.setId(myres.getInt("id_dataset"));
				dataset.setName(myres.getString("name"));
				dataset.setDescription(myres.getString("description"));
				dataset.setSparqlEndpoint(myres.getString("sparql_endpoint"));
				dataset.setStatus(myres.getInt("status"));
				dataset.setUri(myres.getString("uri"));
				datasetList.add(dataset);				
				//System.out.println("Nombre: " + myres.getString("name"));
			}
			myres.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return datasetList;
    }
    
    //update status to 0 or 1, indicated in the parameter
    public static void updateStatusById(int [] ids, int status){
    	//(id1,id2)
    	String idCad ="(";
    	for(int i=0; i<ids.length; i++){
    		if(i>0) idCad += ",";
    		idCad += ids[i];
    	}
    	idCad += ")";
    	
    	String sql = "UPDATE dataset"
    			+ " SET status="+status
    			+ " WHERE id_dataset IN "+ idCad;
    	
    	try {
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();    		
    		myStat.executeUpdate(sql);
    		
			System.out.println("Updated!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
        
    public static void updateOthersStatusById(int [] ids, int status){
    	//(id1,id2)
    	String idCad ="(";
    	for(int i=0; i<ids.length; i++){
    		if(i>0) idCad += ",";
    		idCad += ids[i];
    	}
    	idCad += ")";
    	
    	String sql = "UPDATE dataset"
    			+ " SET status="+status
    			+ " WHERE id_dataset NOT IN "+ idCad;
    	
    	try {
    		myConnec = getConnection();
    		myStat = myConnec.createStatement();    		
    		myStat.executeUpdate(sql);
    		
			System.out.println("Updated!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    //update status to 1
    public static void enableDatasets(int [] ids){
    	
    	updateStatusById(ids, 1);
    	updateOthersStatusById(ids,0);
    	
    }
    
}

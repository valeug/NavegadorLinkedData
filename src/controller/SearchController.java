package controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import dao.DatasetDAO;
import model.Concept;
import model.Dataset;
import sparqlendpoint.Bio2RdfEndpoint;
import sparqlendpoint.BioportalEndpoint;
import sparqlendpoint.DbpediaEndpoint;

public class SearchController {

	/*
	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		//dbpedia
		//bioportal
		String input = request.getParameter("concept");
		
		//DbpediaEndpoint.JenaSparqlQuery(input);
		//BioportalEndpoint.JenaSparqlQuery(input);
		Bio2RdfEndpoint.JenaSparqlQuery(input);
		
		return term;
	}
	*/
	
	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		String input = request.getParameter("concept");
		
		//int searchType = request.getParameter("optradio").charAt(0)-'0';		
		//System.out.println("TIPO DE BUSQUEDA: "+searchType);
				
		/* Consultar los datasets que selcciono el usuario*/
		List<Dataset> datasetList = DatasetDAO.getDatasetByStatus(1);	
		
		//buscar si usuario selecciono dbpedia
		for(int i=0; i< datasetList.size(); i++){
			System.out.println(datasetList.get(i).getName());			
			if(datasetList.get(i).getId() == 1){ // usuario selecciono dataset dbpedia					
				if(!InputSearchProcessor.isUri(input)){ //o en el request podria asignarle 4 al optradio
					//DbpediaEndpoint.JenaSparqlQuery(input);
					term = DbpediaEndpoint.searchTermByExactMatch(input);
				}
				else{
					
					term = DbpediaEndpoint.searchByUri(input);
				}
			}			
		}
		
		// datasets de bio2rdf

		/*
		if(input!=null){
			if(InputSearchProcessor.isUri(input)==1){
				//System.out.println("es uri :)");
				term = SearchController.getConcept(input,1);
			}
			else{
				System.out.println("es cadena :)");
				term = SearchController.getConcept(input,2);
			}			
		}
		*/
		return term;
	}
	
	public static List<Concept> getTermsList(HttpServletRequest request, int searchType) {
		List<Concept> tlist = null;
		String input = request.getParameter("concept");
		
		if(searchType==2){
			tlist = DbpediaEndpoint.searchTermBySimilarName(input);
		}
		else if(searchType == 3){
			tlist = DbpediaEndpoint.searchTermByPropertyMatch(input);
		}
				
		return tlist;		
	}
	
	public static Concept getConcept(String cad, int type){
		Concept c = new Concept();
		
		//ontoList = getAllOntologies();
		
		if(type==1){			
			System.out.println("TYPE 1");
			c = searchUri(cad);
		}
		else if(type==2){
			String uri =transformTermToURI(cad);
		}
		if(c.getName()==null) System.out.println("NOMBRE NULO!!!!! D:");
		
		return c;
	}
	
	
	public static Concept searchUri(String cad){
		//String uriS = ontolgiesGraphNames(ontoList);
		//System.out.println("uriS:");
		//System.out.println(uriS);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms>"+
		        "   SELECT DISTINCT *" +
		        "	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "       ?s1 ?p1 ?o1 . " +
			    "   	?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 . " +
			    "   	?parent rdfs:label ?parentLabel . " +
			    "		{ 		"+
			    "			<" + cad + "> <http://purl.obolibrary.org/obo/def> ?obodef } UNION { <" + cad + "> skos:definition ?skosdef }"+
				"	}" +
			    "LIMIT 100";
		
		/*
		SELECT ?s1, ?label1, ?type1
				WHERE {
				   ?s1 ?p1 ?o1 .
				   ?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 .
				   ?s1     <http://www.w3.org/2000/01/rdf-schema#label>     ?label1 .
				   FILTER (CONTAINS ( UCASE(str(?label1)), "ALZHEIMER"))
				}
				LIMIT 1000
		*/
		//falta reutilizar funciones
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			String uri = "<" + cad + ">";
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
		    	c.setUri(uri);
				c.setName(name);
				if(qsol.contains("obodef")){
			    	System.out.println("definition type: " + qsol.get("obodef"));
			    	c.setDefinition(""+qsol.get("obodef"));
				}
				else {
					System.out.println("definition type: " + qsol.get("skosdef"));
					c.setDefinition(""+qsol.get("skosdef"));
				}
				i++;
		    }
		    
		} 
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		
		qexec.close();
		
		return c;
		
	}
	
	
	
	
	
	public static String transformTermToURI(String term){
		
		//String uriS = ontolgiesGraphNames(ontoList);
		//System.out.println("uriS:");
		//System.out.println(uriS);
		//String x = "	FROM <http://bioportal.bioontology.org/ontologies/XAO> FROM <http://bioportal.bioontology.org/ontologies/ICF>";
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms/>"+
		        "   SELECT DISTINCT *" +
		        "	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "		{ " + 
		        "           ?s2 dc:title ?label2 "+
			    //"			FILTER (CONTAINS ( UCASE(str(?label2)), \"NEURONS\"))" +
			    "			FILTER (CONTAINS ( UCASE(str(?label2)), '"+ term.toUpperCase()+"'))" +
			    //"			FILTER ( UCASE(str(?label2)) = 'NEURONS')"+
			    //"	    	FILTER (UCASE(str(?label2)) = '"+ term.toUpperCase() + "') " +	
			    "		} "+
			    " 		UNION { "+
			  //"       ?s1 ?p1 ?o1 . " +
			    //"   	?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 . " +
			    
			    "  			?s1 rdfs:label ?label1 . " +
			    //"			FILTER (CONTAINS ( UCASE(str(?label1)), \"NEURONS\"))" +
			    "			FILTER (CONTAINS ( UCASE(str(?label1)), '"+ term.toUpperCase()+"'))" +
			    //"			FILTER ( UCASE(str(?label1)) = 'NEURONS')"+	
			    //"	    	FILTER (UCASE(str(?label1)) = '"+ term.toUpperCase() + "') " +    
			    "	 	}"+
				"	}" +
			    "LIMIT 100";
		
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql", query);
		
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			/*
			String uri = qsol.get("x").toString();
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    */
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
		    	/*
		    	c.setUri("<"+uri+">");
				c.setName(name);
				if(qsol.contains("obodef")){
			    	System.out.println("definition type: " + qsol.get("obodef"));
			    	c.setDefinition(""+qsol.get("obodef"));
				}
				else {
					System.out.println("definition type: " + qsol.get("skosdef"));
					c.setDefinition(""+qsol.get("skosdef"));
				}
				i++;
				*/
		    	if(qsol.contains("label2")){
			    	//System.out.println("definition type: " + qsol.get("label2"));
			    	return ""+qsol.get("label2");
				}
				else {
					System.out.println("definition type: " + qsol.get("label1"));
					return ""+qsol.get("label1");
				}
		    	
		    }
		    
		} 
		/*
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		*/
		qexec.close();
		return null;
	}
		
	
	
}

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
import model.Property;
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
	
	
	
	/*		FUNCION PRINCIPAL QUE INICIA LA BUSQUEDA	*/
	
	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		String input = request.getParameter("concept");
		
		//int searchType = request.getParameter("optradio").charAt(0)-'0';		
		//System.out.println("TIPO DE BUSQUEDA: "+searchType);
				
		/* Consultar los datasets que selcciono el usuario*/
		List<Dataset> datasetList = DatasetDAO.getDatasetByStatus(1);	
		List<Concept> tList = null;
		
		
		/**/
		int posBio [] = new int [5]; //posiciones de datasets de bio2rdf en la lista de datasets(datasetList)
		int cant = 0, found = 0, seleccDbpedia=0;

		
		if(!InputSearchProcessor.isUri(input)){
			
			for(int i=0; i<datasetList.size(); i++){
				int idDat = datasetList.get(i).getId();
				if(idDat == 1){ //DBPedia
					if(!InputSearchProcessor.isUri(input)){					
						term = DbpediaEndpoint.searchTermByExactMatch(input); 
					}
					
					if(term!=null) found = 1;
				}
				else { //Bio2RDF
					//term = Bio2RdfEndpoint.searchTermByExactMatch(input);
					posBio[cant++] = i;
				}
			}
			
			
			
			if(cant>0){ // Se seleccionaron datasets de Bio2RDF
				
				List <Concept> termsMappingList = new ArrayList<Concept>();		
				List <Concept> similarTerms = new ArrayList<Concept>();
				List <Concept> exactTerms = new ArrayList<Concept>();
				
				//Se seleccionaron datasets de Bio2rdf y Dbpedia		
					if(found==1){ // Se encontro el termino en DBPedia -> se busca lista de terminos en Bio2rdf

						//printConcept(term);
						System.out.println("term name: " + term.name);
						System.out.println("term properties: " + term.getProperties().size());
						System.out.println("dataset LIST: " + datasetList.size());
						
						termsMappingList = Bio2RdfEndpoint.getMappingPropertiesValues(term, datasetList); //conceptos con sus propiedades (para enriquecer propiedades del termino en contrado en DBPedia)
						similarTerms = Bio2RdfEndpoint.searchTermBySimilarName_Datasets(input, datasetList); // solo nombres de los conceptos (sin mostrar propiedades)
						
						
						if(term==null) System.out.println("term null!");
						
						//System.out.println("termsMappingList size: " + termsMappingList.size());
						System.out.println("ANTES term properties: " + term.getProperties().size());
						addInfoToTerm(term, termsMappingList, similarTerms);
					}
					else {
						// No se encontro el concepto en DBPediam, se busca en bio2rdf
						exactTerms = Bio2RdfEndpoint.searchTermByExactMatch_Datasets(input, datasetList); //exact match			
					}
				
			}
		}
		else {
			System.out.println("BUSCA URI!");
			
			/*
			System.out.println("input: " + input);
			int idDatasetMatch = findUriOrigin(input);
			System.out.println("idDatasetMatch: " + idDatasetMatch);	
			
			System.out.println("datasetList.size(): " + datasetList.size());
			for(int i=0; i<datasetList.size(); i++){
				int idDat = datasetList.get(i).getId();
				if(idDat == 1){ //DBPedia					
					if(input.contains("http://dbpedia.org/")){
						term = DbpediaEndpoint.searchByUri(input);
					}
				}
				else if(idDat == idDatasetMatch){ //Bio2RDF
					System.out.println("dataset origin uri: " + idDat);
					if(input.contains("http://bio2rdf.org/")){
						System.out.println("entro a uri bio2rdf");
						term = Bio2RdfEndpoint.searchTermByExactMatchUri(input, datasetList.get(i));
						
						if(term == null) System.out.println("null wtf");
						else System.out.println("NOT null wtf");
						
						break;
					}
					System.out.println("hallo termino by uri BIO2RDF ;) " + idDat);
				}
			}	
			*/
			
			if(input.contains("http://bio2rdf.org/")){
				int idDatasetMatch = findUriOrigin(input);
				
				System.out.println("idDatasetMatch: " + idDatasetMatch);				
				System.out.println("datasetList.size(): " + datasetList.size());
				
				for(int i=0; i<datasetList.size(); i++){
					Dataset dat = datasetList.get(i);					
					if(dat.getId() == idDatasetMatch){ // el dataset del uri clickeado si esta seleccionado -> si se puede navegar a ese dataset de Bio2RDF
						System.out.println("dataset origin uri: " + dat.getId());
					
						System.out.println("entro a uri bio2rdf");
						term = Bio2RdfEndpoint.searchTermByExactMatchUri(input, dat);
						
						if(term == null) System.out.println("null wtf");
						else System.out.println("NOT null wtf");
						
						break;						
					}
				}
			}
			else if(input.contains("http://dbpedia.org/")){
				//SI ES TERMINO
				term = DbpediaEndpoint.searchByUri(input);
				
				//SI ES CLASE
			}
			
		}
		
		if(term == null) System.out.println("TERM ES NULO DDDD:");
		else System.out.println("lo encontro :)");
		
		
		//term.setName("basurita");
		printConcept(term);
		
		return term; //DEVUELVE TERMINO SOLO DE DBPEDIA
	}
	private static int findUriOrigin(String input){
		
		if(input.contains("http://bio2rdf.org/mesh")){			
			return 2;
		}
		else if(input.contains("http://bio2rdf.org/pharmgkb")){
			return 3;
		}
		else if(input.contains("http://bio2rdf.org/uniprot") || input.contains("http://bio2rdf.org/go") || input.contains("http://bio2rdf.org/goa")){
			return 4;
		}
		else if(input.contains("http://bio2rdf.org/ncbigene")){
			return 5;
		}
		
		return -1;
	}
	
	
	private static void addInfoToTerm(Concept term, List<Concept> termsMappingList, List<Concept> similarTerms){
		
		//pasar las propiedades de los terminos mapeados -> a el termino de DBPedia
		List<Property> props = term.getProperties();
		if(termsMappingList != null && term != null){
			for(int i=0; i<termsMappingList.size(); i++){
				Concept c = termsMappingList.get(i);
				List<Property> pList = c.getProperties();
				for(int j=0; j < pList.size(); j++){
					// agregar las propiedades al termino
					props.add(pList.get(j));
				}			
			}
		}
		
		System.out.println("addInfoToTerm - props size: " + props.size());
		//term.setProperties(props);
		
		System.out.println("similarTerms.size(): "+similarTerms.size());
		for(int k=0; k<similarTerms.size(); k++){
			System.out.println("" + similarTerms.get(k).getUri());
		}
		
		/*
		List<Concept> cList = new ArrayList<Concept>();		
		// terminos linkeados
		if(similarTerms != null && term != null){
			for(int k=0; k < similarTerms.size(); k++){
				cList.add(similarTerms.get(k));
			}
		}
		term.setSimilarTerms(cList);	
		*/
		if(similarTerms != null && term != null){
			term.setSimilarTerms(similarTerms);
		}
	}
	
	
	
	public static List<Concept> getTermsList(HttpServletRequest request, int searchType) {
		
		List<Concept> tlist = null;
		String input = request.getParameter("concept");
		
		 List<Dataset> datasetList = DatasetDAO.getDatasetByStatus(1);
		 
		if(searchType==2){
			//tlist = DbpediaEndpoint.searchTermBySimilarName(input);
			//tlist = Bio2RdfEndpoint.searchTermBySimilarName(input, null);
			tlist = Bio2RdfEndpoint.searchTermBySimilarName_Datasets(input,datasetList);
		}
		else if(searchType == 3){
			//tlist = DbpediaEndpoint.searchTermByPropertyMatch(input);
			tlist = Bio2RdfEndpoint.searchTermByPropertyMatch_Datasets(input, datasetList);
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
	
	public static Concept searchUriBio2RDF(String cad){

		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms>"+
		        "   SELECT DISTINCT *" +
		        //"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "       <"+ cad+ "> ?prop ?value . " +
			    "	} "+
			    "LIMIT 100";
		
		//falta reutilizar funciones
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://bio2rdf.org/sparql/", query);
		
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
		
	
	private static void printConcept (Concept c){
		
		System.out.println("==============================");
		System.out.println("	CONCEPTO FINAL");
		System.out.println("==============================");
		
		if(c.getName() != null)
			System.out.println("name: " + c.getName());
		else System.out.println("Nombre null :/");
		
		if(c.getUri() != null)
			System.out.println("uri: " + c.getUri());
		else System.out.println("Uri null :/");
		
		if(c.getProperties() != null){
			System.out.println("Propiedades: ");
			System.out.println("Propiedades size: " + c.getProperties().size());
			for(int i=0; i<c.getProperties().size(); i++){
				System.out.println(i+") uri: " + c.getProperties().get(i).getUri());
				System.out.println(i+") value: " + c.getProperties().get(i).getValue());
			}
		}
		else System.out.println("Propiedades null :/");
		
		if(c.getSimilarTerms() != null){
			System.out.println("Similar terms: ");
			for(int i=0; i<c.getSimilarTerms().size(); i++){
				System.out.println(i+") " + c.getSimilarTerms().get(i).getName());
			}
		}
		else System.out.println("Similar terms null :/");
		
		if(c.getLinkedTerms() != null){
			System.out.println("Linked terms: ");
			for(int i=0; i<c.getLinkedTerms().size(); i++){
				System.out.println(i+") " + c.getLinkedTerms().get(i).getName());
			}
		}
		else System.out.println("Linked terms null :/");
		
	}
	
	
}

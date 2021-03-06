package sparqlendpoint;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import controller.InputSearchProcessor;
import controller.SearchController;
import model.Concept;

public class BioportalEndpoint {

static List<String>ontoList;
	

	public static void JenaSparqlQuery(String term){		
		/*
		String sparqlQueryString1 = "  PREFIX omv: <http://omv.ontoware.org/2005/05/ontology#>" +
					"	SELECT ?ont ?name ?acr" +
					"   WHERE { " +
					"       ?ont a omv:Ontology . " +
					"		?ont omv:acronym ?acr . " +
					"		?ont omv:name ?name ."+
					"	FILTER( CONTAINS(?name, \"SNOMED\") )"+
					"   }" ;
					//"LIMIT 100";
		*/
		
		
		//http://purl.obolibrary.org/obo/GO_0007129
		//http://purl.bioontology.org/ontology/NCBITAXON/324792
		/**/
		//http://purl.obolibrary.org/obo/PATO_0000220
		//http://purl.obolibrary.org/obo/GO_0031099
		
		String sparqlQueryString1 = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "+
				"	PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " +
				"	SELECT * " +
				"   WHERE { " +
				//"       ?s map:source <http://purl.obolibrary.org/obo/DOID_8545> ." +
				//"		map:target ?target . " +
				//"		<http://purl.obolibrary.org/obo/BFO_0000050> ?p ?b ." +
				"		?x ?p <http://purl.obolibrary.org/obo/GO_0008150> . "+
				"   }";
				//"LIMIT 100";
		
		
		/*
		String sparqlQueryString1 = "  PREFIX omv: <http://omv.ontoware.org/2005/05/ontology#>" +
				"	SELECT *" +
				"   WHERE { " +
				"       <http://purl.bioontology.org/ontology/SNOMEDCT/47220008> ?p1 ?b . " +
				"   }" +
				"LIMIT 100";
		*/
	
		Query query = QueryFactory.create(sparqlQueryString1);	
		//QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		//para mappings:
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
	
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);       
	
		qexec.close();		
	}

	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		String input = request.getParameter("concept");
		if(input!=null){
			/*
			if(InputSearchProcessor.isUri(input)==1){
				//System.out.println("es uri :)");
				//term = SearchController.getConcept(input,1);
			}
			else{
				//System.out.println("es cadena :)");
				//term = SearchController.getConcept(input,2);
			}
			*/			
		}
		return term;
	}	
	
	public static Concept getConcept(String cad, int type){
		Concept c = new Concept();
		
		ontoList = getAllOntologies();
		
		if(type==1){			
			System.out.println("TYPE 1");
			c = searchUri(cad);
		}
		else if(type==2)
			c = searchWord(cad);
		if(c.getName()==null) System.out.println("NOMBRE NULO!!!!! D:");
		c.setSimilarTerms(searchSimilarTerms(c.getName()));
		c.setLinkedTerms(searchSuperClassesURIs(c.getUri()));
		
		return c;
	}
	
	public static Concept searchUri(String cad){
		String uriS = ontolgiesGraphNames(ontoList);
		System.out.println("uriS:");
		System.out.println(uriS);
		//String x = "	FROM <http://bioportal.bioontology.org/ontologies/XAO> FROM <http://bioportal.bioontology.org/ontologies/ICF>";
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX skos:<http://www.w3.org/2004/02/skos/core>"+
		        "   SELECT DISTINCT *" +
		        //uriS +
		        //"	FROM <http://bioportal.bioontology.org/ontologies/XAO>"+
		        //"	FROM <http://bioportal.bioontology.org/ontologies/ICF>" +
		        uriS+
		        "   WHERE { " +
		        "       <" + cad + "> rdfs:label ?label . " +
			    "   	<" + cad + "> rdfs:subClassOf ?parent . " +
			    "   	?parent rdfs:label ?parentLabel . " +
			    "		{ <" + cad + "> <http://purl.obolibrary.org/obo/def> ?obodef } UNION { <" + cad + "> skos:definition ?skosdef }"+
				"	}" +
			    "LIMIT 100";
		
		//falta reutilizar funciones
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		
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
	public static List<String> getAllOntologies(){
		ontoList = new ArrayList<String>();
	
		String sparqlQueryString1 = "PREFIX omv: <http://omv.ontoware.org/2005/05/ontology#> " +
		        "   SELECT ?ont ?name ?acr ?dataGraph" +
		        "   WHERE { " +
		        "       ?ont a omv:Ontology; " +
			    "   	omv:acronym ?acr; "+
			    "   	omv:name ?name; "+
			    "	    <http://bioportal.bioontology.org/metadata/def/hasDataGraph> ?dataGraph . " +
				"	}"+
			    "	LIMIT 10";
		
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		
		ResultSet results = qexec.execSelect();
		while (results.hasNext())
		{
			QuerySolution sol = results.nextSolution();
			Resource subj = (Resource) sol.get("dataGraph");
		    //System.out.println("dataGraph: " + subj.getURI());
			ontoList.add(""+subj.getURI());
		} 

		qexec.close();
		
		return ontoList;
	}
	
	public static Concept searchWord(String term){
		
		String uriS = ontolgiesGraphNames(ontoList);
		System.out.println("uriS:");
		System.out.println(uriS);
		//String x = "	FROM <http://bioportal.bioontology.org/ontologies/XAO> FROM <http://bioportal.bioontology.org/ontologies/ICF>";
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX skos:<http://www.w3.org/2004/02/skos/core>"+
		        "   SELECT DISTINCT *" +
		        //uriS +
		        //"	FROM <http://bioportal.bioontology.org/ontologies/XAO>"+
		        //"	FROM <http://bioportal.bioontology.org/ontologies/ICF>" +
		        uriS+
		        "   WHERE { " +
		        "       ?x rdfs:label ?label . " +
			    "   	?x rdfs:subClassOf ?parent . "+
			    "   	?parent rdfs:label ?parentLabel . "+
			    "		{ ?x <http://purl.obolibrary.org/obo/def> ?obodef } UNION { ?x skos:definition ?skosdef }"+
			    "	    FILTER (UCASE(str(?label)) = '"+ term.toUpperCase() + "') " +
				"	}" +
			    "LIMIT 100";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			String uri = qsol.get("x").toString();
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
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
		    }
		    
		} 
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		
		//demo: harcodeado 
		//searchSuperClassesURIs("<http://purl.obolibrary.org/obo/XAO_0003023>");
		qexec.close();
		
		return c;
	}
	
	public static List<Concept> searchSimilarTerms(String term){
		
		String uriS = ontolgiesGraphNames(ontoList);
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		        "   SELECT DISTINCT *" +
		        //uriS +
		        //"	FROM <http://bioportal.bioontology.org/ontologies/XAO>"+
		        //"	FROM <http://bioportal.bioontology.org/ontologies/ICF>" +
		        uriS+
		        "   WHERE { " +
		        "       ?x rdfs:label ?label . " +
			    "   	?x rdfs:subClassOf ?parent . "+
			    "   	?parent rdfs:label ?parentLabel . "+
			    "	    FILTER (CONTAINS ( UCASE(str(?label)), \""+term.toUpperCase()+"\")) " +
			    //"	    FILTER (UCASE(str(?label)) = '"+ term.toUpperCase() + "') " +
				"	}" +
			    "LIMIT 100";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		
		ResultSet results = qexec.execSelect();
		/*PARA EVITAR MULTIPLES BUSQUEDAS, DEVOLVER CONCEPT(nombre y URI) en lugar de STRING*/
		List<Concept> termList= new ArrayList<Concept>();
		int i=0;		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			Concept c = new Concept();
			c.setUri("<"+qsol.get("x")+">");
			c.setName(""+qsol.get("label"));
			c.setDefinition(null);
			c.setSimilarTerms(null);
			c.setLinkedTerms(null);
			
			System.out.println("similiar terms uri:"+qsol.get("x"));
			System.out.println("similiar terms label:"+qsol.get("label"));
			termList.add(c);
		} 
		qexec.close();
		
		return termList;
	}
	
	public static List<Concept> searchSuperClassesURIs(String seedURI){
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		        "   SELECT DISTINCT *" +		       
		        "   WHERE { " +
		        "       "+ seedURI +" <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?superclass ." +
			    "   	OPTIONAL { ?superclass rdfs:label ?label. }" +
		        "		OPTIONAL { ?superclass <http://www.w3.org/2002/07/owl#someValuesFrom>  ?someValuesFrom ." +
			    "				   ?someValuesFrom rdfs:label ?label ." +
		        "				 }"+
				"	}" +
			    "LIMIT 10";
		
		//System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		
		
		ResultSet results = qexec.execSelect();
		List<Concept> termList= new ArrayList<Concept>();
		int i=0;
		//ResultSetFormatter.out(System.out, results, query);   
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			Concept c = new Concept();
			
			if(qsol.contains("someValuesFrom")){	
				c.setUri("<"+qsol.get("someValuesFrom")+">");
				System.out.println("someValuesFrom: "+qsol.get("someValuesFrom"));
			}
			else { 
				c.setUri("<"+qsol.get("superclass")+">");
				System.out.println("superclass: "+qsol.get("superclass"));
			}
			System.out.println("label: "+qsol.get("label"));
			c.setName(""+qsol.get("label"));
			termList.add(c);
		} 
		
		qexec.close();
		
		return termList;		
	}
	
	public static String ontolgiesGraphNames(List<String> ontoList){
		String uriS = "   ";
		for(int i = 0; i<ontoList.size(); i++){
			uriS += "FROM <" + ontoList.get(i) + "> ";
		}
		return uriS;
	} 
		
}

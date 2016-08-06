package controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import model.Concept;

public class SearchController {

	static List<String>ontoList;
	
	public static Concept getConcept(String cad){
		//demo (deberia obtener info de un query Sparql)
					
		//JenaSparqlQuery("Neuron");
		//List<String>ontoList = getAllOntologies();
		ontoList = getAllOntologies();
		
		Concept c = searchTerm(cad);
		c.setSimilarTerms(searchSimilarTerms(cad));
		c.setLinkedTerms(searchSuperClassesURIs(c.getUri()));
		
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
	
	public static Concept searchTerm(String term){
		
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
			c.setName(""+qsol.get("label"));
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
	
	//demo	
	public static void JenaSparqlQuery( String cad){
		/*
		String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
		        "   SELECT ?x " +
		        //"   FROM" +
		        "   WHERE { " +
		        "       <http://dbpedia.org/resource/Neuron> dbo:abstract ?x. " +
		        "   }";
		        
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);
		*/
		
		
		/*
		String sparqlQueryString1 = "PREFIX dbio: <http://data.bioontology.org/> " +
		        "   SELECT ?label " +
		        //"   FROM" +
		        "   WHERE { " +
		        "		?x dbio:prefLabel ?title " +
		        " 		FILTER regex(?title, \"^melanoma\"). " +
		        "   }";
		*/
		/*
		String sparqlQueryString1 = "PREFIX meta: <http://bioportal.bioontology.org/metadata/def/> " +
		        "   SELECT DISTINCT ?vrtID ?graph " +
		        //"   FROM" +
		        "   WHERE { " +
		        "		?vrtID meta:hasVersion ?version . " +
		        " 		?version meta:hasDataGraph ?graph . " +
		        "   }";
		*/
		
		String sparqlQueryString1 = "PREFIX omv: <http://omv.ontoware.org/2005/05/ontology#> "+
							"SELECT ?ont ?name ?acr" +
							"WHERE {"+
							"	?ont a omv:Ontology ."+
							"	?ont omv:acronym ?acr ."+
							"	?ont omv:name ?name ."+
							"}";
			
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://sparql.bioontology.org/sparql", query);		
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		qexec.addParam("apikey", "8525c5a4-8bd8-4824-bb62-d3785c367f06");
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);       

		qexec.close();		
	}	
}

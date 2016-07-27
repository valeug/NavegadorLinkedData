package controller;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;


import model.Concept;

public class SearchController {

	public static Concept getConcept(String cad){
		//demo (deberia obtener info de un query Sparql)
		Concept c = new Concept();
		c.setName(cad); // primero buscar si existe el termino en algun dataset (if)
		c.setDescription("cute thing");
		c.setLinkedTerms(null);
		
		JenaSparqlQuery("Neuron");
		
		return c;
	}
	
	//demo
	
	public static void JenaSparqlQuery( String cad){
		String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
		        "   SELECT ?x " +
		        //"   FROM" +
		        "   WHERE { " +
		        "       <http://dbpedia.org/resource/Neuron> dbo:abstract ?x. " +
		        "   }";

		Query query = QueryFactory.create(sparqlQueryString1);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);       

		qexec.close() ;
		
	}
	
}

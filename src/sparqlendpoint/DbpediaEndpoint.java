package sparqlendpoint;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class DbpediaEndpoint {

	
	public static void JenaSparqlQuery(String term){
		
		
		/*
		String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
		        "   SELECT ?x " +
		        //"   FROM" +
		        "   WHERE { " +
		        "       <http://dbpedia.org/resource/Neuron> dbo:abstract ?x. " +
		        "   }";
	    */
		System.out.println("buscar: " + term);
		/*
		String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
									"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX skos:<http://www.w3.org/2004/02/skos/core>" +
		        "   SELECT * " +
		        "   WHERE { " +
		        "       ?x rdfs:label ?label . " +
		        " 		?x dbo:WikiLink ?link ." +
		        //"	    FILTER (UCASE(str(?label)) = '"+ term.toUpperCase() + "') " +
		        "   }";
        */
		String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
									"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX skos:<http://www.w3.org/2004/02/skos/core>" +
					"   SELECT * " +
					"   WHERE { " +
					"       <http://es.dbpedia.org/resource/Neurona> rdfs:label ?x ." +
					"   }";
		 
		
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://es.dbpedia.org/sparql/", query);
	
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);       

		qexec.close();		
	}
}

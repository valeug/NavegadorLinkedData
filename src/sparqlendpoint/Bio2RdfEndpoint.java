package sparqlendpoint;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Bio2RdfEndpoint {
	
	public static void JenaSparqlQuery(String term){		
		
		String sparqlQueryString1 = "   SELECT * " +
					"   WHERE { " +
					"        <http://bio2rdf.org/mesh:D009474> ?p1 ?o1 ." +
					"   }" +
					"LIMIT 100";

		
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql", query);
	
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);       

		qexec.close();		
	}
	
	
}

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

import model.Concept;
import sparqlendpoint.Bio2RdfEndpoint;
import sparqlendpoint.BioportalEndpoint;
import sparqlendpoint.DbpediaEndpoint;

public class SearchController {

	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		//dbpedia
		//bioportal
		String input = request.getParameter("concept");
		
		//DbpediaEndpoint.JenaSparqlQuery(input);
		//BioportalEndpoint.JenaSparqlQuery(input);
		//Bio2RdfEndpoint.JenaSparqlQuery(input);
		
		return term;
	}
		
}

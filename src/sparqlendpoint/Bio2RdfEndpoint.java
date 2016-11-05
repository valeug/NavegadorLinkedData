package sparqlendpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import dao.ClassDAO;
import dao.PropertyDAO;
import model.Class;
import model.Concept;
import model.Property;

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
	
	
	/*************************************************************************/
		/*	BUSQUEDA POR COINCIDENCIA EXACTA	*/
	/*************************************************************************/
	
	public static Concept searchTermByExactMatch(String cad){
		
		System.out.println(cad);
		char []lowcad = cad.toLowerCase().toCharArray();
		lowcad[0] = Character.toUpperCase(lowcad[0]);		
		String cad2 = new String(lowcad);
		
		System.out.println(lowcad);
		
		/*
		 SELECT DISTINCT ?s1, ?label1
FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>
WHERE {
   ?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 .
   ?s1   <http://www.w3.org/2000/01/rdf-schema#label>   ?label1 .
   FILTER (CONTAINS ( UCASE(str(?label1)), "NEURON"))
}
		 */
		
		/*
		 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
		PREFIX owl: <http://www.w3.org/2002/07/owl#> 
		SELECT DISTINCT ?s1, ?label1
		FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>
		WHERE {
		   ?s1  rdf:type ?type1 .
		   ?s1   <http://purl.org/dc/terms/title>  ?label1 .
		   FILTER (UCASE(str(?label1)) = "MOTOR NEURON DISEASE")
		}
		LIMIT 100
		 * */
		
		String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			"	SELECT DISTINCT * " +
			"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3> "+
			"	WHERE {"+
			"		?s1 rdf:type ?type1 . "+
			"		?s1 <http://purl.org/dc/terms/title> ?label1 . "+
			"	FILTER (UCASE(str(?label1)) = \"" + cad.toUpperCase() +"\")"+
			"	}"+
			"	LIMIT 100";
		
		/*
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"   WHERE { " +				
		"       ?x rdfs:label ?label . " +		
		"		{ "+
		"			?x rdf:type ?class . "+
		"			?x rdf:type owl:Thing . "+
		"		} "+
		"		UNION"+
		" 		{ "+
		"			?x <http://dbpedia.org/ontology/wikiPageRedirects> ?redirected . "+
		//"			OPTIONAL { }"+
		"			?redirected rdf:type ?class . "+
		"		} "+
		"		UNION { "+
		"			?x <http://dbpedia.org/ontology/wikiPageDisambiguates> ?amb . "+
		"			?amb rdf:type ?type ."+
		"		FILTER (CONTAINS(str(?type), \"http://dbpedia.org/ontology/\" ))"+		//http://dbpedia.org/ontology/Disease
		"		}"+
		"		FILTER ( ?label = \""+ cad2 +"\"@en ) " +
		//"		FILTER regex(str(?x), \"http://dbpedia.org/resource/\" )"+
		//"		FILTER (CONTAINS(str(?x), \"http://dbpedia.org/resource/\" )) "+
		"		FILTER (CONTAINS(str(?x), \"http://dbpedia.org/\" )) "+
		"   } ";
		*/
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
	
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);    
	
		// Informacion del resultado
		
		
		int i=0;
		String uri = null;
		String name = null;	
		// obtener de la BD las clases del dataset
		
		List<Class> classesDataset = ClassDAO.getAllClassesByDataset(1);
		
		for(int k=0; k<classesDataset.size(); k++){
			System.out.println("clase " + k + ": " + classesDataset.get(k).getUri());
		}
		
		List<String> uris = new ArrayList<String>();
		List<String> types = new ArrayList<String>(); // clase del recurso
		String aux = null;
		String pattern = "http://dbpedia.org/ontology/"; /* cambiar */
		
		System.out.println("\nCLASES: \n");
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			// el termino devuelto es el primer resultado de la busqueda por ahora	
			/*
		    if(i==0){		    	
				uri = qsol.get("x").toString();
				name = qsol.getLiteral("label").toString();
				System.out.println("uri: " + uri);
			    System.out.println("label: " + name);
			    
		    }
		    */
	    	
			if(qsol.contains("class") && !qsol.contains("redirected") && !qsol.contains("amb")){				
		    	//System.out.println("class type: " + qsol.get("x")); //uri
		    	//c.setDefinition(""+qsol.get("obodef"));		    	
		    	aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia		    	
		    	if(aux.contains(pattern)){
		    		System.out.println("class: " + aux);
		    		types.add(aux);	
		    		aux = qsol.get("x").toString(); //uri del recurso
			    	uris.add(aux);
		    	}
		    		    	
			}
			else if(qsol.contains("redirected") && !qsol.contains("amb")){
				//System.out.println("redirected type: " + qsol.get("redirected")); //uri			
		    	
				aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia
		    	if(aux.contains(pattern)){	
		    		System.out.println("class: " + aux);
					types.add(aux);
					aux = qsol.get("redirected").toString(); //uri del recurso
					uris.add(aux);
		    	}				
			} 
			else if(qsol.contains("amb")){
				//System.out.println("ambiguos type: " + qsol.get("amb")); //uri
				
				aux = qsol.get("type").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia				
				if(aux.contains(pattern)){	
					System.out.println("class: " + aux);
					types.add(aux);		
					aux = qsol.get("amb").toString();
					System.out.println("uri: " + aux); //uri del recurso
			    	uris.add(aux);
				}
			}
				
			i++;
		} 
		
		int posUri = -1;
		if(uris.size()>0 && types.size()>0){
			//posUri = selectUriMatchClass(types,classesDataset); //obtener uri cuya clase este definida en DBPedia (creo que tambien deberia pasarle "label" y verificar que sean iguales)
		}
		
		System.out.println("Longitud types list: " + types.size());
		System.out.println(" URI ENCONTRADO: "+ posUri);
		System.out.println(" TERMINO BUSQUEDA !");
		
		System.out.println("\n******************************");
		System.out.println("Concept Uri:" + uris.get(posUri));
		System.out.println("Concept Class:" + types.get(posUri));
		System.out.println("\n******************************");
		
		/* AHORA DEVUELVO SOLO UNA CLASE, PODRIA DEVOLVER VARIAS (EJ: BONE Y ANATOMICAL STRUCTURE)*/
		
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		
		System.out.println(" \nMY PROPERTIES! ");
		//getPropertiesValues(uris.get(posUri),pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
		
		System.out.println(" \nPROPERTIES! WITH VALUES");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());		
			System.out.println(pList.get(h).getName());	
			System.out.println(pList.get(h).getValue());			
		}
		
	    // obtener clase a la que pertenece (?class)			    
	    // consultar en la BD las propiedades que corresponden a la clase 	    
	    // PROPIEDADES TIPO 1: navegables	    	    
	    // PROPIEDADES TIPO 2: info sobre el recurso
	    
	    
		qexec.close();	
		Concept c = new Concept();
		c.setProperties(pList);
		return c;
	}

	
	/*************************************************************************/
	/* LISTA DE CONCEPTOS: COINCIDENCIA SIMILAR EN NOMBRE */
	/*************************************************************************/

	public static List<Concept> searchTermBySimilarName(String input){
		
		
		/*
		 * 
		String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
	"	SELECT DISTINCT * " +
	"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3> "+
	"	WHERE {"+
	"		?s1 rdf:type ?type1 . "+
	"		?s1 <http://purl.org/dc/terms/title> ?label1 . "+
	"	FILTER (UCASE(str(?label1)) = \"" + cad.toUpperCase() +"\")"+
	"	}"+
	"	LIMIT 100";
		*/
		
		System.out.println("TERMINO A BUSCAR: " + input);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				"   SELECT DISTINCT * " +
				"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>" +
				"   WHERE { " +				
				"       ?s <http://purl.org/dc/terms/title> ?label . " +		
				"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
				"   } "+
				"	LIMIT 20";
		
		
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query); 
		
		List<Concept> concepts = new ArrayList<>();
		String aux;
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			Concept c = new Concept();
			
			aux = qsol.get("x").toString();
			System.out.println("uri: "+aux);
			c.setUri(aux);
			
						
			aux = qsol.get("label").toString();
			System.out.println("label: "+aux);
			c.setName(aux);			
			
			concepts.add(c);
			i++;
		}
				
		return concepts;		
	}


	/*************************************************************************/
	/* BUSQUEDA POR COINCIDENCIA EN PROPIEDAD */
	/*************************************************************************/

	
	public static List<Concept> searchTermByPropertyMatch(String input){
		
		//String classesQuery = concatenateClassesForSimilarMatch(1);
		
		/*
				String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
						"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>" +
		"   WHERE { " +				
		"       ?s <http://purl.org/dc/terms/title> ?label . " +		
		"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
		"   } "+
		"	LIMIT 20";
		
		*/
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
					"   SELECT DISTINCT * " +
					"   WHERE { " +		
					"		?s <http://purl.org/dc/terms/title> ?label . " +
					"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
					"   } " +
					"LIMIT 20";
					
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);
				
				
		List<Concept> termsList = new ArrayList<Concept>();		
		String aux = null;
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	

			if(qsol.contains("x") && qsol.contains("label")){				
				Concept c = new Concept();
				
		    	aux = qsol.get("x").toString();	   	
		    	c.setUri(aux);
		    	System.out.println("resource uri: " + aux);
		    	
		    	aux = qsol.get("label").toString();	   	
		    	c.setName(aux);
		    	System.out.println("resource label: " + aux);
		    	
		    	termsList.add(c);		    			    		    	
			}
			
		} 
							
		return termsList;
	}
}

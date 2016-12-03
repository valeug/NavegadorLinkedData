package sparqlendpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import dao.ClassDAO;
import dao.PropertyDAO;
import model.Concept;
import model.Property;
import model.Class;

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
					"       <http://dbpedia.org/resource/Neuron> rdfs:label ?x ." +
					"   }";
		 
		
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);
	
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
		
		/* 	ENCONTRAR URI DEL RECURSO  */
		System.out.println(lowcad);
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
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);

		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);    

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
		String pattern1 = "http://dbpedia.org/ontology/"; 
		String pattern2 = "http://www.w3.org/2002/07/owl#Thing";
		
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
			System.out.println("entro :)");
			if(qsol.contains("class") && !qsol.contains("redirected") && !qsol.contains("amb")){	
				System.out.println("entro 11! ");
		    	//System.out.println("class type: " + qsol.get("x")); //uri
		    	//c.setDefinition(""+qsol.get("obodef"));		    	
		    	aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia		    	
		    	if(aux.contains(pattern1) || aux.contains(pattern2)){
		    		System.out.println("entro 11! ");
		    		System.out.println("class: " + aux);
		    		types.add(aux);	
		    		aux = qsol.get("x").toString(); //uri del recurso
			    	uris.add(aux);
		    	}
		    		    	
			}
			else if(qsol.contains("redirected") && !qsol.contains("amb")){
				//System.out.println("redirected type: " + qsol.get("redirected")); //uri			
				System.out.println("entro 21! ");
				aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia
		    	if(aux.contains(pattern1) || aux.contains(pattern2)){	
		    		System.out.println("entro 22! ");
		    		System.out.println("class: " + aux);
					types.add(aux);
					aux = qsol.get("redirected").toString(); //uri del recurso
					uris.add(aux);
		    	}				
			} 
			else if(qsol.contains("amb")){
				//System.out.println("ambiguos type: " + qsol.get("amb")); //uri
				System.out.println("entro 31! ");
				aux = qsol.get("type").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia				
				if(aux.contains(pattern1) || aux.contains(pattern2)){	
					System.out.println("entro 32! ");
					System.out.println("class: " + aux);
					types.add(aux);		
					aux = qsol.get("amb").toString();
					System.out.println("uri: " + aux); //uri del recurso
			    	uris.add(aux);
				}
			}
				
			i++;
		} 
		
		//mostrar las clases en type (verificar si se lleno)
		System.out.println("clases del recurso! ");
		for(int w=0; w<types.size(); w++){
			System.out.println(types.get(w));
		}
		
		int posUri = -1;
		
		System.out.println("Types size dbpedia: " + types.size());
		if(uris.size()>0 && types.size()>0){
			posUri = selectUriMatchClass(types,classesDataset); //obtener uri cuya clase este definida en DBPedia (creo que tambien deberia pasarle "label" y verificar que sean iguales)
		}
		
		System.out.println("Longitud types list: " + types.size());
		System.out.println(" URI ENCONTRADO: "+ posUri);
		System.out.println(" TERMINO BUSQUEDA !");
		
		System.out.println("\n******************************");
		System.out.println("Concept Uri:" + uris.get(posUri));
		System.out.println("Concept Class:" + types.get(posUri));
		System.out.println("\n******************************");
		
		/* AHORA DEVUELVO  propiedades de SOLO UNA CLASE, PODRIA DEVOLVER VARIAS (EJ: BONE Y ANATOMICAL STRUCTURE)*/
		
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		
		System.out.println(" \nMY PROPERTIES! ");
		System.out.println("pList size: "+ pList.size());
		String conceptName = getAllPropertiesValues(uris.get(posUri),pList);
		//String conceptName = getPropertiesValues(uris.get(posUri),pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
		
		System.out.println(" \nPROPERTIES! WITH VALUES");
		System.out.println("pList size: "+ pList.size());
		
		/*
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());		
			System.out.println(pList.get(h).getName());	
			System.out.println(pList.get(h).getValue());			
		}
		*/
	    // obtener clase a la que pertenece (?class)			    
	    // consultar en la BD las propiedades que corresponden a la clase 	    
	    // PROPIEDADES TIPO 1: navegables	    	    
	    // PROPIEDADES TIPO 2: info sobre el recurso
	    
	    
		qexec.close();	
		Concept c = new Concept();
		//c.setName(conceptName);
		c.setProperties(pList);
		return c;
	}
	
	private static String getPropertiesValues(String uri, List<Property> pList){
		
		String apQuery = appendPropertiesInQuery(uri,pList,1); // Navegables, 0:no navegables
		/*
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
			"   SELECT DISTINCT * " +
			"   WHERE { " +	
			//"       <"+uri+"> rdfs:label ?label . " +	
			"		<"+uri+"> ?property ?value ."+
			"   } "+
			"	LIMIT 200";
		*/
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"   WHERE { " +	
		"		{"+
		"			OPTIONAL { "+
		"					<"+uri+">  rdfs:label ?label . " +
		"					FILTER (langMatches(lang(?label), \"en\"))" +
		"			}"+
		"			OPTIONAL { "+
		"					<"+uri+">  <http://dbpedia.org/ontology/abstract>  ?abstract . " +
		"					FILTER (langMatches(lang(?abstract), \"en\")) " +
		"			}"+
		"		}"+
		"		UNION"+
		"		{"+
		"			<"+uri+"> ?property ?value ."+
		"   	} "+
		"	}"+
		"	LIMIT 100";
		
		
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);

		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		List<String> urisList= new ArrayList<String>(), valuesList = new ArrayList<String>();
		String propUri, propValue;
		int cont=0;
		String name = null;
		String abst = null;
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			
			if(qsol.contains("property") && qsol.contains("value")){	
				System.out.println("entro ***! ");

				propUri = qsol.get("property").toString();
				System.out.println("|property: ");
				System.out.println(propUri);
							
				propValue = qsol.get("value").toString();
				System.out.println("|value: ");			
				System.out.println(propValue);
				
				urisList.add(propUri);
				valuesList.add(propValue);
		    		    	
			}
			
			if(qsol.contains("label")){
				System.out.println("entro label***! ");
				
				name = "" + qsol.get("label");
				System.out.println(name);
			}
			if(qsol.contains("abstract")){
				System.out.println("entro abstract***! ");
				abst = "" + qsol.get("abstract");
				System.out.println(abst);
			}
			
			
			/*
			if(propValue.contains("@")){ // tiene lenguaje
				if(propValue.matches("@en")){ // ingles
					System.out.println("agrego en ingles!!!!!!");	
					urisList.add(propUri);
					valuesList.add(propValue);
				}				
			}
			else if(!propValue.contains("@")){
				urisList.add(propUri);
				valuesList.add(propValue);
			}
			*/		
			
			
			/*
			if(qsol.get("label") != null){
				name = "" + qsol.get("label");
			}
			if(qsol.get("abstract") != null){
				abst = "" + qsol.get("abstract");
			}
			*/
			
			cont++;
		}
				
		//pList esta vacio, se llenara con los valores de uriList y valueList
		for(int i=0; i < pList.size(); i++){
			
			for(int j=0; j < urisList.size(); j++){
				
				if(pList.get(i).getUri().compareTo("http://www.w3.org/2000/01/rdf-schema#label") !=0 && pList.get(i).getUri().compareTo("http://dbpedia.org/ontology/abstract") != 0){
						if(pList.get(i).getUri().compareTo(urisList.get(j)) == 0){	
							if(pList.get(i).getIs_mapping() == 1 && pList.get(i).getTarget()>1){ // mapping a dataset en Bio2rdf
								String inputUri = null;
								switch(pList.get(i).getTarget()){								
									case 2: inputUri = "http://bio2rdf.org/mesh:" + valuesList.get(j); // MESH
											break;
									case 3: inputUri = "http://bio2rdf.org/pharmgkb:" + valuesList.get(j);	// PHARMGKB
											break;
									case 4: inputUri = "http://bio2rdf.org/goa:" + valuesList.get(j);	// NCBI
											break;
									case 5: inputUri = "http://bio2rdf.org/ncbi:" + valuesList.get(j);	// NCBI
											break;
								}
								
								pList.get(i).setValue(inputUri);
							}
							else pList.get(i).setValue(valuesList.get(j));
						}					
				}
				else if (pList.get(i).getUri().compareTo("http://www.w3.org/2000/01/rdf-schema#label") == 0){
					System.out.println("name final: " + name);
					pList.get(i).setValue(name);
				}
				else if (pList.get(i).getUri().compareTo("http://dbpedia.org/ontology/abstract") == 0){
					System.out.println("abstract final: " + abst);
					pList.get(i).setValue(abst);
				}
			}			
		}
		
		System.out.println("cant: " + cont);
		
		System.out.println("***NAME DBPEDIA: " + name);
		
		return name;
	}
	
	private static String getAllPropertiesValues(String uri, List<Property> pList){
		
		String apQuery = appendPropertiesInQuery(uri,pList,1); // Navegables, 0:no navegables
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"   WHERE { " +	
		"		{"+
		"			OPTIONAL { "+
		"					<"+uri+">  rdfs:label ?label . " +
		"					FILTER (langMatches(lang(?label), \"en\"))" +
		"			}"+
		"			OPTIONAL { "+
		"					<"+uri+">  <http://dbpedia.org/ontology/abstract>  ?abstract . " +
		"					FILTER (langMatches(lang(?abstract), \"en\")) " +
		"			}"+
		"		}"+
		"		UNION"+
		"		{"+
		"			<"+uri+"> ?property ?value ."+
		"   	} "+
		"	}"+
		"	LIMIT 100";
				
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);

		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		
		/* indicar que las propiedades de la BD no ha sido recien agregadas en la sesion (se obtiene de la BD)*/
		for(int k=0; k<pList.size();k++){
			pList.get(k).setAdd(0);
			pList.get(k).setShow_default(1);
		}
			
		
		
		List<String> urisList= new ArrayList<String>(), valuesList = new ArrayList<String>();
		String propUri, propValue;
		int cont=0;
		String name = null;
		String abst = null;
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			
			if(qsol.contains("property") && qsol.contains("value")){	
				
				
				System.out.println("entro ***! ");

				propUri = qsol.get("property").toString();
				System.out.println("|property: ");
				System.out.println(propUri);
							
				propValue = qsol.get("value").toString();
				System.out.println("|value: ");			
				System.out.println(propValue);
				
				urisList.add(propUri);
				valuesList.add(propValue);
				
				
				int pos = findProperty(propUri,pList);
				System.out.println("pos: "+pos);
				if(pos == -1){ // no encontro la porpiedad en la lista de propiedades del concepto
					//agrega propiedad
					Property p = new Property();
					p.setUri(propUri);
					p.setValue(propValue);
					p.setName("Agregados");
					p.setShow_default(0);
					p.setIs_mapping(0);
					p.setAdd(0);
					p.setNewProperty(1);
					pList.add(p);		
					
					System.out.println("---PROPIEDAD: "+ propUri);
					System.out.println("---Show Default: "+p.getShow_default());
				}
				else { // encontro propiedad -> se actuazlin valores
					pList.get(pos).setValue(propValue);
					//if(pList.get(pos).getNewProperty() == )
					//pList.get(pos).setShow_default(1);
					//pList.get(pos).setAdd(0);
					
					if(propUri.compareTo("http://www.w3.org/2000/01/rdf-schema#label") !=0 && 
							propUri.compareTo("http://dbpedia.org/ontology/abstract") != 0){
						
						if(pList.get(pos).getIs_mapping() == 1 && pList.get(pos).getTarget()>1){ // mapping a dataset en Bio2rdf
							String inputUri = null;
							switch(pList.get(pos).getTarget()){								
								case 2: inputUri = "http://bio2rdf.org/mesh:" + propValue; // MESH
										break;
								case 3: inputUri = "http://bio2rdf.org/pharmgkb:" + propValue;	// PHARMGKB
										break;
								case 4: inputUri = "http://bio2rdf.org/goa:" + propValue;	// NCBI
										break;
								case 5: inputUri = "http://bio2rdf.org/ncbi:" + propValue;	// NCBI
										break;
							}							
						}							
					}
					else if(propUri.compareTo("http://www.w3.org/2000/01/rdf-schema#label") == 0){
						//System.out.println("name final: " + name);
						pList.get(pos).setValue(name);
					}
					else if(propUri.compareTo("http://dbpedia.org/ontology/abstract") == 0){
						//System.out.println("abstract final: " + abst);
						pList.get(pos).setValue(abst);
					}
				}
				
			}
			
			if(qsol.contains("label")){
				System.out.println("entro label***! ");
				
				name = "" + qsol.get("label");
				System.out.println(name);
			}
			if(qsol.contains("abstract")){
				System.out.println("entro abstract***! ");
				abst = "" + qsol.get("abstract");
				System.out.println(abst);
			}
			
			
			cont++;
		}
						
		System.out.println("cant: " + cont);		
		System.out.println("***NAME DBPEDIA: " + name);
		
		return name;
	}
	
	private static int findProperty(String puri, List<Property>pList){
		
		for(int i=0; i<pList.size(); i++){
			//if(pList.get(i).getUri().compareTo(puri) == 0 && pList.get(i).getName().compareTo("Agregados")!=0) 
			Property p = pList.get(i);
			if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0) //propiedades que no han sido recientemente agregadas
				return i;
		}
		
		return -1;
	}
	
	
	private static String appendPropertiesInQuery(String uri, List<Property> pList, int type){
		String cad = "";
		//"   	OPTIONAL { ?superclass rdfs:label ?label. }" +
		
		for(int i=0; i<pList.size(); i++){
			Property p = pList.get(i);
			if(p.getIs_mapping() == type){
				cad += "	OPTIONAL { <"+uri+"> <"+p.getUri()+"> ?value"+i+" . }";
			}
		}
		
		return cad;
	}
	
	private static int selectUriMatchClass(List<String> types, List<Class> classesDataset){
		int i=0;
		boolean found = false;
		for(i=0; i< types.size(); i++){
			
			for(int k = 0; k < classesDataset.size(); k++){
				if(classesDataset.get(k).getUri().compareTo(types.get(i))==0){
					found = true;
					break;
				}
			}
			
			if(found) break;
		}		
		return i; 
	}
	
	/*************************************************************************/
	/* LISTA DE CONCEPTOS: COINCIDENCIA SIMILAR EN NOMBRE */
	/*************************************************************************/

	public static List<Concept> searchTermBySimilarName(String input){
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
						"   SELECT DISTINCT * " +
						"   WHERE { " +				
						"       ?x rdfs:label ?label . " +		
						"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
						"   } "+
						"	LIMIT 20";
		
		
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
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
	
	/* PARA LA NAVEGACION :
	 * al presionar un termino, el uri se coloca en el combobox
	 * se pasa ese parametro al Appservlet para iniciar la nueva busqueda
	 * */
	
	public static Concept searchByUri(String uriInput){
		
				
		/*
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
			"   SELECT DISTINCT * " +
			"   WHERE { " +				
			"       ?x rdfs:label ?label . " +		
			"		{ "+
			"			<" + uriInput + "> rdf:type ?class . "+
			"			<" + uriInput + "> rdf:type owl:Thing . "+
			"		} "+
			"		UNION"+
			" 		{ "+
			"			<" + uriInput + "> <http://dbpedia.org/ontology/wikiPageRedirects> ?redirected . "+
			"			?redirected rdf:type ?class . "+
			"		} "+
			"		UNION { "+
			"			<" + uriInput + "> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?amb . "+
			"			?amb rdf:type ?type ."+
			"			FILTER (CONTAINS(str(?type), \"http://dbpedia.org/ontology/\" ))"+		//http://dbpedia.org/ontology/Disease
			"		}"+
			"   } ";
		*/
		
		System.out.println("URI buscado: " + uriInput);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				"   SELECT DISTINCT * " +
				"   WHERE { " +		
				"		{	"+
				"       	<" + uriInput + "> rdfs:label ?label . " +	
				"		}	"+
				"		UNION	" +
				"		{	"+
				"			<" + uriInput + "> rdf:type ?class . "+
				//owl thing
				"		}	"+
				"		UNION	" +
				"		{	"+
				"			<" + uriInput + "> <http://dbpedia.org/ontology/wikiPageRedirects> ?redirected . "+
				"			?redirected rdf:type ?class . "+
				"		}	"+
				"		UNION	" +
				"		{	"+
				"			<" + uriInput + "> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?amb . "+
				"			?amb rdf:type ?type ."+
				"			FILTER (CONTAINS(str(?type), \"http://dbpedia.org/ontology/\" ))"+		//http://dbpedia.org/ontology/Disease
				"		}"+
				"   } ";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);

		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);    
						
		
		// Informacion del resultado
		int i=0;
		String uri = null;
		String name = null;	
		// obtener de la BD las clases del dataset
		
		List<Class> classesDataset = ClassDAO.getAllClassesByDataset(1);
		//
		for(int k=0; k<classesDataset.size(); k++){
			System.out.println("clase " + k + ": " + classesDataset.get(k).getUri());
		}
		
		
		List<String> uris = new ArrayList<String>();
		List<String> types = new ArrayList<String>(); // clase del recurso
		String aux = null;
		String pattern = "http://dbpedia.org/ontology/"; 
		
		System.out.println("\nCLASES: \n");
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	

			if(qsol.contains("class") && !qsol.contains("redirected") && !qsol.contains("amb")){				
		    	//System.out.println("class type: " + qsol.get("x")); //uri
		    	//c.setDefinition(""+qsol.get("obodef"));		    	
		    	aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia		    	
		    	if(aux.contains(pattern)){
		    		System.out.println("class: " + aux);
		    		types.add(aux);	
		    		/*
		    		aux = qsol.get("x").toString(); //uri del recurso
			    	uris.add(aux);
			    	*/		    		
		    	}
		    		    	
			}
			else if(qsol.contains("redirected") && !qsol.contains("amb")){
				//System.out.println("redirected type: " + qsol.get("redirected")); //uri			
		    	
				aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia
		    	if(aux.contains(pattern)){	
		    		System.out.println("class: " + aux);
					types.add(aux);
					/*
					aux = qsol.get("redirected").toString(); //uri del recurso
					uris.add(aux);
					*/
		    	}				
			} 
			else if(qsol.contains("amb")){
				//System.out.println("ambiguos type: " + qsol.get("amb")); //uri
				
				aux = qsol.get("type").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia				
				if(aux.contains(pattern)){	
					System.out.println("class: " + aux);
					types.add(aux);		
					/*
					aux = qsol.get("amb").toString();
					System.out.println("uri: " + aux); //uri del recurso
			    	uris.add(aux);
			    	*/
				}
			}
				
			i++;
		} 
		
		
		int posUri = -1;
		// AHORA DEVUELVO SOLO UNA CLASE, PODRIA DEVOLVER VARIAS (EJ: BONE Y ANATOMICAL STRUCTURE)
		System.out.println("uris size: " + uris.size());
		System.out.println("types size: " + types.size());
		
		if(types.size()>0){
			
			posUri = selectUriMatchClass(types,classesDataset); //obtener uri cuya clase este definida en DBPedia (creo que tambien deberia pasarle "label" y verificar que sean iguales)
			
			System.out.println("posUri: " + posUri);
			
			System.out.println("Longitud types list: " + types.size());
			System.out.println(" URI ENCONTRADO: "+ posUri);
			System.out.println(" TERMINO BUSQUEDA !");
			
			System.out.println("\n******************************");
			//System.out.println("Concept Uri:" + uris.get(posUri));
			System.out.println("Concept Class:" + types.get(posUri));
			System.out.println("\n******************************");
		}
		
		
		
		
		// PROPIEDADES DE LA CLASE A LA QUE PERTENECE EL RECURSO (URI)
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		
		if(posUri != -1){
			System.out.println(" \nMY PROPERTIES! ");
			getPropertiesValues(uriInput,pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
		}
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
	/* BUSQUEDA POR COINCIDENCIA EN PROPIEDAD */
	/*************************************************************************/

	
	public static List<Concept> searchTermByPropertyMatch(String input){
		
		String classesQuery = concatenateClassesForSimilarMatch(1);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
					"   SELECT DISTINCT ?x ?label    " +
					"   WHERE { " +		
					"		?x ?prop ?p . " +
					"		?x rdfs:label ?label . " +
					classesQuery +
					"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
					"   } " +
					"LIMIT 10";
					
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);
				
				
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
	
	
	private static String concatenateClassesForSimilarMatch(int dataset){
		String cad = null;
		
		List<Class> cList = new ArrayList<Class>();
		
		cList = ClassDAO.getAllClassesByDataset(dataset); // clases de DBPEDIA
		
		int tam = cList.size();
		
		cad = "{";		
		for(int i=0; i< 2; i++){
			cad += " ?x rdf:type <" + cList.get(i).getUri() + "> . ";
			cad += " } ";
			if( i < 2 - 1 ){
				cad += " UNION ";
				cad += " { ";
			}
		}
				
		return cad;		
	}
	
}

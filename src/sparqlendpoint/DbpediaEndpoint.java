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
import org.apache.jena.sparql.function.library.execTime;

import dao.ClassDAO;
import dao.PropertyDAO;
import model.Concept;
import model.Property;
import model.PropertyGroup;
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
		
		System.out.println("cad exactMatch: " + cad);
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
		"			?redirected rdf:type ?class . "+
		"		} "+
		"		UNION { "+
		"			?x <http://dbpedia.org/ontology/wikiPageDisambiguates> ?amb . "+
		"			?amb rdf:type ?type . "+
		"			FILTER (CONTAINS(str(?type), \"dbpedia.org/ontology/\" )) "+		//http://dbpedia.org/ontology/Disease
		"		}"+
		"		FILTER ( ?label = \""+ cad2 +"\"@en ) " +
		//"		FILTER regex(str(?x), \"http://dbpedia.org/resource/\" )"+
		//"		FILTER (CONTAINS(str(?x), \"http://dbpedia.org/resource/\" )) "+
		"		FILTER (CONTAINS(str(?x), \"dbpedia.org/\" )) "+
		"   } ";
		
		System.out.println("search by exact QUERY");
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
			//System.out.println("clase " + k + ": " + classesDataset.get(k).getUri());
		}
		
		List<String> uris = new ArrayList<String>();
		List<String> types = new ArrayList<String>(); // clase del recurso
		String aux = null;
		String pattern1 = "http://dbpedia.org/ontology/"; 
		String pattern2 = "http://www.w3.org/2002/07/owl#Thing";
		
		//System.out.println("\nCLASES: \n");
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
			//System.out.println("entro :)");
			if(qsol.contains("class") && !qsol.contains("redirected") && !qsol.contains("amb")){	
				System.out.println("entro 11! ");
		    	//System.out.println("class type: " + qsol.get("x")); //uri
		    	//c.setDefinition(""+qsol.get("obodef"));		    	
		    	aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia		    	
		    	if(aux.contains(pattern1) || aux.contains(pattern2)){
		    		//System.out.println("entro 11! ");
		    		//System.out.println("class: " + aux);
		    		types.add(aux);	
		    		aux = qsol.get("x").toString(); //uri del recurso
			    	uris.add(aux);
		    	}
		    		    	
			}
			else if(qsol.contains("redirected") && !qsol.contains("amb")){
				//System.out.println("redirected type: " + qsol.get("redirected")); //uri			
				//System.out.println("entro 21! ");
				aux = qsol.get("class").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia
		    	if(aux.contains(pattern1) || aux.contains(pattern2)){	
		    		//System.out.println("entro 22! ");
		    		//System.out.println("class: " + aux);
					types.add(aux);
					aux = qsol.get("redirected").toString(); //uri del recurso
					uris.add(aux);
		    	}				
			} 
			else if(qsol.contains("amb")){
				//System.out.println("ambiguos type: " + qsol.get("amb")); //uri
				//System.out.println("entro 31! ");
				aux = qsol.get("type").toString(); //verificar si esta clase pertenece a las definidas en la BD para DBpedia				
				if(aux.contains(pattern1) || aux.contains(pattern2)){	
					//System.out.println("entro 32! ");
					//System.out.println("class: " + aux);
					types.add(aux);		
					aux = qsol.get("amb").toString();
					//System.out.println("uri: " + aux); //uri del recurso
			    	uris.add(aux);
				}
			}
				
			i++;
		} 
		
		//mostrar las clases en type (verificar si se lleno)
		//System.out.println("clases del recurso! ");
		for(int w=0; w<types.size(); w++){
			System.out.println(types.get(w));
		}
		
		int posUri = -1;
		
		//System.out.println("Types size dbpedia: " + types.size());
		if(uris.size()>0 && types.size()>0){
			posUri = selectUriMatchClass(types,classesDataset); //obtener uri cuya clase este definida en DBPedia (creo que tambien deberia pasarle "label" y verificar que sean iguales)
		}
		/*
		System.out.println("Longitud types list: " + types.size());
		System.out.println(" URI ENCONTRADO: "+ posUri);
		System.out.println(" TERMINO BUSQUEDA !");
		
		System.out.println("\n******************************");
		System.out.println("Concept Uri:" + uris.get(posUri));
		System.out.println("Concept Class:" + types.get(posUri));
		System.out.println("\n******************************");
		*/
		/* AHORA DEVUELVO  propiedades de SOLO UNA CLASE, PODRIA DEVOLVER VARIAS (EJ: BONE Y ANATOMICAL STRUCTURE)*/
		// DENTRO DE UN FOR ...
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println("PROPERTIES CLASS :P");
		printProperties(pList);
		
		/*
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		
		System.out.println(" \nMY PROPERTIES! ");
		System.out.println("pList size: "+ pList.size());
		*/
		List<PropertyGroup> pgList = new ArrayList<>();
		
		Concept c = getAllPropertiesValues(uris.get(posUri),pList, pgList);
		
		//String conceptName = getPropertiesValues(uris.get(posUri),pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
		
		//if(pgList.size()==0) pgList = null;
		
		/*
		System.out.println(" \nPROPERTIES! WITH VALUES");
		System.out.println("pList size: "+ pList.size());
		*/
		
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
	    
	    c.setDataset("DBPedia");
		qexec.close();
		/*
		Concept c = new Concept();
		//c.setName(conceptName);
		c.setUri(uris.get(posUri));
		c.setProperties(pList);
		c.setPropertyGroups(pgList);
		*/
		return c;
	}
	
	private static void printProperties(List<Property> pList){
		
		System.out.println("-------------------------------\n     PROPERTIES DBPEDIA \n-------------------------------");
		for(int i=0; i< pList.size(); i++){
			System.out.println("" +i +")");
			System.out.println("uri: " + pList.get(i).getUri());
			System.out.println("value: "+ pList.get(i).getValue());
			System.out.println("show_default: "+ pList.get(i).getShow_default());
			System.out.println("consolidated: "+ pList.get(i).getConsolidated());
			System.out.println("instances: "+ pList.get(i).getInstances());
		}
		
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
		
		
		System.out.println("query-Dbpedia, byUri: ");
		System.out.println(sparqlQueryString1);
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
			
		
		System.out.println("====================================================");
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
						pList.get(i).setShow_default(1); /* busqueda por uri*/
				}
				else if (pList.get(i).getUri().compareTo("http://www.w3.org/2000/01/rdf-schema#label") == 0){
					System.out.println("name final: " + name);
					pList.get(i).setValue(name);
					pList.get(i).setShow_default(1); /* busqueda por uri*/
				}
				else if (pList.get(i).getUri().compareTo("http://dbpedia.org/ontology/abstract") == 0){
					System.out.println("abstract final: " + abst);
					pList.get(i).setValue(abst);
					pList.get(i).setShow_default(1); /* busqueda por uri*/
				}
			}			
		}
		
		System.out.println("cant: " + cont);
		
		System.out.println("***NAME DBPEDIA: " + name);
		
		qexec.close();
		
		return name;
	}
	
	private static Concept getAllPropertiesValues(String uri, List<Property> pList, List<PropertyGroup> pgList){
		
		//String apQuery = appendPropertiesInQuery(uri,pList,1); // Navegables, 0:no navegables
		
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
		//"			FILTER (langMatches(lang(?value), \"en\")) "+
		"			OPTIONAL { " + 
		"						?value <http://www.w3.org/2000/01/rdf-schema#label> ?proplabel . "+
		"						FILTER (langMatches(lang(?proplabel), \"en\")) " +
		"					} " +
		"   	} "+
		"	}";
		//"	LIMIT 200";
				
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql", query);

		System.out.println("dbpedia query: \n\n" + query);
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		
		/* indicar que las propiedades de la BD no ha sido recien agregadas en la sesion (se obtiene de la BD)*/
		/*
		for(int k=0; k<pList.size();k++){
			pList.get(k).setAdd(0);
			pList.get(k).setShow_default(1);
		}
		*/
		
		/* CODIGO NUEVO */
		
		List<Property> propList = new ArrayList<>();
		List<PropertyGroup> propgroupList = new ArrayList<>();
		Concept c = new Concept();
		String aux,value,property;
		
		while (results.hasNext()) {
			QuerySolution qsol = results.nextSolution();		
			if(qsol.contains("property") && qsol.contains("value")){	
				property = "" + qsol.get("property");
				value = "" + qsol.get("value");
				
				//type
				if(property.toString().compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")==0){
					if(value.contains("www.w3.org/2002/07/owl#Thing") || value.contains("dbpedia.org/ontology/")){
						//int posProperty = searchProperty(aux, pList);
						
						//if(posProperty >0 ){
							Property p = new Property();
												
							p.setUri(property);		
							p.setValue(value);
							
							if(qsol.contains("proplabel")){
								aux = "" + qsol.get("proplabel");				
								p.setLabel(aux);
							}
							//copiar datos de la prop original
							p.setConsolidated(1);
							p.setShow_default(1);
							p.setInstances(1);
							p.setName("Clase");
							propList.add(p);						
						//}
					}
				}
				else {
					//asegurar que sea en ingles
					boolean valid = true;
					if(value.contains(".@") || value.contains("?@") || value.contains("@") ){
						System.out.println("value:\n" + value);
						if(value.contains("@en"))
							valid = true;
						else
							valid = false;
						
						System.out.println("valid: " + valid);
					}					
					if(valid){
						aux = "" + qsol.get("property");					
						int posProperty = searchProperty(aux, pList);
						/*
						if(posProperty >0 ){
							Property p = new Property();
												
							p.setUri(aux);		
							p.setValue(value);
							
							if(qsol.contains("proplabel")){
								aux = "" + qsol.get("proplabel");				
								p.setLabel(aux);
							}
							//copiar datos de la prop original
							p.setConsolidated(pList.get(posProperty).getConsolidated());
							p.setShow_default(pList.get(posProperty).getShow_default());
							
							propList.add(p);						
						}
						*/
						
						Property p = new Property();
											
						p.setUri(aux);		
						p.setValue(value);
						
						if(qsol.contains("proplabel")){
							aux = "" + qsol.get("proplabel");				
							p.setLabel(aux);
						}
						
						//copiar datos de la prop original
						if(posProperty >0 ){
							p.setConsolidated(pList.get(posProperty).getConsolidated());
							p.setShow_default(1);
							p.setIs_mapping(pList.get(posProperty).getIs_mapping());
							p.setName(pList.get(posProperty).getName());
							p.setInstances(pList.get(posProperty).getInstances());
						}
						else {
							p.setConsolidated(0);
							p.setShow_default(0);
							p.setIs_mapping(0);
						}
							
						propList.add(p);						
						
					}					
				}	
			}
			
			if(qsol.contains("label")){
				//System.out.println("entro label***! ");
				
				aux = "" + qsol.get("label");
				c.setName(aux);
				//System.out.println(name);
				Property p = new Property();
				p.setUri("http://www.w3.org/2000/01/rdf-schema#label");
				p.setName("Nombre");
				p.setValue(aux);
				//copiar datos de la prop original
				p.setConsolidated(1);
				p.setShow_default(1);
				propList.add(p);
			}
			/*
			if(qsol.contains("abstract")){
				//System.out.println("entro abstract***! ");
				
				aux = "" + qsol.get("abstract");
				c.setDefinition(aux);
				//System.out.println(abst);
				Property p = new Property();
				p.setUri("http://dbpedia.org/ontology/abstract");
				p.setValue(aux);
				p.setConsolidated(1);
				p.setShow_default(1);
				propList.add(p);
			}
			*/
		}
		
		System.out.println("     ANTES:     ");
		System.out.println("propList size: " + propList.size());
		System.out.println("propgroupList size: " + propgroupList.size());		
		
		System.out.println("PRINT propList");
		printProperties(propList);
		regroupPropertyList(propList, propgroupList);
		
		System.out.println("     DESPUES:     ");
		System.out.println("propList size: " + propList.size());
		System.out.println("propgroupList size: " + propgroupList.size());
		
		c.setUri(uri);
		c.setProperties(propList);
		c.setPropertyGroups(propgroupList);
		
		//////////////////////////////////////////
		printProperties(pList);		
		qexec.close();
		
		return c;
	}
	
	
	private static int searchProperty(String uriProp, List<Property> pList){
		
		int pos = 0;
		
		
		for(int i=0; i<pList.size(); i++){
			if(uriProp.compareTo(pList.get(i).getUri())==0){
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	
	private static void getMappingUri(Property p, String propValue, String propLabel){
		
		// Es propiedad de mapeo
		if(p.getIs_mapping() == 1 && p.getTarget()>1){ // mapping a dataset en Bio2rdf
			String inputUri = null;
			switch(p.getTarget()){								
				case 2: inputUri = "http://bio2rdf.org/mesh:" + propValue; // MESH
						break;
				case 3: inputUri = "http://bio2rdf.org/pharmgkb:" + propValue;	// PHARMGKB
						break;
				case 4: inputUri = "http://bio2rdf.org/goa:" + propValue;	// NCBI
						break;
				case 5: inputUri = "http://bio2rdf.org/ncbi:" + propValue;	// NCBI
						break;
			}	
			p.setValue(inputUri);
			return ;
		}
		
		p.setValue(propValue);
		p.setLabel(propLabel);
	}
	
	/*
	 * res:
	 * -1 -> no exite la propiedad en la lista simple (son las que son opcionales para el usuario, no esta la relacion en la BD)
	 * 0 -> encontro la propiedad vacia, la llena (es la primera coincidencia)
	 * 1 -> encontro la propiedad con un valor, se debe crear un grupo y pasar ambas props a ese grupo
	 * 2 -> encontro grupo y propiedad exactamente igual (con mismo valor) en el grupo
	 * 3 -> encontro grupo, pero no propiedad
	 * */
	
	
//	private static int [] findProperty(String puri, String propvalue, String propLabel, List<Property>pList, List<PropertyGroup> pgList){
//		int [] res = new int [3];
//		
//		if(pList!=null){
//			System.out.println("entro a lista simple");
//			for(int i=0; i<pList.size(); i++){
//				//if(pList.get(i).getUri().compareTo(puri) == 0 && pList.get(i).getName().compareTo("Agregados")!=0) 
//				Property p = pList.get(i);
//				/*
//				if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0){ //propiedades que no han sido recientemente agregadas
//					res[0] = 1; //  lo encontro en la lista simple de propieades 
//					res[1] = i; 
//					return res;
//				}
//				*/				
//				if(p.getUri().compareTo(puri) == 0 && p.getValue()==null){ //propiedades que no han sido recientemente agregadas
//					res[0] = 0; //  lo encontro en la lista simple de propieades 
//					res[1] = -1; 
//					res[2] = -1;
//					getMappingUri(p, propvalue, propLabel);	//EVALUA SI ES QUE MAPEA O NO				
//					p.setAdd(0);
//					return res;
//				}
//				if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0 && 
//					p.getValue()!= null && p.getValue().compareTo(propvalue)!=0){ //propiedades que no han sido recientemente agregadas
//					res[0] = 1; //  lo encontro en la lista simple de propieades 
//					res[1] = i; 
//					res[2] = -1;
//					return res;
//				}
//			}
//		}
//		res[0] = 1;
//		res[1] = -1;
//		res[2] = -1;
//		
//		boolean groupFound = false;
//		if(pgList != null){
//			//System.out.println("entro a lista group");
//			//System.out.println("pURI: " + puri);
//			for(int i=0; i<pgList.size(); i++){				
//				if(pgList.get(i).getUri().compareTo(puri) == 0){ //propiedades que no han sido recientemente agregadas
//					res[0] = 3; /* existe la lista group para esa propiedad*/
//					res[1] = i; /* posicion del grupo en la lista*/ 
//					groupFound = true;
//					List<Property> propgList = pgList.get(i).getPropertyList();
//					for(int x=0; x < propgList.size(); x++){
//						//System.out.println("propgList value: " + propgList.get(x).getValue());
//						if(propgList.get(x).getValue().compareTo(propvalue)==0){
//							res[0] = 2; // Existe propiedad en el grupo
//							res[2] = x; /* encontro propiedad en el grupo*/
//							return res;
//						}
//					}
//					res[2] = -1;
//				}
//			}
//		}
//		if(groupFound) return res;
//		
//		res[0] = -1;
//		return res;
//	}
	
	
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
					System.out.println("uri class (dbpedia): " + classesDataset.get(k).getUri());
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

		System.out.println("URI buscado: " + uriInput);

		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
						"   SELECT DISTINCT * " +
						"   WHERE { " +		
						"       <" + uriInput + "> rdfs:label ?label . " +	
						"		{	"+
						"			<" + uriInput + "> rdf:type ?class . "+
						"			<" + uriInput + "> rdf:type owl:Thing . "+
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
						"			FILTER (CONTAINS(str(?type), \"dbpedia.org/ontology/\" ))"+		//http://dbpedia.org/ontology/Disease
						"		}"+
						"   } ";
		
		System.out.println("search by uri query: ");
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
		    	//System.out.println("class antes 1: " + aux);
		    	if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){
		    		//System.out.println("class despues 1: " + aux);
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
				//System.out.println("class antes 2: " + aux);
		    	if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){	
		    		//System.out.println("class despues 2: " + aux);
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
				//System.out.println("class despues 3: " + aux);
				if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){	
					//System.out.println("class despues 3: " + aux);
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
		
		System.out.println("TYPES:");
		for(int k=0; k<types.size(); k++){
			System.out.println("clase " + k + ": " + types.get(k));
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
		System.out.println("se cae - posUri: " + posUri);
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		List<PropertyGroup> pgList = new ArrayList<>();
		
		Concept c = new Concept();
		if(posUri != -1){
			System.out.println(" \nMY PROPERTIES! ");
			//getPropertiesValues(uriInput,pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
			c = getAllPropertiesValues(uriInput, pList, pgList);
		}
		System.out.println(" \nPROPERTIES! WITH VALUES");
		for(int h=0; h< pList.size(); h++){
			System.out.println("uri: " + pList.get(h).getUri());		
			//System.out.println(pList.get(h).getName());	
			//System.out.println(pList.get(h).getValue());	
			System.out.println("show_default: " + pList.get(h).getShow_default());
		}
		
	    // obtener clase a la que pertenece (?class)			    
	    // consultar en la BD las propiedades que corresponden a la clase 	    
	    // PROPIEDADES TIPO 1: navegables	    	    
	    // PROPIEDADES TIPO 2: info sobre el recurso
	    
	    
		qexec.close();	
		
		/* REAGRUPAR EN PREPERTY GROUPS*/
		
		/*
		System.out.println("antes de regroup--");
		regroupPropertyList(pList, pgList);
		System.out.println("despues de regroup--");
		*/
		
		//c.setProperties(pList);
		//c.setPropertyGroups(pgList);
		c.setDataset("DBPedia");
		return c;
	}
	
	
	private static void regroupPropertyList(List<Property> pList, List<PropertyGroup> pgList){

		List<Property> auxList = new ArrayList<Property>(pList);
		boolean repite = false;
		boolean agrupada = false;
		
		//limpiar pList
		pList.clear();
		for(int i=0; i<auxList.size(); i++){ //para cada propiedad
			Property p = auxList.get(i);
			repite = false;
			int j;
			//buscar en lista simple
			for(j=0; j < pList.size(); j++){ 
				if(p.getUri().compareTo(pList.get(j).getUri()) ==0 ){
					repite = true;
					break;
				}
			}
			
			if(repite){ //crear nuevo grupo
				PropertyGroup pg = new PropertyGroup();
				List<Property> props = new ArrayList<Property>();
				props.add(p);
				props.add(pList.get(j));
				//property group
				pg.setUri(p.getUri());
				pg.setName(p.getName());
				pg.setConsolidated(p.getConsolidated());
				pg.setShow_default(p.getShow_default());
				pg.setPropertyList(props);
				pg.setMapping(p.getIs_mapping());
				pg.setInstances(p.getInstances());
				//agregar a lista final
				pgList.add(pg);
				
				//eliminar de lista simple
				p = pList.remove(j);
			}
			else {
				//si no esta en lista simple, buscar en lista de grupos
				int k;
				agrupada = false;
				for(k=0; k < pgList.size(); k++){
					if(p.getUri().compareTo(pgList.get(k).getUri()) == 0){
						agrupada = true;
						break;
					}
				}
				
				//agregar las que no se repiten a la lista simple
				if(agrupada){
					PropertyGroup auxpg = pgList.get(k);
					auxpg.getPropertyList().add(p);
				}
				else {
					pList.add(auxList.get(i));
				}
			}
		}
		
		System.out.println("\nANTES\n");
		System.out.println("pList size: " + pList.size());
		System.out.println("auxList size: " + auxList.size());
		System.out.println("pgList size: " + pgList.size());
		//pList = auxList;		
		
		System.out.println("\nDESPUES\n");
		System.out.println("pList size: " + pList.size());
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
	
	static public List<Concept> getInstances(String classUri){
		List<Concept> conceptList = new ArrayList<Concept>();
		
		//
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"   WHERE { " +				
		"       ?s <http://purl.org/dc/terms/subject>  <" + classUri + "> . " + 
		" 		?s rdfs:label ?label . " +
		" 		?s rdfs:comment ?comment . " +
		"		FILTER (langMatches(lang(?label), \"en\"))" +
		"		FILTER (langMatches(lang(?comment), \"en\"))" +
		"   } "+
		"	LIMIT 30";
		
					
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);
					
		String aux;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			Concept c = new Concept();
		    aux = qsol.get("s").toString();		    
		    c.setUri(aux);
		    aux = qsol.get("label").toString();
		    c.setName(aux);
		    aux = qsol.get("comment").toString();
		    c.setDefinition(aux);
		    
		    conceptList.add(c);
		} 
		qexec.close();

		//
		
		return conceptList;
	}
}

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
		"			?redirected rdf:type ?class . "+
		"		} "+
		"		UNION { "+
		"			?x <http://dbpedia.org/ontology/wikiPageDisambiguates> ?amb . "+
		"			?amb rdf:type ?type . "+
		"		FILTER (CONTAINS(str(?type), \"http://dbpedia.org/ontology/\" )) "+		//http://dbpedia.org/ontology/Disease
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
		
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
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
		
		String conceptName = getAllPropertiesValues(uris.get(posUri),pList, pgList);
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
	    
	    
		qexec.close();	
		Concept c = new Concept();
		//c.setName(conceptName);
		c.setUri(uris.get(posUri));
		c.setProperties(pList);
		c.setPropertyGroups(pgList);
		
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
	
	private static String getAllPropertiesValues(String uri, List<Property> pList, List<PropertyGroup> pgList){
		
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
		"			OPTIONAL { ?value <http://www.w3.org/2000/01/rdf-schema#label> ?proplabel . } " +
		"   	} "+
		"	}"+
		"	LIMIT 100";
				
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql", query);

		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		
		/* indicar que las propiedades de la BD no ha sido recien agregadas en la sesion (se obtiene de la BD)*/
		for(int k=0; k<pList.size();k++){
			pList.get(k).setAdd(0);
			pList.get(k).setShow_default(1);
		}
			
		
		
		List<String> urisList= new ArrayList<String>(), valuesList = new ArrayList<String>();
		List<String> propLabelList = new ArrayList<String>();
		String propUri, propValue, propLabel = null;
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
				
				if(qsol.contains("proplabel")){ // Si la propiedad tiene como valor un URI -> busca su "label"
					propLabel = qsol.get("proplabel").toString();
					System.out.println("\ntiene label!!! \n"+ propLabel +"\n\n");
					//propLabelList.add(propLabel);
				}
				
				int [] res = findProperty(propUri, propValue, propLabel,pList,pgList);
								
				//int pos = -1;
				int posPG = -1;
				int posP = -1;

				//System.out.println("pos: "+pos);
				System.out.println("res 0: "+res[0]);
				System.out.println("res 1: "+res[1]);
				System.out.println("res 2: "+res[2]);
				
								
				if(res[0] == -1){ // no encontro la porpiedad en la lista de propiedades del concepto
					//agrega propiedad  (PROPIEDADES QUE EL USUARIO AGREGARA SI DESEA)
					
					if(propUri.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){
						if((propValue.contains("http://dbpedia.org/ontology/") || propValue.compareTo("http://www.w3.org/2002/07/owl#Thing")==0 )){ //podria sacar las clases de la BD
							Property p = new Property();
							p.setUri(propUri);
							p.setValue(propValue);
							/////................
							System.out.println("aux label 1: " + propLabel);
							p.setLabel(propLabel);
							p.setName("Agregados");
							p.setShow_default(0);
							p.setIs_mapping(0);
							p.setAdd(0);
							p.setNewProperty(1);
							pList.add(p);
							
							System.out.println("---PROPIEDAD: "+ propUri);
							System.out.println("---Show Default: "+p.getShow_default());
						}
					}
					else{
						Property p = new Property();
						p.setUri(propUri);
						p.setValue(propValue);
						/////................
						System.out.println("aux label 2: " + propLabel);
						p.setLabel(propLabel);
						p.setName("Agregados");
						p.setShow_default(0);
						p.setIs_mapping(0);
						p.setAdd(0);
						p.setNewProperty(1);
						pList.add(p);
						
						System.out.println("---PROPIEDAD: "+ propUri);
						System.out.println("---Show Default: "+p.getShow_default());
					}							
					
					
				}
				else { // encontro propiedad -> se actuazlin valores
					//pList.get(pos).setValue(propValue);
					//if(pList.get(pos).getNewProperty() == )
					//pList.get(pos).setShow_default(1);
					//pList.get(pos).setAdd(0);
					
					if(propUri.compareTo("http://www.w3.org/2000/01/rdf-schema#label") !=0 && 
							propUri.compareTo("http://dbpedia.org/ontology/abstract") != 0){
						
						
						//if(pos!= -1){ 
							//if(pList.get(pos).getIs_mapping() == 0 || (pList.get(pos).getIs_mapping() == 1 && pList.get(pos).getTarget()==1)){ // DBPEDIA (ej: subject)

								System.out.println("ENTRA A PROPIEDADES MAPPING - GROUP PROPERTY");
								// se crea gouplist, para tener las propiedades que tienen el mismo uri agrupadas
								//if(res[0] == 1 && res[1] == -1){ //NO ENCONTRO GROUP
								if(res[0] == 1 && res[1] != -1){ 
									String aux = pList.get(res[1]).getName();
									if (pgList == null) pgList = new ArrayList<PropertyGroup>();
									else {	
											
											if(propUri.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){
												if((propValue.contains("http://dbpedia.org/ontology/") || propValue.compareTo("http://www.w3.org/2002/07/owl#Thing")==0 )){ //podria sacar las clases de la BD
													//buscar la propiedad en los grupos
													Property pOrig = pList.get(res[1]);											
													Property p = new Property();
													
													aux = pOrig.getUri();
													p.setUri(aux);
													aux = pOrig.getName();
													p.setName(aux);
													aux = pOrig.getLabel();
													System.out.println("aux label 3: " + aux);
													p.setLabel(aux);
													aux = pOrig.getDescription();
													p.setDescription(aux);
													int n = pOrig.getId();
													p.setId(n);
													n = pOrig.getIs_mapping();
													p.setIs_mapping(n);
													n = pOrig.getTarget();
													p.setTarget(n);	
													n = pOrig.getConsolidated();
													p.setConsolidated(n);
													getMappingUri(p,propValue,propValue);
													//p.setValue(propValue);
													p.setAdd(0);
													
													// si no esta inicializado, inicializar lista de grupos
													//if(pgList == null) pgList = new ArrayList<PropertyGroup>();
													
													// crear grupo
													PropertyGroup pg = new PropertyGroup();
													pg.setUri(p.getUri());
													pg.setName(p.getName());
													pg.setConsolidated(p.getConsolidated());
													List<Property> props = new ArrayList<Property>();
													pg.setPropertyList(props);
													
													// agregar a la lista de grupos
													pg.getPropertyList().add(p);	
													
													Property copy = new Property();
													copy.setId(pOrig.getId());
													copy.setUri(pOrig.getUri());
													copy.setName(pOrig.getName());
													System.out.println("aux label 4: " + pOrig.getLabel());
													copy.setLabel(pOrig.getLabel());
													copy.setDescription(pOrig.getDescription());
													copy.setIs_mapping(pOrig.getIs_mapping());
													copy.setAdd(pOrig.getAdd());
													copy.setNewProperty(pOrig.getNewProperty());
													copy.setShow_default(pOrig.getShow_default());
													copy.setTarget(pOrig.getTarget());
													copy.setValue(pOrig.getValue());
													copy.setConsolidated(pOrig.getConsolidated());
													
													pg.getPropertyList().add(copy); // mueve la propiedad que esta en la lista simple -> a un grupo
													// REMOVER pOrig de la lissta inicial
													System.out.println("//////pList size ANTES: "+pList.size());
													Property removed = pList.remove(res[1]);
													
													System.out.println("//////pList size DESPUES: "+pList.size());
													pgList.add(pg);
													System.out.println("CREO GROUP PROPERTY ;)");
												}
												
											}
											else {
												//buscar la propiedad en los grupos
												Property pOrig = pList.get(res[1]);											
												Property p = new Property();
												
												aux = pOrig.getUri();
												p.setUri(aux);
												aux = pOrig.getName();
												p.setName(aux);
												aux = pOrig.getLabel();
												System.out.println("aux label 5: " + aux);
												p.setLabel(aux);
												aux = pOrig.getDescription();
												p.setDescription(aux);
												int n = pOrig.getId();
												p.setId(n);
												n = pOrig.getIs_mapping();
												p.setIs_mapping(n);
												n = pOrig.getTarget();
												p.setTarget(n);	
												n = pOrig.getConsolidated();
												p.setConsolidated(n);
												
												getMappingUri(p,propValue,propValue);
												//p.setValue(propValue);
												p.setAdd(0);
												
												// si no esta inicializado, inicializar lista de grupos
												//if(pgList == null) pgList = new ArrayList<PropertyGroup>();
												
												// crear grupo
												PropertyGroup pg = new PropertyGroup();
												pg.setUri(p.getUri());
												pg.setName(p.getName());
												pg.setConsolidated(p.getConsolidated());
												List<Property> props = new ArrayList<Property>();
												pg.setPropertyList(props);
												
												// agregar a la lista de grupos
												pg.getPropertyList().add(p);	
												
												Property copy = new Property();
												copy.setId(pOrig.getId());
												copy.setUri(pOrig.getUri());
												copy.setName(pOrig.getName());
												System.out.println("aux label 6: " + pOrig.getLabel());
												copy.setLabel(pOrig.getLabel());
												copy.setDescription(pOrig.getDescription());
												copy.setIs_mapping(pOrig.getIs_mapping());
												copy.setAdd(pOrig.getAdd());
												copy.setNewProperty(pOrig.getNewProperty());
												copy.setShow_default(pOrig.getShow_default());
												copy.setTarget(pOrig.getTarget());
												copy.setValue(pOrig.getValue());
												copy.setConsolidated(pOrig.getConsolidated());
												
												pg.getPropertyList().add(copy); // mueve la propiedad que esta en la lista simple -> a un grupo
												// REMOVER pOrig de la lissta inicial
												System.out.println("//////pList size ANTES: "+pList.size());
												Property removed = pList.remove(res[1]);
												
												System.out.println("//////pList size DESPUES: "+pList.size());
												pgList.add(pg);
												System.out.println("CREO GROUP PROPERTY ;)");
											}
										
											
									}
								}
							//}
							
							//if(res[0] == 2 && posPG != -1){ // encontro group
							if(res[0] == 2){
								if(posP != -1){ /* encontro propiedad identica, incluso mismo valor -> no deberia agregar */
									System.out.println("*****PROPIEDAD IDENTICA! ");
								}
							}
							if(res[0] == 3) { /* NO ENCONTRO PROPIEDAD EN EL GROUP -> crearla y agregarla */
								// en  res[2] esta la posicion de la propiedad en la lista simple
								
								if(propUri.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){
									if((propValue.contains("http://dbpedia.org/ontology/") || propValue.compareTo("http://www.w3.org/2002/07/owl#Thing")==0 )){ //podria sacar las clases de la BD
										Property p = new Property();
										Property oldy = pgList.get(res[1]).getPropertyList().get(0); // busco el 1ere elemento del grupo para copiar algo de info
										
										String aux = oldy.getUri();
										p.setUri(aux);
										aux = oldy.getName();
										p.setName(aux);
										aux = oldy.getLabel();
										System.out.println("aux label 7: " + aux);
										p.setLabel(aux);
										aux = oldy.getDescription();
										p.setDescription(aux);
										int n = oldy.getId();
										p.setId(n);
										n = oldy.getIs_mapping();
										p.setIs_mapping(n);
										n = oldy.getTarget();								
										p.setTarget(n);
										//p.setValue(propValue);
										getMappingUri(p,propValue,propValue);
										p.setAdd(0);
										p.setNewProperty(oldy.getNewProperty());
										p.setShow_default(oldy.getShow_default());
										
										pgList.get(res[1]).getPropertyList().add(p);
										
										System.out.println("ENCONTRO GROUP, CREAR PROPERTY ;)");
									}
								}
								else{
									Property p = new Property();
									Property oldy = pgList.get(res[1]).getPropertyList().get(0); // busco el 1ere elemento del grupo para copiar algo de info
									
									String aux = oldy.getUri();
									p.setUri(aux);
									aux = oldy.getName();
									p.setName(aux);
									aux = oldy.getLabel();
									System.out.println("aux label 8: " + aux);
									p.setLabel(aux);
									aux = oldy.getDescription();
									p.setDescription(aux);
									int n = oldy.getId();
									p.setId(n);
									n = oldy.getIs_mapping();
									p.setIs_mapping(n);
									n = oldy.getTarget();								
									p.setTarget(n);
									//p.setValue(propValue);
									getMappingUri(p,propValue, propLabel);
									p.setAdd(0);
									p.setNewProperty(oldy.getNewProperty());
									p.setShow_default(oldy.getShow_default());
									
									pgList.get(res[1]).getPropertyList().add(p);
									
									System.out.println("ENCONTRO GROUP, CREAR PROPERTY ;)");
								}
								
							}
							/*							
							else { //no encontro group -> crear group, propiedad y agregarla (NOTA: si no encontro group, deberia estar en la lista simple)
								
							}
							*/
						//}
					
					}
					/*
					else if(propUri.compareTo("http://www.w3.org/2000/01/rdf-schema#label") == 0){
						//System.out.println("name final: " + name);
						pList.get(pos).setValue(name);
					}
					else if(propUri.compareTo("http://dbpedia.org/ontology/abstract") == 0){
						//System.out.println("abstract final: " + abst);
						pList.get(pos).setValue(abst);
					}
					*/
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
		
		for(int j=0; j<pList.size(); j++){
			if(pList.get(j).getUri().compareTo("http://www.w3.org/2000/01/rdf-schema#label") == 0){
				//System.out.println("name final: " + name);
				pList.get(j).setValue(name);
			}
			if(pList.get(j).getUri().compareTo("http://dbpedia.org/ontology/abstract") == 0){
				//System.out.println("abstract final: " + abst);
				pList.get(j).setValue(abst);
			}
		}
						
		System.out.println("cant: " + cont);		
		System.out.println("***NAME DBPEDIA: " + name);
		
		System.out.println("pgList GROUP size: " + pgList.size());
		
		System.out.println("++++++++++++++++++++++++++++++++++");
		System.out.println("		PG LABEL");
		System.out.println("++++++++++++++++++++++++++++++++++");
		for(int i=0; i<pgList.size(); i++){
			System.out.println(i+") Group uri: " + pgList.get(i).getUri());
			System.out.println(i+") Group consolidated: " + pgList.get(i).getConsolidated());
			System.out.println("pg size: " + pgList.get(i).getPropertyList().size());
			for(int k=0; k < pgList.get(i).getPropertyList().size() ; k++){
				System.out.println(k+". Property value: " + pgList.get(i).getPropertyList().get(k).getValue());
				System.out.println(k+". Property label: " + pgList.get(i).getPropertyList().get(k).getLabel());
				//System.out.println(k+") consolidated: " + c.getPropertyGroups().get(i).getPropertyList().get(k).getConsolidated());
			}
		}
		
		qexec.close();
		
		return name;
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
	private static int [] findProperty(String puri, String propvalue, String propLabel, List<Property>pList, List<PropertyGroup> pgList){
		int [] res = new int [3];
		
		if(pList!=null){
			System.out.println("entro a lista simple");
			for(int i=0; i<pList.size(); i++){
				//if(pList.get(i).getUri().compareTo(puri) == 0 && pList.get(i).getName().compareTo("Agregados")!=0) 
				Property p = pList.get(i);
				/*
				if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0){ //propiedades que no han sido recientemente agregadas
					res[0] = 1; //  lo encontro en la lista simple de propieades 
					res[1] = i; 
					return res;
				}
				*/				
				if(p.getUri().compareTo(puri) == 0 && p.getValue()==null){ //propiedades que no han sido recientemente agregadas
					res[0] = 0; //  lo encontro en la lista simple de propieades 
					res[1] = -1; 
					res[2] = -1;
					getMappingUri(p, propvalue, propLabel);	//EVALUA SI ES QUE MAPEA O NO				
					p.setAdd(0);
					return res;
				}
				if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0 && 
					p.getValue()!= null && p.getValue().compareTo(propvalue)!=0){ //propiedades que no han sido recientemente agregadas
					res[0] = 1; //  lo encontro en la lista simple de propieades 
					res[1] = i; 
					res[2] = -1;
					return res;
				}
			}
		}
		res[0] = 1;
		res[1] = -1;
		res[2] = -1;
		
		boolean groupFound = false;
		if(pgList != null){
			System.out.println("entro a lista group");
			System.out.println("pURI: " + puri);
			for(int i=0; i<pgList.size(); i++){				
				if(pgList.get(i).getUri().compareTo(puri) == 0){ //propiedades que no han sido recientemente agregadas
					res[0] = 3; /* existe la lista group para esa propiedad*/
					res[1] = i; /* posicion del grupo en la lista*/ 
					groupFound = true;
					List<Property> propgList = pgList.get(i).getPropertyList();
					for(int x=0; x < propgList.size(); x++){
						System.out.println("propgList value: " + propgList.get(x).getValue());
						if(propgList.get(x).getValue().compareTo(propvalue)==0){
							res[0] = 2; // Existe propiedad en el grupo
							res[2] = x; /* encontro propiedad en el grupo*/
							return res;
						}
					}
					res[2] = -1;
				}
			}
		}
		if(groupFound) return res;
		
		res[0] = -1;
		return res;
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
		    	System.out.println("class antes 1: " + aux);
		    	if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){
		    		System.out.println("class despues 1: " + aux);
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
				System.out.println("class antes 2: " + aux);
		    	if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){	
		    		System.out.println("class despues 2: " + aux);
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
				System.out.println("class despues 3: " + aux);
				if(aux.contains(pattern) || aux.contains("www.w3.org/2002/07/owl#Thing")){	
					System.out.println("class despues 3: " + aux);
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
		System.out.println("se cae - posUri: " + posUri);
		List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
		
		System.out.println(" \nPROPERTIES! EMPTY ");
		for(int h=0; h< pList.size(); h++){
			System.out.println(pList.get(h).getUri());			
		}
				
		// 
		List<PropertyGroup> pgList = new ArrayList<>();
		if(posUri != -1){
			System.out.println(" \nMY PROPERTIES! ");
			//getPropertiesValues(uriInput,pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
			getAllPropertiesValues(uriInput, pList, pgList);
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
		
		/* REAGRUPAR EN PREPERTY GROUPS*/
		
		/*
		System.out.println("antes de regroup--");
		regroupPropertyList(pList, pgList);
		System.out.println("despues de regroup--");
		*/
		
		Concept c = new Concept();
		c.setProperties(pList);
		c.setPropertyGroups(pgList);
		
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
				pg.setPropertyList(props);
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
		"       ?s rdf:type  <" + classUri + "> . " + 
		" 		?s rdfs:label ?label . " +
		"   } "+
		"	LIMIT 20";
		
					
		
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
		    
		    conceptList.add(c);
		} 
		qexec.close();

		//
		
		return conceptList;
	}
}

package sparqlendpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import controller.InputSearchProcessor;
import dao.ClassDAO;
import dao.DatasetDAO;
import dao.PropertyDAO;
import model.Class;
import model.Concept;
import model.Dataset;
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
	
	public static Concept searchTermByExactMatch(String cad, Dataset dataset){
		System.out.println("DATASET : " + dataset.getName());
		System.out.println("DATASET id: " + dataset.getId());
		System.out.println("cad: " + cad);
		String fromQ = "";
		
		if(dataset!=null)
			fromQ = " FROM <" + dataset.getUri() + "> ";
		
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			" SELECT DISTINCT * " +
			fromQ +
			" WHERE { "+
			" ?s rdf:type ?type . " +
			//"		?s ?property ?value . " + //obtener propiedades
			" ?s <http://purl.org/dc/terms/title> ?label . " +
			" FILTER (UCASE(str(?label)) = \"" + cad.toUpperCase() +"\") " +
			"} "+
			"LIMIT 10";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
	
		ResultSet results = qexec.execSelect();
		
		//ResultSetFormatter.out(System.out, raux, query);    
	
		// Informacion del resultado		
		
		
		String uri = null;
		String aux = null;	

		
		List<Class> classesDataset = ClassDAO.getAllClassesByDataset(dataset.getId());
		
		//List<Property> pList = new ArrayList<Property>();

		int i=0;
		
		List<String> uris = new ArrayList<String>();
		List<String> types = new ArrayList<String>(); // clase del recurso
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			aux = qsol.get("s").toString();
			uris.add(aux);
			aux = qsol.get("type").toString();
			System.out.println("aux : "+aux);
			types.add(aux);

		} 
		
		if(uris.size() > 0){
			// faltaria obtener la DEFINICION del concepto -> DEPENDE DE CADA DATASET
			System.out.println("size URIS: " + uris.size());
			System.out.println("size TYPES: " +types.size());
			System.out.println("size classesDataset: " +classesDataset.size());
			// ELEGIR URI
			int posUri = selectUriMatchClass(types, classesDataset);
			
			List<Property> pList = PropertyDAO.getAllPropertiesByClass(classesDataset.get(posUri).getIdClass());
			
			System.out.println("uris size: " + uris.size());
			System.out.println("pList size: " + pList.size());
			getPropertiesValues(uris.get(posUri),pList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
			
			
			qexec.close();		
			Concept c = new Concept();
			c.setUri(uris.get(posUri));
			c.setProperties(pList);
			c.setProperties(pList);
			return c;
		}
		
		qexec.close();
		
		Concept c = new Concept();
		return c;
	}

	private static void getPropertiesValues(String uri, List<Property> pList){
		
		//String apQuery = appendPropertiesInQuery(uri,pList,1); // Navegables, 0:no navegables
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
			"   SELECT DISTINCT * " +
			"   WHERE { " +		
			"		<"+uri+"> ?property ?value ."+
			"   } "+
			"	LIMIT 200";
		
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);

		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		List<String> urisList= new ArrayList<String>(), valuesList = new ArrayList<String>();
		String propUri, propValue;
		int cont=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			propUri = qsol.get("property").toString();
			System.out.println("|property: ");
			System.out.println(propUri);
						
			propValue = qsol.get("value").toString();
			System.out.println("|value: ");			
			System.out.println(propValue);
			
			urisList.add(propUri);
			valuesList.add(propValue);
			cont++;
		}
				
		for(int i=0; i < pList.size(); i++){
			
			for(int j=0; j < urisList.size(); j++){
				
				if(pList.get(i).getUri().compareTo(urisList.get(j)) == 0){
					pList.get(i).setValue(valuesList.get(j));
				}					
			}			
		}
		
		qexec.close();
		System.out.println("cant: " + cont);
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
	
	
	
	public static Concept searchTermByExactMatchUri(String cad, Dataset dataset){
		
		System.out.println(cad);
		
		System.out.println("DATASET : " + dataset.getName());
		
		String fromQ = "";
		if(dataset!=null)
			fromQ = "	FROM <" + dataset.getUri() + "> ";
		
			/*
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			"	SELECT DISTINCT * " +
			fromQ +
			"	WHERE {"+
			"	<" + cad + "> rdf:type ?type . " +
			" 	<" + cad + "> ?property ?value ." +
			"	<" + cad + "> <http://purl.org/dc/terms/title> ?label1 . " +
			"	}"+
			"	LIMIT 100";
			*/
		
		String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
									" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"   SELECT DISTINCT * " +
				"   WHERE { " +	
				"		{"+
				"			OPTIONAL { "+
				"					<"+cad+">  <http://purl.org/dc/terms/title> ?title . " +
				"			}"+
				"			OPTIONAL { "+
				"					<"+cad+">  <http://purl.org/dc/terms/description>  ?description . " +
				"			}"+
				"			OPTIONAL { "+
				"					<"+cad+">  rdfs:label ?label . " +
				"					FILTER (langMatches(lang(?label), \"en\")) " +
				"			}"+
				"		}"+
				"		UNION"+
				"		{"+
				"			<"+cad+"> ?property ?value ."+
				"   	} "+
				"	}"+
				"	LIMIT 100";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
	
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);    
	
		// Informacion del resultado
		
		String uri = null;
		String aux = null;	

		List<Property> pList = new ArrayList<Property>();
		List<String> classTypeList = new ArrayList<String>();
		Concept c = new Concept();
		c.setUri(cad);
		
		int i=0;
		String name = null;
		String descr = null;
	
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			if(qsol.contains("title")){
				aux = qsol.get("title").toString();
				c.setName(aux);
				name = aux;
			}
			else if(qsol.contains("label")){
					aux = qsol.get("label").toString();
					c.setName(aux);
				}				
			
			if(qsol.contains("description")){
				aux = qsol.get("description").toString();
				c.setDefinition(aux);
				descr = aux;
			}
			
						
			if(qsol.contains("property") && qsol.contains("value")){	
				
				
				
				System.out.println("entro ***! ");
				
				Property p = new Property();
				
				aux = qsol.get("property").toString();
				
				System.out.println("|property: ");
				System.out.println(aux);
				p.setUri(aux);
				
				if(aux.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){					
					classTypeList.add(qsol.get("value").toString());
				}
				aux = qsol.get("value").toString();
				System.out.println("|value: ");			
				System.out.println(aux);
				p.setValue(aux);
				p.setShow_default(1);
				//p.setName("gg "+i);
				pList.add(p);		    		    	
			}
	
			i++;
		} 
	
		System.out.println("classTypeList size: " + classTypeList.size());
		/*
		for(int t=0; t<classTypeList.size(); t++){
			System.out.println(t+") " + classTypeList.get(t));
		}
		*/
		
		// obtener las propiedades de las clases del recurso
		List<Property> propsTotal = new ArrayList<Property>();
		System.out.println("LAS CLASES del recurso: baia baia");
		for(int w=0; w < classTypeList.size(); w++){
			System.out.println("clase "+w +": " + classTypeList.get(w));
			List<Property> props = PropertyDAO.getAllPropertiesByClassUri(classTypeList.get(w));
			System.out.println("props size : " + props.size());
			propsTotal.addAll(props);
		}
		
		System.out.println("pList size ANTES: " + pList.size());
		System.out.println("propsTotal size DESPUES: " + propsTotal.size());
		// asignar aquellas propiedades que hagan match
		boolean found = false;
		List<Property> pFinal = new ArrayList<Property>();
		for(int k=0; k < pList.size(); k++){
			String pUri = pList.get(k).getUri();
			//System.out.println("pUri: " + pUri);
			for(int h=0; h<propsTotal.size(); h++){
				//System.out.println("prop(h): "+propsTotal.get(h).getUri());
				if(pUri.compareTo(propsTotal.get(h).getUri())==0){
					System.out.println("/n/n/nprop is mapping: " + propsTotal.get(h).getIs_mapping());
					System.out.println("pUri: " + pUri);
					System.out.println("prop(h): "+propsTotal.get(h).getUri());					
					pList.get(k).setIs_mapping(propsTotal.get(h).getIs_mapping()); //mapping
					pList.get(k).setName(propsTotal.get(h).getName()); //name
					pFinal.add(pList.get(k));
					//found = true;
					break;
				}
			}
			/*
			if(!found) 
				pList.remove(k);
			found = false;
			*/
		}

		System.out.println("pList size DESPUES: " + pList.size());
		System.out.println("pFinal size DESPUES: " + pFinal.size());
		qexec.close();			
		c.setProperties(pFinal);
		
		return c;
	}
	
	/****************************************************************
	 * 		PARA DBPEDIA: LISTA DE TERMINOS POR COINCIDENCIA "EXACTA" O "SIMILAR"
	 ****************************************************************/
	
	public static List<Concept> getRelatedTermsList(Concept cad, int cant, int posBio [], List<Dataset> datasetList){
		
		System.out.println(cad);
		//char []lowcad = cad.toLowerCase().toCharArray();
		//lowcad[0] = Character.toUpperCase(lowcad[0]);		
		//String cad2 = new String(lowcad);
		
		//System.out.println(lowcad);
				
		String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			"	SELECT DISTINCT * " +
			"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3> "+
			"	WHERE {"+
			"		?s1 rdf:type ?type1 . "+
			"		?s1 <http://purl.org/dc/terms/title> ?label1 . "+
			//"	FILTER (UCASE(str(?label1)) = \"" + cad.toUpperCase() +"\")"+
			"	}"+
			"	LIMIT 100";
		
				
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
		
		return null;
	}
	
	
	
	/*************************************************************************/
	/* LISTA DE CONCEPTOS: COINCIDENCIA SIMILAR EN NOMBRE */
	/*************************************************************************/

	public static List<Concept> searchTermBySimilarName(String input, Dataset dataset){
		
		System.out.println("DATASET : " + dataset.getName());
		
		String fromQ = "";
		if(dataset!=null)
			fromQ = "	FROM <" + dataset.getUri() + "> ";
		
		System.out.println("TERMINO A BUSCAR: " + input);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				"   SELECT DISTINCT * " +
				fromQ +
				"   WHERE { " +				
				"       ?s <http://purl.org/dc/terms/title> ?label . " +		
				"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
				"   } "+
				"	LIMIT 5";				
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		List<Concept> concepts = new ArrayList<>();
		String aux;
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			Concept c = new Concept();
			
			aux = qsol.get("s").toString();
			System.out.println("uri: "+aux);
			c.setUri(aux);
									
			aux = qsol.get("label").toString();
			System.out.println("label: "+aux);
			c.setName(aux);			
			
			concepts.add(c);
			
			i++;
		}
				
		qexec.close();
		return concepts;		
	}


	/*************************************************************************/
	/* BUSQUEDA POR COINCIDENCIA EN PROPIEDAD */
	/*************************************************************************/

	
public static List<Concept> searchTermByPropertyMatch(String input, Dataset dataset){
		
		
		String fromQ = "";
		if(dataset!=null)
			fromQ = "	FROM <" + dataset.getUri() + "> ";
		
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
		
		/*
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
					"   SELECT DISTINCT * " +
					fromQ +
					"   WHERE { " +		
					"		?s <http://purl.org/dc/terms/title> ?label . " +
					"		?s ?property ?value . "+
					"	    FILTER (CONTAINS ( UCASE(str(?value)), \"" + input.toUpperCase() + "\")) " +
					"   } " +
					"LIMIT 5";
		*/
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		fromQ +
		"   WHERE { " +				
		"       ?s <http://purl.org/dc/terms/title> ?label . " +
		"		?s <http://purl.org/dc/terms/description> ?value"+
		"	    FILTER (CONTAINS ( UCASE(str(?value)), \"" + input.toUpperCase() + "\")) " +
		"   } "+
		"	LIMIT 5";
		
					
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);
				
				
		List<Concept> termsList = new ArrayList<Concept>();		
		String aux = null;
		
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	

			if(qsol.contains("s") && qsol.contains("value") ){				
				Concept c = new Concept();
				
		    	aux = qsol.get("s").toString();	   	
		    	c.setUri(aux);
		    	System.out.println("resource uri: " + aux);
		    	
		    	
		    	aux = qsol.get("label").toString();	   	
		    	c.setName(aux);
		    	System.out.println("resource label: " + aux);
		    	
		    	aux = qsol.get("value").toString();	   	
		    	c.setDefinition(aux);
		    	System.out.println("resource descr: " + aux);
		    	
		    	termsList.add(c);		    			    		    	
			}
			
		} 
		qexec.close();
		System.out.println("Property match - termList size: " + termsList.size());
		return termsList;
	}
	
	
	/****************************************************************
	 * 				VERSION 	FINAL
	 * 		PARA DBPEDIA: OBTENER TERMINOS MAPPING y sus propiedades
	 ****************************************************************/
	
	static public List<Concept> getMappingPropertiesValues(Concept term, List<Dataset> datasetList){

		//Buscar si dentro de las propiedades del recurso hay mapping a mesh
		List<Property> pList = term.getProperties();
		List<Concept> cList = new ArrayList<Concept>();
		
		for(int j=0; j < pList.size(); j++){
			Property p = pList.get(j);
			if(p.getIs_mapping() == 1 && p.getValue() != null){
				Dataset dataset = DatasetDAO.getDatasetById(p.getTarget());
				
				if(dataset.getId() != 1){ // SOLO MAPPING A DATASETS DE BIO2RDF
					/*
					String inputUri = null;
					switch(p.getTarget()){
					
						case 2: inputUri = "http://bio2rdf.org/mesh:" + p.getValue(); // MESH
								break;
						case 3: inputUri = "http://bio2rdf.org/pharmgkb:" + p.getValue();	// PHARMGKB
								break;
						case 4: inputUri = "http://bio2rdf.org/goa:" + p.getValue();	// NCBI
								break;
						case 5: inputUri = "http://bio2rdf.org/ncbi:" + p.getValue();	// NCBI
								break;
					}
					*/
					Concept c = null;
					/*
					if(inputUri != null){
						System.out.println("ENTRO A inputUri !!");
						c = searchTermByExactMatchUri(inputUri, dataset);
					}
					*/
					if(p.getValue() != null){
						System.out.println("ENTRO A inputUri !!");
						c = searchTermByExactMatchUri(p.getValue(), dataset);
					}
					
					if(c != null) cList.add(c);
					else continue;
					
					//System.out.println("getMappingPropertiesValues : " +  dataset);
					/*
					if(term !=null){
						c = searchTermByExactMatch(term.name, dataset);
					}
					else System.out.println("Termino nulo D:");
					*/
					cList.add(c);
				}
			}		
		}
		System.out.println("cList size: " + cList.size());
		return cList;
	}
	
	static public List<Concept> searchTermBySimilarName_Datasets(String input, List<Dataset> datasetList){
		
		List<Concept> cList = new ArrayList<Concept>();
		
		for(int i=0; i < datasetList.size(); i++){
			Dataset dat = datasetList.get(i);
			if(dat.getId() != 1){ // DBPEDIA
				List<Concept> aux = searchTermBySimilarName(input, dat);
				cList.addAll(aux);
			}
			
		}

		return cList;
	}
	
	static public List<Concept> searchTermByExactMatch_Datasets(String input, List<Dataset> datasetList){
		
		List<Concept> cList = new ArrayList<Concept>();
		
		for(int i=0; i < datasetList.size() ; i++){
			Dataset dat = datasetList.get(i);
			if(dat.getId() != 1){ // DBPEDIA
				if(!InputSearchProcessor.isUri(input)){					
					Concept aux = searchTermByExactMatch(input, dat);
					cList.add(aux);					
				}
				else { //es uri
					if(input.matches("http://bio2rdf.org/")){
						Concept aux = searchTermByExactMatchUri(input, dat);
						cList.add(aux);
					}
				}			
			}
		}

		return cList;
	}
	
	static public List<Concept> searchTermByPropertyMatch_Datasets(String input, List<Dataset> datasetList){
		
		List<Concept> cList = new ArrayList<Concept>();
		
		for(int i=0; i < datasetList.size(); i++){
			Dataset dat = datasetList.get(i);
			if(dat.getId() != 1){ // DBPEDIA
				List<Concept> aux = searchTermByPropertyMatch(input, dat);
				cList.addAll(aux);
			}
			
		}
		
		return cList;
	}
	
	
}

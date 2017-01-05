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
import model.PropertyGroup;

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
		
		//ResultSetFormatter.out(System.out, results, query);    
	
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
			
			List<PropertyGroup> pgList = new ArrayList<>();
			
			imprimirLista(pList);
			getPropertiesValues(uris.get(posUri),pList,pgList); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
			imprimirLista(pList);
			
			qexec.close();		
			Concept c = new Concept();
			c.setUri(uris.get(posUri));
			//c.setProperties(pList);
			c.setProperties(pList);
			c.setPropertyGroups(pgList);
			
			System.out.println("pglist size*: " + pgList.size());
			return c;
		}
		
		qexec.close();
		
		Concept c = new Concept();
		return c;
	}

	private static void imprimirLista(List<Property> pList){
		
		System.out.println("*****************");
		System.out.println("      LISTA    ");		
		System.out.println("*****************");

		for(int i=0; i<pList.size(); i++){
			System.out.println("uri: " + pList.get(i).getUri());
			System.out.println("value: " + pList.get(i).getValue());
		}
	}
	
	private static void getPropertiesValues(String uri, List<Property> pList, List<PropertyGroup> pgList){
		
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
			//System.out.println("|property: ");
			//System.out.println(propUri);
						
			propValue = qsol.get("value").toString();
			//System.out.println("|value: ");			
			//System.out.println(propValue);
			
			urisList.add(propUri);
			valuesList.add(propValue);
			cont++;
		}
		System.out.println("wtf");
		imprimirLista(pList);
		for(int i=0; i < pList.size(); i++){
			Property prop = pList.get(i);
			for(int j=0; j < urisList.size(); j++){			
				
				if(prop.getUri().compareTo(urisList.get(j)) == 0){ //Se debe agregar la propiedad al termino
					
					propUri = prop.getUri();
					propValue = prop.getValue();
					
					int [] res = findProperty(prop.getUri(), prop.getValue(), pList, pgList);
					
					System.out.println("=========================");
					System.out.println("res 0: " + res[0]);
					System.out.println("res 1: " + res[1]);
					System.out.println("res 3: " + res[2]);
					
					if(res[0] == -1){ // no encontro la porpiedad en la lista de propiedades del concepto
						//agrega propiedad  (PROPIEDADES QUE EL USUARIO AGREGARA SI DESEA)
						
						if(propUri.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){
							if(propValue.contains("http://bio2rdf.org/")){ //podria sacar las clases de la BD para compararlas contra ellas
								Property p = new Property();
								p.setUri(propUri);
								p.setValue(propValue);
								p.setName("Agregados");
								p.setShow_default(0);
								p.setIs_mapping(0);
								p.setAdd(0);
								p.setNewProperty(1);
								pList.add(p);
								
								//System.out.println("---PROPIEDAD: " + propUri);
								//System.out.println("---Show Default: " + p.getShow_default());
							}
						}
						else{
							
							Property p = new Property();
							p.setUri(propUri);
							p.setValue(propValue);
							p.setName("Agregados");
							p.setShow_default(0);
							p.setIs_mapping(0);
							p.setAdd(0);
							p.setNewProperty(1);
							pList.add(p);
							
							//System.out.println("---PROPIEDAD: "+ propUri);
							//System.out.println("---Show Default: "+p.getShow_default());
						}						
												
					}
					else { // encontro propiedad -> se actuazlin valores
						//pList.get(pos).setValue(propValue);
						//if(pList.get(pos).getNewProperty() == )
						//pList.get(pos).setShow_default(1);
						//pList.get(pos).setAdd(0);
						
						if(propUri.compareTo("http://www.w3.org/2000/01/rdf-schema#label") !=0 && 
								propUri.compareTo("http://purl.org/dc/terms/title") != 0){
							
							
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
													if(propValue.contains("http://bio2rdf.org/")){ //podria sacar las clases de la BD
														System.out.println("entro a type :D");
														//buscar la propiedad en los grupos
														Property pOrig = pList.get(res[1]);											
														Property p = new Property();
														
														aux = pOrig.getUri();
														p.setUri(aux);
														aux = pOrig.getName();
														p.setName(aux);
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
														//getMappingUri(p,propValue);
														p.setValue(propValue);
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
													
													//getMappingUri(p,propValue);
													p.setValue(propValue);
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
									/*
									if(posP != -1){ //  encontro propiedad identica, incluso mismo valor -> no deberia agregar 
										System.out.println("*****PROPIEDAD IDENTICA! ");
									}
									*/
								}
								if(res[0] == 3) { /* NO ENCONTRO PROPIEDAD EN EL GROUP -> crearla y agregarla */
									// en  res[2] esta la posicion de la propiedad en la lista simple
									
									if(propUri.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){
										if(propValue.contains("http://bio2rdf.org/")){ //podria sacar las clases de la BD
											Property p = new Property();
											Property oldy = pgList.get(res[1]).getPropertyList().get(0); // busco el 1ere elemento del grupo para copiar algo de info
											
											String aux = oldy.getUri();
											p.setUri(aux);
											aux = oldy.getName();
											p.setName(aux);
											aux = oldy.getDescription();
											p.setDescription(aux);
											int n = oldy.getId();
											p.setId(n);
											n = oldy.getIs_mapping();
											p.setIs_mapping(n);
											n = oldy.getTarget();								
											p.setTarget(n);
											p.setValue(propValue);
											//getMappingUri(p,propValue);
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
										aux = oldy.getDescription();
										p.setDescription(aux);
										int n = oldy.getId();
										p.setId(n);
										n = oldy.getIs_mapping();
										p.setIs_mapping(n);
										n = oldy.getTarget();								
										p.setTarget(n);
										p.setValue(propValue);
										//getMappingUri(p,propValue);
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
					}
					
					if(pList != null) System.out.println("property: " + prop.getUri());
					else System.out.println("plist es null D:");
					
					//System.out.println("valueslist: " + valuesList.get(j));
					prop.setValue(valuesList.get(j));
				}					
			}			
		}
				
		qexec.close();
		System.out.println("cant: " + cont);
		System.out.println("pglist size (get function): " + pgList.size());
	}
		
	
	/*
	 * res:
	 * -1 -> no exite la propiedad en la lista simple (son las que son opcionales para el usuario, no esta la relacion en la BD)
	  * 0  EN VEZ DE VACIA, SI NO LA ENCUENTRA -> encontro la propiedad vacia, la llena (es la primera coincidencia)
	 * 1 -> encontro la propiedad con un valor, se debe crear un grupo y pasar ambas props a ese grupo
	 * 2 -> encontro grupo y propiedad exactamente igual (con mismo valor) en el grupo
	 * 3 -> encontro grupo, pero no propiedad
	 * */
	private static int [] findProperty(String puri, String propvalue, List<Property>pList, List<PropertyGroup> pgList){
		int [] res = new int [3];
		
		if(pList!=null){
			System.out.println("entro a lista simple");
			for(int i=0; i<pList.size(); i++){

				Property p = pList.get(i);
				System.out.println("i: " + i);
				if(p.getUri().compareTo(puri) == 0 && p.getValue()==null){ //propiedades que no han sido recientemente agregadas
					res[0] = 0; //  lo encontro en la lista simple de propieades, PERO VACIO
					res[1] = -1; 
					res[2] = -1;
					//getMappingUri(p, propvalue);	//EVALUA SI ES QUE MAPEA O NO		
					p.setValue(propvalue);
					p.setAdd(0);
					return res;
				}
				
				if(p.getUri().compareTo(puri) == 0 && p.getAdd()==0 && 
					p.getValue()!= null && p.getValue().compareTo(propvalue)!=0){ //propiedades que no han sido recientemente agregadas
					res[0] = 1; //  lo encontro en la lista simple de propieades CON VALOR
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
				p.setShow_default(0);
				//p.setName("gg "+i);
				pList.add(p);		    		    	
			}
	
			i++;
		} 
	
		System.out.println("classTypeList size: " + classTypeList.size());
		System.out.println("pList size: " + pList.size());
		
		/*
		for(int t=0; t<classTypeList.size(); t++){
			System.out.println(t+") " + classTypeList.get(t));
		}
		*/
		
		// obtener las propiedades de las clases del recurso
		List<Property> propsClases = new ArrayList<Property>();
		System.out.println("\n\nLAS CLASES del recurso: baia baia");
		for(int w=0; w < classTypeList.size(); w++){
			System.out.println("clase "+w +": " + classTypeList.get(w));
			List<Property> props = PropertyDAO.getAllPropertiesByClassUri(classTypeList.get(w));
			System.out.println("props size : " + props.size());
			propsClases.addAll(props);
		}
		
		System.out.println("pList size ANTES: " + pList.size());
		System.out.println("propsTotal size DESPUES: " + propsClases.size());
		// asignar aquellas propiedades que hagan match
		boolean found = false;
		List<Property> pFinal = new ArrayList<Property>();
		for(int k=0; k < pList.size(); k++){
			String pUri = pList.get(k).getUri();
			//System.out.println("pUri: " + pUri);
			for(int h=0; h<propsClases.size(); h++){
				//System.out.println("prop(h): "+propsTotal.get(h).getUri());
				if(pUri.compareTo(propsClases.get(h).getUri())==0){
					System.out.println("/n/n/nprop is mapping: " + propsClases.get(h).getIs_mapping());
					System.out.println("pUri: " + pUri);
					System.out.println("prop(h): "+propsClases.get(h).getUri());					
					pList.get(k).setIs_mapping(propsClases.get(h).getIs_mapping()); //mapping
					pList.get(k).setName(propsClases.get(h).getName()); //name
					//pFinal.add(pList.get(k));
					pList.get(k).setShow_default(1);
					
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
		//c.setProperties(pFinal);
		List<PropertyGroup> pgList = new ArrayList<PropertyGroup>();
		
		regroupPropertyList(pList, pgList);
		c.setProperties(pList);
		c.setPropertyGroups(pgList);
		return c;
	}
	
	
	private static void regroupPropertyList(List<Property> pList, List<PropertyGroup> pgList){

		List<Property> auxList = new ArrayList<Property>();
		boolean repite = false;
		boolean agrupada = false;
		
		for(int i=0; i<pList.size(); i++){ //para cada propiedad
			Property p = pList.get(i);
			repite = false;
			int j;
			//buscar en lista simple
			for(j=0; j < auxList.size(); j++){ 
				/*
				System.out.println("p: " + p);
				System.out.println("p uri: " + p.getUri());
				System.out.println("aux: " + auxList.get(j));
				*/
				if(p.getUri().compareTo(auxList.get(j).getUri()) ==0 ){
					repite = true;
					break;
				}
			}
			
			if(repite){ //crear nuevo grupo
				PropertyGroup pg = new PropertyGroup();
				List<Property> props = new ArrayList<Property>();
				props.add(p);
				props.add(auxList.get(j));
				//property group
				pg.setUri(p.getUri());
				pg.setName(p.getName());
				pg.setPropertyList(props);
				//agregar a lista final
				pgList.add(pg);
				
				//eliminar de lista simple
				p = auxList.remove(j);
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
					auxList.add(pList.get(i));
				}
			}
		}
		
		System.out.println("\nANTES\n");
		System.out.println("pList size: " + pList.size());
		System.out.println("auxList size: " + auxList.size());
		System.out.println("pgList size: " + pgList.size());
		pList = auxList;		
		
		System.out.println("\nDESPUES\n");
		System.out.println("pList size: " + pList.size());
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

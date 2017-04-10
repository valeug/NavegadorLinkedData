package sparqlendpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import model.Association;
import model.Class;
import model.Concept;
import model.Dataset;
import model.InferredAssociation;
import model.Property;
import model.PropertyGroup;

public class Bio2RdfEndpoint {
	
	public static final String TREE_ENTRY_PROPERTY_MESH = "http://bio2rdf.org/mesh_vocabulary:mesh-tree-number";
	
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
	
	public static Concept searchTermByExactMatch(String cad, Dataset dataset, List<Concept> mappingList){ //mappingList para que no se repita concepto
		System.out.println("DATASET : " + dataset.getName());
		System.out.println("DATASET id: " + dataset.getId());
		System.out.println("cad: " + cad);
		String fromQ = "";
		
		
		/* 1. dividir cadena en palabras */
		
		//String nombre="Angel Franco García";
		System.out.println("TOKENIZAR: ");
		StringTokenizer tokens=new StringTokenizer(cad);
		
		
		/* 2. verificar si alguna de las palabras es una clase */
		
		List<Class> classesDataset = ClassDAO.getAllClassesByDataset(dataset.getId());
		String clase = null;
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
            System.out.println(token);
            
            for(int t=0; t < classesDataset.size(); t++){
            	if(classesDataset.get(t).getUri().toUpperCase().contains(token.toUpperCase()) == true){
            		clase = classesDataset.get(t).getUri();
            		System.out.println("SI EXISTE LA CLASE!!");
            		break;
            	}
            }
		}
		
		/* 3. generar query */
		
		String innerQuery = "";
		
		if(clase!=null){
			innerQuery =  buildInnerQueryByClass(clase, cad.toUpperCase());
		}
		else {
			innerQuery = buildInnerQuery(classesDataset, cad.toUpperCase());
		}
		System.out.println("inner:\n" + innerQuery);
		
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			" SELECT DISTINCT * " +
			//fromQ +
			" WHERE { "+
			innerQuery +
			" } "+
			"LIMIT 10";
		
		System.out.println("exact match query!!");
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		System.out.println("endpoint: " + dataset.getSparqlEndpoint());
		
		ResultSet results = qexec.execSelect();
		
		//ResultSetFormatter.out(System.out, results, query);    
	
		// Informacion del resultado		
		
		
		String uri = null;
		String auxUri = null;	
		
		//List<Class> classesDataset = ClassDAO.getAllClassesByDataset(dataset.getId());
		
		//List<Property> pList = new ArrayList<Property>();

		int i=0;
		
		//List<String> uris = new ArrayList<String>();
		//List<String> types = new ArrayList<String>(); // clase del recurso
		
		boolean repite = false;
		/* SE OBTIENE URI A PARTIR DEL CUAL SE EMPIEZA NAVEGACION*/
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			auxUri = qsol.get("s").toString();
			repite = false;
			
			for(int p=0; p<mappingList.size(); p++){
				if(auxUri.compareTo(mappingList.get(p).getUri())==0){
					repite = true;
					break;
				}
			}
			
			if(repite == false){	
				break;
			}
		} 
		
		Concept c = null;
		
		System.out.println("auxUri: " + auxUri);
		
		if(repite == false){
			
			c = searchTermByExactMatchUri(auxUri, dataset); // SE CONSULTA CONCEPTO (sus props)
			
			System.out.println("CONCEPT!!!");
			System.out.println("prop size: " + c.getProperties().size());
			System.out.println("pg size: " + c.getPropertyGroups().size());
		}
		
		/*
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
			
			Concept c = new Concept();
			
			//imprimirLista(pList);			
			System.out.println("================================");
			System.out.println("	ANTES GETPROPERTY VALUES  ");
			System.out.println("================================");
			getPropertiesValues(c,uris.get(posUri),pList, dataset); // obtener valores de las propiedades (NAVEGABLES Y DE CARACTERISTICA)
			System.out.println("================================");
			System.out.println("	DESPUES GETPROPERTY VALUES ");
			System.out.println("================================");
			//imprimirLista(pList);
			
			List<PropertyGroup> pgList = new ArrayList<>();
				
			
			c.setUri(uris.get(posUri));
			c.setDataset(dataset.getSparqlEndpoint());
			//c.setProperties(pList);
			c.setProperties(pList);
			c.setPropertyGroups(pgList);
			
			
			regroupPropertyList(pList, pgList);
			//c.setProperties(pList);
			
			c.setPropertyGroups(pgList);
			System.out.println("** plist size: " + pList.size());
			System.out.println("** pGlist size: " + pgList.size());
			
			
			qexec.close();	
			return c;
		}
		
		qexec.close();
		
		System.out.println("fuera exact");
		Concept c = new Concept();
		*/
		return c;
	}
	
	private static void printProperties(List<Property> propList){
		
		System.out.println("---------\n  PROPS\n---------");
		for(int i=0; i< propList.size(); i++){
			System.out.println(i+") ");
			System.out.println("uri: " + propList.get(i).getUri());
			System.out.println("consolidated: " + propList.get(i).getConsolidated());
		}
		
	}
	
	private static String buildInnerQuery(List<Class> classesDataset, String cad){
		String query = "";
		
		for(int i=0; i < classesDataset.size(); i++){
			if(i>0){
				query += " UNION ";				
			}
			query += " { ";
			query += "  ?s rdf:type ?type . ";
			query += "  ?s rdf:type <" + classesDataset.get(i).getUri() + "> . ";			
			query += "  ?s <http://purl.org/dc/terms/title> ?label . ";
			query += "  FILTER (UCASE(str(?label)) = \"" + cad + "\") ";
			query += " } ";
		}
		
		return query;
	}
	
	private static String buildInnerQueryByClass(String classUri, String cad){
		String query = "";
				
		query += " { ";
		query += "  ?s rdf:type ?type . ";
		query += "  ?s rdf:type <" + classUri + "> . ";			
		query += "  ?s <http://purl.org/dc/terms/title> ?label . ";
		query += "  FILTER (UCASE(str(?label)) = \"" + cad + "\") ";
		query += " } ";
			
		return query;
	}
	
	private static void imprimirLista(List<Property> pList){
		
		System.out.println("*****************");
		System.out.println("      LISTA    ");		
		System.out.println("*****************");

		for(int i=0; i<pList.size(); i++){
			System.out.println("uri: " + pList.get(i).getUri());
			System.out.println("value: " + pList.get(i).getValue());
			System.out.println("label: " + pList.get(i).getLabel());
		}
	}
	
	private static void getPropertiesValues(Concept c ,String uri, List<Property> propsClases, Dataset dataset){
		
		//String apQuery = appendPropertiesInQuery(uri,pList,1); // Navegables, 0:no navegables
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
			"   SELECT DISTINCT * " +
			"   WHERE { " +	
			"		{"+
			"			OPTIONAL { "+
			"					<"+uri+">  <http://purl.org/dc/terms/title> ?title . " +
			"			}"+
			"			OPTIONAL { "+
			"					<"+uri+">  <http://purl.org/dc/terms/description>  ?description . " +
			"			}"+
			"			OPTIONAL { "+
			"					<"+uri+">  rdfs:label ?label . " +
			"					FILTER (langMatches(lang(?label), \"en\")) " +
			"			}"+
			"		}"+
			"		UNION"+
			"		{"+
			"			<"+uri+"> ?property ?value . " +
			//"			OPTIONAL { ?value <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?propidentifier . } " +
			"			OPTIONAL { ?value <http://purl.org/dc/terms/title> ?proptitle. } " +
			//"			OPTIONAL { ?value <http://www.w3.org/2000/01/rdf-schema#label> ?proplabel . } " +
			"   	} "+
			"	}"+
			"	LIMIT 300";
		
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		
		System.out.println("getPropertiesValues query: " + query);
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query); 
		
		//List<String> urisList= new ArrayList<String>(), valuesList = new ArrayList<String>(), labelList = new ArrayList<String>();
		List<Property> pList = new ArrayList<Property>();
		
		String aux, name, descr;
		int cont=0;
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
				//System.out.println("entro ***! ");				
				Property p = new Property();
				
				aux = qsol.get("property").toString();
				
				//System.out.println("|property: ");
				//System.out.println(aux);
				p.setUri(aux);
				
				/*
				if(aux.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){					
					classTypeList.add(qsol.get("value").toString());
				}
				*/
				
				if(qsol.contains("proptitle")){
					aux = qsol.get("proptitle").toString();
					p.setLabel(aux);
				}
										
				aux = qsol.get("value").toString();
				//System.out.println("|value: ");			
				//System.out.println(aux);
				p.setValue(aux);					
					
				p.setShow_default(0);
				//p.setName("gg "+i);
				pList.add(p);
			}
		}
		
		System.out.println("wtf");
		//imprimirLista(pList);
		System.out.println("labelList !!!");
		for(int i=0; i < propsClases.size(); i++){
			Property prop = pList.get(i);
			/*
			for(int j=0; j < urisList.size(); j++){			
									
			}
			*/			
		}
				
		qexec.close();
		//System.out.println("cant: " + cont);
		//System.out.println("pglist size (get function): " + pgList.size());
	}
		
	
	/*
	 * res:
	 * -1 -> no exite la propiedad en la lista simple (son las que son opcionales para el usuario, no esta la relacion en la BD)
	  * 0  EN VEZ DE VACIA, SI NO LA ENCUENTRA -> encontro la propiedad vacia, la llena (es la primera coincidencia)
	 * 1 -> encontro la propiedad con un valor, se debe crear un grupo y pasar ambas props a ese grupo
	 * 2 -> encontro grupo y propiedad exactamente igual (con mismo valor) en el grupo
	 * 3 -> encontro grupo, pero no propiedad
	 * */
	private static int [] findProperty(String puri, String propvalue, String label, List<Property>pList, List<PropertyGroup> pgList){
		int [] res = new int [3];
		
		if(pList!=null){
			System.out.println("entro a lista simple");
			for(int i=0; i<pList.size(); i++){

				Property p = pList.get(i);
				
				//System.out.println("i: " + i);
				
				if(p.getUri().compareTo(puri) == 0 && p.getValue()==null){ //propiedades que no han sido recientemente agregadas
					res[0] = 0; //  lo encontro en la lista simple de propieades, PERO VACIO
					res[1] = -1; 
					res[2] = -1;
					//getMappingUri(p, propvalue);	//EVALUA SI ES QUE MAPEA O NO		
					p.setValue(propvalue);
					p.setLabel(label);
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
		/*
		String fromQ = "";
		if(dataset!=null)
			fromQ = "	FROM <" + dataset.getUri() + "> ";
		*/
		
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
				"			<"+cad+"> ?property ?value . " +
				//"			OPTIONAL { ?value <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?propidentifier . } " +
				"			OPTIONAL { ?value <http://purl.org/dc/terms/title> ?proptitle. } " +
				//"			OPTIONAL { ?value <http://www.w3.org/2000/01/rdf-schema#label> ?proplabel . } " +
				"   	} "+
				"	}"+
				"	LIMIT 300";
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		
		System.out.println("uri: " + cad);
		System.out.println("dataset: " + dataset.getSparqlEndpoint());
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);    
	
		// Informacion del resultado
		
		String uri = null;
		String aux = null;	

		List<Property> pList = new ArrayList<Property>();
		List<String> classTypeList = new ArrayList<String>();
		Concept c = new Concept();
		c.setUri(cad);
		c.setDataset(dataset.getSparqlEndpoint());
		
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
				//System.out.println("entro ***! ");				
				Property p = new Property();
				
				aux = qsol.get("property").toString();
				
				if(aux.compareTo(TREE_ENTRY_PROPERTY_MESH) == 0){
					List<Property> treeNodes = getHerarchyElements(qsol.get("value").toString(), dataset);
					if(pList== null ) System.out.println("wtf pList null");
					pList.addAll(treeNodes);
					
				} else {
					//System.out.println("|property: ");
					//System.out.println(aux);
					p.setUri(aux);
					
					if(aux.compareTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") == 0){					
						classTypeList.add(qsol.get("value").toString());
					}
					
					
					if(qsol.contains("proptitle")){
						aux = qsol.get("proptitle").toString();
						p.setLabel(aux);
					}
											
					aux = qsol.get("value").toString();
					//System.out.println("|value: ");			
					//System.out.println(aux);
					p.setValue(aux);					
						
					p.setShow_default(0);
					//p.setName("gg "+i);
					pList.add(p);
				}
				
				
				
			}
	
			i++;
		} 
		
		
		System.out.println("---------------------------------");
		System.out.println("classTypeList size: " + classTypeList.size());
		System.out.println("pList size: " + pList.size());
		
		
		for(int t=0; t<classTypeList.size(); t++){
			System.out.println(t+") " + classTypeList.get(t));
			
			/* si la clase es de CTD  --> buscar asociacion */
			
			//directas
			//List<Property> associations 
			
			//inferidas
			if(classTypeList.get(t).toUpperCase().contains("DISEASE")){
				List<Association> impAssociations = searchImplicitAssociations(cad, classTypeList.get(t));
				System.out.println("impAssociations size: " + impAssociations.size());
				infereAssociations(impAssociations, cad, classTypeList.get(t));
				System.out.println("impAssociations size: " + impAssociations.size());
				c.setAssociations(impAssociations);
			}
		}
		
		
		// obtener las propiedades de las clases del recurso
		List<Property> propsClases = new ArrayList<Property>();
		System.out.println("\n\nLAS CLASES del recurso: baia baia");
		for(int w=0; w < classTypeList.size(); w++){
			System.out.println("clase "+w +": " + classTypeList.get(w));
			List<Property> props = PropertyDAO.getAllPropertiesByClassUri(classTypeList.get(w));
			System.out.println("props size : " + props.size());
			//printProperties(props);
			propsClases.addAll(props);
		}
		
		System.out.println("PROPIEDADES CLASE DAO!");
		//printProperties(propsClases);
		
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
					//System.out.println("/n/n/nprop is mapping: " + propsClases.get(h).getIs_mapping());
					//System.out.println("pUri: " + pUri);
					//System.out.println("prop(h): "+propsClases.get(h).getUri());					
					pList.get(k).setIs_mapping(propsClases.get(h).getIs_mapping()); //mapping
					pList.get(k).setName(propsClases.get(h).getName()); //name
					//pFinal.add(pList.get(k));
					pList.get(k).setShow_default(1);
					pList.get(k).setConsolidated(propsClases.get(h).getConsolidated()) ; //OJOOOOO
					
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
		
		
		System.out.println("ANTES DEL REGROUP");
		
		for(int k=0; k<pList.size(); k++){
			System.out.println(k+") uri: " + pList.get(k).getUri());
			System.out.println(k+") show_default: " + pList.get(k).getShow_default());
		}
		
		
		regroupPropertyList(pList, pgList);
		c.setProperties(pList);
		System.out.println("** plist size: " + pList.size());
		

		System.out.println("DESPUES DEL REGROUP");
		/*
		for(int k=0; k<pList.size(); k++){
			System.out.println(k+") uri: " + pList.get(k).getUri());
			System.out.println(k+") show_default: " + pList.get(k).getShow_default());
		}
		*/
		
		c.setPropertyGroups(pgList);
		
		return c;
	}
	
	private static List<Association> searchImplicitAssociations(String uri, String clase){
		
		List<Association> associations = new ArrayList<>();
		
		// gene
		List<Association> genes = getGenesAssociations(uri, clase);		
		associations.addAll(genes);
		
		// chemical
		List<Association> chemicals = getChemicalsAssociations(uri, clase);
		associations.addAll(chemicals);
		
		return associations;
	}
	
	private static List<Association>  getChemicalsAssociations(String uri, String clase){
		
		List<Association> chemicals = new ArrayList<>();		
	
			
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
										" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
										" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
								" SELECT DISTINCT *" +
								//fromQ +
								" WHERE { " +
								" 	?chemical_disease  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ctd_vocabulary:Chemical-Disease-Association> ." +
								"	?chemical_disease <http://bio2rdf.org/ctd_vocabulary:chemical> ?chemical . " +
								"	?chemical_disease <http://bio2rdf.org/ctd_vocabulary:disease> <" + uri+ "> . " +
								"	?chemical <http://purl.org/dc/terms/title> ?chemical_title . "+
								" } " +
								"LIMIT 3";
	
			System.out.println("association query getChemicalsAssociations!!");
			System.out.println(sparqlQueryString1);
			
			Query query = QueryFactory.create(sparqlQueryString1);
			
			QueryEngineHTTP qexec = new QueryEngineHTTP("http://ctd.bio2rdf.org/sparql", query);
			//System.out.println("endpoint: " + dataset.getSparqlEndpoint());
			
			ResultSet results = qexec.execSelect();
			
			//ResultSetFormatter.out(System.out, results, query);    
		
			// Informacion del resultado				
			String aux = null;

			while (results.hasNext())
			{
				QuerySolution qsol = results.nextSolution();	
				
				if(qsol.contains("chemical") && qsol.contains("chemical_title")){
					Association a = new Association();
					
					a.setAssociation_uri("http://bio2rdf.org/ctd_vocabulary:Gene-Disease-Association");
					a.setAssociation_name("Disease-Gene");
					
					/*
					aux = qsol.get("action_disease_chemical").toString();						
					a.setAction(aux);
					*/
					aux = qsol.get("chemical").toString();						
					a.setConcept_uri(aux);
					
					aux = qsol.get("chemical_title").toString();						
					a.setConcept_name(aux);
					
					a.setOrigin("DISEASE");
					a.setTarget("CHEMICAL");
					chemicals.add(a);
				}

			}	
			qexec.close();
		
		
		return chemicals;
	}
	
	private static List<Association>  getGenesAssociations(String uri, String clase){
		
		List<Association> genes = new ArrayList<>();		
	
			
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
										" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
										" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
								" SELECT DISTINCT *" +
								//fromQ +
								" WHERE { " +
								" 	?gene_disease  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ctd_vocabulary:Gene-Disease-Association> ." +
								"	?gene_disease <http://bio2rdf.org/ctd_vocabulary:gene> ?gene . " +
								"	?gene_disease <http://bio2rdf.org/ctd_vocabulary:disease> <" + uri+ "> . " +
								"	?gene <http://purl.org/dc/terms/title> ?gene_title . "+
								" } " +
								"LIMIT 3";
	
			
			System.out.println("association query getGenesAssociations!!");
			System.out.println(sparqlQueryString1);
			
			Query query = QueryFactory.create(sparqlQueryString1);
			
			QueryEngineHTTP qexec = new QueryEngineHTTP("http://ctd.bio2rdf.org/sparql", query);
			//System.out.println("endpoint: " + dataset.getSparqlEndpoint());
			
			ResultSet results = qexec.execSelect();
			
			//ResultSetFormatter.out(System.out, results, query);    
		
			// Informacion del resultado				
			String aux = null;

			while (results.hasNext())
			{
				QuerySolution qsol = results.nextSolution();	
				
				if(qsol.contains("gene") && qsol.contains("gene_title")){
					Association a = new Association();
					
					a.setAssociation_uri("http://bio2rdf.org/ctd_vocabulary:Gene-Disease-Association");
					a.setAssociation_name("Disease-Gene");
					
					/*
					aux = qsol.get("action_disease_gene").toString();						
					a.setAction(aux);
					*/
					aux = qsol.get("gene").toString();						
					a.setConcept_uri(aux);
					
					aux = qsol.get("gene_title").toString();						
					a.setConcept_name(aux);
					a.setOrigin("DISEASE");
					a.setTarget("GEN");
					genes.add(a);
				}

			}	
			qexec.close();
		
		
		return genes;
	}
	
	private static void infereAssociations(List<Association> associations , String uri, String clase){
		
		List<Property> asocList = new ArrayList<Property>();
		String claseUpper = clase.toUpperCase();
		
		
		/* Si es enfermedad */
		
		if(claseUpper.contains("DISEASE")){
			
			
			for(int i=0; i<associations.size(); i++){
				
				if(associations.get(i).getTarget().compareTo("GEN")==0){  //DISEASE-GEN
					getChemicalsFromGene(associations.get(i));
				}
				
				if(associations.get(i).getTarget().compareTo("CHEMICAL")==0){//DISEASE-CHEMICAL
					getGenesFromChemical(associations.get(i));
				}
				
			}
						
		}
		
		//CREO QUE SI ES GEN O CHEMICAL -> BUSCAR !!SOLO!! ASOCIACIONES EXPLICITAS
		
//		/* Si es gen */
//		
//		if(claseUpper.contains("GEN")){
//			
//		}
//		
//		/* Si es chemical */
//		
//		if(claseUpper.contains("CHEMICAL")){
//			
//		}
		
	}
	
	private static void getChemicalsFromGene(Association gen){
		
		
	
			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
										" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
										" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
								" SELECT DISTINCT ?action_chemical_gene ?chemical ?chemical_title " +
								//fromQ +
								" WHERE { " +
								" 	?chemical_gene <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ctd_vocabulary:Chemical-Gene-Association> . " +
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:action> ?action_chemical_gene . "+
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:chemical> ?chemical . " +
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:gene> <" + gen.getConcept_uri() + "> . " +
								"	?chemical <http://purl.org/dc/terms/title> ?chemical_title . "+
								" } " +
								"LIMIT 1";
	
			System.out.println("assoctiation query getChemicalsFromGene!!");
			System.out.println(sparqlQueryString1);
			Query query = QueryFactory.create(sparqlQueryString1);

			QueryEngineHTTP qexec = new QueryEngineHTTP("http://ctd.bio2rdf.org/sparql", query);
			//System.out.println("endpoint: " + dataset.getSparqlEndpoint());
			
			ResultSet results = qexec.execSelect();
			
			//ResultSetFormatter.out(System.out, results, query);    
		
			// Informacion del resultado				
			String aux = null;				
			List<InferredAssociation> infList = new ArrayList<>();	
			
			while (results.hasNext())
			{
				QuerySolution qsol = results.nextSolution();	
				
				if(qsol.contains("action_chemical_gene") && qsol.contains("chemical") && qsol.contains("chemical_title")){						
					
					InferredAssociation asso = new InferredAssociation();
					
					asso.setAssociation_uri("http://bio2rdf.org/ctd_vocabulary:Chemical-Gene-Association");
					asso.setAssociation_name("Chemical-Gene");
					
					aux = qsol.get("chemical").toString();
					asso.setConcept_uri(aux);
					aux = qsol.get("chemical_title").toString();
					asso.setConcept_name(aux);
					aux = qsol.get("action_chemical_gene").toString();
					asso.setAction(aux);
					
					infList.add(asso);						
					
				}
			}
			
			gen.setInferredAssociations(infList);
			
			qexec.close();
		
	
	}
	
	private static void getGenesFromChemical(Association chemical){
		

			String sparqlQueryString1 =	" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
										" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
										" PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
								" SELECT DISTINCT ?action_chemical_gene ?gene ?gene_title " +
								//fromQ +
								" WHERE { " +
								" 	?chemical_gene <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ctd_vocabulary:Chemical-Gene-Association> . " +
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:action> ?action_chemical_gene . " +
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:chemical> <" + chemical.getConcept_uri() + "> . " +
								"	?chemical_gene <http://bio2rdf.org/ctd_vocabulary:gene> ?gene . " +
								"	?gene <http://purl.org/dc/terms/title> ?gene_title . " +
								" } " +
								"LIMIT 1";
	
			System.out.println("associations query getGenesFromChemical!!");
			System.out.println(sparqlQueryString1);
			Query query = QueryFactory.create(sparqlQueryString1);

			QueryEngineHTTP qexec = new QueryEngineHTTP("http://ctd.bio2rdf.org/sparql", query);
			//System.out.println("endpoint: " + dataset.getSparqlEndpoint());
			
			ResultSet results = qexec.execSelect();
			
			//ResultSetFormatter.out(System.out, results, query);    
		
			// Informacion del resultado				
			String aux = null;
			List<InferredAssociation> infList = new ArrayList<>();	

			while (results.hasNext())
			{
				QuerySolution qsol = results.nextSolution();	
				
				if(qsol.contains("action_chemical_gene") && qsol.contains("chemical") && qsol.contains("chemical_title")){						
					
					InferredAssociation asso = new InferredAssociation();
					
					asso.setAssociation_uri("http://bio2rdf.org/ctd_vocabulary:Chemical-Gene-Association");
					asso.setAssociation_name("Chemical-Gene");
					
					aux = qsol.get("gene").toString();
					asso.setConcept_uri(aux);
					aux = qsol.get("gene_title").toString();
					asso.setConcept_name(aux);
					aux = qsol.get("action_chemical_gene").toString();
					asso.setAction(aux);
					
					infList.add(asso);						
					
				}

			}	
			chemical.setInferredAssociations(infList);
			qexec.close();	

	}
	
	
	private static List<Property> getHerarchyElements(String treeId, Dataset dataset){
		
		List<Property> propsList = new ArrayList<Property>();
		
		String strquery = " SELECT DISTINCT * " +
					   " WHERE { "+
					   "	{ " +
					   "		<"+ treeId +"> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?superclass . " +
					   "    	?superclass <http://www.w3.org/2000/01/rdf-schema#label> ?superlabel . "+
					   "	}" +
					   "	UNION"+
					   "	{ " +
					   "		?subclass <http://www.w3.org/2000/01/rdf-schema#subClassOf> <"+ treeId +"> . " +
					   "    	?subclass <http://www.w3.org/2000/01/rdf-schema#label> ?sublabel . "+
					   "	}" +
					   " } ";
		
					   
		System.out.println("tree query!!");
		System.out.println(strquery);
		
		Query query = QueryFactory.create(strquery);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		System.out.println("endpoint: " + dataset.getSparqlEndpoint());
		
		ResultSet results = qexec.execSelect();
		
		//ResultSetFormatter.out(System.out, results, query);  
		
		String aux;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			
			if(qsol.contains("superclass") && qsol.contains("superlabel")){
				
				Property p = new Property();
				aux = qsol.get("superclass").toString();
				p.setUri(aux);
				aux = qsol.get("superlabel").toString();
				p.setLabel(aux);
				p.setShow_default(1);
				p.setInverseRelation(0);
				
				propsList.add(p);
			}
			
			if(qsol.contains("subclass") && qsol.contains("sublabel")){
				
				Property p = new Property();
				aux = qsol.get("subclass").toString();
				p.setUri(aux);
				aux = qsol.get("sublabel").toString();
				p.setLabel(aux);
				p.setShow_default(1);
				p.setInverseRelation(1);
				
				propsList.add(p);
			}
		}
				
		return propsList;
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
				/*
				System.out.println("p: " + p);
				System.out.println("p uri: " + p.getUri());
				System.out.println("aux: " + auxList.get(j));
				*/
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

	/*public static List<Concept> searchTermBySimilarName(String input, Dataset dataset){
		
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
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		
		System.out.println("dataset: " + dataset.getSparqlEndpoint());
		
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
	}*/

	public static List<Concept> searchTermBySimilarName(String input, Dataset dataset){
		
		System.out.println("DATASET : " + dataset.getName());
		
//		/* 1) separar la cadena en varias palabras */
//		
//		System.out.println("TOKENIZAR: ");
//		StringTokenizer tokens=new StringTokenizer(input);
//		
//		/* 2) agrupar los tokens */  
//		
//		List<String> tokenList = new ArrayList<String>();
//		while(tokens.hasMoreTokens()){
//			String token = tokens.nextToken();
//            System.out.println(token);
//            
//            tokenList.add(token);
//		}
//		
//		/* 3. generar query */
//		
//		String innerQuery = buildInnerQuery_Similar(tokenList);
		
		
		////////////////////////////////////////
		
		
		/* 1. obtener clases del dataset */
		
		List<Class> classesDataset = ClassDAO.getAllClassesByDataset(dataset.getId());
		
		/* 3. generar query */
		
		String innerQuery = "";

		innerQuery = buildInnerQuery_Similar(classesDataset, input);

		
		System.out.println("inner:\n" + innerQuery);
		
		
		/*
		String fromQ = "";
		if(dataset!=null)
			fromQ = "	FROM <" + dataset.getUri() + "> ";
		*/
		
		System.out.println("TERMINO A BUSCAR: " + input);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
									"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
									"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				"   SELECT DISTINCT ?s ?label  " +
				//fromQ +
				"   WHERE { " +				
				//"       ?s <http://purl.org/dc/terms/title> ?label . " +		
				//"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
				innerQuery + 
				"   } "+
				"	LIMIT 5";				
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		QueryEngineHTTP qexec = new QueryEngineHTTP(dataset.getSparqlEndpoint(), query);
		
		System.out.println("dataset: " + dataset.getSparqlEndpoint());
		
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

	public static String buildInnerQuery_Similar(List<Class> classList, String input){

		
		/*
		 * 
		 * 				"       ?s <http://purl.org/dc/terms/title> ?label . " +		
				"	    FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) " +
		 * 
		 */
		
		String query = "";
		
		for(int i=0; i < classList.size(); i++){
			if(i>0){
				query += " UNION ";				
			}
			query += " { ";
			query += "  ?s rdf:type ?type . ";
			query += "  ?s rdf:type <" + classList.get(i).getUri() + "> . ";			
			query += "  ?s <http://purl.org/dc/terms/title> ?label . ";
			query += "  FILTER (CONTAINS ( UCASE(str(?label)), \"" + input.toUpperCase() + "\")) ";
			query += " } ";
		}
		
		return query;
		
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

					if(p.getValue() != null){
						System.out.println("ENTRO A inputUri !!");
						//si no se repite
						boolean repite = false;
						
						for(int k=0; k<cList.size(); k++){
							System.out.println("p.getvalue: " + p.getValue());
							System.out.println("clist: " + cList.get(k).getUri());
							if(p.getValue().compareTo(cList.get(k).getUri()) == 0){
								repite = true;
								break;
							}
						}
						
						if(repite == false){
							System.out.println(" no repite :/");
							c = searchTermByExactMatchUri(p.getValue(), dataset);
						}
						
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
					//cList.add(c);
				}
			}
			
			System.out.println( j + ". cList size: " + cList.size());
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
	
	static public List<Concept> searchTermByExactMatch_Datasets(String input, List<Dataset> datasetList, List<Concept> mappingList){
		
		List<Concept> cList = new ArrayList<Concept>();
		
		for(int i=0; i < datasetList.size() ; i++){
			Dataset dat = datasetList.get(i);
			if(dat.getId() != 1){ // DBPEDIA
				if(!InputSearchProcessor.isUri(input)){					
					Concept aux = searchTermByExactMatch(input, dat, mappingList);
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
	
	
	static public List<Concept> getInstances(String classUri){
		List<Concept> conceptList = new ArrayList<Concept>();
		
		//
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
		"   SELECT DISTINCT * " +
		"   WHERE { " +				
		"       ?s rdf:type  <" + classUri + "> . " + 
		" 		?s <http://purl.org/dc/terms/title> ?label . " +
		"   } "+
		"	LIMIT 20";
		
					
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql/", query);
		
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

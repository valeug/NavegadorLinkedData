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

import dao.ClassDAO;
import dao.DatasetDAO;
import dao.PropertyDAO;
import model.Concept;
import model.Dataset;
import model.Property;
import model.PropertyGroup;
import sparqlendpoint.Bio2RdfEndpoint;
import sparqlendpoint.BioportalEndpoint;
import sparqlendpoint.DbpediaEndpoint;

public class SearchController {

	/*
	public static Concept searchConcept(HttpServletRequest request) {
		Concept term = null;
		//dbpedia
		//bioportal
		String input = request.getParameter("concept");
		
		//DbpediaEndpoint.JenaSparqlQuery(input);
		//BioportalEndpoint.JenaSparqlQuery(input);
		Bio2RdfEndpoint.JenaSparqlQuery(input);
		
		return term;
	}
	*/
	
	
	
	/*		FUNCION PRINCIPAL QUE INICIA LA BUSQUEDA	*/
	
	public static  int  searchConcept(HttpServletRequest request, List<Concept> termList  ) {
		
		int flagUri;
		
		Concept term = null;
		String input = request.getParameter("concept");
		
		//int searchType = request.getParameter("optradio").charAt(0)-'0';		
		//System.out.println("TIPO DE BUSQUEDA: "+searchType);
				
		/* Consultar los datasets que selcciono el usuario*/
		List<Dataset> datasetList = DatasetDAO.getDatasetByStatus(1);	
		List<Concept> tList = null;
		
		
		System.out.println("DATASETLIST size: " + datasetList.size());
		for(int p =0; p<datasetList.size(); p++){
			System.out.println("id: "+datasetList.get(p).getId()+"  -  nom:"+datasetList.get(p).getName());
		}
		
		/**/
		int posBio [] = new int [7]; //posiciones de datasets de bio2rdf en la lista de datasets(datasetList)
		int cant = 0, found = 0, seleccDbpedia=0;

		if(!InputSearchProcessor.isUri(input)){ // cadena, 1era busqueda
			flagUri = 0;
			for(int i=0; i<datasetList.size(); i++){
				int idDat = datasetList.get(i).getId();
				if(idDat == 1){ //DBPedia
					if(!InputSearchProcessor.isUri(input)){					
						term = DbpediaEndpoint.searchTermByExactMatch(input); 
						//termList.add(term);
						System.out.println("\n---------------------\n---------------------\n IMPRIMELO 1\n---------------------\n---------------------");
						printConcept(term);
					}
					
					if(term!=null) found = 1;
				}
				else { //Bio2RDF
					//term = Bio2RdfEndpoint.searchTermByExactMatch(input);
					posBio[cant++] = i;
				}
			}
			if(cant>0){ // Se seleccionaron datasets de Bio2RDF				
				List <Concept> termsMappingList = new ArrayList<Concept>();		
				List <Concept> similarTerms = new ArrayList<Concept>();
				List <Concept> exactTerms = new ArrayList<Concept>();
				
				//Se seleccionaron datasets de Bio2rdf y Dbpedia		
					if(found==1){ // Se encontro el termino en DBPedia -> se busca lista de terminos en Bio2rdf

						//printConcept(term);
						/*
						System.out.println("term name: " + term.name);
						System.out.println("term properties: " + term.getProperties().size());
						System.out.println("dataset LIST: " + datasetList.size());
						*/
						
						/* ELIMINAR ESTO?? mapping: */
						//termsMappingList = Bio2RdfEndpoint.getMappingPropertiesValues(term, datasetList); //conceptos con sus propiedades (para enriquecer propiedades del termino en contrado en DBPedia)
						System.out.println("\n---------------------\n---------------------\n IMPRIMELO 2\n---------------------\n---------------------");
						printConcept(term);
						similarTerms = Bio2RdfEndpoint.searchTermBySimilarName_Datasets(input, datasetList); // solo nombres de los conceptos (sin mostrar propiedades)
						
						
						//if(term==null) System.out.println("term null!");
						
						//System.out.println("termsMappingList size: " + termsMappingList.size());
						System.out.println("ANTES term properties: " + term.getProperties().size());
						
						/* CREO QUE HAY QUE BORRAR ESTOOOOOOOOOOO!!!!!!!!!!!!!!!!!!!!1*/
						addInfoToTerm(term, termsMappingList, similarTerms);
						
						System.out.println("\n---------------------\n---------------------\n IMPRIMELO 3\n---------------------\n---------------------");
						//printConcept(term);
					}
					//else {
						// No se encontro el concepto en DBPediam, se busca en bio2rdf
						System.out.println("\n\n\nENTRO exacTerms\n");
						
						exactTerms = Bio2RdfEndpoint.searchTermByExactMatch_Datasets(input, datasetList, termsMappingList); //exact match		
						
						for(int i=0; i < exactTerms.size(); i++){
							System.out.println("--------/n exact TERM " + i + "--------");
							System.out.println("uri: " +exactTerms.get(i).getUri() );
							termList.add(exactTerms.get(i));
							//printConcept(exactTerms.get(i));						
						}
						
						System.out.println("\n\nmapping size: " + termsMappingList.size());
						for(int i=0; i < termsMappingList.size(); i++){
							System.out.println("--------/n mapping TERM " + i + "--------");
							System.out.println("uri: " +termsMappingList.get(i).getUri() );
							termList.add(termsMappingList.get(i));
							//printConcept(termsMappingList.get(i));						
						}
					//}				
			}
		}
		else {
			System.out.println("BUSCA URI!");
						
			flagUri = 1;
			
			if(input.contains("purl.org/dc/terms/subject") || input.contains("www.w3.org/1999/02/22-rdf-syntax-ns#type")){  /* el URI es rdf:type y subject */
				
				return 2;
			}
			
			if(input.contains("bio2rdf.org/")){
				
				int idDatasetMatch = findUriOrigin(input); /* ADAPTAR A NUEVOS DATASETS */
				
				System.out.println("idDatasetMatch: " + idDatasetMatch);				
				System.out.println("datasetList.size(): " + datasetList.size());
				
				for(int i=0; i<datasetList.size(); i++){
					Dataset dat = datasetList.get(i);					
					if(dat.getId() == idDatasetMatch){ // el dataset del uri clickeado si esta seleccionado -> si se puede navegar a ese dataset de Bio2RDF
						System.out.println("dataset origin uri: " + dat.getId());
					
						System.out.println("entro a uri bio2rdf");
						// si no es goa
						term = Bio2RdfEndpoint.searchTermByExactMatchUri(input, dat);
						// termList.add(term);
						// si es goa 
						
						
						if(term == null) System.out.println("null wtf");
						else System.out.println("NOT null wtf");
						
						break;						
					}
				}
			}
			else if(input.contains("dbpedia.org/")){
				//SI ES TERMINO
				System.out.println("ENTRO DBPedia URI");
				
				if(request.getParameter("property-uri").compareTo("") != 0) return 1; //porque es categoria/clase
				
				System.out.println("buscar DBPedia URI");
				term = DbpediaEndpoint.searchByUri(input);
				flagUri = 1;
				//termList.add(term);
				//SI ES CLASE
			}
			
		}
		
		if(term == null) System.out.println("TERM ES NULO DDDD:");
		else System.out.println("lo encontro :)");
		
		
		//term.setName("basurita");
		//printConcept(term);
		
		
		//PRUEBA!!!!
		Concept c = conceptPrueba();
		
		termList.add(term);
		termList.add(c); //SOLO PARA PRUEBA
		
		System.out.println("\n\n\n\n\n\n---------------\n FINAL \n---------------\n\n\n\n");
		System.out.println("termlist size: " + termList.size());
		for(int i=0; i < termList.size(); i++){
			System.out.println("--------/n  TERM " + i + "--------");			
			printConcept(termList.get(i));						
		}
		
		return flagUri;
	}
	
	private static Concept conceptPrueba(){
		Concept c = new Concept();
		List<PropertyGroup> pgList = new ArrayList<>();
		List<Property> pList;
		PropertyGroup pg;
		
		List<Property> propertyList = new ArrayList<>();
		Property p;
		
		/*
		  	3) Group uri: http://www.w3.org/2000/01/rdf-schema#label
			3) Group consolidated: 0
			pg size: 3
			0. Property value: Alzheimer Disease [mesh:D000544]@en
			0. Property label: null
			1. Property value: Alzheimer Disease
			1. Property label: null
			2. Property value: Alzheimer Disease [mesh:D000544]
			2. Property label: null
		 
		 */
		
				
		
		/*
		 *	2) Group uri: http://www.w3.org/1999/02/22-rdf-syntax-ns#type
			2) Group consolidated: 0
			pg size: 3
			0. Property value: http://bio2rdf.org/mesh_vocabulary:Descriptor
			0. Property label: MeSH Descriptor@en
			1. Property value: http://bio2rdf.org/ctd_vocabulary:Disease
			1. Property label: CTD Disease@en
			2. Property value: http://bio2rdf.org/mesh_vocabulary:Resource
			2. Property label: null
					 
		 * */
		
		pg = new PropertyGroup();
		pg.setName("Es del tipo");
		pg.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		pg.setConsolidated(1);
		pg.setShow_default(1);
		
		pList = new ArrayList<>();
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/mesh_vocabulary:Descriptor");
		p.setValue("MeSH Descriptor@en");
		pList.add(p);
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/ctd_vocabulary:Disease");
		p.setValue("CTD Disease@en");
		pList.add(p);
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/mesh_vocabulary:Resource");
		p.setValue("mesh resource [mesh_vocabulary:Resource]");
		pList.add(p);
		
		pg.setPropertyList(pList);
		
		pgList.add(pg);
				
		/*
		 	1) uri: http://purl.org/dc/terms/title
			1) value: Alzheimer Disease@en
			1) label: null
			1) show_default: 1
			1) consolidated: 1

		 */
		
		p = new Property();
		p.setName("Nombre");
		p.setUri("http://purl.org/dc/terms/title");
		p.setValue("Alzheimer Disease@en");
		p.setConsolidated(0);
		p.setShow_default(1);
		propertyList.add(p);
		
		/*
		16) uri: http://bio2rdf.org/mesh_vocabulary:mesh-scope-note
			16) value: A degenerative disease of the BRAIN characterized by the insidious onset of DEMENTIA. Impairment of MEMORY, judgment, attention span, and problem solving skills are followed by severe APRAXIAS and a global loss of cognitive abilities. The condition primarily occurs after age 60, and is marked pathologically by severe cortical atrophy and the triad of SENILE PLAQUES; NEUROFIBRILLARY TANGLES; and NEUROPIL THREADS. (From Adams et al., Principles of Neurology, 6th ed, pp1049-57)
			16) label: null
			16) show_default: 1
			16) consolidated: 0
		*/
		
		p = new Property();
		p.setName("Descripción breve");
		p.setUri("http://bio2rdf.org/mesh_vocabulary:mesh-scope-note");
		p.setValue("A degenerative disease of the BRAIN characterized by the insidious onset of DEMENTIA. Impairment of MEMORY, judgment, attention span, and problem solving skills are followed by severe APRAXIAS and a global loss of cognitive abilities. The condition primarily occurs after age 60, and is marked pathologically by severe cortical atrophy and the triad of SENILE PLAQUES; NEUROFIBRILLARY TANGLES; and NEUROPIL THREADS. (From Adams et al., Principles of Neurology, 6th ed, pp1049-57)");
		p.setShow_default(1);
		p.setConsolidated(1);
		
		propertyList.add(p);
		
		/*
		2) uri: http://purl.org/dc/terms/description
			2) value: A degenerative disease of the BRAIN characterized by the insidious onset of DEMENTIA. Impairment of MEMORY, judgment, attention span, and problem solving skills are followed by severe APRAXIAS and a global loss of cognitive abilities. The condition primarily occurs after age 60, and is marked pathologically by severe cortical atrophy and the triad of SENILE PLAQUES; NEUROFIBRILLARY TANGLES; and NEUROPIL THREADS. (From Adams et al., Principles of Neurology, 6th ed, pp1049-57)@en
			2) label: null
			2) show_default: 1
			2) consolidated: 0
		*/
		
		p = new Property();
		p.setName("Description");
		p.setUri("http://purl.org/dc/terms/description");
		p.setValue("A degenerative disease of the BRAIN characterized by the insidious onset of DEMENTIA. Impairment of MEMORY, judgment, attention span, and problem solving skills are followed by severe APRAXIAS and a global loss of cognitive abilities. The condition primarily occurs after age 60, and is marked pathologically by severe cortical atrophy and the triad of SENILE PLAQUES; NEUROFIBRILLARY TANGLES; and NEUROPIL THREADS. (From Adams et al., Principles of Neurology, 6th ed, pp1049-57)@en");	
		p.setShow_default(1);
		p.setConsolidated(0);
		
		////////////////TREE ENTRY
		/*
			rdfs:subClassOf	
			Tauopathies [mesh:C10.574.945]
		*/
		
		/*
		  	rdfs:subClassOf	
			Dementia [mesh:C10.228.140.380]
		 */
		 
		/*
		 	Dementia [mesh:F03.087.400]		 	
		 */
				
		pg = new PropertyGroup();
		pg.setName("Type");
		pg.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		pg.setConsolidated(0);
		pg.setShow_default(1);
		
		pList = new ArrayList<>();
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/mesh_vocabulary:Descriptor");
		p.setValue("MeSH Descriptor@en");
		pList.add(p);
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/ctd_vocabulary:Disease");
		p.setValue("CTD Disease@en");
		pList.add(p);
		
		p = new Property();
		p.setUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		p.setValue("http://bio2rdf.org/mesh_vocabulary:Resource");
		p.setValue("mesh resource [mesh_vocabulary:Resource]");
		pList.add(p);
		
		pg.setPropertyList(pList);
		
		pgList.add(pg);
		
		
		// final concept
		c.setName("Alzheimer Disease@en");
		c.setDataset("MESH");
		c.setPropertyGroups(pgList);
		c.setProperties(propertyList);
		
		return c;
	}
	
	private static int findUriOrigin(String input){
		
		System.out.println("input: "+input);
		if(input.contains("bio2rdf.org/mesh")){	
			System.out.println("es mesh :)");
			return 2;
		}
		else if(input.contains("bio2rdf.org/omim")){
			System.out.println("es omim :)");
			return 7;
		}
		else if(input.contains("bio2rdf.org/uniprot") || input.contains("bio2rdf.org/go") || input.contains("bio2rdf.org/goa")){
			System.out.println("es de goa :)");
			return 4;
		}
		else if(input.contains("bio2rdf.org/ncbigene")){
			System.out.println("es ncbigene :)");
			return 5;
		}
		
		return -1;
	}
	
	
	private static void addInfoToTerm(Concept term, List<Concept> termsMappingList, List<Concept> similarTerms){
		
		//pasar las propiedades de los terminos mapeados -> a el termino de DBPedia
		List<Property> props = term.getProperties();
		if(termsMappingList != null && term != null){
			for(int i=0; i<termsMappingList.size(); i++){
				Concept c = termsMappingList.get(i);
				List<Property> pList = c.getProperties();
				for(int j=0; j < pList.size(); j++){
					// agregar las propiedades al termino
					props.add(pList.get(j));
				}			
			}
		}
		
		System.out.println("addInfoToTerm - props size: " + props.size());
		//term.setProperties(props);
		
		System.out.println("similarTerms.size(): "+similarTerms.size());
		for(int k=0; k<similarTerms.size(); k++){
			System.out.println("" + similarTerms.get(k).getUri());
		}
		
		/*
		List<Concept> cList = new ArrayList<Concept>();		
		// terminos linkeados
		if(similarTerms != null && term != null){
			for(int k=0; k < similarTerms.size(); k++){
				cList.add(similarTerms.get(k));
			}
		}
		term.setSimilarTerms(cList);	
		*/
		if(similarTerms != null && term != null){
			term.setSimilarTerms(similarTerms);
		}
	}
	
	
	
	public static List<Concept> getTermsList(HttpServletRequest request, int searchType) {
		
		List<Concept> tlist = null;
		String input = request.getParameter("concept");
		
		 List<Dataset> datasetList = DatasetDAO.getDatasetByStatus(1);
		 
		if(searchType==2){
			//tlist = DbpediaEndpoint.searchTermBySimilarName(input);
			//tlist = Bio2RdfEndpoint.searchTermBySimilarName(input, null);
			
			tlist = Bio2RdfEndpoint.searchTermBySimilarName_Datasets(input,datasetList);
		}
		else if(searchType == 3){
			//tlist = DbpediaEndpoint.searchTermByPropertyMatch(input);
			tlist = Bio2RdfEndpoint.searchTermByPropertyMatch_Datasets(input, datasetList);
		}
				
		return tlist;		
	}
	
	public static Concept getConcept(String cad, int type){
		Concept c = new Concept();
		
		//ontoList = getAllOntologies();
		
		if(type==1){			
			System.out.println("TYPE 1");
			c = searchUri(cad);
		}
		else if(type==2){
			String uri =transformTermToURI(cad);
		}
		if(c.getName()==null) System.out.println("NOMBRE NULO!!!!! D:");
		
		return c;
	}
	
	
	public static Concept searchUri(String cad){
		//String uriS = ontolgiesGraphNames(ontoList);
		//System.out.println("uriS:");
		//System.out.println(uriS);
		
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms>"+
		        "   SELECT DISTINCT *" +
		        "	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "       ?s1 ?p1 ?o1 . " +
			    "   	?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 . " +
			    "   	?parent rdfs:label ?parentLabel . " +
			    "		{ 		"+
			    "			<" + cad + "> <http://purl.obolibrary.org/obo/def> ?obodef } UNION { <" + cad + "> skos:definition ?skosdef }"+
				"	}" +
			    "LIMIT 100";
		
		/*
		SELECT ?s1, ?label1, ?type1
				WHERE {
				   ?s1 ?p1 ?o1 .
				   ?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 .
				   ?s1     <http://www.w3.org/2000/01/rdf-schema#label>     ?label1 .
				   FILTER (CONTAINS ( UCASE(str(?label1)), "ALZHEIMER"))
				}
				LIMIT 1000
		*/
		//falta reutilizar funciones
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://sparql.bioontology.org/sparql", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			String uri = "<" + cad + ">";
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
		    	c.setUri(uri);
				c.setName(name);
				if(qsol.contains("obodef")){
			    	System.out.println("definition type: " + qsol.get("obodef"));
			    	c.setDefinition(""+qsol.get("obodef"));
				}
				else {
					System.out.println("definition type: " + qsol.get("skosdef"));
					c.setDefinition(""+qsol.get("skosdef"));
				}
				i++;
		    }
		    
		} 
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		
		qexec.close();
		
		return c;
		
	}
	
	public static Concept searchUriBio2RDF(String cad){

		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms>"+
		        "   SELECT DISTINCT *" +
		        //"	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "       <"+ cad+ "> ?prop ?value . " +
			    "	} "+
			    "LIMIT 100";
		
		//falta reutilizar funciones
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://bio2rdf.org/sparql/", query);
		
		ResultSet results = qexec.execSelect();
		//ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			String uri = "<" + cad + ">";
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
		    	c.setUri(uri);
				c.setName(name);
				if(qsol.contains("obodef")){
			    	System.out.println("definition type: " + qsol.get("obodef"));
			    	c.setDefinition(""+qsol.get("obodef"));
				}
				else {
					System.out.println("definition type: " + qsol.get("skosdef"));
					c.setDefinition(""+qsol.get("skosdef"));
				}
				i++;
		    }
		    
		} 
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		
		qexec.close();
		
		return c;
		
	}
	
	
	
	public static String transformTermToURI(String term){
		
		//String uriS = ontolgiesGraphNames(ontoList);
		//System.out.println("uriS:");
		//System.out.println(uriS);
		//String x = "	FROM <http://bioportal.bioontology.org/ontologies/XAO> FROM <http://bioportal.bioontology.org/ontologies/ICF>";
		String sparqlQueryString1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"	PREFIX dc:<http://purl.org/dc/terms/>"+
		        "   SELECT DISTINCT *" +
		        "	FROM <http://bio2rdf.org/mesh_resource:bio2rdf.dataset.mesh.R3>"+
		        //"	FROM <http://bio2rdf.org/pharmgkb_resource:bio2rdf.dataset.pharmgkb.R3>" +
		        //"	FROM <http://bio2rdf.org/ncbigene_resource:bio2rdf.dataset.ncbigene.R3>" +
		        //"	FROM <http://bio2rdf.org/goa_resource:bio2rdf.dataset.goa.R3>" +
		        "   WHERE { " +
		        "		{ " + 
		        "           ?s2 dc:title ?label2 "+
			    //"			FILTER (CONTAINS ( UCASE(str(?label2)), \"NEURONS\"))" +
			    "			FILTER (CONTAINS ( UCASE(str(?label2)), '"+ term.toUpperCase()+"'))" +
			    //"			FILTER ( UCASE(str(?label2)) = 'NEURONS')"+
			    //"	    	FILTER (UCASE(str(?label2)) = '"+ term.toUpperCase() + "') " +	
			    "		} "+
			    " 		UNION { "+
			  //"       ?s1 ?p1 ?o1 . " +
			    //"   	?s1   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   ?type1 . " +
			    
			    "  			?s1 rdfs:label ?label1 . " +
			    //"			FILTER (CONTAINS ( UCASE(str(?label1)), \"NEURONS\"))" +
			    "			FILTER (CONTAINS ( UCASE(str(?label1)), '"+ term.toUpperCase()+"'))" +
			    //"			FILTER ( UCASE(str(?label1)) = 'NEURONS')"+	
			    //"	    	FILTER (UCASE(str(?label1)) = '"+ term.toUpperCase() + "') " +    
			    "	 	}"+
				"	}" +
			    "LIMIT 100";
		
		
		System.out.println(sparqlQueryString1);
		Query query = QueryFactory.create(sparqlQueryString1);	
		QueryEngineHTTP qexec = new QueryEngineHTTP("http://bio2rdf.org/sparql", query);
		
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results, query);     
		
		System.out.println("antes");
		Concept c = new Concept();
		int i=0;
		while (results.hasNext())
		{
			QuerySolution qsol = results.nextSolution();	
			/*
			String uri = qsol.get("x").toString();
			String name = qsol.getLiteral("label").toString();
			System.out.println("uri: " + uri);
		    System.out.println("label: " + name);
		    */
		    // el termino devuelto es el primer resultado de la busqueda por ahora		    
		    if(i==0){
		    	/*
		    	c.setUri("<"+uri+">");
				c.setName(name);
				if(qsol.contains("obodef")){
			    	System.out.println("definition type: " + qsol.get("obodef"));
			    	c.setDefinition(""+qsol.get("obodef"));
				}
				else {
					System.out.println("definition type: " + qsol.get("skosdef"));
					c.setDefinition(""+qsol.get("skosdef"));
				}
				i++;
				*/
		    	if(qsol.contains("label2")){
			    	//System.out.println("definition type: " + qsol.get("label2"));
			    	return ""+qsol.get("label2");
				}
				else {
					System.out.println("definition type: " + qsol.get("label1"));
					return ""+qsol.get("label1");
				}	
		    }
		    
		} 
		/*
		System.out.println("despues");
		
		if(i==0){
			c.setUri(null);
			c.setName(null);
			c.setDefinition(null);
		}
		*/
		qexec.close();
		return null;
	}
		
	
	private static void printConcept (Concept c){
		
		System.out.println("==============================");
		System.out.println("	CONCEPTO FINAL");
		System.out.println("==============================");
		
		if( c != null){
			if(c.getName() != null)
				System.out.println("name: " + c.getName());
			else System.out.println("Nombre null :/");
			
			if(c.getUri() != null)
				System.out.println("uri: " + c.getUri());
			else System.out.println("Uri null :/");
						
			if(c.getDataset() != null)
				System.out.println("dataset: " + c.getDataset());
			else System.out.println("Dataset null :/");
			
			if(c.getProperties() != null){
				System.out.println("Propiedades: ");
				System.out.println("Propiedades size: " + c.getProperties().size());
				for(int i=0; i<c.getProperties().size(); i++){
					System.out.println(i+") uri: " + c.getProperties().get(i).getUri());
					System.out.println(i+") value: " + c.getProperties().get(i).getValue());
					System.out.println(i+") label: " + c.getProperties().get(i).getLabel());
					System.out.println(i+") show_default: " + c.getProperties().get(i).getShow_default());
					//System.out.println(i+") consolidated: " + c.getProperties().get(i).getConsolidated());
					//System.out.println(i+") instances: " + c.getProperties().get(i).getInstances());
				}
			}
			else System.out.println("Propiedades null :/");
			
			if(c.getPropertyGroups() != null){
				System.out.println("PROPERTY GROUP: ");
				System.out.println("list pg size: " + c.getPropertyGroups().size());
				for(int i=0; i<c.getPropertyGroups().size(); i++){
					System.out.println(i+") Group uri: " + c.getPropertyGroups().get(i).getUri());
					System.out.println(i+") Show_default: " + c.getPropertyGroups().get(i).getShow_default());
					System.out.println(i+") Group consolidated: " + c.getPropertyGroups().get(i).getConsolidated());
					//System.out.println(i+") Mapping: " + c.getPropertyGroups().get(i).getMapping());
					System.out.println(i+") Instances: " + c.getPropertyGroups().get(i).getInstances());
					System.out.println("pg size: " + c.getPropertyGroups().get(i).getPropertyList().size());
					for(int k=0; k < c.getPropertyGroups().get(i).getPropertyList().size() ; k++){
						System.out.println(k+". Property value: " + c.getPropertyGroups().get(i).getPropertyList().get(k).getValue());
						System.out.println(k+". Property label: " + c.getPropertyGroups().get(i).getPropertyList().get(k).getLabel());
						//System.out.println(k+") consolidated: " + c.getPropertyGroups().get(i).getPropertyList().get(k).getConsolidated());
					}
				}
			}
			else System.out.println("Propiedades GROUP null :/");
			
			if(c.getSimilarTerms() != null){
				System.out.println("Similar terms: ");
				for(int i=0; i<c.getSimilarTerms().size(); i++){
					System.out.println(i+") " + c.getSimilarTerms().get(i).getName());
				}
			}
			else System.out.println("Similar terms null :/");
			
			if(c.getLinkedTerms() != null){
				System.out.println("Linked terms: ");
				for(int i=0; i<c.getLinkedTerms().size(); i++){
					System.out.println(i+") " + c.getLinkedTerms().get(i).getName());
				}
			}
			else System.out.println("Linked terms null :/");
			
			if(c.getAssociations() != null){
				System.out.println("Associations ");
				for(int i=0; i<c.getAssociations().size(); i++){
					System.out.println(i+") " + c.getAssociations().get(i).getConcept_uri());
				}
			}
			else System.out.println("Associations null :/");
			
		}
	}
	
	
	public static int es_clase_valida(HttpServletRequest request){
		
		String input = request.getParameter("concept");
		return ClassDAO.searchClass(input);		
	}
	
	public static List<Concept> search_instances(HttpServletRequest request, int datasetId){
		
		String classUri = request.getParameter("concept");
		List<Concept> conceptList = new ArrayList<Concept>();
		
		if(datasetId == 1){ // DBPEDIA
			conceptList = DbpediaEndpoint.getInstances(classUri);
		}
		else { // BIO2RDF
			conceptList = Bio2RdfEndpoint.getInstances(classUri);
		}
		
		return conceptList;
	}
	
	
	public static int is_class(HttpServletRequest request){
		
		String propUri = request.getParameter("property-uri");
		String classUri = request.getParameter("concept");
		int id_dataset = 0;
		

		Property p = PropertyDAO.getPropertyByUri(propUri);
		
		if(p.getInstances() == 1){
			
			if(classUri.contains("dbpedia")) return 1;
			//if(classUri.contains("dbpedia")) return 1;
		}
		
		
		return id_dataset;
	}
}



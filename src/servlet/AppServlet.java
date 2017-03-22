package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.SearchController;
import model.Concept;
import controller.InputSearchProcessor;

@WebServlet("/AppServlet")
//@WebServlet({"/AppServlet/*"})
//@WebServlet({"/AppServlet", ""})
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public AppServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		int searchType=-1;
		
		
				
		if(request.getParameter("optradio")!=null){
			searchType = request.getParameter("optradio").charAt(0)-'0';
			session.setAttribute("optradio", "" + searchType);
		}
		else {
			searchType = session.getAttribute("optradio").toString().charAt(0)-'0';
		}
		
		RequestDispatcher dispatcher;
		Concept term = null;
		List<Concept> termList = new ArrayList<Concept>(); 
		String view ="";
		
		System.out.println("Tipo busqueda: " + searchType);
		if(searchType == 1){
			int esUri = -1;
			
			int id_dataset = 0;
			if(request.getParameter("property-uri")!=null){ 
				if(request.getParameter("property-uri").toString().compareTo("") != 0){ //tiene texto
					String aux = request.getParameter("property-uri");
					
					//System.out.println("property-uri: " + aux);				
					//System.out.println("concept: " + request.getParameter("concept"));
					
					id_dataset = SearchController.is_class(request);
				}
			}
			
			esUri = SearchController.searchConcept(request, termList);
			System.out.println("termList: " + termList.size());
			
			System.out.println("es URI value: " + esUri);
			if(esUri ==-1) System.out.println("tf");
			if(esUri == 1){
				System.out.println("esURI");
				
				/*
				int id_dataset = 0;
				if(request.getParameter("property-uri-input")!=null){
					String aux = request.getParameter("property-uri-input");
					System.out.println("property-uri-input: " + aux);
					
					id_dataset = SearchController.is_class(request);
				}
				*/
				
				/* VERIFICAR SI ES UNA CLASE DEL DOMINIO (que este en la BD)*/
				/* EN EL CASO DE DBPDIA, al darle click a "CATEGORY" tambien devuelve sus instancias*/
				//int datasetId = 1;
				//datasetId = SearchController.es_clase_valida(request);
								
				if(id_dataset > 0){
					List<Concept> instances = new ArrayList<Concept>();
					Concept clase = new Concept();
					
					instances = SearchController.search_instances(request, id_dataset);
					System.out.println("\n\n\n++++++++++++++++\nINSTANCES\n");
					System.out.println("size: " + instances.size());
					
					//clase.setUri(request.getParameter("concept"));
					request.setAttribute("term", clase);
					request.setAttribute("instances", instances);
					
					view = "searchresult.jsp";
				}				
				else { /* NO ES UNA CLASE */
					/* SI ES URI (o sea, como el uri pertenece a 1 solo recurso) -> MOSTRAR TODA LA INFO DE UN CONCEPTO */
					request.setAttribute("term", termList.get(0));
					view = "searchresult.jsp";
				}
				
				
			}
			else { 
				System.out.println("NO esURI");
				// busca por palabra, devolver MEZCLA de varios conceptos. devolver  una lista de conceptos (con las coincidencias en cada dataset)
			
				request.setAttribute("termsConsolidated", termList);
				view = "searchresult-consolidated.jsp";
			}	
		}
		else if (searchType == 2 || searchType == 3) {  // por coincidencia similar en nombre o por coincidencia en propiedades
			
			termList = SearchController.getTermsList(request, searchType);
			
			/*
			System.out.println("------------------------\n        SERVLET");
			System.out.println("Terms list size: " + termList.size());
			for(int i=0; i<termList.size(); i++){
				System.out.println("URI: " + termList.get(i).getUri());
				System.out.println("LABEL: " + termList.get(i).getName());
			}
			*/
			session.setAttribute("optradio", "" + 1);
			request.setAttribute("termList", termList);
			request.setAttribute("optradio", "1");
			view = "termslistresult.jsp";
		}
		
		System.out.println("view: " + view);
		dispatcher = request.getRequestDispatcher("jsp/"+view);
	    dispatcher.forward( request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}
}
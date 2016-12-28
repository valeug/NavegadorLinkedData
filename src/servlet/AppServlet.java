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
			esUri = SearchController.searchConcept(request, termList);
			System.out.println("termList: " + termList.size());
			if(esUri ==-1) System.out.println("tf");
			if(esUri == 1 ){
				System.out.println("esURI");
				/* SI ES URI (o sea, como el uri pertenece a 1 solo recurso) -> MOSTRAR TODA LA INFO DE UN CONCEPTO */
				request.setAttribute("term", termList.get(0));
				view = "searchresult.jsp";
			}
			else { 
				System.out.println("NO esURI");
				// devolver  una lista de conceptos (con las coincidencias en cada dataset)
			
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
		
		dispatcher = request.getRequestDispatcher("jsp/"+view);
	    dispatcher.forward( request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}
}
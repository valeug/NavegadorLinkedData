package servlet;

import java.io.IOException;
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
		List<Concept> termList = null; 
		String view ="";
		
		System.out.println("Tipo busqueda: " + searchType);
		if(searchType == 1){
			term = SearchController.searchConcept(request);	
			request.setAttribute("term", term);	
			//request.setAttribute("optradio", "1");
			
			view = "searchresult.jsp";
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
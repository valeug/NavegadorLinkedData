package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		Concept term = SearchController.searchConcept(request);				
		request.setAttribute("term", term);
		RequestDispatcher dispatcher = request.getRequestDispatcher("jsp/searchresult.jsp");
	    dispatcher.forward( request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}
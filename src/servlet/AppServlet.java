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


//@WebServlet("/AppServlet")
@WebServlet({"/AppServlet", ""})
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AppServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("jsp/home.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		  envio como parametro la palabra buscada
		  devuelve resultados -> descripcion del concepto y lista de conceptos relacionados
		*/
		
		//demo		
		String cad = request.getParameter("concept");
		// devolveria una clase "concepto/termino" (tiene descripcion y lista de conceptos linkeados a el)
		Concept term = SearchController.getConcept(cad);
		
		
		request.setAttribute("term", term);
		RequestDispatcher dispatcher = request.getRequestDispatcher("jsp/searchresult.jsp");
	    dispatcher.forward( request, response);
	}

}
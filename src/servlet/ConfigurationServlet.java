package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ConfigurationServlet
 */
@WebServlet("/Configuration")
public class ConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RequestDispatcher dispatcher;
		//request.setAttribute("term", term);
		dispatcher = request.getRequestDispatcher("jsp/"+"configuration.jsp");
	    dispatcher.forward( request, response);    
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestDispatcher dispatcher;
		
		System.out.println( "para 1: "+ request.getParameter("dataset"));
		System.out.println( "para 2: "+ request.getParameter("class"));
		System.out.println( "para 3: "+ request.getParameter("uriInputProperty"));
		System.out.println( "para 4: "+ request.getParameter("nameInputProperty"));
		System.out.println( "para 5: "+ request.getParameter("descriptionInputProperty"));
		System.out.println( "para 6: "+ request.getParameter("checkbox-mapping")); // verificar que es null para mapeo o no
		System.out.println( "para 7: "+ request.getParameter("datasetMapping"));
		System.out.println( "para 8: "+ request.getParameter("mappingInputProperty"));
		
		
		dispatcher = request.getRequestDispatcher("jsp/"+"home.jsp");
	    dispatcher.forward( request, response);  
		
	}

}

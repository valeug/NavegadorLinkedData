package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ClassDAO;
import dao.PropertyDAO;
import model.Property;

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
		
		/*
		System.out.println( "para 1: "+ request.getParameter("dataset"));
		System.out.println( "para 2: "+ request.getParameter("class"));
		System.out.println( "para 3: "+ request.getParameter("uriInputProperty"));
		System.out.println( "para 4: "+ request.getParameter("nameInputProperty"));
		System.out.println( "para 5: "+ request.getParameter("descriptionInputProperty"));
		*/
		
		Property p = new Property();
		p.setUri(request.getParameter("uriInputProperty"));
		p.setName(request.getParameter("nameInputProperty"));
		// FALTA insertar la clase x property
		p.setDescription(request.getParameter("descriptionInputProperty"));
		
		if(request.getParameter("checkbox-mapping")!=null && request.getParameter("checkbox-mapping").compareTo("on")==0){ //se selecciono mapeo
			int idDatasetMapping = request.getParameter("datasetMapping").charAt(0)-'0';
			//String code = request.getParameter("mappingInputProperty");
			
			p.setIs_mapping(1);
			p.setTarget(idDatasetMapping);			
		}		
		else {
			p.setIs_mapping(-1);
			p.setTarget(-1);
		}
		
		/*
		System.out.println( "para 6: "+ request.getParameter("checkbox-mapping")); // verificar que es null para mapeo o no
		System.out.println( "para 7: "+ request.getParameter("datasetMapping"));
		System.out.println( "para 8: "+ request.getParameter("mappingInputProperty"));
		*/
		
		PropertyDAO dao = new PropertyDAO();
		int idProperty = dao.storeProperty(p);
		
		ClassDAO daoC = new ClassDAO();
		int idClass = request.getParameter("class").charAt(0)-0;
		daoC.storeClassxProperty(idClass, idProperty);
		
		/*
		RequestDispatcher dispatcher;
		//dispatcher = request.getRequestDispatcher("jsp/"+"home.jsp");
		
		response.sendRedirect("http://localhost:8080/HelloWorld/test");
		dispatcher = request.getRequestDispatcher("jsp/"+view);
	    dispatcher.forward( request, response);  
	    */
		response.sendRedirect("/NavegadorLinkedData/");
		//doGet(request, response);
		
	}

}

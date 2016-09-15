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

import model.Ontology;

/**
 * Servlet implementation class HomeServlet
 */

// POPULATETABLE
@WebServlet({"/HomeServlet"})
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<Ontology> ontList = new ArrayList<>();
		Ontology o1 = new Ontology();
		o1.setName("ontologia 1");
		o1.setDescription("wololooooooooooooooo");
		Ontology o2 = new Ontology();
		o2.setName("ontologia 2");
		o2.setDescription("asdfghjkl");
		ontList.add(o1);
		ontList.add(o2);
		request.setAttribute("ontologias", ontList);
		//RequestDispatcher dispatcher = request.getRequestDispatcher("jsp/home.jsp");
	    //dispatcher.forward( request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import model.Ontology;

/**
 * Servlet implementation class RetrieveOntologiesServlet
 */
@WebServlet("/RetrieveOntologies")
public class RetrieveOntologiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<Ontology> ontList = new ArrayList<>();
		Ontology o1 = new Ontology();
		o1.setName("ontologia 1");
		o1.setDescription("wololooooooooooooooo");
		
		Ontology o2 = new Ontology();
		o2.setName("ontologia 2");
		o2.setDescription("asdfghjkl;");
		
		ontList.add(o1);
		ontList.add(o2);
		
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(ontList, new TypeToken<List<Ontology>>() {}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);
		
		//request.setAttribute("ontologias", ontList);
		//RequestDispatcher dispatcher = request.getRequestDispatcher("jsp/home.jsp");
	    //dispatcher.forward( request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String[] results = request.getParameterValues("arr[]");		
		System.out.println("tamano: " + results.length);
		
		for (int i = 0; i < results.length; i++) {
		    System.out.println(results[i]); 			
		}				
	}

}

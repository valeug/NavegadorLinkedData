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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import dao.DatasetDAO;
import model.Dataset;
import model.Ontology;

/**
 * Servlet implementation class RetrieveOntologiesServlet
 */
@WebServlet("/RetrieveOntologies")
public class RetrieveOntologiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		DatasetDAO dao = new DatasetDAO();
		List<Dataset> datasetList = dao.getAllDatasets();

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(datasetList, new TypeToken<List<Dataset>>() {}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String[] results = request.getParameterValues("arr[]");		
		
		int [] ids = new int [results.length];
		
		for (int i = 0; i < results.length; i++) {
		    ids[i] = results[i].charAt(0) - '0'; 			
		}	
		
		DatasetDAO dao = new DatasetDAO();
		dao.enableDatasets(ids);
		//dao.updateStatusById(ids, 1);
		
	}

}

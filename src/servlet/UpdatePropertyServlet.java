package servlet;

import java.io.IOException;
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
import dao.PropertyDAO;
import model.Dataset;
import model.Property;

/**
 * Servlet implementation class UpdatePropertyServlet
 */
@WebServlet({"/UpdateProperty","/Configuration/Property/Update"})
public class UpdatePropertyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<Property> pList = PropertyDAO.getAllPropertyGroupedByDataset();
		/*
		for(int i=0; i < pList.size(); i++){
			System.out.println("id: " + pList.get(i).getId());
			System.out.println("uri: " + pList.get(i).getUri());
			System.out.println("dataset: " + pList.get(i).getDataset());
			System.out.println("==========================\n");
			//System.out.println("dataset: " + pList.get(i).getDataset());
		}
		*/
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(pList, new TypeToken<List<Property>>() {}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] results = request.getParameterValues("arr[]");		
		
		int [] ids = new int [results.length];
		
		
		for (int i = 0; i < results.length-1; i+=2) {
		    ids[i] = Integer.parseInt(results[i]);
		    ids[i+1] = Integer.parseInt(results[i+1]);
		    System.out.println("id actualizado: " + ids[i]);
		    System.out.println("dataset actualizado: " + ids[i+1]);
		    System.out.println("==========================\n");
		    PropertyDAO.updateConsolidate(ids[i], ids[i+1], 1);
		}	
		
		
		

	}

}

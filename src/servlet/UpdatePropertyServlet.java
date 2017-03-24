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
		
		System.out.println("entro getAllPropertyGroupedByDataset");
		
		List<Property> pList = PropertyDAO.getAllPropertyGroupedByDataset();
		System.out.println("sale getAllPropertyGroupedByDataset");
		
		System.out.println("UPDATE pList!!");
		for(int i=0; i < pList.size(); i++){
			System.out.println("id: " + pList.get(i).getId());
			System.out.println("uri: " + pList.get(i).getUri());
			System.out.println("dataset: " + pList.get(i).getDataset());
			System.out.println("==========================\n");
			//System.out.println("dataset: " + pList.get(i).getDataset());
		}
		
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(pList, new TypeToken<List<Property>>() {}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		//System.out.println(request.getParameterValues("arr"));
		String[] results = request.getParameterValues("arr[]");		
		
		int id_property, id_class, checked;
		
		
		for (int i = 0; i < results.length; i++) {
			String[] parts = results[i].split(" ");
			id_property = Integer.parseInt(parts[0]);
			id_class = Integer.parseInt(parts[1]);
			checked = Integer.parseInt(parts[2]);
			
		    System.out.println("id_property: " + id_property);
		    System.out.println("id_class: " + id_class);
		    System.out.println("checked: " + checked);
		    System.out.println("==========================\n");
		    
		    PropertyDAO.updateConsolidated(id_property, id_class, checked);
		}	
		
		
		

	}

}

package servlet;

import java.io.IOException;
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

import dao.ClassDAO;
import model.Class;

/**
 * Servlet implementation class RetrieveDatasetClassesServlet
 */
@WebServlet("/RetrieveDatasetClasses")
public class RetrieveDatasetClassesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClassDAO dao = new ClassDAO();
		
		System.out.println("servlet idDataset par: " + request.getParameter("idDataset"));
		System.out.println("servlet idDataset atr: " + request.getAttribute("idDataset"));
		//List<Class> classList = dao.getAllClassesByDataset(request.getAttribute("idDataset").toString().charAt(0) - '0');
		List<Class> classList = dao.getAllClassesByDataset(request.getParameter("idDataset").toString().charAt(0) - '0');
		
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(classList, new TypeToken<List<Class>>() {}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

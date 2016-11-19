package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DatasetDAO;


@WebServlet({"/HomeServlet",""})
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public HomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		/* SETEAR TODOS LOS DATASETS A ELEGIDOS*/		
		int ids [] = {1,2,3,4,5};
		DatasetDAO.updateStatusById(ids, 1);
		
		System.out.println("inicializo datasets");
		/* Llamar a la vista principal de busqueda*/
		RequestDispatcher dispatcher;
		dispatcher = request.getRequestDispatcher("jsp/home.jsp");
	    dispatcher.forward( request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

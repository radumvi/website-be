package servlets;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.order.DBOrderRepository;
import repository.order.OrderRepositoryInterface;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;

import entities.Item;

/**
 * HttpServlet implementation that handles actions on the orders.
 * Mapped to /orders/{id}
 */
public class OrdersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	OrderRepositoryInterface orderRepository;
	
	/**
	 * Constructor that sets the repository.
	 */
	public OrdersServlet() {
		super();
		orderRepository = new DBOrderRepository();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		JSONObject metaData = new JSONObject();
		JSONObject singleJSON = null;
		
		JSONArray result = new JSONArray();

		try {
			// can throw an exception
			ArrayList<Item> items = orderRepository.read(Integer.parseInt(request.getRequestURI().substring(8)));

			if (items != null) {
				metaData.put("success", true);
	    		metaData.put("count", items.size());
	    		
				for (Item item : items) {
					singleJSON = new JSONObject();

					singleJSON.put("name", item.getName());
					singleJSON.put("brand", item.getBrand());
					singleJSON.put("quantity", item.getQuantity());

					result.put(singleJSON);
				}
			}
			else {
				metaData.put("success", true);
	    		metaData.put("count", 0);
			}
		}
		catch (NumberFormatException e) {
			metaData.put("success", false);
    		metaData.put("message", "Invalid ID");
    		response.sendError(400); // bad request
		}
		
		output.put("result", result);
		output.put("metaData", metaData);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.order.DBOrderRepository;
import repository.order.OrderRepositoryInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

import entities.Order;


/**
 * HttpServlet implementation that handles actions on the orders.
 * Mapped to /store/users/orders
 */
public class UserOrdersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	OrderRepositoryInterface orderRepository;

	/**
	 * Contructor that sets the repository.
	 */
	public UserOrdersServlet() {
		super();
		orderRepository = new DBOrderRepository();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();
		JSONObject metaData = new JSONObject();
		JSONObject singleJSON;

		JSONArray result = new JSONArray();


		HttpSession session = request.getSession(false);

		if (session == null) {
			metaData.put("success", false);
			metaData.put("message", "The user is not logged in");
			response.sendError(401); // unauthorized
		}
		else {

			ArrayList<Order> orders = orderRepository.readByUser(((int) session.getAttribute("id")));

			if (orders != null) {
				metaData.put("success", true);
				metaData.put("count", orders.size());

				for (Order order : orders) {
					singleJSON = new JSONObject();

					singleJSON.put("id", order.getId());
					singleJSON.put("date", order.getDate());
					singleJSON.put("total", order.getTotal());
					singleJSON.put("status", order.getStatus());

					result.put(singleJSON);
				}
			}
			else {
				metaData.put("success", true);
				metaData.put("count", 0);
			}
		}

		output.put("result", result);
		output.put("metaData", metaData);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();

		try {
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));

			HttpSession session = request.getSession(false);

			if(session == null) {
				output.put("success", false);
				output.put("message", "The user is not logged in.");
			}
			else {
				// creating the order based on the userID, addressID, and list of items in the cart
				boolean isSuccessful = orderRepository.create((int)session.getAttribute("id"), input.getInt("addressID"), 
						(HashMap<Integer, Integer>) session.getAttribute("cart"));

				output.put("success", isSuccessful);

				if (isSuccessful) {
					session.setAttribute("cart", new HashMap<Integer, Integer>());
				}
				else {
					output.put("message", "The operation was not possible");
				}
			}
		}
		catch (IOException e) {
			output.put("success", false);
			output.put("message", "Invalid request body.");
			response.sendError(400); // bad request
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}

}

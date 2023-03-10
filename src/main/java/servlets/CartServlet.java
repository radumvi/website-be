package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

/**
 * HttpServlet implementation that handles actions on the cart.
 * Mapped to /store/users/cart
 */
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public CartServlet() {
		super();
	}
	
	/**
	 * Responds with all the items in the cart and their quantity.
	 * @throws IOException 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		JSONObject singleJSON = null;
		JSONObject metaData = new JSONObject();
		
		JSONArray result = new JSONArray();
		
		HttpSession session = request.getSession(false);

		if (session == null) {
			// if the user is not logged in
			metaData.put("success", false);
			metaData.put("message", "The user is not logged in.");
			
			response.sendError(401); // unauthorized
		}
		else {
			// getting the hashmap that represents the cart
			@SuppressWarnings("unchecked")
			Map<Integer, Integer> hm = (HashMap<Integer, Integer>) session.getAttribute("cart");

			for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
				singleJSON = new JSONObject();

				// adding each item and its quantity to the response
				if (entry.getValue() != 0) {
					singleJSON = new JSONObject();
					
					singleJSON.put("id", entry.getKey());
					singleJSON.put("count", entry.getValue());
					
					result.put(singleJSON);
				}
			}
			
			metaData.put("success", true);
			metaData.put("count", result.length());
		}
		
		output.put("result", result);
		output.put("metaData", metaData);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// input / output JSONObjects
		JSONObject output = new JSONObject();
		
		try {
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));

			HttpSession session = request.getSession(false);

			if (session == null) {
				// if the user is not logged in
				output.put("success", false);
				output.put("message", "The user is not logged in.");
			}
			else {
				// modifying the hashmap in the session
				@SuppressWarnings("unchecked")
				HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) session.getAttribute("cart");
				// then, I add the id and the count from the request
				hm.put(input.getInt("id"), input.getInt("count"));
				output.put("success", true);
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

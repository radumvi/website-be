package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.item.*;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

import entities.Item;

/**
 * HttpServlet implementation that handles actions on the reviews.
 * Mapped to /store/items/{id}
 */
public class ItemsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private ItemRepositoryInterface itemRepository;
    
	/**
	 * Contructor that sets the item repository.
	 */
    public ItemsServlet() {
        super();
        itemRepository = new DBItemRepository();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		
		/*
		 * URL:
		 * getting all the items -- /Store/items      (length = 12)
		 * getting a single item -- /Store/items/{id}
		 */
		
		if (request.getRequestURI().length() == 12) {
			
	    	JSONArray result = new JSONArray();
	    	JSONObject metaData = new JSONObject();
	    	JSONObject singleJSON;
	    			
	    	// getting all the items from the database
	    	ArrayList<Item> items = itemRepository.read();
	    			
	    	if (items != null) {
	    		metaData.put("success", true);
	    		metaData.put("count", items.size());
	    		
	    		// adding each item to the result JSON
	    		for (Item item : items) {
	    			singleJSON = new JSONObject();
	    					
	    			singleJSON.put("id", item.getId());
					singleJSON.put("name", item.getName());
					singleJSON.put("brand", item.getBrand());
					singleJSON.put("quantity", item.getQuantity());
					singleJSON.put("price", item.getPrice());
					singleJSON.put("rating", item.getRating());

	    			result.put(singleJSON);
	    		}
	    	}
	    	else {
	    		// there are no items in the result
	    		metaData.put("success", true);
	    		metaData.put("count", 0);
	    	}
	    	output.put("result", result);
	    	output.put("metaData", metaData);
		}
		
		else {
			try {
				Item item = itemRepository.read(Integer.parseInt(request.getRequestURI().substring(13)));
				
				if (item != null) {
					// adding item fields values to the response
					output.put("success", true);
					output.put("name", item.getName());
					output.put("brand", item.getBrand());
					output.put("quantity", item.getQuantity());
					output.put("price", item.getPrice());
					output.put("rating", item.getRating());
				}
				else {
					output.put("success", false);
				}
			}
			catch(NumberFormatException e) {
				output.put("success", false);
				response.sendError(400); // bad request
			}
		}
						
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject output = new JSONObject();
		HttpSession session = request.getSession();
		
		if (session == null) {
			output.put("success", false);
			output.put("message", "The user is not logged in.");
			response.sendError(401); // unauthorized
		}
		else {
			if ((boolean) session.getAttribute("isAdmin")) {
				// here I should allow the action
				try {
					// this can throw an IOException
					JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));
					
					Item item = new Item();
					item.setName(input.getString("name"));
					item.setBrand(input.getString("brand"));
					item.setQuantity(input.getString("quantity"));
					item.setPrice(input.getFloat("price"));
					item.setCategory(input.getString("category"));
					
					output.put("success", itemRepository.create(item));
				}
				
				catch(IOException e) {
					output.put("success", false);
					output.put("message", "The action was not possible.");
				}
				
			}
			else {
				output.put("success", false);
				output.put("message", "The user is not an admin.");
				response.sendError(401); // unauthorized
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		HttpSession session = request.getSession();
		
		if (session == null) {
			output.put("success", false);
			output.put("message", "The user is not logged in.");
		}
		else {
			if ((boolean) session.getAttribute("isAdmin")) {
				// here I should allow the action
				try {
					// this can throw an IOException
					JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));
					
					Item item = new Item();
					
					item.setId(Integer.parseInt(request.getRequestURI().substring(13)));
					item.setName(input.getString("name"));
					item.setBrand(input.getString("brand"));
					item.setPrice(input.getFloat("price"));
					
					boolean isSuccessful = itemRepository.create(item);
					output.put("success", isSuccessful);
					
					if (!isSuccessful) {
						output.put("message", "The action was not possible.");
					}
				}
				
				catch(IOException e) {
					output.put("success", false);
					output.put("message", "Invalid request body.");
					response.sendError(400); // bad request
				}
				catch(NumberFormatException e) {
					output.put("success", false);
					output.put("message", "Invalid ID.");
					response.sendError(400); // bad request
				}
				
			}
			else {
				output.put("success", false);
				output.put("message", "The user is not an admin.");
				response.sendError(401); // unauthorized
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();
		
		try {
			// passing the id
			
			boolean isSuccessful = itemRepository.delete(Integer.parseInt(request.getRequestURI().substring(13)));
			output.put("success", isSuccessful);
			
			if(!isSuccessful) {
				output.put("message", "The action was not possible.");
			}
		}
		catch(NumberFormatException e) {
			output.put("success", false);
			output.put("message", "Invalid URL.");
			response.sendError(400); // bad request
		}
						
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

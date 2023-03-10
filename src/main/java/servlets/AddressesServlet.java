package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

import repository.address.*;

import entities.Address;

/**
 * HttpServlet implementation that handles actions on addresses.
 * Mapped to /store/users/addresses/{id}
 */
public class AddressesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AddressRepositoryInterface addressRepository;
    
	/**
	 * Contructor that sets the repository.
	 */
    public AddressesServlet() {
        super();
        addressRepository = new DBAddressRepository();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		JSONObject metaData = new JSONObject();
		JSONObject singleJSON = null;
		
		JSONArray result = new JSONArray();
				
		HttpSession session = request.getSession(false);
				
		if (session == null) {
			
			// the user has not logged in
			metaData.put("success", false);
    		metaData.put("message", "The user is not logged in");
    		
    		response.sendError(401); // unauthorized
		}
		else {
			// result of the query
			ArrayList<Address> addresses = addressRepository.read((int) session.getAttribute("id"));
					
			// adding them to the response
			if (addresses != null) {
				metaData.put("success", true);
	    		metaData.put("count", addresses.size());
	    		
				for (Address addr : addresses) {
					singleJSON = new JSONObject();
					
					singleJSON.put("id", addr.getId());
					singleJSON.put("county", addr.getCounty());
					singleJSON.put("city", addr.getCity());
					singleJSON.put("exactAddress", addr.getExactAddress());
					
					result.put(singleJSON);
				}
			}
			else {
				// there are no addresses
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		
		try {
			// can throw an exception
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));
			
			HttpSession session = request.getSession(false);
			
			if (session == null) {
				// the user is not logged in
				output.put("success", false);
				output.put("message", "The user is not logged in.");
			}
			else {
				// setting the address object
				Address address = new Address();
				
				address.setCounty(input.getString("county"));
				address.setCity(input.getString("city"));
				address.setExactAddress(input.getString("exactAddress"));
				address.setUserId((int)session.getAttribute("id"));
				
				// adding it to the database
				boolean isSuccessful = addressRepository.create(address);
				
				output.put("success", isSuccessful);
				if (!isSuccessful) {
					output.put("message", "The operation was not possible.");
				}
			}
		}
		catch (IOException e) {
			// the url is not valid
			output.put("success", false);
			output.put("message", "Invalid request body.");
			response.sendError(400); // bad request
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();
		
		try {
			HttpSession session = request.getSession(false);
			
			if (session == null) {
				output.put("success", false);
				output.put("message", "The user is not logged in.");
			}
			else {
				// trying to delete the address from the database
				boolean isSuccessful = addressRepository.delete(Integer.parseInt(request.getRequestURI().substring(23)), 
						(int) session.getAttribute("id"));
				
				output.put("success", isSuccessful);
				if (!isSuccessful) {
					output.put("message", "The operation was not possible");
				}
			}
			
		}
		catch(NumberFormatException e) {
			output.put("success", false);
			output.put("message", "The URL is not valid.");
			response.sendError(400); // bad syntax
		}
				
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.review.DBReviewRepository;
import repository.review.ReviewRepositoryInterface;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

import entities.Item;
import entities.Review;
import entities.User;

/**
 * HttpServlet implementation that handles actions on the reviews.
 * Mapped to /store/items/reviews/{id}
 * 
 * !!! the id is of the item, not review
 */
public class ItemReviewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ReviewRepositoryInterface reviewRepository;
	
	/**
	 * Contructor that sets the repository.
	 */
    public ItemReviewsServlet() {
        super();
        reviewRepository = new DBReviewRepository();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject output = new JSONObject();
		JSONObject metaData = new JSONObject();
		JSONObject singleJSON = null;
		
		JSONArray result = new JSONArray();
		
		try {
			// can throw an exception
			ArrayList<Review> reviews = reviewRepository.readByItem(Integer.parseInt(request.getRequestURI().substring(21)));
			
			if (reviews != null) {
				metaData.put("success", true);
	    		metaData.put("count", reviews.size());
	    		
				// adding each review to the response body
				for (Review review : reviews) {
					singleJSON = new JSONObject();
								
					singleJSON.put("id", review.getId());
					singleJSON.put("rating", review.getRating());
					singleJSON.put("title", review.getTitle());
					singleJSON.put("productName", review.getItem().getName());
					singleJSON.put("brand", review.getItem().getBrand());
								
					result.put(singleJSON);
				}
			}
			else {
				// there are no reviews associated with the item
				metaData.put("success", true);
				metaData.put("count", 0);
			}
		}
		catch(NumberFormatException e) {
			metaData.put("success", false);
			metaData.put("message", "The URL is not valid.");
			
			response.sendError(400); // bad request
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
			// can throw am exception
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));
			
			HttpSession session = request.getSession(false);
					
			if (session == null) {
				// the user is not logged in and wants to post a review
				output.put("success", false);
				output.put("message", "The user is not logged in.");
				
				response.sendError(401); // unauthorized
			}
			else {
				Review reviewObject = new Review();
				reviewObject.setUser(new User());
				reviewObject.setItem(new Item());
				
				reviewObject.getUser().setId((int) session.getAttribute("id"));
				// catch block for this
				reviewObject.getItem().setId(Integer.parseInt(request.getRequestURI().substring(21)));
				reviewObject.setRating(input.getInt("rating"));
				reviewObject.setTitle(input.getString("title"));
							
				// trying to add the review to the database
				
				boolean isSuccessful = reviewRepository.create(reviewObject);
				output.put("success", isSuccessful);
				output.put("message", "The operation was not possible");
			}
		}
		catch (IOException e) {
			output.put("success", false);
			output.put("message", "Invalid request body.");
			response.sendError(400); // bad request
		}
		catch (NumberFormatException e) {
			output.put("success", false);
			output.put("message", "Invalid ID");
			response.sendError(400); // bad request
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

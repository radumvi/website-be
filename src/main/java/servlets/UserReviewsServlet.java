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

import org.json.*;

import entities.Review;

public class UserReviewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ReviewRepositoryInterface reviewRepository;
	
	/**
	 * Constructor that sets the repository.
	 */
    public UserReviewsServlet() {
        super();
        reviewRepository = new DBReviewRepository();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// output as JSONObject
		JSONObject output = new JSONObject();
				
		// JSON for building the response
		JSONArray result = new JSONArray();
		JSONObject metaData = new JSONObject();
		JSONObject singleJSON;
				
		HttpSession session = request.getSession(false);
				
		ArrayList<Review> reviews = null;
				
		if (session != null) {
			// the user is logged in
			// getting all the reviews from the database based on the user's id
			reviews = reviewRepository.readByUser((int) session.getAttribute("id"));
		}
		else {
			metaData.put("success", false);
    		metaData.put("message", "The user is not logged in.");
    		response.sendError(401); // unauthorized
		}
					
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
			// there are no reviews associated with the user
			metaData.put("success", true);
    		metaData.put("count", 0);
		}
		
		output.put("result", result);
		output.put("metaData", metaData);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

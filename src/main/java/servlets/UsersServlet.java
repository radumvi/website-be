package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.user.*;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.*;

import entities.User;
import helpers.StringOperations;

public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserRepositoryInterface userRepository;

	/**
	 * Constructor that sets the repository.
	 */
	public UsersServlet() {
		super();
		userRepository = new DBUserRepository();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();

		JSONArray result = new JSONArray();
		JSONObject metaData = new JSONObject();
		JSONObject prodJSON;

		HttpSession session = request.getSession(false);

		/*
		 * The URL looks like:
		 * /Store/users
		 * /Store/users/{id}
		 */

		if (request.getRequestURI().length() == 12) {

			if (session != null) {
				if ((boolean) session.getAttribute("isAdmin")) {
					// user is logged in and an admin
					ArrayList<User> users = userRepository.read();

					if (users != null) {
						metaData.put("success", true);
						metaData.put("count", users.size());

						// adding each user to the response
						for (User user : users) {
							prodJSON = new JSONObject();

							prodJSON.put("id", user.getId());
							prodJSON.put("firstName", user.getFirstName());
							prodJSON.put("lastName", user.getLastName());
							prodJSON.put("emailAddress", user.getEmailAddress());

							result.put(prodJSON);
						}
					}
					else {
						// there are no users in the result
						metaData.put("success", true);
						metaData.put("count", 0);
					}
				}
				else {
					// not owner nor admin
					metaData.put("success", false);
					metaData.put("message", "The user is not an admin.");
					response.sendError(401); // unauthorized
				}
			}
			else {
				// user not logged in
				metaData.put("success", false);
				metaData.put("message", "The user is not logged in.");
				response.sendError(401); // unauthorized
			}

			// adding the metadata to the response
			output.put("result", result);
			output.put("metaData", metaData);
		}

		else {
			// one user's information is required

			try {

				if (session == null) {
					output.put("success", false);
					output.put("message", "The user is not logged in.");
				}
				else {
					if (((Integer)session.getAttribute("id") == Integer.parseInt(request.getRequestURI().substring(13))) 
							|| (boolean) session.getAttribute("isAdmin")) {
						// the user requires their information or the admin wants information
						User user = userRepository.read(Integer.parseInt(request.getRequestURI().substring(13)));

						if (user != null) {
							// adding user fields values to the response
							output.put("success", true);
							output.put("id", user.getId());
							output.put("firstName", user.getFirstName());
							output.put("lastName", user.getLastName());
							output.put("phoneNumber", user.getPhoneNumber());
							output.put("emailAddress", user.getEmailAddress());
						}
						else {
							output.put("success", false);
							output.put("message", "The action was not possible.");
						}
					}
					else {
						// guest
						output.put("success", false);
						output.put("message", "Not allowed");
						response.sendError(401); // unauthorized
					}
				}
			}

			catch (NumberFormatException e) {
				output.put("success", false);
				output.put("message", "Invalid ID.");
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// input / output as JSONObjects
		JSONObject output = new JSONObject();

		try {
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));

			// setting the user's attributes
			User user = new User();
			user.setFirstName(input.getString("firstName"));
			user.setLastName(input.getString("lastName"));
			user.setPhoneNumber(input.getString("phoneNumber"));
			user.setEmailAddress(input.getString("emailAddress"));
			user.setPassword(input.getString("password"));
			user.setAdmin(input.getBoolean("isAdmin"));


			// checking to see if the password is valid
			if (!StringOperations.checkPassword(user.getPassword())) {
				output.put("success", false);
			}
			else {
				// attempting to add the user to the database
				output.put("success", userRepository.create(user));
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

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject output = new JSONObject();
		HttpSession session = request.getSession(false);

		try {
			if (session != null) {
				if (( (Integer) session.getAttribute("id") == Integer.parseInt(request.getRequestURI().substring(13)))
						|| (boolean) session.getAttribute("isAdmin")) {
					// user modifies their data or admin
					JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));
					User user = new User();
					user.setId(Integer.parseInt(request.getRequestURI().substring(13)));
					user.setPhoneNumber(input.getString("phoneNumber"));
					user.setFirstName(input.getString("firstName"));
					user.setLastName(input.getString("lastName"));

					// trying to update the information in the database
					boolean isSuccessful = userRepository.update(user);
					output.put("success", isSuccessful);

					if (!isSuccessful) {
						output.put("message", "The operation was not possible.");
					}	
				}
				else {
					// not owner or admin
					output.put("success", false);
					output.put("message", "Not allowed.");
					response.sendError(401); // unauthorized
				}
			}
			else {
				// guest
				output.put("success", false);
				output.put("message", "User is not logged in.");
				response.sendError(401); // unauthorized
			}
		}
		catch(IOException e) {
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

		HttpSession session = request.getSession(false);

		try {
			if (session != null) {
				if (( (Integer) session.getAttribute("id") == Integer.parseInt(request.getRequestURI().substring(13)))
						|| (boolean) session.getAttribute("isAdmin")) {
					// admin or owner action
					boolean isSuccessful = userRepository.delete(Integer.parseInt(request.getRequestURI().substring(13)));

					output.put("success", isSuccessful);

					if (!isSuccessful) {
						output.put("success", "The operation was not possible");
					}
				}
				else {
					// not owner or admin
					output.put("success", false);
					output.put("message", "Not allowed.");
					response.sendError(401); // unauthorized
				}
			}
			else {
				// guest
				output.put("success", false);
				output.put("message", "User is not logged in.");
				response.sendError(401); // unauthorized
			}
		}
		catch(NumberFormatException e) {
			output.put("success", false);
			output.put("message", "Invalid ID");
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}
}

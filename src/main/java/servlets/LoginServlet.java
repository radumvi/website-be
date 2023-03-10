package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.login.DBLogin;

import java.io.IOException;
import java.util.HashMap;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Servlet that allows login.
 * Mapped to /store/login
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private DBLogin login;
    
    public LoginServlet() {
        super();
        login = new DBLogin();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// input / output as JSONObjects
		JSONObject output = new JSONObject();
		
		try {
			// can throw an exception
			JSONObject input = new JSONObject((String) IOUtils.toString(request.getReader()));

			boolean isSuccessful = login.isRegistered(input.getString("emailAddress"), input.getString("password"));
			output.put("success", isSuccessful);
			
			if (!isSuccessful) {
				output.put("message", "Wrong credentials.");
			}
			
			// sending the role of the user to the front-end application
			if (login.isAdmin()) {
				output.put("role", "admin");
			}
			else {
				output.put("role", "customer");
			}
			
			if ((boolean) output.get("success")) {
				// if the login operations has been successful, I create a session
				HttpSession sess = request.getSession(true);
				sess.setAttribute("id", login.getId());
				sess.setAttribute("isAdmin", login.isAdmin());
				sess.setAttribute("cart", new HashMap<Integer, Integer>());
				output.put("id", login.getId());
			}
		}
		catch (IOException e) {
			output.put("success", false);
			output.put("message", "Invalid request body");
			response.sendError(400); // bad request
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "text/plain");
		response.getWriter().write(output.toString());
		response.getWriter().flush();
	}

}

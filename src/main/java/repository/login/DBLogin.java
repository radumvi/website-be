package repository.login;

import java.sql.*;

import helpers.StringOperations;

/**
 * Class that interacts with the database and checks users.
 *
 */
public class DBLogin {
	static final String DB_URL = "jdbc:mysql://localhost:3306/magazin";
	static final String USER = "root";
	static final String PASS = "parola";
	
	// needed for communication with the servlet
	private int id;
	private boolean isAdmin;
	
	/**
	 * Checks if the user is registered.
	 * @param emailAddress	the email address of the user
	 * @param password		the password to be checked
	 * @return				true if the user is registered
	 */
	public boolean isRegistered(String emailAddress, String password) {
		
		// applying the hash function on the password, s.t. it can be checked
		password = StringOperations.hashPassword(password);
		
		try {
			// getting some information on the user with the provided email
			String query = "SELECT UserID, HashedPassword, IsAdmin "
					+ "FROM User WHERE EmailAddress=\"" + emailAddress + "\"";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(rs.next() == false) {
				// there is no user with that email
				return false;
			}
			else {
				// checking if the password match
				if (rs.getString("HashedPassword").equals(password)) {
					this.isAdmin = rs.getBoolean("IsAdmin");
					this.id = rs.getInt("UserID");
					return true;
				}
				else {
					// wrong password
					return false;
				}
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			// something went wrong
			return false;
		}
	}
	
	public int getId() {
		return id;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
}

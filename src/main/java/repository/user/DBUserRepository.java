package repository.user;

import java.sql.*;
import java.util.ArrayList;

import entities.User;
import helpers.StringOperations;

public class DBUserRepository implements UserRepositoryInterface {
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean create(User userObject) {
		// hashing the password
		userObject.setPassword(StringOperations.hashPassword(userObject.getPassword()));
		
		try {
			// building the instruction and executing it
			String insertString = "INSERT INTO "
					+ "User(LastName, FirstName, PhoneNumber, EmailAddress, HashedPassword, IsAdmin)"
					+ " VALUES(\"" + userObject.getLastName() 
					+ "\", \"" + userObject.getFirstName() 
					+ "\", \"" + userObject.getPhoneNumber()
					+ "\", \"" + userObject.getEmailAddress() 
					+ "\", \"" + userObject.getPassword() 
					+ "\", \"" + (userObject.isAdmin() ? 1 : 0) + "\");";
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertString);
			
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			// something has gone wrong
			return false;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public ArrayList<User> read() {
		
		ArrayList<User> result = new ArrayList<>();
		User buffer;
		
		try {
			String query = "SELECT UserID, LastName, FirstName, EmailAddress, PhoneNumber "
					+ "FROM User";
	
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				return null;
			} 
			else {
				do {
					// adding each user to the list that will be returned
					buffer = new User();
					
					buffer.setId(rs.getInt("UserId"));
					buffer.setFirstName(rs.getString("FirstName"));
					buffer.setLastName(rs.getString("LastName"));
					buffer.setEmailAddress(rs.getString("EmailAddress"));
					buffer.setPhoneNumber(rs.getString("PhoneNumber"));
					
					result.add(buffer);
				}
				while (rs.next() != false);
				// no exception was thrown
				return result;
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public User read(int id) {
		User user = new User();
		
		try {
			// getting a user's info based on their id
			String query = "SELECT LastName, FirstName, PhoneNumber, EmailAddress "
					+ "FROM User "
					+ "WHERE UserId=" + id + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				return null;
			} 
			else {
				// setting the object with the information in the resulting table
				user.setId(id);
				user.setFirstName(rs.getString("FirstName"));
				user.setLastName(rs.getString("LastName"));
				user.setPhoneNumber(rs.getString("PhoneNumber"));
				user.setEmailAddress(rs.getString("EmailAddress"));
				
				return user;
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean update(User userObject) {
		
		try {
			String updateString = "UPDATE User "
					+ "SET LastName = " + "\"" + userObject.getLastName() + "\", "
					+ "FirstName = " + "\"" + userObject.getFirstName() + "\","
					+ "PhoneNumber = " + "\"" + userObject.getPhoneNumber()
					+ "\" WHERE UserID = \"" + userObject.getId() +"\";";
			
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(updateString);
			
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean delete(int id) {
		try {
			// delete an user based on their id
			String deleteString = "DELETE FROM User WHERE UserId = " + id + ";";
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(deleteString);
			// there were no exceptions
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}

}
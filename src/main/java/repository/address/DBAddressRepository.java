package repository.address;

import java.sql.*;
import java.util.ArrayList;

import entities.Address;

/**
 * Implementation of the AddressRepositoryInterface. 
 * Makes possible operations on the addresses in the database.
 */
public class DBAddressRepository implements AddressRepositoryInterface {
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean create(Address address) {
		try {
			String insertString = "INSERT INTO "
					+ "Address(UserID, County, City, ExactAddress) "
					+ "values(" + address.getUserId() 
					+ ", \"" + address.getCounty() 
					+ "\",\"" + address.getCity()
					+ "\",\"" + address.getExactAddress() + "\");"; 
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertString);
			
			// there was no exception thrown
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
	public ArrayList<Address> read(int userId) {
		
		ArrayList<Address> result = new ArrayList<>();
		Address buffer;
				
		try {
			String query = "SELECT AddressID, County, City, ExactAddress "
					+ "FROM Address "
					+ "WHERE UserID = \"" + userId + "\";";
					
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
					
			if (rs.next() == false) {
				// there were no addresses
				return null;
			} 
			else {
				do {
					// adding each entry in the result list
					buffer = new Address();
					
					buffer.setId(rs.getInt("AddressID"));
					buffer.setCounty(rs.getString("County"));
					buffer.setCity(rs.getString("City"));
					buffer.setExactAddress(rs.getString("ExactAddress"));
					
					result.add(buffer);
				}
				while (rs.next() != false);
			}
					
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;			
		}
				
		return result;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean delete(int addressId, int userId) {
		try {
			// checking if the address is in the database 
			// and associated with the user
			String query = "SELECT * "
					+ "FROM Address WHERE AddressID = " + addressId
					+ " AND UserID = " + userId + ";";
	
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(rs.next() == false) {
				// that combination doesn't exist in the database
				return false;
			}
			else {
				// actual delete statement
				String deleteString = "DELETE FROM Address WHERE AddressID=" + addressId + ";";
				
				stmt = conn.createStatement();
				stmt.executeUpdate(deleteString);
				
				return true;
			}
		}
		
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}
}
